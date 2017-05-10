package com.fredrick.mixedsignals.model;

public class BlockItem {

	private int iIndex = 0;

	private String strName = null;

	private String strPhone = null;

	private String strSMS = null;

	private boolean bChecked = false;


	public BlockItem(int Index, String Name, String Phone, String SMS, boolean Checked) {
		iIndex = Index;
		strName = Name;
		strPhone = Phone;
		strSMS = SMS;
		bChecked = Checked;
	}

	public int getIndex() {
		return iIndex;
	}

	public void setIndex(int value) {
		this.iIndex = value;
	}
	
	public String getName() {
		return strName;
	}
	
	public void setName(String value) {
		this.strName = value;
	}
	
	public String getPhone() {
		return strPhone;
	}
	
	public void setPhone(String value) {
		this.strPhone = value;
	}

	public String getSMS() {
		return strSMS;
	}

	public void setSMS(String value) {
		this.strSMS = value;
	}

	public boolean getChecked() {
		return bChecked;
	}

	public void setChecked(boolean value) {
		this.bChecked = value;
	}
	
}
