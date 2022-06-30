package com.wimoor.erp.purchase.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("t_erp_purchase_plan_warahouse")
public class PurchasePlanWareHouse {
	
 
	@TableId(value= "warehouseid")
	private String warehouseid;
    
    @TableField(value= "shopid")
	private String shopid;	
    
    @TableField(value= "planid")
    private String planid;
 

}