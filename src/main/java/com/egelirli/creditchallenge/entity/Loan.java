package com.egelirli.creditchallenge.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Loan {

    @Id
    @GeneratedValue
    private Long loanId;
    
    
    private BigDecimal loanAmount;
    private Integer numberOfInstallment;
    private Date createDate;
    private Boolean isPaid;
   
    //private Long customerId;
    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private Customer customer;
    
    
    @OneToMany(mappedBy = "loan")
    private List<LoanInstallment> installmentList;

    
    
//	public Long getCustomerId() {
//		return customerId;
//	}
//
//	public void setCustomerId(Long customerId) {
//		this.customerId = customerId;
//	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(BigDecimal loanAmount) {
		this.loanAmount = loanAmount;
	}

	public Integer getNumberOfInstallment() {
		return numberOfInstallment;
	}

	public void setNumberOfInstallment(Integer numberOfInstallment) {
		this.numberOfInstallment = numberOfInstallment;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}

	public List<LoanInstallment> getInstallmentList() {
		return installmentList;
	}

	public void setInstallmentList(List<LoanInstallment> installmentList) {
		this.installmentList = installmentList;
	}

	public Long getLoanId() {
		return loanId;
	}

	@Override
	public String toString() {
		return "Loan [loanId=" + loanId + ", customerId=" + customer.getId() + ", loanAmount=" + loanAmount
				+ ", numberOfInstallement=" + numberOfInstallment + ", createDate=" + createDate + ", isPaid=" + isPaid
				+ "]";
	}  
    
	
	
}
