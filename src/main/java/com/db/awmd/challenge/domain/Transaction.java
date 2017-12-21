package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class Transaction {

	@NotNull
	@NotEmpty
	private String accountFrom;
	
	@NotNull
	@NotEmpty
	private String accountTo;
	
	@NotNull
	@Min(value=0, message="Amount to transfer cannot be negative")
	private BigDecimal amount;

	@JsonCreator
	public Transaction(@JsonProperty("accountFrom") String accountFrom, 
			@JsonProperty("accountTo") String accountTo, 
			@JsonProperty("amount") BigDecimal amount) {
		this.accountFrom = accountFrom;
		this.accountTo = accountTo;
		this.amount = amount;
	}
	
}
