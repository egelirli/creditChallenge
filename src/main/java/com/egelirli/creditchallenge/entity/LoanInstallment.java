package com.egelirli.creditchallenge.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class LoanInstallment {

    @Id
    @GeneratedValue
    private Long id;
    
    //private Long loanId;
    private BigDecimal amount;
    
	private BigDecimal paidAmount;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private Boolean isPaid;
    
    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "loanId", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore 
	private Loan loan;
    
    public BigDecimal getAmount() {
		return amount;
	}


	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}


	public BigDecimal getPaidAmount() {
		return paidAmount;
	}


	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}


	public LocalDate getDueDate() {
		return dueDate;
	}


	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}


	public LocalDate getPaymentDate() {
		return paymentDate;
	}


	public void setPaymentDate(LocalDate paymentDate) {
		this.paymentDate = paymentDate;
	}


	public Boolean getIsPaid() {
		return isPaid;
	}


	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}


	public Loan getLoan() {
		return loan;
	}


	public void setLoan(Loan loan) {
		this.loan = loan;
	}


	public Long getId() {
		return id;
	}


	@Override
	public String toString() {
		return "LoanInstallment [id=" + id + ", amount=" + amount + ", paidAmount=" + paidAmount + ", dueDate="
				+ dueDate + ", paymentDate=" + paymentDate + ", isPaid=" + isPaid + "]";
	}


//	public Long getLoanId() {
//		return loanId;
//	}


    
	
}
