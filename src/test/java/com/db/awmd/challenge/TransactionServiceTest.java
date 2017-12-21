package com.db.awmd.challenge;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.TransactionService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest {

	@Autowired
	private AccountsService accountsService;

	@Autowired
	private TransactionService transactionService;


	@Test
	public void transfer() throws InterruptedException{
		Account account1 = new Account("123");
		account1.setBalance(new BigDecimal(10000));
		this.accountsService.createAccount(account1);

		Account account2 = new Account("456");
		account2.setBalance(new BigDecimal(10000));
		this.accountsService.createAccount(account2);

		Account account3 = new Account("789");
		account3.setBalance(new BigDecimal(10000));
		this.accountsService.createAccount(account3);


		Callable<Void> transfer1 = createTask(transactionService, "123", "456", new BigDecimal("1000"));
		Callable<Void> transfer2 = createTask(transactionService, "456", "789", new BigDecimal("1000"));
		Callable<Void> transfer3 = createTask(transactionService, "789", "456", new BigDecimal("1000"));
		List<Callable<Void>> tasks1 = Collections.nCopies(5, transfer1);
		List<Callable<Void>> tasks2 = Collections.nCopies(5, transfer2);
		List<Callable<Void>> tasks3 = Collections.nCopies(5, transfer3);

		List<Callable<Void>> tasks = new ArrayList<>();
		tasks.addAll(tasks1);
		tasks.addAll(tasks2);
		tasks.addAll(tasks3);

		ExecutorService executorService = Executors.newFixedThreadPool(15);
		List<Future<Void>> futures = executorService.invokeAll(tasks);

		assertThat(this.accountsService.getAccount("123").getBalance()).isEqualByComparingTo(new BigDecimal("5000"));
		assertThat(this.accountsService.getAccount("456").getBalance()).isEqualByComparingTo(new BigDecimal("15000"));
		assertThat(this.accountsService.getAccount("789").getBalance()).isEqualByComparingTo(new BigDecimal("10000"));

	}

	private Callable<Void> createTask(final TransactionService service, String from, String to, BigDecimal amount){
		Callable<Void> task = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				service.transfer(from, to, amount);
				return null;
			}
		};
		return task;
	}
}
