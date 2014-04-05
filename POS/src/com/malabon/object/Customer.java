package com.malabon.object;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Customer implements Serializable {
	public String customer_id;
	public String first_name;
	public String last_name;
	public String address;
	public String address_landmark;
	public String tel_no;
	public String mobile_no;
}