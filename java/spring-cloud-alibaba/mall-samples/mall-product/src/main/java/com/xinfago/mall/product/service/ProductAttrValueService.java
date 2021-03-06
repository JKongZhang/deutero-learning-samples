package com.xinfago.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinfago.common.utils.PageUtils;
import com.xinfago.mall.product.entity.ProductAttrValueEntity;

import java.util.Map;

/**
 * spu属性值
 *
 * @author xinfago
 * @email xinfago@163.com
 * @date 2021-04-18 14:48:11
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

