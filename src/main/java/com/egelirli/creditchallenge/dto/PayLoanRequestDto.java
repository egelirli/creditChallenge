package com.egelirli.creditchallenge.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
@Builder
public class PayLoanRequestDto {
	private long customerId;
	private long loanId;
	private BigDecimal paymentAmount;
	private LocalDate paymentDate;
	
	@Override
	public String toString() {
		return "PayLoanRequestDto [customerId=" + customerId + ", loanId=" + loanId + ", paymentAmount=" + paymentAmount
				+ ", paymentDate=" + paymentDate + "]";
	}
	
	
}
