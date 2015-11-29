package com.acctrue.jlyj.entity;


public class StoreItem {
	public String CodeId;
	public int Amount;
	public String ProduceBatchNo;
	public String ProduceDate;
	public boolean IsCitic;

	public StoreItem(String CodeId, int Amount, String ProduceBatchNo,
			String ProduceDate, boolean IsCitic) {
		super();
		this.CodeId = CodeId;
		this.Amount = Amount;
		this.ProduceBatchNo = ProduceBatchNo;
		this.ProduceDate = ProduceDate;
		this.IsCitic = IsCitic;
	}
}
