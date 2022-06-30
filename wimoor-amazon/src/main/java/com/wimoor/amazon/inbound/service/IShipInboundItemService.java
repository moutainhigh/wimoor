package com.wimoor.amazon.inbound.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wimoor.amazon.inbound.pojo.entity.ShipInboundItem;
import com.wimoor.api.amzon.inbound.pojo.vo.ShipInboundItemVo;
import com.wimoor.api.amzon.inbound.pojo.vo.ShipInboundShipmenSummarytVo;
 

public interface IShipInboundItemService extends IService<ShipInboundItem> {
	   public int updateItem(ShipInboundItem item);
	   public ShipInboundItem findItem(String shipmentid, String inboundplanid, String sellersku);
	   public ShipInboundShipmenSummarytVo summaryShipmentItem(String shipmentid) ;
	   public List<ShipInboundItem> getItemByShipment(String shipmentid);
	   public List<ShipInboundItemVo> listByShipmentid(String shipmentid);
	 
 
}
