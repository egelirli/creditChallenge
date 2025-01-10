package com.egelirli.creditchallenge.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;


@Getter 
@Setter
public class LoanRequestDto {
	private Long customerId; 
	private BigDecimal loanAmount; 
	private Float interestRate;
	private Integer numOfInstallments;

	@Override
	public String toString() {
		return "LoanRequestDto [customerId=" + customerId + ", loanAmount=" + loanAmount + ", interestRate="
				+ interestRate + ", numOfInstallments=" + numOfInstallments + "]";
	}
	
	
}
