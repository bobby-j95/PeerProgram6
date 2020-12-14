package com.meritamerica.assignment6.model;

import javax.persistence.*;

@Entity
@Table(name = "accountHolderContact")
public class AccountHolderContactInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;
	private String phoneNumber;
	private String email;
	private String address;

	@OneToOne(cascade = CascadeType.ALL)
	private AccountHolder accountHolder;
	
	public AccountHolderContactInfo() {
		
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public AccountHolder getAccountHolder() {
		return accountHolder;
	}

	public void setAccountHolder(AccountHolder accountHolder) {
		this.accountHolder = accountHolder;
	}
}
