package com.meritamerica.assignment6.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.meritamerica.assignment6.exceptions.*;

@Entity
@Table(name = "accountHolder")
@RestController()
@RequestMapping("AccountHolder")
public class AccountHolder implements Comparable<AccountHolder>{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id")
	private static long id;

	@NotBlank(message = "First Name is required")
	private String firstName;

	private String middleName;

	@NotBlank(message = "Last Name is required")
	private String lastName;

	@Size(min = 9)
	@NotBlank(message = "SSN is required")
	private String ssn;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private AccountHolder accountHolderData;
  
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<CheckingAccount> checkingAccounts;

	@OneToMany(cascade = CascadeType.ALL)
	private List<SavingsAccount> savingsAccounts;

	@OneToMany(cascade = CascadeType.ALL)
	private List<CDAccount> CDAccounts;	
	
// Declare a class that implements an interface 
//public class AccountHolder implements Comparable{ 
		//private static long ID = 1;	
		
	    // Class member variables 
//		@NotNull(message="First name can not be Null")
//	 	private String firstName;
//		private String middleName;
//	    @NotNull(message="Last name can not be Null")
//	    private String lastName;
//	    @NotNull
//	    @Size(min=9, message="SNN can not be less than 9 characters")
//	    private String ssn;
	   // private CheckingAccount[] checkingAccounts;
	    //private SavingsAccount[] savingsAccounts;
	    //private CDAccount[] CDAccounts;
	    
	    // keep track of numbers of checking and saving accounts
	    private int numberOfCheckings = 0;
	    private int numberOfSavings = 0;
	    private int numberOfCDAs = 0;
	    

	    // Used split method to split a string into an array 
	    public static AccountHolder readFromString(String accountHolderData) {
	    	String[] data = accountHolderData.split(",");
	    	String firstName = data[0];
	    	String middleName = data[1];
	    	String lastName = data[2];
	    	String ssn = data[3];
	    	
	    	return new AccountHolder(firstName, middleName, lastName, ssn);
	    }
	    
	    public AccountHolder (){	
	    	this.id = AccountHolder.id;
	    	AccountHolder.id++;
	    	// instantiate array of Checking
	        this.checkingAccounts = new ArrayList<>();
	        this.savingsAccounts = new ArrayList<>();
	        this.CDAccounts = new ArrayList<>(); 
	    }
	    public AccountHolder(String firstName, String middleName, String lastName, String ssn){
	    	this();
	    	
	        this.firstName = firstName;
	        this.middleName = middleName;
	        this.lastName = lastName;
	        this.ssn = ssn;
	    }
	            
      /*If combined balance limit is exceeded, throw ExceedsCombinedBalanceLimitException
       * also add a deposit transaction with the opening balance */
	    public boolean addCheckingAccount(CheckingAccount checkingAccount) throws ExceedsCombinedBalanceLimitException {
	    	// check the opening account condition
	    	if (checkingAccount == null) {
				return false;
			}
			if (getCheckingBalance() + getSavingsBalance() + checkingAccount.getBalance() >= 250000) {
				throw new ExceedsCombinedBalanceLimitException();
			}
			checkingAccounts.add(checkingAccount);
			checkingAccount.setAccountNumber(this.id);
			return true;
	    }
	    
	    public List<CheckingAccount> getCheckingAccounts( ) {
	    	return checkingAccounts;
	    }
	    
	    public int getNumberOfCheckingAccounts() {
	    	return this.checkingAccounts.size();
	    }
	    
	    public double getCheckingBalance() {
	    	double total = 0;
	    	for (BankAccount account : checkingAccounts) {
				total += account.getBalance();
			}
	    	
	    	return total;
	    }
	    
	    /*If combined balance limit is exceeded, throw ExceedsCombinedBalanceLimitException
	     * also add a deposit transaction with the opening balance */
	    public boolean addSavingsAccount(SavingsAccount savingsAccount) throws ExceedsCombinedBalanceLimitException{
	    	// check if total amount is greater than 250, 000
	    	if (savingsAccount == null) {
				return false;
			}
			if (getCheckingBalance() + getSavingsBalance() + savingsAccount.getBalance() >= 250000) {
				throw new ExceedsCombinedBalanceLimitException();
			}
			savingsAccounts.add(savingsAccount);
			savingsAccount.setAccountNumber(this.id);
			return true;
	    }
	    
	    public List<SavingsAccount> getSavingsAccounts() {
	    	return savingsAccounts;
	    }
	    
	    public int getNumberOfSavingsAccounts() {
	    	return this.savingsAccounts.size();
	    }
	    
	    public double getSavingsBalance() {
	    	double total = 0;
	    	for (BankAccount account : savingsAccounts) {
				total += account.getBalance();
			}
	    	
	    	return total;
	    }
	    
	    //Should also add a deposit transaction with the opening balance
	    public boolean addCDAccount(CDAccount cdAccount) throws ExceedsFraudSuspicionLimitException {
	    	if (cdAccount == null) {
				return false;
			}
			CDAccounts.add(cdAccount);
			cdAccount.setAccountNumber(this.id);
			return true;
	    }
	    
	    public int getNumberOfCDAccounts() {
	    	return this.CDAccounts.size();
	    }
	    
	    public List<CDAccount> getCDAccounts() {
	    	return CDAccounts;
	    }
	    
	    public double getCDBalance() {
	    	double total = 0;
	    	for (BankAccount account : CDAccounts) {
				total += account.getBalance();
			}
	    	
	    	return total;
	    }
	    
	    public double getCombinedBalance() {
	    	//return this.getCDBalance() + this.getCheckingBalance() + this.getSavingsBalance();
	    	return this.getCheckingBalance() + this.getSavingsBalance() + this.getCDBalance();
	    }
	    
	    
	    // This method validates that the total amount of combined balance and deposit is less than $250,000.00
	    private boolean canOpen(double deposit) throws ExceedsCombinedBalanceLimitException {
	    	if (this.getCombinedBalance() < 250000.00) {
	    		return true;
	    	} else {
	    		System.out.println("Total is over 250,000. Can not open a new account");
	    		throw new ExceedsCombinedBalanceLimitException();
	    	}
	    }

//		//@Override
//		public int compareTo(Object o) {
//			AccountHolder acc = (AccountHolder) o;
//			if (this.getCombinedBalance() < acc.getCombinedBalance())
//				return -1;
//			else if (this.getCombinedBalance() > acc.getCombinedBalance())
//				return 1;
//			else
//				return 0;
//		}
		
	    /*
		// find the account has that ID in this account holder and return that account, if can not find, return null
		public BankAccount findAccount(long ID) {
			for (int i = 0; i < this.numberOfCheckings; i++) {
				if (this.checkingAccounts[i].getAccountNumber() == ID) {
					return this.checkingAccounts[i];
				}
			}
			
			for (int j = 0; j < this.numberOfSavings; j++) {
				if (this.savingsAccounts[j].getAccountNumber() == ID) {
					return this.savingsAccounts[j];
				}
			}
			
			for (int j = 0; j < this.numberOfCDAs; j++) {
				if (this.CDAccounts[j].getAccountNumber() == ID) {
					return this.CDAccounts[j];
				}
			}
			
			return null;
		}*/
				
		// extend account array capacity of 
						 
	    public String getFirstName() {
	        return firstName;
	    }
	    public void setFirstName(String firstName) {
	        this.firstName = firstName;
	    }
	    public String getMiddleName() {
	        return middleName;
	    }
	    public void setMiddleName(String middleName) {
	        this.middleName = middleName;
	    }
	    public String getLastName() {
	        return lastName;
	    }
	    public void setLastname(String lastName) {
	        this.lastName = lastName;
	    }
	    public String getSSN() {
	        return ssn;
	    }
	    public void setSSN(String ssn) {
	        this.ssn = ssn;
	    }
	    
	    public long getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		@Override
		public int compareTo(AccountHolder o) {
			// TODO Auto-generated method stub
			return 0;
		}
}