package com.acctrue.jlyj.entity;

import java.util.ArrayList;

public class DocumentInfo {

	//������ö��
	public enum DocumentStyle{
		
		PurchaseInDocument,		//�ɹ���ⵥ      
		
		ReturnInDocument,		//�˻���ⵥ
		
		SaleOutDcoment			//���۳��ⵥ
		
	}
	
	public String documentNum;			//���ݱ���
	
	public DocumentStyle documentStyle;	//��������
	
	public ArrayList<CodeInfo> codeInfoList;	//���б�
	
	public String imageBase64Str;
}
