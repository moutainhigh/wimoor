package com.wimoor.erp.assembly.service.impl;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wimoor.api.erp.assembly.pojo.vo.AssemblyVO;
import com.wimoor.common.GeneralUtil;
import com.wimoor.erp.assembly.mapper.AssemblyMapper;
import com.wimoor.erp.assembly.pojo.entity.Assembly;
import com.wimoor.erp.assembly.service.IAssemblyService;
import com.wimoor.erp.material.service.IMaterialService;

import lombok.RequiredArgsConstructor;
 
@Service("assemblyService")
@RequiredArgsConstructor
public class AssemblyServiceImpl extends  ServiceImpl<AssemblyMapper,Assembly> implements IAssemblyService {
	 
	final AssemblyMapper assemblyMapper;
	 
	@Lazy
	@Autowired
	IMaterialService materialService;


	public IPage<Assembly> findByCondition(Page<?> page,String search, String shopid){
		return assemblyMapper.findByCondition(page,search, shopid);
	}

	public List<AssemblyVO> selectByMainmid(String mainmid) {
		List<AssemblyVO> list=this.baseMapper.selectByMainmid(mainmid);
		if(list!=null && list.size()>0) {
			for(int i=0;i<list.size();i++) {
				 String pricestr="";
				 List<Map<String, Object>> historylist = materialService.selectProPriceHisById(list.get(i).getSubmid());
				 if(historylist!=null && historylist.size()>0) {
					 for(int j=0;j<historylist.size();j++) {
						 Map<String,Object>  history = historylist.get(j);
						 if(history!=null) {
							 if(history.get("price")!=null) {
								 String price=history.get("price").toString();
								 int len = price.indexOf(".");
								 price=price.substring(0, len+3);
								 pricestr+="历史价格("+
					    				   GeneralUtil.formatDate((Date)history.get("opttime"))+
					    				   "): "+price+"<br/>";
							 }
						 }
					 }
					 if(GeneralUtil.isEmpty(pricestr)) {
	    				 pricestr="暂无价格历史!";
	    			 }
				 }else {
					 pricestr="暂无价格历史!";
				 }
				 list.get(i).setHistoryprice(pricestr);
			}
		}
		return list;
	}

	public void deleteIsNull() {
		assemblyMapper.deleteIsNull();
	}

	public void deleteByMainmid(String mainmid) {
		assemblyMapper.deleteByMainmid(mainmid);
		
	}


	public List<Assembly> selectAssemblySub(String mainmid) {
		return assemblyMapper.selectAssemblySub(mainmid);
	}

	public List<Map<String, Object>> selectBySubid(String materialid) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> assemblyMainlist= assemblyMapper.selectMainBySubid(materialid);
		for(Map<String, Object> item:assemblyMainlist) {
			List<AssemblyVO> assemblysublist= assemblyMapper.selectByMainmid(item.get("mainmid").toString());
			item.put("sublist", assemblysublist);
		}
		return assemblyMainlist;
	}

	public List<Map<String, Object>> selectByMainDetailmid(String materialid,String warehouseid) {
		// TODO Auto-generated method stub
		return assemblyMapper.selectByMainDetailmid(materialid, warehouseid);
	}

	public Integer findCanAssembly(String materialid, String warehouseid, String shopid) {
		// TODO Auto-generated method stub
		return assemblyMapper.findCanAssembly(materialid,warehouseid,shopid);
	}

}
