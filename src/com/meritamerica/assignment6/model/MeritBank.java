package com.meritamerica.assignment6.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.meritamerica.assignment6.exceptions.ExceedsAvailableBalanceException;
import com.meritamerica.assignment6.exceptions.ExceedsFraudSuspicionLimitException;
import com.meritamerica.assignment6.exceptions.NegativeAmountException;


public class MeritBank {
	private static long accIndex = 0;
	private static List<AccountHolder> accountHolders = new ArrayList<>();
	private static List<CDOffering> cdOfferings = new ArrayList<>();
	
	private static int numbOfAccountHolder = 0;
	public static FraudQueue fraudQueue = new FraudQueue();
	
	public static void addAccountHolder(AccountHolder accountHolder) {
		accountHolders.add(accountHolder);
		
	}
	
	public static void addCDOffering(CDOffering offering) {
		cdOfferings.add(offering);
	}
	
	public static AccountHolder getAccountHolder(long id) {
		for (AccountHolder account : MeritBank.accountHolders) {
			if (account == null) {
				return null;
			}
			if (account.getId() == id) {
				return account;
			}
		}
		
		return null;
		
		
	}
	public static BankAccount findAccount(long ID) {
		for (AccountHolder accountHolder : accountHolders) {
			for (BankAccount account : accountHolder.getCheckingAccounts()) {
				if (account.getAccountNumber() == ID) {
					return account;
				}
			}
			for (BankAccount account : accountHolder.getSavingsAccounts()) {
				if (account.getAccountNumber() == ID) {
					return account;
				}
			}
			for (BankAccount account : accountHolder.getCDAccounts()) {
				if (account.getAccountNumber() == ID) {
					return account;
				}
			}
		}
		return null;
	}
	
	public static String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(date);
	}
	
	public static String decimalFormat(double numb) {
		DecimalFormat df = new DecimalFormat("#.####");
		return df.format(numb);
	}
	
	public static String formatNumber(double d) {
	    if(d == (int) d)
	        return String.format("%d",(int)d);
	    else
	        return String.format("%s",d);
	}
	
	private static void readFraudQueue(BufferedReader reader) throws IOException, ParseException {
		int pendingNum = Integer.parseInt(reader.readLine());
	
		for (int i= 0; i < pendingNum; i++) {
			MeritBank.fraudQueue.addTransaction(readTransactionType(reader.readLine()));
		}
	}
	
	private static Transaction readTransactionType(String line) throws ParseException {
		String[] datas = line.split(",");
		
		// Create a date formatter
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		int sourceID = Integer.parseInt(datas[0]);
		int targetID = Integer.parseInt(datas[1]);
		BankAccount targetAcc = MeritBank.findAccount(targetID);
		double amount = Integer.parseInt(datas[2]);
		Date date = formatter.parse(datas[3]);
		
		// if this is not a transfer transaction
		if (sourceID != -1) {
			if (amount >= 0) {
				return new DepositTransaction(targetAcc, amount, date);
			} else {
				return new WithdrawTransaction(targetAcc, amount, date);
			}
		} else {
			// if this is a transfer transaction
			BankAccount sourceAcc = MeritBank.findAccount(sourceID);
			return new TransferTransaction(sourceAcc, targetAcc, amount, date);
		}
	}
	
	private static void readTransactions(BufferedReader reader, BankAccount acc) throws IOException, ParseException, 
	  ExceedsFraudSuspicionLimitException, NegativeAmountException, ExceedsAvailableBalanceException {
		int numOfTransaction = Integer.valueOf(reader.readLine()); // number of transactions
		
		for (int i = 0; i < numOfTransaction; i++) {
			String line = reader.readLine();
			String[] datas = line.split(",");
			
			// Create a date formatter
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			
			int sourceID = Integer.parseInt(datas[0]);
			int targetID = Integer.parseInt(datas[1]);
			double amount = Double.parseDouble(datas[2]);
			Date date = formatter.parse(datas[3]);
			
			// if this is not a transfer transaction
			if (sourceID != -1) {
				if (amount >= 0) {
					acc.addTransaction(new DepositTransaction(acc, amount, date));
				} else {
					acc.addTransaction(new WithdrawTransaction(acc, amount, date));
				}
			} else {
				// if this is a transfer transaction
				BankAccount sourceAcc = MeritBank.findAccount(sourceID);
				acc.addTransaction(new TransferTransaction(sourceAcc, acc, amount, date));
			}
		}
	}
	
	private static String addSavingData(AccountHolder acc) {
		StringBuilder data = new StringBuilder();
		int numbOfSavings = 0;
		List<SavingsAccount> savings = acc.getSavingsAccounts();
		
		for (BankAccount account : savings) {
			if (account == null) {
				break;
			}
			
			// increase number of checking
			numbOfSavings++;
			
			data.append(account.writeToString() + "\n");
		}
		
		return numbOfSavings + "\n" + data.toString();
	}
	/*
	// sort account from small to large
	public static AccountHolder[] sortAccountHolders() {
		AccountHolder[] accountHolder = MeritBank.accountHolders;
		
		int n = accountHolder.length; 
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n-i-1; j++) 
                if (accountHolder[j].compareTo(accountHolder[j+1]) > 0) 
                { 
                    // swap accountHolder[j+1] and accountHolder[i] 
                    AccountHolder temp = accountHolder[j]; 
                    accountHolder[j] = accountHolder[j+1]; 
                    accountHolder[j+1] = temp; 
                } 
        }
        
        return accountHolder;
	}*/
	
	private static String addCDData(AccountHolder acc) {
		StringBuilder data = new StringBuilder();
		int numbOfCDs = 0;
		List<CDAccount> cds = acc.getCDAccounts();
		
		for (BankAccount account : cds) {
			if (account == null) {
				break;
			}
			
			// increase num of checking
			numbOfCDs++;
			
			data.append(account.writeToString() + "\n");
		}
		
		return numbOfCDs + "\n" + data.toString();
	}
	
	/*
	 * convert all the needed checking account information to String and return
	 */
	private static String addCheckingData(AccountHolder acc) {
		StringBuilder data = new StringBuilder();
		int numbOfCheckings = 0;
		List<CheckingAccount> checkings = acc.getCheckingAccounts();
		
		for (BankAccount account : checkings) {
			if (account == null) {
				break;
			}
			
			// increase num of checking
			numbOfCheckings++;
			
			data.append(account.writeToString() + "\n");
		}
		
		return numbOfCheckings + "\n" + data.toString();
	}
	
	public static List<AccountHolder> getAccountHolders() {
		return accountHolders;
	}
	
	public static List<CDOffering> getCDOfferings() {
		return cdOfferings;
	}
	
	public static CDOffering getBestCDOffering(double depositAmount) {
		double highestYield = 0;
		CDOffering best = null; 		// position of the best offerings in the CDOffering array
		
		if(cdOfferings.size() == 0) {
			return null;
		}
		for(CDOffering cdOffering : cdOfferings) {
			if(cdOffering.getInterestRate() > highestYield) {
				best = cdOffering;
				highestYield = cdOffering.getInterestRate();
			}
		}
		return best;
		
	}
	
	public static CDOffering getSecondBestCDOffering(double depositAmount) {

		double highestYield = 0;
		int secondBestI = 0; // second best offer index
		int bestI = 0;
		double secondBestYield = 0;
		double tempYield = 0;
		
		if (MeritBank.cdOfferings != null) {
			for (int i=0; i < MeritBank.cdOfferings.length; i++) {
				tempYield = MeritBank.futureValue(depositAmount, cdOfferings[i].getInterestRate(), cdOfferings[i].getTerm());
				if (tempYield > highestYield) {
					
					// let the second best offer take over the old best offer
					secondBestI = bestI;
					secondBestYield = highestYield;
					
					// the best offer get the new position and value
					highestYield = tempYield;
					bestI = i;
					
				}
			}
			
			return cdOfferings[secondBestI];
		} else {
			return null;
		}
	}
	
	public static void clearCDOfferings() {
		MeritBank.cdOfferings = null;
	}
	
	public static void setCDOfferings(List<CDOffering> offerings) {
		for(CDOffering offering : offerings) {
			cdOfferings.add(offering);
		}
	}
	
	public static long getNextAccountNumber() {
		// get back later
		MeritBank.accIndex++;
		return accIndex;
	}
	
	public static void setNextAccountNumber(long nextAccountNumb) {
		MeritBank.accIndex = nextAccountNumb - 1;
	}
	
	public static double totalBalances() {
		double total = 0.0;
		
		// total all balances (checking and saving) in every account		
		for (int i=0; i < MeritBank.numbOfAccountHolder; i++) {
			total += MeritBank.accountHolders[i].getCheckingBalance() + MeritBank.accountHolders[i].getCheckingBalance();
		}
		
		return total;
	}
	
	public static double recursionFutureValue(double amount, int years, double interestRate) {
		if (years == 0) {
			return amount;
		} else {
			return amount * (1 + interestRate) * recursionFutureValue(1, years - 1, interestRate);
		}
		
	}

	public static double futureValue(double presentValue, double interestRate, int term) {
		double futureVal = presentValue * Math.pow(1 + interestRate, term);
		
		return futureVal;
	}
	
	// add transaction to an account
	// -- needed to be fixed, use instanceof, calling bankaccount.withdraw, deposit..etc
	public static boolean processTransaction(Transaction transaction) throws NegativeAmountException, ExceedsFraudSuspicionLimitException, 
	ExceedsAvailableBalanceException {
		double amount = transaction.getAmount();
		BankAccount source = transaction.getSourceAccount();
		BankAccount target = transaction.getTargetAccount();
		
		// if amount > 1000, add to fraud queue
		if (Math.abs(transaction.getAmount()) > 1000) {
			MeritBank.fraudQueue.addTransaction(transaction);
			throw new ExceedsFraudSuspicionLimitException();
		}
		
		// if amount < 0
		if (transaction.getAmount() < 0) {
			throw new NegativeAmountException();
		}
		
		// deposit transaction
		if (transaction instanceof DepositTransaction) {
			
			// deposit money into account
			target.deposit(amount);
			
			// add transaction record
			target.addTransaction(transaction);
		} else if (transaction instanceof WithdrawTransaction) {
			// if withdraw amount larger than balance
			if (transaction.getAmount() + transaction.getTargetAccount().getBalance() < 0 ) {
				throw new ExceedsAvailableBalanceException();
			}
			
			// withdraw money
			target.withdraw(amount);
			
			// add transaction record
			transaction.getTargetAccount().addTransaction(transaction);
		} else if (transaction instanceof TransferTransaction) {
			// if transfer money more than source account balance
			if (source.getBalance() - amount  < 0) {
				throw new ExceedsAvailableBalanceException();
			}
			
			// withdraw money from source account
			source.withdraw(amount);
			
			// deposit money to target account
			target.deposit(amount);
			
			// add transaction record to both accounts
			transaction.getSourceAccount().addTransaction(transaction);
			transaction.getTargetAccount().addTransaction(transaction);
		}
		
		return true;
	}	
}