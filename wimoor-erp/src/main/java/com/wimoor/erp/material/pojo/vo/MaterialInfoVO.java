package com.wimoor.erp.material.pojo.vo;

import java.util.List;
import java.util.Map;

import com.wimoor.api.erp.assembly.pojo.vo.AssemblyVO;
import com.wimoor.erp.material.pojo.entity.DimensionsInfo;
import com.wimoor.erp.material.pojo.entity.MaterialCustoms;
import com.wimoor.erp.material.pojo.entity.StepWisePrice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MaterialInfoVO对象", description="产品详情")
public class MaterialInfoVO {

	@ApiModelProperty(value = "产品基础数据")
	Map<String,Object> material;
	
	@ApiModelProperty(value = "产品item尺寸重量对象")
	DimensionsInfo itemDim;
	
	@ApiModelProperty(value = "产品box尺寸重量对象")
	DimensionsInfo boxDim;
	
	@ApiModelProperty(value = "产品pkg尺寸重量对象")
	DimensionsInfo pkgDim;
	
	@ApiModelProperty(value = "产品阶梯价格")
	List<StepWisePrice> stepWisePrice;
	
	@ApiModelProperty(value = "产品组装列表")
	List<AssemblyVO> assemblyList;
	
	@ApiModelProperty(value = "产品耗材列表")
	List<MaterialConsumableVO> consumableList;
	
	@ApiModelProperty(value = "产品供应商列表")
	List<MaterialSupplierVO> supplierList;
	
	@ApiModelProperty(value = "产品海关数据")
	MaterialCustoms customs;
	
}
