package com.acctrue.jlyj.util;

import java.util.ArrayList;

import android.test.AndroidTestCase;

import com.acctrue.jlyj.entity.CodeInfo;
import com.acctrue.jlyj.entity.DocumentInfo;
import com.acctrue.jlyj.entity.DocumentInfo.DocumentStyle;
import com.acctrue.jlyj.service.commonService;

public class Test extends AndroidTestCase{
	
	public void test(){
		DocumentInfo documentInfo=new DocumentInfo();
		documentInfo.documentNum="601348921";
		documentInfo.documentStyle=DocumentStyle.SaleOutDcoment;
		ArrayList<CodeInfo> list=new ArrayList<CodeInfo>();
		for (int i = 0; i < 3; i++) {
			CodeInfo codeInfo=new CodeInfo();
			codeInfo.produceCropName="jl"+(int)Math.random()*100;
			codeInfo.productBatchNum="2014"+(int)Math.random()*100;
			codeInfo.isYJCode=(Math.random()*2>0)?true:false;
			codeInfo.productName="65432";
			codeInfo.productPrice=Math.random()*100+"";
			codeInfo.productProduceDate="2014";
			codeInfo.produtCode="543215";
			codeInfo.prudictNum=(int)Math.random()*100+"";
			list.add(codeInfo);
		}
		documentInfo.codeInfoList=list;
		String json=commonService.entityToJson(documentInfo);
		System.out.println(json);
	}

//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		DocumentInfo documentInfo=new DocumentInfo();
//		documentInfo.documentNum="601348921";
//		documentInfo.documentStyle=DocumentStyle.SaleOutDcoment;
//		ArrayList<CodeInfo> list=new ArrayList<CodeInfo>();
//		for (int i = 0; i < 3; i++) {
//			CodeInfo codeInfo=new CodeInfo();
//			codeInfo.produceCropName="jl"+(int)Math.random()*100;
//			codeInfo.productBatchNum="2014"+(int)Math.random()*100;
//			codeInfo.isYJCode=(Math.random()*2>0)?true:false;
//			codeInfo.productName="65432";
//			codeInfo.productPrice=Math.random()*100+"";
//			codeInfo.productProduceDate="2014";
//			codeInfo.produtCode="543215";
//			codeInfo.prudictNum=(int)Math.random()*100+"";
//			list.add(codeInfo);
//		}
//		documentInfo.codeInfoList=list;
//		String json=commonService.entityToJson(documentInfo);
//		System.out.println(json);
//	}

}
