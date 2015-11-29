package com.acctrue.jlyj.update;

public class UpdateResult {
	private String currentVersion; //��ǰ�汾
	
	private String serverVersion;//�������汾
	
	private String downUrl = "";//App���ص�ַ
	
	private String msg = "";//��Ϣ
	
	private String context = "";//��������
	
	
	public String getCurrentVersion() {
		return currentVersion;
	}
	public void setCurrentVersion(String currentVersion) {
		this.currentVersion = currentVersion;
	}
	public String getServerVersion() {
		return serverVersion;
	}
	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}
	public String getDownUrl() {
		return downUrl;
	}
	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}
	 
	private void setMsg(String msg)
	{
		this.msg = msg;
	}
	
	public String getMsg()
	{
		return this.msg;
	}
	
	public Boolean Check()
	{ 
		if(getCurrentVersion() == null || getCurrentVersion().length() ==0)
			return false;
		if(getServerVersion() == null || getServerVersion().length() ==0)
			return false;
		Boolean isCurrentHigh = false;
		String[] currentVersions = getCurrentVersion().split("\\.");
		String[] serverVersions = getServerVersion().split("\\.");
		int length = currentVersions.length >= serverVersions.length ? serverVersions.length : currentVersions.length;
		for(int i =0;i<length;i++)
		{
			String c_s = currentVersions[i];
			String s_s = serverVersions[i];
			if(c_s.compareTo(s_s) >0)
			{
				setMsg("�������汾С�ڱ��ذ汾");
				isCurrentHigh = true;
				return false;
			}
			else if(c_s.compareTo(s_s) <0){
				isCurrentHigh = false;
				return true;
			}				
		}
		if(isCurrentHigh) return false;
		return true;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
}
