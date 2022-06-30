package com.wimoor.amazon.report.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wimoor.amazon.report.pojo.entity.ReportRequestRecord;
@Mapper
public interface ReportRequestRecordMapper  extends BaseMapper<ReportRequestRecord> {
	Date oldestRequestByType(Map<String, Object> map);

	Date lastUpdateRequestByType(Map<String, Object> map);

	List<ReportRequestRecord> selectNoProcessRequest(@Param("reporttype") String reporttype, @Param("marketplaceid") String marketplaceid);

	List<ReportRequestRecord> selectByReportType(@Param("sellerid") String sellerid, @Param("reporttype") String reporttype);

	List<ReportRequestRecord> selectNeedTreatOrderReport(String sellerid);

	List<ReportRequestRecord> hasNeedTreatInventoryReportList();
	
	List<ReportRequestRecord> selectFBArptByShopidLast(String sellerid);
	
	List<ReportRequestRecord> selectNoTreatReport(@Param("reporttype")String reporttype,@Param("marketplaceid")String marketplaceid);
}
