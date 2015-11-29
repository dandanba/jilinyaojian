package com.acctrue.jlyj.entity;

import java.util.List;

public class Stores {

	public String StoreNo;
	public String StoreTypeString;
	public String CorpCode;
	public String ImageBase64;
	public List<StoreItem> StoreItems;
	public boolean isYBK;
	
	public Stores(String StoreNo,String StoreTypeString,String CorpCode,String ImageBase64,List<StoreItem> StoreItems,boolean isYBK){
		super();
		this.StoreNo=StoreNo;
		this.StoreTypeString=StoreTypeString;
		this.CorpCode=CorpCode;
		this.ImageBase64=ImageBase64;
		this.StoreItems=StoreItems;
		this.isYBK = isYBK;
	}

}
