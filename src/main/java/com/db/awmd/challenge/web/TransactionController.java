package com.db.awmd.challenge.web;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.Transaction;
import com.db.awmd.challenge.exception.AccountNotExistException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.service.TransactionService;

/**
 * 
 * Controller for the transaction that can be done. Currently this supports the transfer money option only
 * 
 * @author nikhil.agrawal
 *
 */
@RestController
@RequestMapping("/v1/transaction")
@Slf4j
public class TransactionController {

	private final TransactionService transactionService;

	@Autowired
	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	/**
	 * POST request method for transferring the money between two accounts. 
	 * 
	 * @param transaction
	 * @return OK - successful transfer, BAD REQUEST - insufficient balance or account does not exist
	 * 
	 */
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> transfer(@RequestBody @Valid Transaction transaction) {
		log.info("Executing transaction {}", transaction);
		try {
			this.transactionService.transfer(transaction.getAccountFrom(), transaction.getAccountTo(), transaction.getAmount());
		} catch(AccountNotExistException | InsufficientBalanceException e){
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
