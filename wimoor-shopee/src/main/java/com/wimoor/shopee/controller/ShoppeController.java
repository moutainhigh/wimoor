package com.wimoor.shopee.controller;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.wimoor.shopee.entity.ShopeeAuthority;
import com.wimoor.shopee.mapper.ShopeeAuthorityMapper;
 
@RestController
@RequestMapping("/shoppe")
public class ShoppeController {

	@Resource
	ShopeeAuthorityMapper shopeeAuthorityMapper;
	
	  public static String HMACSHA256(String data, String key) throws Exception {
	  Mac sha256_HMAC = Mac.getInstance("HmacSHA256"); SecretKeySpec secret_key =
	  new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
	  sha256_HMAC.init(secret_key); byte[] array =
	  sha256_HMAC.doFinal(data.getBytes("UTF-8")); StringBuilder sb = new
	  StringBuilder(); for (byte item : array) {
	  sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3)); }
	  return sb.toString().toLowerCase(); }
	 
 

	@ResponseBody
	@GetMapping("/getShoppeToken")
	public String getShopeeTkoen() {
		
		// 授权url
		// 参数：必填
		// https://partner.shopeemobile.com/api/v1/shop/auth_partner
		// v2
		// 1.partner_id(int) 2.redirect(String) 3.sign(String) 4.timestamp(int)
		//String url = "https://partner.shopeemobile.com/api/v2/shop/auth_partner";
		String url="https://partner.test-stable.shopeemobile.com/api/v2/shop/auth_partner";
		//https://sellingpartnerapi-na.amazon.com/authorization/v1/authorizationCode
		Date date = new Date();
		int timest = (int) (date.getTime() / 1000);
		String host = "https://partner.shopeemobile.com";
		String path = "/api/v2/shop/auth_partner";
		String redirecturl = "http://localhost:8084/shopeeAuth";
		int partner_id =2002918;
		String baseString = partner_id + path + timest;
		String sign = null;
		try {
			sign=HMACSHA256(baseString,"1dd99dc58530fec6152cbcec83d19e63bac5dc4b898486ab6863a397132c000f");
			//sign = HMACSHA256(baseString, "1dd99dc58530fec6152cbcec83d19e63bac5dc4b898486ab6863a397132c000f");

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		url = host + path + "?partner_id=" + partner_id + "&timestamp=" + timest + "&sign=" + sign + "&redirect="
				+ redirecturl;
		//url=calToken(redirecturl,"06a0bb4da9b0d30fda276d38a79265378787b58373f14408863d2ee8a448c92e");
		return url;
	}
	
	@ResponseBody
	@GetMapping("/getShoppeTokens")
	public Object getShopeeTkoens(HttpServletRequest request) {
		//code=4a70615378434d4f744a634a4852724e&shop_id=158081846
		String code=request.getParameter("code");
		String shopids=request.getParameter("shopid");
		int shopid=0;
		if(shopids!=null) {
			Integer.parseInt(shopids);
		}
		System.out.println(code+","+shopid);
		int partner_id = 2002918;
		Date date = new Date();
		int timest = (int) (date.getTime() / 1000);
		String host = "https://partner.shopeemobile.com";
		String path = "/api/v2/auth/token/get";
		String baseString=partner_id+path+timest;
		String sign = null;
		if(code==null) {
			return null;
		}
		try {
			sign=HMACSHA256(baseString,"1dd99dc58530fec6152cbcec83d19e63bac5dc4b898486ab6863a397132c000f");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String url=host+path+"?partner_id="+partner_id+"&timestamp="+timest+"&sign="+sign;
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(800);
		cm.setDefaultMaxPerRoute(1000);
		CloseableHttpClient client = HttpClients.custom().setConnectionManager(cm).build();
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(60000).setConnectionRequestTimeout(60000)
				.setSocketTimeout(60000).build();
		 CloseableHttpResponse response=null;
		 String result="";
		try {
			  HttpPost httppost = new HttpPost(url);
			  httppost.setConfig(requestConfig);
			  httppost.setHeader("Content-Type", "application/json");
				JSONObject json = new JSONObject();
				json.put("code",code);
				json.put("shop_id",shopid);
				json.put("partner_id",partner_id);
				httppost.setEntity(new StringEntity(json.toString(), "UTF-8"));
		  
				  response = client.execute(httppost); 
				  HttpEntity entitys = response.getEntity();
				  System.out.println(EntityUtils.toString(entitys));
				  result= EntityUtils.toString(entitys,"utf-8");
				  
				  ShopeeAuthority token=new ShopeeAuthority();
				  Calendar c=Calendar.getInstance();
				  c.setTime(date);
				  c.add(Calendar.HOUR, 4);
				  token.setValidtime(c.getTime());
				  JSONObject jsons = JSONObject.parseObject(result);
				  String acctoken = jsons.getString("access_token");
				  String refreshtoken = jsons.getString("refersh_token");
				  token.setRefresh_token(refreshtoken);
				  token.setToken(acctoken);
				  shopeeAuthorityMapper.insert(token);
		  }catch (IOException e) {
			  e.printStackTrace();
		  }finally {
			  try {
				cm.close();
				client.close();
				response.close();
			  } catch (IOException e) {
				e.printStackTrace();
			  }
		  }
		return result;
	}
	
	@GetMapping("/refreshShoppeTokens")
	public Object refreshShoppeTokens() {
		//code=4a70615378434d4f744a634a4852724e&shop_id=158081846
		int partner_id = 2002918;
		Date date = new Date();
		int timest = (int) (date.getTime() / 1000);
		String host = "https://partner.shopeemobile.com";
		String path = "/api/v2/auth/access_token/get";
		String baseString=partner_id+path+timest;
		String refresh_token="77427874794e5a41754a496251654d61";
		String sign = null;
		int shopid=158081846;
		try {
			sign=HMACSHA256(baseString,"1dd99dc58530fec6152cbcec83d19e63bac5dc4b898486ab6863a397132c000f");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String url=host+path+"?partner_id="+partner_id+"&timestamp="+timest+"&sign="+sign;
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(800);
		cm.setDefaultMaxPerRoute(1000);
		CloseableHttpClient client = HttpClients.custom().setConnectionManager(cm).build();
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(60000).setConnectionRequestTimeout(60000)
				.setSocketTimeout(60000).build();
		 CloseableHttpResponse response=null;
		try {
			  HttpPost httppost = new HttpPost(url);
			  httppost.setConfig(requestConfig);
			  httppost.setHeader("Content-Type", "application/json");
				JSONObject json = new JSONObject();
				json.put("shop_id",shopid);
				json.put("refresh_token",refresh_token);
				json.put("partner_id",partner_id);
				httppost.setEntity(new StringEntity(json.toString(), "UTF-8"));
		  
				  response = client.execute(httppost); 
				  HttpEntity entitys = response.getEntity();
				  System.out.println(EntityUtils.toString(entitys));
		  }catch (IOException e) {
			  e.printStackTrace();
		  }

		return response;
	}
	
	@GetMapping("/getShoppeOrders")
	public Object getShoppeOrders() throws Exception {
		//code=4a70615378434d4f744a634a4852724e&shop_id=158081846
		int partner_id = 2002918;
		String key="1dd99dc58530fec6152cbcec83d19e63bac5dc4b898486ab6863a397132c000f";
		Date date = new Date();
		int timest = (int) (date.getTime() / 1000);
		int timefrom = (int) (date.getTime() / 1000)-(7*24*3600);
		int timeto = (int) (date.getTime() / 1000);
		String path="/api/v2/order/get_order_list";
	    String access_token="6669745047686a51427079744d447257";
		int shopid=158081846;
		String host = "https://partner.shopeemobile.com";
		
		String baseString=partner_id+path+timest+access_token+shopid;
		 
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(800);
		cm.setDefaultMaxPerRoute(1000);
		CloseableHttpClient client = HttpClients.custom().setConnectionManager(cm).build();
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(60000).setConnectionRequestTimeout(60000)
				.setSocketTimeout(60000).build();
		 CloseableHttpResponse response=null;
		try {
			String sign=HMACSHA256(baseString,key);
			String url=host+path+"?partner_id="+partner_id+"&timestamp="+timest+"&sign="+sign+"&shop_id="+shopid+"&access_token="+access_token+
					"&page_size="+20+"&time_to="+timeto+"&time_from="+timefrom+"&time_range_field="+"create_time";
			 HttpGet httpget = new HttpGet(url);
			 httpget.setConfig(requestConfig);
			 httpget.setHeader("Content-Type", "application/json");
			 //httpget.setHeader("Authorization", sign);
			  
			  JSONObject json = new JSONObject();
				json.put("page_size",20);
				json.put("time_to",timeto);
				json.put("time_range_field", "create_time");
				json.put("time_from", timefrom);
				//httpget.setEntity(new StringEntity(json.toString(), "UTF-8"));
				 
				 
		  
				  response = client.execute(httpget); 
			  
			  
				/*
				 * // 配置信息 RequestConfig requestConfig = RequestConfig.custom()
				 * .setConnectTimeout(5000) // 设置请求超时时间(单位毫秒) .setConnectionRequestTimeout(5000)
				 * // socket读写超时时间(单位毫秒) .setSocketTimeout(5000) // 设置是否允许重定向(默认为true)
				 * .setRedirectsEnabled(true).build();
				 * 
				 * // 将上面的配置信息 运用到这个Get请求里 . httppost.setConfig(requestConfig);
				 */
		  
		  
		  // 由客户端执行(发送)Get请求
		  
		  HttpEntity entitys = response.getEntity();
		  System.out.println(EntityUtils.toString(entitys));
		  //HttpEntity responseEntity = response.getEntity(); } catch (IOException e) {
		  // TODO Auto-generated catch block e.printStackTrace(); }finally { try { //
		  
		  }catch (IOException e) {
			  e.printStackTrace();
		  }

		return response;
	}
	
	
	
	

}
