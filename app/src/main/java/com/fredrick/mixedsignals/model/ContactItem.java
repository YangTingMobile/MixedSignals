package com.fredrick.mixedsignals.model;

import android.graphics.drawable.Drawable;

public class ContactItem {

	private String strName = null;

	private String strPhone = null;

	private boolean bChecked = false;


	public ContactItem(String Name, String Phone, boolean Checked) {
		strName = Name;
		strPhone = Phone;
		bChecked = Checked;
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

	public boolean getChecked() {
		return bChecked;
	}

	public void setChecked(boolean value) {
		this.bChecked = value;
	}
	
}
