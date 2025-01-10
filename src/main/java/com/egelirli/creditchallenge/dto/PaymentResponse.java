package com.egelirli.creditchallenge.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PaymentResponse {
	
	private  int numOfInstallmentsPaid = 0;
	private BigDecimal totalAmountSpent = new BigDecimal(0);
	private boolean isLoanPaidCompletely = false;
	
	
	@Override
	public String toString() {
		return "PaymentResponse [numOfInstallmentsPaid=" + numOfInstallmentsPaid + ", totalAmountSpent="
				+ totalAmountSpent + ", isLoanPaidCompletely=" + isLoanPaidCompletely + "]";
	}
	
	
	
}
