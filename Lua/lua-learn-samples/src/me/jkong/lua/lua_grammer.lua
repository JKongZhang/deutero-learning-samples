---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by zhdh.
--- DateTime: 2020-03-06 17:52
---

--注释方式
--[[
	多行注释
--]]


--********************变量类型********************
--lua中所有的变量 不需要申明变量类型
--lua中的所有变量 默认为全局变量  非全局前面加local
--lua中没有各种数值类型 统一都是Number类型 表示浮点数和
print("*************全局本地变量*************")
local a = 1 --局部 仅限于本文本访问  编程时 尽量都加local
print(a)
a1 = 1.5 --全局
print(a1)
--lua中只有false和nil才为假  0和空字符串为真
e = true;
print(e)


--lua中的string 可以用单引号或者双引号括起来
print("*************字符串相关*************")
b = '单引号字符串'
print(b)
c = "双引号字符串"
print(c)
--lua中字符串的换行 省略了\n换行替代符号
c = [[我是
你的
小宝贝
]]
print(c)
--字符串相关
str = "abCdefg"
print(string.upper(str))--小写转大写
print(string.lower(str))--大写转小写
print(string.reverse(str))--翻转字符串
print(string.find(str, "Cde"))--字符串索引查找
print(string.sub(str, 3))--字符串截取
print(string.rep(str, 3))--字符串重复
print(string.gsub(str, "Cd", "**"))--字符串修改
print(#str)--字符串长度
a = true
print(tostring(a));
print(string.format("我是%s,我今年%d岁了", "小宝贝", 2))--字符串长度


--lua中空的关键字时nil 相当于C#中的null
print("*************空引用*************")
d = nil
print(d)
print("*************类型获取*************")
--字符串拼接符号..
--type 类型判断
print("类型判断" .. type(e))


--********************运算符********************
print("*************运算符*************")
--lua中没有自增减运算符
print("加法运算" .. 1 + 2)
print("余数运算" .. 9 % 2)
print("减法运算" .. 9 - 2)
print("乘法运算" .. 9 * 2)
print("除法运算" .. 9 / 2)
print("幂运算" .. 9 ^ 2)


--********************条件分支语句********************
print("*************条件分支语句*************")
a = 9
if a > 9 then
    print("大于9")
elseif a >= 5 then
    print("大于5")
elseif a == 3 then
    print("值为3")
elseif a ~= 2 then
    --不等于
    print("值为3")
end


--********************逻辑运算符********************
print("*************逻辑运算符*************")
a = true
b = false
if a and b then
    --逻辑与
    print("大于9")
elseif a or b5 then
    --逻辑或
    print("大于5")
elseif not a then
    -- 逻辑否
    print("其它数")
end


--********************循环语句********************
print("*************循环语句*************")
--while 循环
print("*************while循环*************");
num = 0
while num < 5 do
    print(num);
    num = num + 1
end
--do while 循环
print("*************do while循环*************");
a = 1;
repeat
    print(a);
    a = a + 1
until a > 5
--for循环
print("*************for循环*************");
for i = 1, 5 do
    --默认递增+1
    print(i)
end

for i = 5, 1, -1 do
    --递减1
    print(i)
end


--********************函数********************
print("*************函数*************")
function f(x)
    return x ^ 2
end
print(f(2))

function f2(x2)
    if x2 < 10 then
        print("白卷")
    elseif x2 < 60 then
        print("分太低")
    elseif x2 < 70 then
        print("及格")
    end
end
f2(2)
print("*************变长参数函数*************")
function ff(...)
    local arg = { ... }
    local total = 0
    for i = 1, #arg do
        total = total + arg[i]
    end
    print(total)
end
ff(1, 2, 3, 4, 5, 6)

print("*************函数嵌套*************")
function f3(x)
    return function(y)
        return x + y
    end
end
a = f3(1)
print(a(2))

--********************复杂数据类型********************
print("*************复杂数据类型*************")
--所有的复杂类型都是table(表)
--数组
print("*************数组，列表*************")
--数组的存储类型可以混合，默认索引从1开始
a = { 1, 2, 3, "12312", 123123 }
print(a[1])
print(a[4])
print(#a)--打印长度 从1开始计数
print("*************数组遍历*************")
for i = 1, #a do
    print(a[i])
end
--可以自定义索引 甚至负索引
print("*************自定义索引*************")
a = { [0] = 1, 2, 3, [-1] = 4, 5 }
print(a[0])
print(a[1])
print(a[-1])
print(a[2])
print(a[3])
print(#a)--打印长度 中间产生索引断裂了 会停止计数
print("*************二维数组*************")
a = { { 1, 2, 3 }, { 4, 5, 6 } }
print(a[1][1])
print(a[1][2])
print(a[1][3])
print(a[2][1])
print(a[2][2])
print(a[2][3])
--迭代器遍历
print("*************ipairs迭代遍历键值*************")
a = { [0] = 1, 2, [-1] = 3, 4, 5 }
--遍历键值
for i, k in ipairs(a) do
    print("ipairs遍历键" .. i)
    print("ipairs遍历值" .. k)
end
print("*************pairs迭代遍历键值*************")
for i, k in pairs(a) do
    print("pairs遍历键" .. i)
    print("pairs遍历值" .. k)
end
print("*************迭代遍历键*************")
--遍历键
for i in pairs(a) do
    print("键" .. i)
end
print("*************字典*************")
a = { ["1"] = 5, ["2"] = 4 }
print(a["1"])
print(a["2"])
print("*************类*************")
student = {
    --年龄
    age = 1,
    --性别
    sex = 0,
    --unity成绩
    unity = 90,
    --成长行为
    Up = function(self)
        self.age = self.age + 1
        print("我成长了" .. self.age)
    end,
    --上课行为
    Learn = function()
        print("学习")
    end
}

student.Learn()
student.Up(student)
student:Up()
--student.Learn()
student.age = 2
print(student.age)

print("*************特殊用法*************")
a, b, c = 1, 2, 3, 4 --4会被忽略
print(a)
print(b)
print(c)
a, b, c = 1, 2 --c会变为nil
print(a)
print(b)
print(c)

--多个返回值
function Test()
    return 10, 20, 30, 40
end

a, b, c = Test()
print(a)
print(b)
print(c)