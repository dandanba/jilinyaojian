package com.acctrue.jlyj.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

import javax.crypto.spec.IvParameterSpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;

import com.acctrue.jlyj.entity.CodeInfo;
import com.acctrue.jlyj.entity.DocumentInfo;
import com.acctrue.jlyj.entity.Stores;
import com.acctrue.jlyj.entity.lv_item3;

public class commonService {
	public static int personflag;
	public static String lastPhoto = "";
	public static String serverUrl = "http://211.137.215.54:8080/TTS";
	public static String cropCode = "";
	public static int cropType;
	public static String getData = "/Portal/ScanServices/GetJLProductInfo.svc/getProductInfoByCodeV2/";
	public static String setPrice = "/Portal/ScanServices/GetJLProductInfo.svc/UpdateSaleCorpPrice/";
	public static String setOrder = "/Portal/ScanServices/CodeService.svc/UpLoadStore";
	public static String getStockInfo="/Portal/ScanServices/CodeService.svc/SearchStockInfoList/";
	public static String setItem="/Portal/ScanServices/CodeService.svc/UploadStoreCheckIn";
	public static String getNum="/Portal/ScanServices/CodeService.svc/GetStockCount/";

	// 产品码查询--API
	public static CodeInfo getCodeInfoByHttp(String code){
		// 获取HttpGet对象
		String Url=serverUrl+getData+cropCode+"/"+code;
		HttpGet request = new HttpGet(Url);
		HttpResponse response;
		try {
			response = new DefaultHttpClient().execute(request);
		HttpEntity httpEntity = response.getEntity();
		InputStream is = httpEntity.getContent();
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = "";
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		
		if (response.getStatusLine().getStatusCode() == 200) {
			JSONObject jsonObject = new JSONObject(sb.toString());
			CodeInfo codeInfo = new CodeInfo();
			if(jsonObject.getString("ProductCode")!=null&&!jsonObject.getString("ProductCode").equals("null")){
			codeInfo.produtCode = code;
			codeInfo.productName=jsonObject.getString("ProductName");
			codeInfo.productProduceDate=jsonObject.getString("ProduceDate");
			codeInfo.produceCropName=jsonObject.getString("ProduceCorpName");
			codeInfo.productBatchNum=jsonObject.getString("ProducebatchNo");
			codeInfo.productPrice=jsonObject.getDouble("UnitPrice")+"";
			codeInfo.isYJCode=jsonObject.getBoolean("IsCitic");
			
//				codeInfo.produtCode = "81561440018428952163 ";
//				codeInfo.productName="克拉霉素片";
//				codeInfo.productProduceDate="2014-03-06";
//				codeInfo.produceCropName="浙江贝得药业有限公司（广东逸舒） ";
//				codeInfo.productBatchNum="20140301";
//				codeInfo.productPrice="14.0";
//				codeInfo.isYJCode=true;
			return codeInfo;
			}else{
				codeInfo.produtCode = code;
				codeInfo.productName="临时商品";
				codeInfo.productProduceDate="暂无";
				codeInfo.produceCropName="暂无";
				codeInfo.productBatchNum="暂无";
				codeInfo.productPrice="0.0";
				codeInfo.isYJCode=true;
				return codeInfo;
			}
		}
		} catch (Exception e) {
			e.printStackTrace();

			CodeInfo codeInfo = new CodeInfo();
			codeInfo.produtCode = code;
			codeInfo.productName="临时商品";
			codeInfo.productProduceDate="暂无";
			codeInfo.produceCropName="暂无";
			codeInfo.productBatchNum="暂无";
			codeInfo.productPrice="0.0";
			codeInfo.isYJCode=true;
			return codeInfo;
		} 


		return null;
	}


	// 单据上传--API
	public static boolean uploadDocumentByHttp(Stores stores) {
		String Url = serverUrl + setOrder;
		HttpPost request = new HttpPost(Url);
		String entitystr = toJson2(stores);
		System.out.println("======================="+entitystr);
		try {
			StringEntity entity = new StringEntity(entitystr, HTTP.UTF_8);
			entity.setContentType("application/json");
			request.setEntity(entity);
			HttpResponse response = new DefaultHttpClient().execute(request);
			HttpEntity httpEntity = response.getEntity();
			InputStream is = httpEntity.getContent();
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			System.out.println("======================="+response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				if (sb.toString().equals("<string xmlns=\"http://schemas.microsoft.com/2003/10/Serialization/\"/>")) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// 修改价格--API
	public static boolean uploadPriceByHttp(String code, String price) {
		String Url = serverUrl + setPrice + cropCode+"/"+code+"/" + price;
		HttpGet request = new HttpGet(Url);
		HttpResponse response;
		try {
			response = new DefaultHttpClient().execute(request);
			HttpEntity httpEntity = response.getEntity();
			InputStream is = httpEntity.getContent();
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			if (response.getStatusLine().getStatusCode() == 200) {
				if (sb.toString().equals("")) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	public static ArrayList<lv_item3> getStockInfo(String code,String batch,int page){
		String Url=serverUrl+getStockInfo+batch+"/"+code+"/"+cropCode+"/"+page+"/10";
		HttpGet request = new HttpGet(Url);
		HttpResponse response;
		ArrayList<lv_item3> list=new ArrayList<lv_item3>();
		try {
			response = new DefaultHttpClient().execute(request);
			HttpEntity httpEntity = response.getEntity();
			InputStream is = httpEntity.getContent();
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			if (response.getStatusLine().getStatusCode() == 200) {
					JSONObject jsonObject ;
					JSONArray jsonArray=new JSONArray(sb.toString()); 
					for (int i = 0; i < jsonArray.length(); i++) {
						jsonObject=jsonArray.getJSONObject(i);
						lv_item3 item3=new lv_item3();
						item3.item_1=((page-1)*10+i+1)+"";
						item3.item_2=jsonObject.getString("ProductCode");
						item3.item_3=jsonObject.getString("ProductName");
						item3.item_5=jsonObject.getString("ProduceBatchNo");
						item3.item_8=jsonObject.getString("Amount");
						list.add(item3);
					}
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static boolean updateItem(Stores stores){
		String Url=serverUrl+setItem;
		HttpPost request = new HttpPost(Url);
		String entitystr = toJson2(stores);
		System.out.println(entitystr);
		try {
			StringEntity entity = new StringEntity(entitystr, HTTP.UTF_8);
			entity.setContentType("application/json");
			request.setEntity(entity);
			HttpResponse response = new DefaultHttpClient().execute(request);
			HttpEntity httpEntity = response.getEntity();
			InputStream is = httpEntity.getContent();
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			if (response.getStatusLine().getStatusCode() == 200) {
				if (sb.toString().equals("<string xmlns=\"http://schemas.microsoft.com/2003/10/Serialization/\"/>")) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static int getTotal(){
		int count=0;
		String Url=serverUrl+getNum+cropCode;
		HttpGet request = new HttpGet(Url);
		HttpResponse response;
		try {
			response = new DefaultHttpClient().execute(request);
			HttpEntity httpEntity = response.getEntity();
			InputStream is = httpEntity.getContent();
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			if (response.getStatusLine().getStatusCode() == 200) {
				count=Integer.parseInt(sb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}


	// 实体序列化方法
	public static String entityToJson(DocumentInfo documentInfo) {
		String json;
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("/'documentNum/':");
		sb.append("/'" + documentInfo.documentNum + "/',");
		sb.append("/'documentStyle/':");
		sb.append(documentInfo.documentStyle.ordinal() + ",");
		sb.append("/'codeInfoList/':");
		sb.append("[");
		for (int i = 0; i < documentInfo.codeInfoList.size(); i++) {
			sb.append("{");
			sb.append("/'produtCode/':");
			sb.append("/'" + documentInfo.codeInfoList.get(i).produtCode
					+ "/',");
			sb.append("/'productName/':");
			sb.append("/'" + documentInfo.codeInfoList.get(i).productName
					+ "/',");
			sb.append("/'productProduceDate/':");
			sb.append("/'"
					+ documentInfo.codeInfoList.get(i).productProduceDate
					+ "/',");
			sb.append("/'productBatchNum/':");
			sb.append("/'" + documentInfo.codeInfoList.get(i).productBatchNum
					+ "/',");
			sb.append("/'produceCropName/':");
			sb.append("/'" + documentInfo.codeInfoList.get(i).produceCropName
					+ "/',");
			sb.append("/'productPrice/':");
			sb.append("/'" + documentInfo.codeInfoList.get(i).productPrice
					+ "/',");
			sb.append("/'prudictNum/':");
			sb.append("/'" + documentInfo.codeInfoList.get(i).prudictNum
					+ "/',");
			sb.append("/'isYJCode/':");
			sb.append("+documentInfo.codeInfoList.get(i).isYJCode+");
			if (i == documentInfo.codeInfoList.size() - 1) {
				sb.append("}");
			} else {
				sb.append("},");
			}
		}
		sb.append("]}");
		json = sb.toString();
		return json;
	}

	public static String toJson(Stores stores) {
		String json;
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"CorpCode\":");
		sb.append("\"" + stores.CorpCode + "\",");
		sb.append("\"ImageBase64\":");
		sb.append("\"" + stores.ImageBase64 + "\",");
		sb.append("\"StoreItems\":");
		sb.append("[");
		for (int i = 0; i < stores.StoreItems.size(); i++) {
			sb.append("{");
			sb.append("\"CodeId\":");
			sb.append("\"" + stores.StoreItems.get(i).CodeId + "\",");
			sb.append("\"Amount\":");
			sb.append(stores.StoreItems.get(i).Amount + ",");
			sb.append("\"ProduceBatchNo\":");
			sb.append("\"" + stores.StoreItems.get(i).ProduceBatchNo + "\",");
			sb.append("\"ProduceDate\":");
			sb.append("\"" + stores.StoreItems.get(i).ProduceDate + "\",");
			sb.append("\"IsCitic\":");
			sb.append(stores.StoreItems.get(i).IsCitic);
			if (i == stores.StoreItems.size() - 1) {
				sb.append("}");
			} else {
				sb.append("},");
			}
		}
		sb.append("],");
		sb.append("\"StoreNo\":");
		sb.append("\"" + stores.StoreNo + "\",");
		sb.append("\"StoreTypeString\":");
		sb.append("\"" + stores.StoreTypeString + "\"");
		sb.append("}");
		json = sb.toString();
		return json;
	}

	public static String toJson2(Stores stores) {
		//生成jsonObejct
		JSONArray array = new JSONArray();
		
		for (int i = 0; i < stores.StoreItems.size(); i++) {
			
			JSONObject o = new JSONObject();
			try {
				
			o.put("CodeId", stores.StoreItems.get(i).CodeId);
			o.put("Amount", stores.StoreItems.get(i).Amount);
			o.put("ProduceBatchNo", stores.StoreItems.get(i).ProduceBatchNo);
			o.put("ProduceDate", stores.StoreItems.get(i).ProduceDate);
			o.put("IsCitic", stores.StoreItems.get(i).IsCitic);
			array.put(i,o);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}

		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("CorpCode", stores.CorpCode);
			jsonObj.put("ImageBase64",  stores.ImageBase64);
			jsonObj.put("StoreItems", array);
			jsonObj.put("StoreNo", stores.StoreNo);
			jsonObj.put("StoreTypeString", stores.StoreTypeString);
			jsonObj.put("IsYBK", stores.isYBK);
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return jsonObj.toString();
		
	}
	
	
	// public static String getResult(int temp,String json){
	// return null;
	// }

}
