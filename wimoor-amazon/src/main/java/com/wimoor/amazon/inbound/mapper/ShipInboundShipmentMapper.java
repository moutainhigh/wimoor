package com.wimoor.amazon.inbound.mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wimoor.amazon.inbound.pojo.entity.ShipInboundShipment;
import com.wimoor.api.amzon.inbound.pojo.vo.ShipInboundShipmenSummarytVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
@Mapper
public interface ShipInboundShipmentMapper extends BaseMapper<ShipInboundShipment> {

	IPage<ShipInboundShipmenSummarytVo> findByTraceCondition(Page<?> page,@Param("param")Map<String,Object> param);

	List<String> findCarrierByshipmentid(@Param("country") String country, @Param("transtyle") String transtyle);

	List<Map<String, Object>> findLabelByshipmentid(@Param("shopid") String shopid, @Param("shipmentid") String shipmentid);

	List<Map<String, Object>> findAllShiped(@Param("amazongroupid") String amazongroupid, @Param("marketplaceid") String marketplaceid);

	List<Map<String, Object>> findLabelBySku(Map<String, Object> param);

	List<LinkedHashMap<String, Object>> findBoxDetailByShipmentId(@Param("shipmentid") String shipmentid);

	List<ShipInboundShipment> selectBySellerSku(@Param("sku") String sku, @Param("shopid") String shopid);
	
	List<ShipInboundShipment> selectBySellerSkuMarket(@Param("sku") String sku, @Param("shopid") String shopid,@Param("marketplaceid") String marketplaceid);

	Map<String,Object> findToCountry(@Param("destinationFulfillmentCenterId")String destinationFulfillmentCenterId,@Param("region") String region);

}