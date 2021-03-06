package com.wimoor.erp.common.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wimoor.common.GeneralUtil;
import com.wimoor.common.user.UserInfo;
import com.wimoor.erp.common.pojo.entity.ERPBizException;
import com.wimoor.erp.common.service.IEstimatedSalesService;
import com.wimoor.erp.material.mapper.EstimatedSalesMapper;
import com.wimoor.erp.material.pojo.entity.EstimatedSales;
import com.wimoor.erp.material.pojo.entity.MaterialCategory;

import lombok.RequiredArgsConstructor;
 

@Service("estimatedSalesService")
@RequiredArgsConstructor
public class EstimatedSalesServiceImpl extends  ServiceImpl<EstimatedSalesMapper,EstimatedSales> implements IEstimatedSalesService{
	 
 
	 
 
	public EstimatedSales findEstimatedSales(Map<String, Object> param) {
		return this.baseMapper.findEstimatedSales(param);
	}
	
	public int deleteEstimatedSale(UserInfo user, Map<String, Object> param) {
		EstimatedSales estimatedSales =  this.baseMapper.findEstimatedSales(param);
		if(estimatedSales != null) {
			return   this.baseMapper.deleteById(estimatedSales.getId());
		} 
		return 0;
	}
	
	public Boolean updateEstimatedSale(UserInfo user, Map<String, Object> param) {
		String sku = (String) param.get("sku");
		String marketplaceid = (String) param.get("marketplaceid");
		String groupid = (String) param.get("groupid");
		int presales = (Integer) param.get("presales");
		Object hasTime = param.get("hasTime");
		Date startTime = null;
		Date endTime = null;
		if(hasTime != null) {
			startTime = GeneralUtil.getDatez(param.get("startTime").toString());
			endTime = GeneralUtil.getDatez(param.get("endTime").toString());
		}
		int condition = Integer.parseInt(param.get("condition").toString());
		BigDecimal conditionNum = null;
		if(condition != 0) {
			conditionNum = new BigDecimal(param.get("conditionNum").toString());
		}
		EstimatedSales estimatedSales = this.baseMapper.findEstimatedSales(param);
		if(estimatedSales == null) {
			estimatedSales = new EstimatedSales();
			estimatedSales.setSku(sku);
			estimatedSales.setMarketplaceid(marketplaceid);
			estimatedSales.setGroupid(groupid);
		}
		estimatedSales.setPresales(presales);
		estimatedSales.setStarttime(startTime);
		estimatedSales.setEndtime(endTime);
		estimatedSales.setConditions(condition);
		estimatedSales.setConditionnum(conditionNum);
		estimatedSales.setOperator(user.getId());
		estimatedSales.setOpttime(new Date());
		//Boolean isInvalid = checkEstimatedSaleIsinvalid(estimatedSales);
		estimatedSales.setIsInvalid(false);
		if(estimatedSales.getId() != null) {
			this.baseMapper.updateById(estimatedSales);
			return false;
		}else {
			this.baseMapper.insert(estimatedSales);
			return false;
		}
	}
	
	public Boolean checkEstimatedSaleIsinvalid(EstimatedSales estimatedSales) {
		if(estimatedSales == null || estimatedSales.getPresales() == null) {
			throw new ERPBizException("???sku??????????????????");
		}
		int condition = estimatedSales.getConditions();//condition???0 = ????????????1 = ??????>?????? ; 2 = ??????<??????
		Date startTime = estimatedSales.getStarttime();//??????????????????
		Date endTime = estimatedSales.getEndtime();//??????????????????
		if(condition == 0 && startTime == null && endTime == null) {
			return false;
		}else {
			if(startTime != null && endTime != null) {
				 Calendar calendar = Calendar.getInstance();
	             calendar.setTime(new Date());
	             calendar.set(Calendar.HOUR_OF_DAY, 0);
	             calendar.set(Calendar.MINUTE, 0);
	             calendar.set(Calendar.SECOND, 1);
	             Date date = calendar.getTime();;
	             endTime.setSeconds(2);
				//???????????????????????????[startTime, endTime]??????,??????????????????????????????true
				if(!GeneralUtil.isEffectiveDate(date, startTime, endTime)) {
					return true;
				}
			}
			if(condition != 0) {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("sku", estimatedSales.getSku());
				param.put("marketplaceid", estimatedSales.getMarketplaceid());
				param.put("groupid", estimatedSales.getGroupid());
				Integer presales = estimatedSales.getPresales();//??????????????????
				Integer sales =0;// productInOrderMapper.getProductOrderSales(param);//??????????????????????????????
				//??????>?????? ?????? ??????<??????  ???????????????????????????????????????
				//BigDecimal conditionNum = estimatedSales.getConditionnum().divide(new BigDecimal("100"), 2);
				BigDecimal sysSales = new BigDecimal("0");
				BigDecimal userSales = new BigDecimal(presales.toString());
				if(sales  != null) {
					sysSales = new BigDecimal(sales.toString());
				}
				BigDecimal temp = sysSales;
				if(condition == 1) {// ??????????????? ????????????<????????????????????????????????????????????? ??? ??????????????????????????????
					if(userSales.compareTo(temp) == 1) {
						return false;
					} 
				}
				else if(condition == 2) {// ??????????????? ????????????>????????????????????????????????????????????? ??? ??????????????????????????????
					if(userSales.compareTo(temp) == -1) {
						return false;
					} 
				}
			}
		}
		return true;
	}
	
	public void checkEstimatedSaleIsinvalid() {
		QueryWrapper<EstimatedSales> queryWrapper=new QueryWrapper<EstimatedSales>();
		queryWrapper.eq("isInvalid", false);
		List<EstimatedSales> list = this.baseMapper.selectList(queryWrapper);
		if(list == null || list.size() == 0) {
			return;
		}
		for(EstimatedSales estimatedSales : list) {
			Boolean isInvalid = checkEstimatedSaleIsinvalid(estimatedSales);
			if(isInvalid) {
				estimatedSales.setIsInvalid(isInvalid);
				this.baseMapper.updateById(estimatedSales);
			}
		}
	}
	
}
