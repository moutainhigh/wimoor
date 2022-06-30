package com.wimoor.erp.common.pojo.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_product_in_presale")
public class ProductInPresale extends BaseEntity{
    /**
	 * 
	 */
	private static final long serialVersionUID = -8401333489880986919L;

	@TableField(value= "sku")
	private String sku;

    @TableField(value= "marketplaceid")
    private String marketplaceid;

    @TableField(value= "groupid")
    private String groupid;

	@TableField(value= "date")
    private Date date;

	@TableField(value= "quantity")
    private Integer quantity;

	@TableField(value= "operator")
    private String operator;

	@TableField(value= "opttime")
    private Date opttime;
	
	@TableField(value= "start")
    private Date start;
	
	@TableField(value= "end")
    private Date end;
	
	@TableField(value= "month")
    private String month;
	
	@TableField(value= "hasdaysales")
    private boolean hasdaysales;
 
}