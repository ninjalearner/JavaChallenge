package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;

/*
 * Test class for TransactionController
 * 
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransactionControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private AccountsService accountsService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void prepareMockMvc() {
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

		// Reset the existing accounts before each test.
		accountsService.getAccountsRepository().clearAccounts();
	}
	
	/*
	 * Test case for testing the valid POST request for money transfer
	 * 
	 */
	@Test
	public void transfer() throws Exception{
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
			      .content("{\"accountId\":\"123\",\"balance\":10000}"));
		
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
			      .content("{\"accountId\":\"456\",\"balance\":10000}"));
		
		this.mockMvc.perform(post("/v1/transaction").contentType(MediaType.APPLICATION_JSON)
			      .content("{\"accountFrom\":\"123\",\"accountTo\":\"456\",\"amount\":\"5000\"}")).andExpect(status().isOk());
		
		Account account = accountsService.getAccount("123");
	    assertThat(account.getBalance()).isEqualByComparingTo("5000");
	    
	    account = accountsService.getAccount("456");
	    assertThat(account.getBalance()).isEqualByComparingTo("15000");
	}
	
	/*
	 * Test case to test the invalid account scenario
	 * 
	 */
	@Test
	public void transferAccountNotExist() throws Exception{
		this.mockMvc.perform(post("/v1/transaction").contentType(MediaType.APPLICATION_JSON)
			      .content("{\"accountFrom\":\"123\",\"accountTo\":\"456\",\"amount\":\"5000\"}")).andExpect(status().isBadRequest());;
	}
	
	/**
	 * Test case to test the insufficient balance exception
	 * 
	 * @throws Exception
	 */
	@Test
	public void transferInsufficientBalance() throws Exception{
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
			      .content("{\"accountId\":\"123\",\"balance\":10000}"));
		
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
			      .content("{\"accountId\":\"456\",\"balance\":10000}"));
		
		this.mockMvc.perform(post("/v1/transaction").contentType(MediaType.APPLICATION_JSON)
			      .content("{\"accountFrom\":\"123\",\"accountTo\":\"456\",\"amount\":\"50000\"}")).andExpect(status().isBadRequest());;
		
	}
}
