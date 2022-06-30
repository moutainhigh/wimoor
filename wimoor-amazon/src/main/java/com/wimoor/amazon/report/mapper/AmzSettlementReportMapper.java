package com.wimoor.amazon.report.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wimoor.amazon.report.pojo.entity.AmzSettlementReport;
 
@Mapper
public interface AmzSettlementReportMapper extends BaseMapper<AmzSettlementReport> {
 
	List<Map<String, Object>> settlement(Map<String, Object> param);

	Map<String, Object> settlement_sum(Map<String, Object> param);

	Map<String, Object> settlement_sump(Map<String, Object> param);

	int deleteBatch(Map<String, Object> param);

	int insertBatch(List<AmzSettlementReport> list);

	int insertReplaceBatch(List<AmzSettlementReport> list);
	
	List<Long> findSettlementID(@Param(value = "amazonAuthId") String id, @Param(value = "pointname") String pointName);

	List<AmzSettlementReport> findSettlementByPostDateAndOrder(@Param("orderid") String orderid,
			@Param("startDate") String startDate, @Param("endDate") String endDate);

	List<Map<String, Object>> findBySettlementAcc(Map<String, Object> parameter);

	BigDecimal getTotalAmountBySettementId(Map<String, Object> param);

	List<Map<String, Object>> getDetail(Map<String, Object> param);
	List<Map<String, Object>> getDetailDay(Map<String, Object> param);
	List<Map<String, Object>> getDetailDaySKU(Map<String, Object> param);
	List<Map<String, Object>> getDetailDaySKUOther(Map<String, Object> param);
	
	List<Map<String, Object>> getDescNotSKU(Map<String, Object> param);
}