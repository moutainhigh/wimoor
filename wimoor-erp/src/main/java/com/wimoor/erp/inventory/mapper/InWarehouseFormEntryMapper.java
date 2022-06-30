package com.wimoor.erp.inventory.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wimoor.erp.inventory.pojo.entity.InWarehouseFormEntry;
@Mapper
public interface InWarehouseFormEntryMapper extends BaseMapper<InWarehouseFormEntry> {

	List<Map<String, Object>> selectByFormid(String formid);
 

	void deleteByFormid(String formid);

	List<Map<String, Object>> findFormDetailByFormid(String formid);
}