package com.eshore.fileExport;

import java.io.Serializable;
import java.sql.Timestamp;

public class BillData implements Serializable{
	private String businessNumber;
	private int payType;
	private String areaCode;
	private Timestamp createTime;
	private String productId;
	private String productSpecCode;
	private String productName;
	private int price;
	//订购类型
	private int bookType;
	//业务类型
	private String accType;
	//剔除原因,默认为空
	private String deleCode;
	
	public String getBusinessNumber() {
		return businessNumber;
	}

	public int getPayType() {
		return payType;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public String getProductId() {
		return productId;
	}

	public String getProductSpecCode() {
		return productSpecCode;
	}

	public String getProductName() {
		return productName;
	}

	public int getPrice() {
		return price;
	}

	public int getBookType() {
		return bookType;
	}

	public String getAccType() {
		return accType;
	}

	public String getDeleCode() {
		return deleCode;
	}

	public void setBusinessNumber(String businessNumber) {
		this.businessNumber = businessNumber;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public void setProductSpecCode(String productSpecCode) {
		this.productSpecCode = productSpecCode;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setBookType(int bookType) {
		this.bookType = bookType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public void setDeleCode(String deleCode) {
		this.deleCode = deleCode;
	}

	@Override
	public String toString() {
		return "BillData [businessNumber=" + businessNumber + ", payType=" + payType + ", areaCode=" + areaCode
				+ ", createTime=" + createTime + ", productId=" + productId + ", productSpecCode=" + productSpecCode
				+ ", productName=" + productName + ", price=" + price + ", bookType=" + bookType + "]";
	}
	
}
