package com.egelirli.creditchallenge.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class PayLoanRequestDto {
	private long customerId;
	private long loanId;
	private BigDecimal paymentAmount;
}
