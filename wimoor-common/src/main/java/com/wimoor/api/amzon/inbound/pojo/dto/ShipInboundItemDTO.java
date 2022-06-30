package com.wimoor.api.amzon.inbound.pojo.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wimoor.common.pojo.entity.BaseEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="ShipInboundItem对象", description="货件Item")
@TableName("t_erp_ship_inbounditem")
public class ShipInboundItemDTO extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3072300909415719829L;

	@ApiModelProperty(value = "货件ID")
	@TableField(value="ShipmentId")
    private String shipmentid;
	
	@ApiModelProperty(value = "亚马逊仓库SKU【系统填写】")
	@TableField(value="fulfillmentnetworksku")
    private String fulfillmentnetworksku;

	@ApiModelProperty(value = "发货量")
	@TableField(value="quantityshipped")
    private Integer quantityshipped;

	@ApiModelProperty(value = "接收数量【系统填写】")
	@TableField(value="quantityreceived")
    private Integer quantityreceived;

	@ApiModelProperty(value = "单箱数量【系统填写】")
	@TableField(value="quantityincase")
    private Integer quantityincase;

	@ApiModelProperty(value = "订单ID【规划提交订单】")
	@TableField(value="inboundplanid")
    private String inboundplanid;

	@ApiModelProperty(value = "平台SKU")
	@TableField(value="SellerSKU")
    private String sellersku;
	
	@ApiModelProperty(value = "订单数量")
	@TableField(value="quantity")
    private Integer quantity;

	@ApiModelProperty(value = "是否label【系统内置】")
	@TableField(value="PrepInstruction")
    private String prepInstruction;
	
	@ApiModelProperty(value = "打label的人【系统内置】")
	@TableField(value="PrepOwner")
    private String prepOwner;
 
	@ApiModelProperty(value = "本地产品ID【系统内置】")
	@TableField(value="materialid")
    private String materialid;
}