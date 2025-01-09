package com.egelirli.creditchallenge.dto;

import java.math.BigDecimal;

public class PaymentResponse {
	
	private  int numOfInstallmentsPaid = 0;
	private BigDecimal totalAmountSpent = new BigDecimal(0);
	private boolean isLoanPaidCompletely = false;
	
	
	public int getNumOfInstallmentsPaid() {
		return numOfInstallmentsPaid;
	}
	public void setNumOfInstallmentsPaid(int numOfInstallmentsPaid) {
		this.numOfInstallmentsPaid = numOfInstallmentsPaid;
	}
	public BigDecimal getTotalAmountSpent() {
		return totalAmountSpent;
	}
	public void setTotalAmountSpent(BigDecimal totalAmountSpent) {
		this.totalAmountSpent = totalAmountSpent;
	}
	public boolean isLoanPaidCompletely() {
		return isLoanPaidCompletely;
	}
	public void setLoanPaidCompletely(boolean isLoanPaidCompletely) {
		this.isLoanPaidCompletely = isLoanPaidCompletely;
	}
	@Override
	public String toString() {
		return "PaymentResponse [numOfInstallmentsPaid=" + numOfInstallmentsPaid + ", totalAmountSpent="
				+ totalAmountSpent + ", isLoanPaidCompletely=" + isLoanPaidCompletely + "]";
	}
	
	
	
}
