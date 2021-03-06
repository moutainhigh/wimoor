package com.wimoor.amazon.orders.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;
import com.wimoor.amazon.auth.pojo.entity.AmazonAuthority;
import com.wimoor.amazon.auth.pojo.entity.AmazonGroup;
import com.wimoor.amazon.auth.pojo.entity.Marketplace;
import com.wimoor.amazon.auth.service.IAmazonAuthorityService;
import com.wimoor.amazon.auth.service.IAmazonGroupService;
import com.wimoor.amazon.auth.service.IMarketplaceService;
import com.wimoor.amazon.orders.pojo.dto.AmazonOrdersDTO;
import com.wimoor.amazon.orders.pojo.dto.AmazonOrdersRemoveDTO;
import com.wimoor.amazon.orders.pojo.dto.AmazonOrdersReturnDTO;
import com.wimoor.amazon.orders.pojo.dto.AmazonOrdersShipDTO;
import com.wimoor.amazon.orders.pojo.vo.AmazonOrdersDetailVo;
import com.wimoor.amazon.orders.pojo.vo.AmazonOrdersRemoveVo;
import com.wimoor.amazon.orders.pojo.vo.AmazonOrdersReturnVo;
import com.wimoor.amazon.orders.pojo.vo.AmazonOrdersShipVo;
import com.wimoor.amazon.orders.pojo.vo.AmazonOrdersVo;
import com.wimoor.amazon.orders.service.IAmzOrderItemService;
import com.wimoor.amazon.orders.service.IAmzOrderMainService;
import com.wimoor.amazon.orders.service.IOrderManagerService;
import com.wimoor.amazon.orders.service.impl.OrderWordHandler;
import com.wimoor.common.mvc.BizException;
import com.wimoor.common.result.Result;
import com.wimoor.common.service.IPictureService;
import com.wimoor.common.service.ISerialNumService;
import com.wimoor.common.service.impl.OSSApiService;
import com.wimoor.common.user.UserInfo;
import com.wimoor.common.user.UserInfoContext;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * <p>
 * ???????????? ???????????????
 * </p>
 *
 * @author wimoor team
 * @since 2022-05-14
 */
@Api(tags = "????????????")
@RestController
@RequestMapping("/api/v0/orders")
public class OrdersController{
	 
	@Autowired
	IAmzOrderMainService amzOrderMainService;
	@Autowired
	IAmzOrderItemService amzOrderItemService;
	@Autowired
	IMarketplaceService marketplaceService;
	@Autowired
	IAmazonAuthorityService amazonAuthorityService;
	@Resource
	IOrderManagerService orderManagerService;
	@Resource
	IAmazonGroupService amazonGroupService;
	@Resource
	ISerialNumService serialNumService;
	@Resource
	IPictureService  pictureService;
	@Resource
	OSSApiService ossApiService;
	
	@ApiOperation(value = "??????????????????")
	@GetMapping("/refreshOrder")
	public Result<String> requestReportAction() {
		amazonAuthorityService.executTask(amzOrderMainService);
		return Result.success();
	}
	 
	@GetMapping("/refreshOrdersItem")
	public Result<?> ordersItemAction() {
		amazonAuthorityService.executTask(amzOrderItemService);
		return Result.success();
	}
	
	
	/**
	 * @param condition
	 * @return
	 */
	@ApiOperation(value = "??????????????????")
	@PostMapping("/list")
	public Result<IPage<AmazonOrdersVo>> getOrderlistAction(@RequestBody AmazonOrdersDTO condition) {
		UserInfo userinfo = UserInfoContext.get();
		condition.setOrderid(null);
		if ("sku".equals(condition.getSearchtype()) && StrUtil.isNotEmpty(condition.getSearch()))
      		{condition.setSku(condition.getSearch());}
		if ("asin".equals(condition.getSearchtype()) && StrUtil.isNotEmpty(condition.getSearch()))
  		{condition.setAsin(condition.getSearch());}
		if ("number".equals(condition.getSearchtype()) && StrUtil.isNotEmpty(condition.getSearch()))
  		{condition.setOrderid(condition.getSearch());}else {
  			condition.setOrderid(null);
  		}
	 
		if (StrUtil.isNotEmpty(condition.getGroupid())) {
			if("all".equals(condition.getGroupid())) {
				List<AmazonGroup> groupList =amazonGroupService.getGroupByUser(userinfo);
				condition.setGroupList(groupList);
				condition.setGroupid(null);
			} 
		}else {
			throw new BizException("??????ID???????????????");
		}
		if(StrUtil.isNotEmpty(condition.getPointname()) &&!"all".equals(condition.getPointname())) {
			String pointname=condition.getPointname();
			Map<String, Marketplace> mamap = marketplaceService.findMapByPoint();
			Marketplace market = mamap.get(pointname);
			condition.setMarketplaceid(market.getMarketplaceid());
			if(market!=null) {
				AmazonAuthority auth = amazonAuthorityService.selectByGroupAndMarket(condition.getGroupid(), market.getMarketplaceid());
				condition.setAmazonAuthId(auth.getId());
			}
		}else {
			condition.setPointname(null);
		}
 
		condition.setEndDate(condition.getEndDate().trim()+" 23:59:59");
		IPage<AmazonOrdersVo> list=orderManagerService.selectOrderList(condition);
		return Result.success(list);
	}
	
	@ApiOperation(value ="??????????????????")
	@GetMapping("/downloadOrderList")
	public void getDownloadOrderListAction(
			@ApiParam("search??????")@RequestParam String search,
			@ApiParam("??????ID")@RequestParam String groupid,
			@ApiParam("????????????")@RequestParam String searchtype,
			@ApiParam("??????")@RequestParam String channel,
			@ApiParam("????????????")@RequestParam String startDate,
			@ApiParam("????????????")@RequestParam String endDate,
			@ApiParam("??????")@RequestParam String pointname,
			@ApiParam("????????????")@RequestParam String status,
			@ApiParam("????????????")@RequestParam String color,
			@ApiParam("?????????????????????")@RequestParam String isbusiness,
		    HttpServletResponse response) {
		    AmazonOrdersDTO condition=new AmazonOrdersDTO();
		    condition.setSearch(search);
		    condition.setGroupid(groupid);
		    condition.setSearchtype(searchtype);
		    condition.setChannel(channel);
		    condition.setStartDate(startDate);
		    condition.setEndDate(endDate);
		    condition.setPointname(pointname);
		    condition.setStatus(status);
		    condition.setColor(color);
		    condition.setIsbusiness(isbusiness);
			// ????????????Excel?????????
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			// ???????????????Excel
			UserInfo userinfo = UserInfoContext.get();
			condition.setOrderid(null);
			if ("sku".equals(condition.getSearchtype()) && StrUtil.isNotEmpty(condition.getSearch()))
	      		{condition.setSku(condition.getSearch());}
			if ("asin".equals(condition.getSearchtype()) && StrUtil.isNotEmpty(condition.getSearch()))
	  		{condition.setAsin(condition.getSearch());}
			if ("number".equals(condition.getSearchtype()) && StrUtil.isNotEmpty(condition.getSearch()))
	  		{condition.setOrderid(condition.getSearch());}else {
	  			condition.setOrderid(null);
	  		}
		 
			if (StrUtil.isNotEmpty(condition.getGroupid())) {
				if("all".equals(condition.getGroupid())) {
					List<AmazonGroup> groupList =amazonGroupService.getGroupByUser(userinfo);
					condition.setGroupList(groupList);
					condition.setGroupid(null);
				} 
			}
			if(StrUtil.isNotEmpty(condition.getPointname()) ) {
				Marketplace market = marketplaceService.findMapByPoint().get(condition.getPointname());
				condition.setMarketplaceid(market.getMarketplaceid());
				if(market!=null) {
					AmazonAuthority auth = amazonAuthorityService.selectByGroupAndMarket(condition.getGroupid(), market.getMarketplaceid());
					condition.setAmazonAuthId(auth.getId());
				}
			}
			condition.setEndDate(condition.getEndDate().trim()+" 23:59:59");
			try {
				orderManagerService.setOrdersExcelBook(workbook, condition);
				response.setContentType("application/force-download");// ???????????????????????????
				response.addHeader("Content-Disposition", "attachment;fileName=orderList" + System.currentTimeMillis() + ".xlsx");// ???????????????
				ServletOutputStream fOut = response.getOutputStream();
				workbook.write(fOut);
				workbook.close();
				fOut.flush();
				fOut.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	
	
	@ApiOperation(value = "??????????????????????????????")
	@PostMapping("/getParamOfSummaryOrder")
	public Result<Map<String,Object>> getParamOfSummaryOrderAction(@RequestBody AmazonOrdersDTO condition) {
		UserInfo userinfo = UserInfoContext.get();
		condition.setOrderid(null);
		if ("sku".equals(condition.getSearchtype()) && StrUtil.isNotEmpty(condition.getSearch()))
      		{condition.setSku(condition.getSearch());}
		if ("asin".equals(condition.getSearchtype()) && StrUtil.isNotEmpty(condition.getSearch()))
  		{condition.setAsin(condition.getSearch());}
		if ("number".equals(condition.getSearchtype()) && StrUtil.isNotEmpty(condition.getSearch()))
  		{condition.setOrderid(condition.getSearch());}else {
  			condition.setOrderid(null);
  		}
	 
		if (StrUtil.isNotEmpty(condition.getGroupid())) {
			if("all".equals(condition.getGroupid())) {
				List<AmazonGroup> groupList =amazonGroupService.getGroupByUser(userinfo);
				condition.setGroupList(groupList);
				condition.setGroupid(null);
			} 
		}else {
			throw new BizException("??????ID???????????????");
		}
		if(StrUtil.isNotEmpty(condition.getPointname()) ) {
			Marketplace market = marketplaceService.findMapByPoint().get(condition.getPointname());
			condition.setMarketplaceid(market.getMarketplaceid());
			if(market!=null) {
				AmazonAuthority auth = amazonAuthorityService.selectByGroupAndMarket(condition.getGroupid(), market.getMarketplaceid());
				condition.setAmazonAuthId(auth.getId());
			}
		}
		condition.setEndDate(condition.getEndDate().trim()+" 23:59:59");
		Map<String, Object> maps=orderManagerService.getParamOfSummaryOrder(condition);
		return Result.success(maps);
	}
	
  	@ApiOperation("??????????????????")
  	@PostMapping("/returnlist")
    public  Result<IPage<AmazonOrdersReturnVo>> getReceiveReportExcel(
    		@RequestBody AmazonOrdersReturnDTO condition){
  		UserInfo userinfo = UserInfoContext.get();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String shopid =userinfo.getCompanyid();
		String color=condition.getColor();
		String marketplaceid=condition.getMarketplaceid();
		String search=condition.getSearch();
		String searchtype=condition.getSearchtype();
		String groupid=condition.getGroupid();
		String startDate=condition.getStartDate();
		String endDate=condition.getEndDate();
		if (StrUtil.isNotEmpty(color))
			paramMap.put("color", color);
		if (StrUtil.isNotEmpty(marketplaceid))
			paramMap.put("marketplaceid", marketplaceid);
		if (searchtype.equals("sku") && StrUtil.isNotEmpty(search))
			paramMap.put("sku", search);
		if (searchtype.equals("asin") && StrUtil.isNotEmpty(search))
			paramMap.put("asin", search);
		if (searchtype.equals("number") && StrUtil.isNotEmpty(search))
			paramMap.put("orderid", search);
		if (StrUtil.isNotEmpty(groupid)) {
			if("all".equals(groupid)) {
				List<AmazonGroup> groupList = amazonGroupService.getGroupByUser(userinfo);
				paramMap.put("groupList", groupList);
			}else {
				paramMap.put("groupid", groupid);
			}
		}
		paramMap.put("startDate", startDate.trim());
		paramMap.put("endDate", endDate.trim());
		paramMap.put("shopid", shopid);
		Page<AmazonOrdersReturnVo> page=condition.getPage();
		IPage<AmazonOrdersReturnVo> list=orderManagerService.selectReturnsList(paramMap, page);
		return Result.success(list);
	}
  	
  	

	@ApiOperation("??????????????????")
	@PostMapping("/removelist")
	public Result<IPage<AmazonOrdersRemoveVo>> getRemovelistAction(@RequestBody AmazonOrdersRemoveDTO condition) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		UserInfo userinfo = UserInfoContext.get();
		String shopid=userinfo.getCompanyid();
		String region =condition.getRegion();
		String groupid = condition.getGroupid();
		String startDate =condition.getStartDate();
		String endDate = condition.getEndDate();
		String search = condition.getSearch().trim();
		String searchtype = condition.getSearchtype();
		if(StrUtil.isEmpty(search)) {
			paramMap.put("sku", null);
		}else {
			paramMap.put("sku", search);
		}
		if (StrUtil.isNotEmpty(groupid)) {
			if("all".equals(groupid)) {
				List<AmazonGroup> groupList = amazonGroupService.getGroupByUser(userinfo);
				paramMap.put("groupList", groupList);
			}else {
				paramMap.put("groupid", groupid);
			}
		}
		paramMap.put("searchtype", searchtype);
		if("all".equals(region) || StrUtil.isEmpty(region)) {
			paramMap.put("region", null);
		}else {
			paramMap.put("region", region);
		}
		paramMap.put("startDate", startDate.trim());
		paramMap.put("endDate", endDate.trim());
		paramMap.put("shopid", shopid);
		
		Page<AmazonOrdersRemoveVo> page = condition.getPage();
		IPage<AmazonOrdersRemoveVo> list = orderManagerService.selectRemoveList(paramMap,page);
		return Result.success(list);
	}
	
	@ApiOperation("????????????????????????")
	@PostMapping("/shiplist")
	public Result<IPage<AmazonOrdersShipVo>> getShipListAction(@RequestBody AmazonOrdersShipDTO condition) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		UserInfo userinfo = UserInfoContext.get();
		String shopid=userinfo.getCompanyid();
		String color = condition.getColor();
		String marketplaceid = condition.getMarketplaceid();
		String groupid = condition.getGroupid();
		String startDate = condition.getStartDate();
		String endDate = condition.getEndDate();
		String searchtype = condition.getSearchtype();
		String search = condition.getSearch().trim();
		if (StrUtil.isNotEmpty(color))
			paramMap.put("color", color);
		if (StrUtil.isNotEmpty(marketplaceid))
			paramMap.put("marketplaceid", marketplaceid);
		if (searchtype.equals("sku") && StrUtil.isNotEmpty(search))
			paramMap.put("sku", search);
		if (searchtype.equals("asin") && StrUtil.isNotEmpty(search))
			paramMap.put("asin", search);
		if (searchtype.equals("number") && StrUtil.isNotEmpty(search))
			paramMap.put("orderid", search);
		if (StrUtil.isNotEmpty(groupid)) {
			if("all".equals(groupid)) {
				List<AmazonGroup> groupList =amazonGroupService.getGroupByUser(userinfo);
				paramMap.put("groupList", groupList);
			}else {
				paramMap.put("groupid", groupid);
			}
		}
		if(StrUtil.isNotEmpty(marketplaceid)&&StrUtil.isNotEmpty(groupid)) {
              	AmazonAuthority auth = amazonAuthorityService.selectByGroupAndMarket(groupid,marketplaceid);
				paramMap.put("amazonauthid", auth.getId());
		}
		paramMap.put("startDate", startDate.trim());
		paramMap.put("endDate", endDate.trim()+" 23:59:59");
		paramMap.put("shopid", shopid);
		Page<AmazonOrdersShipVo> page = condition.getPage();
		IPage<AmazonOrdersShipVo> list = orderManagerService.getOrderAddressList(paramMap,page);
		return Result.success(list);
	}
	
	@ApiOperation("??????????????????????????????")
	@PostMapping("/downloadOrderAddressList")
	public void getOrderAddressListAction(
			@RequestBody AmazonOrdersShipDTO condition,HttpServletResponse response)  throws BizException {
		        // ????????????Excel?????????
				SXSSFWorkbook workbook = new SXSSFWorkbook();
				// ???????????????Excel
				Map<String, Object> paramMap = new HashMap<String, Object>();
				UserInfo userinfo = UserInfoContext.get();
				String shopid=userinfo.getCompanyid();
				String color = condition.getColor();
				String marketplaceid = condition.getMarketplaceid();
				String groupid = condition.getGroupid();
				String startDate = condition.getStartDate();
				String endDate = condition.getEndDate();
				String searchtype = condition.getSearchtype();
				String search = condition.getSearch().trim();
				if (StrUtil.isNotEmpty(color))
					paramMap.put("color", color);
				if (StrUtil.isNotEmpty(marketplaceid))
					paramMap.put("marketplaceid", marketplaceid);
				if (searchtype.equals("sku") && StrUtil.isNotEmpty(search))
					paramMap.put("sku", search);
				if (searchtype.equals("asin") && StrUtil.isNotEmpty(search))
					paramMap.put("asin", search);
				if (searchtype.equals("number") && StrUtil.isNotEmpty(search))
					paramMap.put("orderid", search);
				if (StrUtil.isNotEmpty(groupid)) {
					if("all".equals(groupid)) {
						List<AmazonGroup> groupList = amazonGroupService.getGroupByUser(userinfo);
						paramMap.put("groupList", groupList);
					}else {
						paramMap.put("groupid", groupid);
					}
				}
				if(StrUtil.isNotEmpty(marketplaceid)&&StrUtil.isNotEmpty(groupid)) {
		              	AmazonAuthority auth = amazonAuthorityService.selectByGroupAndMarket(groupid,marketplaceid);
						paramMap.put("amazonauthid", auth.getId());
				}
				paramMap.put("startDate", startDate.trim());
				paramMap.put("endDate", endDate.trim()+" 23:59:59");
				paramMap.put("shopid", shopid);
				try {
					orderManagerService.setAddressExcelBook(workbook, paramMap);
					response.setContentType("application/force-download");// ???????????????????????????
					response.addHeader("Content-Disposition", "attachment;fileName=orderAddress" + System.currentTimeMillis() + ".xlsx");// ???????????????
					ServletOutputStream fOut = response.getOutputStream();
					workbook.write(fOut);
					workbook.close();
					fOut.flush();
					fOut.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
	
	@ApiOperation("????????????????????????")
	@GetMapping("/sendAmzVatInvoince")
	public Result<Map<String,Object>> setOrderVatSourceAction(
			@ApiParam("??????ID")@RequestParam String groupid,
			@ApiParam("????????????[de,fr,es,it,uk]")@RequestParam String country,
			@ApiParam("??????ID")@RequestParam String orderid,
			@ApiParam("??????Item??????[itemstatus]")@RequestParam String itemstatus,
			@ApiParam("????????????")@RequestParam String postDate,
			@ApiParam("????????????[Word,PDF]")@RequestParam String vatlabel,
			@ApiParam("????????????[Vat,normal]")@RequestParam String vattype
			){
		UserInfo userinfo = UserInfoContext.get();
		Map<String,Object> map=new HashMap<String, Object>();
		int res=orderManagerService.setAmzOrderVatHandler(userinfo,groupid,country,orderid,itemstatus,postDate,vatlabel,vattype);
		if(res>0) {
			map.put("isOk", "true");
			map.put("msg", "????????????!");
		}else {
			map.put("isOk", "fasle");
			map.put("msg", "????????????!");
			
		}
		return Result.success(map);
	}
	
	@ApiOperation("??????????????????")
	@GetMapping("/showOrderDetail")
	public Result<List<AmazonOrdersDetailVo>> showOrderDetailAction(@ApiParam("??????ID")@RequestParam String groupid,
			@ApiParam("AmazonOrderID")@RequestParam String orderid,
			@ApiParam("??????????????????")@RequestParam String purchaseDate) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		UserInfo userinfo = UserInfoContext.get();
		String shopid = userinfo.getCompanyid();
		paramMap.put("purchaseDate", purchaseDate);
		paramMap.put("orderid", orderid);
		paramMap.put("groupid", groupid);
		paramMap.put("shopid", shopid);
		List<AmazonOrdersDetailVo> list = orderManagerService.selectOrderDetail(paramMap);
		return Result.success(list);
	}
	
	@ApiOperation("??????????????????????????????")
	@GetMapping("/selectVatInfoByGroup")
	public Result<Map<String, Object>> selectVatInfoByGroupAction(@ApiParam("??????ID")@RequestParam String groupid) {
		UserInfo userinfo = UserInfoContext.get();
		String shopid = userinfo.getCompanyid();
		Map<String, Object> obj = orderManagerService.selectVatInfo(groupid);
		String vatvoice = null;
		try {
			vatvoice = serialNumService.findSerialNumber(shopid, "22");
			obj.put("vatNo", vatvoice);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!obj.isEmpty()) {
			return Result.success(obj);
		} else {
			return null;
		}
	}
	
	@ApiOperation("??????????????????")
	@PostMapping(value="/saveOrderVat",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Result<Map<String, Object>> saveOrderVatAction(@ApiParam("??????ID")@RequestParam String groupid,
			@ApiParam("??????")@RequestParam String Vatphone,@ApiParam("??????")@RequestParam String Vatcompany,
			@ApiParam("??????")@RequestParam String Vatpostal,@ApiParam("??????")@RequestParam String Vatcountry,
			@ApiParam("??????")@RequestParam String Vatemail,@ApiParam("??????")@RequestParam String Vatprovince,
			@ApiParam("??????")@RequestParam String Vatsign,@ApiParam("??????")@RequestParam String Vatcity,
			@ApiParam("??????logo")@RequestParam String image,@ApiParam("????????????")@RequestParam String Vataddress,
			@ApiParam("ukvat") String ukvat,@ApiParam("uknum") String uknum,@ApiParam("devat") String devat,
			@ApiParam("denum") String denum,@ApiParam("frvat") String frvat,@ApiParam("frnum") String frnum,
			@ApiParam("itvat") String itvat,@ApiParam("itnum") String itnum,@ApiParam("esvat") String esvat,
			@ApiParam("esnum") String esnum,
			@RequestParam("file")MultipartFile file
			) throws FileNotFoundException {
		UserInfo userinfo = UserInfoContext.get();
		Map<String, Object> maps = new HashMap<String, Object>();
		String shopid = userinfo.getCompanyid();
		Map<String, Object> vatfeeMap = new HashMap<String, Object>();
		if (StrUtil.isNotEmpty(ukvat))
			vatfeeMap.put("ukvat", ukvat);
		if (StrUtil.isNotEmpty(uknum))
			vatfeeMap.put("uknum", uknum);
		if (StrUtil.isNotEmpty(devat))
			vatfeeMap.put("devat", devat);
		if (StrUtil.isNotEmpty(denum))
			vatfeeMap.put("denum", denum);
		if (StrUtil.isNotEmpty(frvat))
			vatfeeMap.put("frvat", frvat);
		if (StrUtil.isNotEmpty(frnum))
			vatfeeMap.put("frnum", frnum);
		if (StrUtil.isNotEmpty(esvat))
			vatfeeMap.put("esvat", esvat);
		if (StrUtil.isNotEmpty(esnum))
			vatfeeMap.put("esnum", esnum);
		if (StrUtil.isNotEmpty(itvat))
			vatfeeMap.put("itvat", itvat);
		if (StrUtil.isNotEmpty(itnum))
			vatfeeMap.put("itnum", itnum);
		int result=0;
		try {
			result = orderManagerService.saveAmazonVat(shopid, groupid, Vatcompany, Vatcountry, Vatprovince, Vatcity,
					Vataddress, Vatphone, Vatpostal, Vatemail, Vatsign, image, vatfeeMap,file.getInputStream(),file.getOriginalFilename());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (result > 0) {
			maps.put("msg", "???????????????");
			maps.put("isSuccess", "true");
		} else {
			maps.put("msg", "???????????????");
			maps.put("isSuccess", "fail");
		}
		return Result.success(maps);
	}
	
	
	@ApiOperation("??????????????????")
	@GetMapping("/downloadOrderVatInvoice")
	public void downloadOrderVatInvoiceAction(
			@ApiParam("orderID")@RequestParam String orderid,
			@ApiParam("??????ID")@RequestParam String groupid,
			@ApiParam("?????????pdf??????word??????")@RequestParam String vatlabel,
			@ApiParam("???vat??????????????????")@RequestParam String vattype,
			@ApiParam("??????")@RequestParam String country,
			@ApiParam("????????????")@RequestParam String itemstatus,
			@ApiParam("????????????")@RequestParam String postDate,
			HttpServletResponse response) {
		UserInfo userinfo = UserInfoContext.get();
		String shopid = userinfo.getCompanyid();
		String language="en";
		response.setContentType("application/force-download");// ???????????????????????????
		if ("PDF".equals(vatlabel)) {
			response.addHeader("Content-Disposition", "attachment;fileName=AmzOrderVatInvoicePDF" + System.currentTimeMillis() + ".pdf");// ???????????????
		} else {
			response.addHeader("Content-Disposition", "attachment;fileName=AmzOrderVatInvoiceWord" + System.currentTimeMillis() + ".doc");// ???????????????
		}
		if ("PDF".equals(vatlabel)) {
			Document document = new Document(PageSize.A4);
			try {
				// ??????pdf??????
				PdfWriter.getInstance(document, response.getOutputStream());
				Map<String, Object> maps = orderManagerService.setAmzOrderVatInvoicePDF(shopid, document, orderid, language, groupid,
						vatlabel, vattype, country, postDate, itemstatus);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (document != null && document.isOpen()) {
					document.close();
				}
			}
		} else {
			com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4);
			// ??????word??????
			try {
				RtfWriter2.getInstance(document, response.getOutputStream());
				OrderWordHandler handler=new OrderWordHandler();
				handler.setSerialNumService(this.serialNumService);
				handler.setAmazonAuthorityService(this.amazonAuthorityService);
				handler.setOrderManagerService(this.orderManagerService);
				handler.setMarketplaceService(this.marketplaceService);
				handler.setAmzOrderVatInvoiceWord(shopid, document, orderid, language, groupid,
						vatlabel, vattype, country, postDate, itemstatus);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (document != null && document.isOpen()) {
					document.close();
				}
			}
		}
	}

}

