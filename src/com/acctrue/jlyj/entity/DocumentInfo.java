package com.acctrue.jlyj.entity;

import java.util.ArrayList;

public class DocumentInfo {

	//表单类型枚举
	public enum DocumentStyle{
		
		PurchaseInDocument,		//采购入库单      
		
		ReturnInDocument,		//退货入库单
		
		SaleOutDcoment			//销售出库单
		
	}
	
	public String documentNum;			//单据编码
	
	public DocumentStyle documentStyle;	//单据类型
	
	public ArrayList<CodeInfo> codeInfoList;	//码列表
	
	public String imageBase64Str;
}
