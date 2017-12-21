package com.db.awmd.challenge.service;

import java.math.BigDecimal;
import java.util.Random;

import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountNotExistException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;

/**
 * 
 * Service class that handles all the business logic for the transactions.
 * 
 * @author nikhil.agrawal
 *
 */
@Service
public class TransactionService {

	@Getter
	private final AccountsService accountsService;

	@Getter
	private final NotificationService notificationService;

	@Autowired
	public TransactionService(AccountsService accountsService, NotificationService notificationService) {
		this.accountsService = accountsService;
		this.notificationService = notificationService;
	}

	/**
	 * This function takes care of transferring the money from one account to other. This is thread-safe method.
	 * 
	 * @param accountFrom - The source account from which money to be deducted
	 * @param accountTo - The target account where money to be transferred
	 * @param amount - Amount of money that should be transferred
	 * 
	 * @throws Exception
	 */
	public void transfer(String accountFrom, String accountTo, BigDecimal amount) throws Exception{
		Random number = new Random(123L);
		Account accountFromObj = accountsService.getAccount(accountFrom);
		Account accountToObj = accountsService.getAccount(accountTo);
		if(accountFromObj==null || accountToObj==null)
			throw new AccountNotExistException("Either from or to account is not valid");
		while (true) {
			if (accountFromObj.getLock().tryLock()) {
				try {
					if (accountToObj.getLock().tryLock()) {
						try {
							if (amount.compareTo(accountFromObj.getBalance()) == 1) {
								throw new InsufficientBalanceException(
										"Insufficient Balance");
							} else {
								BigDecimal deposit = accountToObj.getBalance().add(amount);
								BigDecimal withdraw = accountFromObj.getBalance().subtract(amount);
								accountFromObj.setBalance(withdraw);
								accountToObj.setBalance(deposit);
								accountsService.updateAccount(accountToObj);
								accountsService.updateAccount(accountFromObj);
								notificationService.notifyAboutTransfer(accountToObj, "Amount " + amount + " deposited in your account by " + accountFrom);
								notificationService.notifyAboutTransfer(accountFromObj, "Amount " + amount + " transferred to " + accountTo);
								break;
							}
						} finally {
							accountToObj.getLock().unlock();
						}
					}
				} finally {
					accountFromObj.getLock().unlock();
				}
			}
			int n = number.nextInt(1000);
			int TIME = 1000 + n; 
			Thread.sleep(TIME);
		}
	}
}
