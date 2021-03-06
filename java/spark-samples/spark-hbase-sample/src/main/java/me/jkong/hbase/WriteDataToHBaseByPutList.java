package me.jkong.hbase;


import lombok.Getter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.security.UserProvider;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.parquet.hadoop.BadConfigurationException;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * 使用 sparksql 操作 Hbase [仅供测试]
 *
 * @author JKong
 * @version v0.0.1
 * @date 2020/5/6 14:30.
 */
public class WriteDataToHBaseByPutList {

    private static final String HBASE_ZOOKEEPER_QUORUM = "10.10.27.47,10.10.27.48,10.10.27.49";
//    private static final String HBASE_ZOOKEEPER_QUORUM = "10.10.34.41,10.10.34.42,10.10.34.43";

    private static final String HBASE_ZOOKEEPER_PROPERTY_CLIENT_PORT = "2181";

    public static void main(String[] args) {
        // 1. Spark相关各项配置初始化
        SparkConf sparkConf = new SparkConf()
                .setAppName("WriteDataToHBaseByPutList")
                .setMaster("local[*]");
        sparkConf.set("spark.sql.shuffle.partitions", "2");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);
        SQLContext sqlContext = new SQLContext(jsc);

        // 2. 加载测试数据 -> df
        Dataset<Row> dataset = loadDataFrameFromJson(sqlContext);

        // 3. 将数据转为 iterator 或者 集合对象
        Iterator<Row> rowIterator = dataset.toLocalIterator();

        try {
            // 4. 创建 HBase 配置管理对象，并获取HBase连接对象
            HBaseConfigurationHolder confHolder = new HBaseConfigurationHolder(HBASE_ZOOKEEPER_QUORUM);
            Admin admin = confHolder.getAdmin();

            // 5. 验证需要导入的表是否存在，如果不存在将创建表，及表结构
            // Verify the existence of the table, create an HBase table if it does not exist
            TableName tableName = TableName.valueOf("user_table");

            if (!admin.tableExists(tableName)) {
                HTableDescriptor desc = new HTableDescriptor(tableName);
                HColumnDescriptor information = new HColumnDescriptor("information".getBytes());
                HColumnDescriptor contact = new HColumnDescriptor("contact".getBytes());
                desc.addFamily(information);
                desc.addFamily(contact);
                admin.createTable(desc);
            } else {
                // 6. 根据设置如果表存在将表中数据清空
                admin.clearBlockCache(tableName);
                admin.disableTable(tableName);
                // 使用truncate清空表之后，对table进行enable，所以不需要再进行enable table
                admin.truncateTable(tableName, false);
            }

            final List<Put> putList = new ArrayList<>();
            Row row;
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                // 测试行schema输出，以及根据字段名获取数
                printRowSchema(row);
                Put put = new Put(Bytes.toBytes(String.valueOf(row.get(0))));
                put.addColumn("information".getBytes(), "username".getBytes(), Bytes.toBytes(String.valueOf(row.get(1))));
                put.addColumn("information".getBytes(), "age".getBytes(), Bytes.toBytes(String.valueOf(row.get(2))));
                put.addColumn("information".getBytes(), "gender".getBytes(), Bytes.toBytes(String.valueOf(row.get(3))));
                put.addColumn("contact".getBytes(), "email".getBytes(), Bytes.toBytes(String.valueOf(row.get(4))));
                put.addColumn("contact".getBytes(), "phone".getBytes(), Bytes.toBytes(String.valueOf(row.get(5))));
                putList.add(put);
            }

            Table userTable = confHolder.getConnection().getTable(tableName);
            userTable.put(putList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jsc.close();
        }
        System.out.println("ending");
    }

    /**
     * 测试行schema输出，以及根据字段名获取数
     *
     * @param row 行数据
     */
    private static void printRowSchema(Row row) {
        StructType schema = row.schema();
        StructField[] fields = schema.fields();
        for (StructField field : fields) {
            System.out.println(field.name() + " " + field.dataType() + " " + field.toString());
        }
        System.out.println(row.getAs("name").toString() + "\n");
    }

    /**
     * 加载json文件到DataFrame
     *
     * @param sqlContext
     * @return
     */
    private static Dataset<Row> loadDataFrameFromJson(SQLContext sqlContext) {
        Map<String, String> options = new HashMap<>();
        options.put("url", "jdbc:mysql://127.0.0.1:3306/spark?useUnicode=true&characterEncoding=utf8&TreatTinyAsBoolean=false&tinyInt1isBit=false");
        options.put("driver", "com.mysql.jdbc.Driver");
        options.put("user", "root");
        options.put("password", "123456");
        options.put("dbtable", "user");
        Dataset<Row> userDataFrame = sqlContext.read().format("jdbc").options(options).load();
        userDataFrame.createOrReplaceTempView("user_tmp");
        Dataset<Row> result = sqlContext.sql("select id,name,age,gender,email,phone from user_tmp");
        result.show();
        return result;
    }

    private static class HBaseConfigurationHolder implements Serializable {
        private static final long serialVersionUID = 2L;

        private static final String DEFAULT_PORT = "2181";
        private static final String HBASE_ZOOKEEPER_QUORUM = "hbase.zookeeper.quorum";
        private static final String HBASE_ZOOKEEPER_PROPERTY_CLIENT_PORT = "hbase.zookeeper.property.clientPort";

        @Getter
        private transient Configuration configuration;
        @Getter
        private transient Connection connection;
        @Getter
        private transient Admin admin;

        public HBaseConfigurationHolder(String zkQuorum) {
            this(zkQuorum, DEFAULT_PORT);
        }

        public HBaseConfigurationHolder(String zkQuorum, String port) {
            if (zkQuorum == null || "".equals(zkQuorum)) {
                throw new IllegalArgumentException("zookeeper quorum must not null.");
            }

            this.configuration = HBaseConfiguration.create();
            this.configuration.set(HBASE_ZOOKEEPER_QUORUM, zkQuorum);
            this.configuration.set(HBASE_ZOOKEEPER_PROPERTY_CLIENT_PORT,
                    port == null || port.trim().length() == 0 ? DEFAULT_PORT : port);

            try {
                User user = UserProvider.instantiate(configuration)
                        .create(UserGroupInformation.createRemoteUser("hbase"));
                this.connection =
                        ConnectionFactory.createConnection(this.configuration,user);
                this.admin = this.connection.getAdmin();
            } catch (IOException e) {
                throw new BadConfigurationException("create Hbase connection error.");
            }
        }
    }
}