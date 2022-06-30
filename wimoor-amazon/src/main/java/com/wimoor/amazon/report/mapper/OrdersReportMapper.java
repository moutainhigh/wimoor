package com.wimoor.amazon.report.mapper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wimoor.amazon.orders.pojo.dto.AmazonOrdersDTO;
import com.wimoor.amazon.orders.pojo.vo.AmazonOrdersDetailVo;
import com.wimoor.amazon.orders.pojo.vo.AmazonOrdersRemoveVo;
import com.wimoor.amazon.orders.pojo.vo.AmazonOrdersReturnVo;
import com.wimoor.amazon.orders.pojo.vo.AmazonOrdersShipVo;
import com.wimoor.amazon.orders.pojo.vo.AmazonOrdersVo;
import com.wimoor.amazon.report.pojo.entity.OrdersReport;

@Mapper
public interface OrdersReportMapper extends BaseMapper<OrdersReport> {

	void deleteItem(Map<String, Object> param);

	void insertIntoDownload(OrdersReport record);

	List<String> findAllSaleChannel();

	List<OrdersReport> selectByOrderIDSKU(Map<String, Object> parameter);
 
	List<Map<String, Object>> selectByMutilParameter(Map<String, Object> parameter);

	Map<String, Object> selectLastByMutilParameter(Map<String, Object> parameter);

	List<OrdersReport> selectByAuth(Map<String, Object> parameter);

	int selectCountByMonth(@Param("shop_id") String shop_id);
 
	void insertBatch(List<OrdersReport> list);

	void deleteBeforeDownload(HashMap<String, Object> param);

	Date hasDownload(@Param("amazonAuthId") String amazonAuthId);

	List<AmazonOrdersVo> selectOrderList(@Param("dto") AmazonOrdersDTO dto);
	List<Map<String, Object>> selectOrderTodayList(Map<String, Object> paramMap);
	
	Map<String, Object> selectProductOrdersTodayList_SalesOfYesterday(Map<String, Object> paramMap);
	Map<String, Object> selectProductOrdersTodayList_SalesOfLast(Map<String, Object> paramMap);
	List<Map<String, Object>> selectProductOrdersTodayList(Map<String, Object> paramMap);
	IPage<Map<String, Object>> selectOrderTodayList(Page<?> page,Map<String, Object> paramMap);
	Map<String, Object> selectOrderTodaySummary(Map<String, Object> paramMap);
	
	Map<String, Object> selectOrderYesDaySummary(Map<String, Object> paramMap);
	
	
	IPage<Map<String, Object>> selectProductOrdersTodayList(Page<?> page,Map<String, Object> paramMap);
//	Map<String, Object> selectProductOrdersTodaySummary(Map<String, Object> paramMap);
	
	List<AmazonOrdersDetailVo> selectOrderDetail(@Param("param")Map<String, Object> paramMap);
	
	List<AmazonOrdersDetailVo> selectOrderItemDetail(@Param("param")Map<String, Object> paramMap);
	
	IPage<AmazonOrdersReturnVo> selectReturnsList(Page<?> page,Map<String, Object> paramMap);

	OrdersReport getOrdersReportByOrderid(Map<String, Object> paramMap);

	void clearOrderReportDownload();
    List<Map<String,Object>> allDownloadAuth();
	// List<Map<String, Object>> selectSetmentByOrderId(Map<String, Object>
	// paramMap);

    IPage<Map<String, Object>> selectReviewOrderList(Page<?> page,Map<String, Object> paramMap);
	Integer selectProductOrdersTodayOne(Map<String, Object> paramMap);
 
	void insertNewDataToOldItem(Map<String, Object> paramMap);
	void insertNewDataToOldMain(Map<String, Object> paramMap);

	List<Map<String, Object>> getAccRptByAuth(Map<String, Object> params);
	
	List<Map<String, Object>> selectDataByDay(@Param("amazonAuthId") String amazonAuthId,@Param("startDate") String startDate,@Param("endDate") String endDate);

	int deleRptByAuth(@Param("amazonAuthId")String authid, @Param("startDate")String time, @Param("endDate")String time2);

	int insertReplaceBatch(List<Map<String,Object>> list);
	
	IPage<AmazonOrdersShipVo> getOrderAddressList(Page<?> page,Map<String, Object> params);
	
	Map<String, Object> getOrderReportById(Map<String, Object> params);
	
    Map<String, Object> getOrderItemById(Map<String, Object> params);

	String getLastShippedOrder(@Param("authid")String authid, @Param("pointname")String pointName,@Param("sku") String sku);
	
	IPage<AmazonOrdersRemoveVo> selectRemoveList(Page<?> page,Map<String, Object> paramMap);

	Map<String, Object> selectPInfoBySku(Map<String, Object> paramMap);

	List<Map<String, Object>> getOrderAddressListDownload(Map<String, Object> paramMap);
	
	List<AmazonOrdersVo>  selectOrderMainList( @Param("dto")AmazonOrdersDTO dto);

}
