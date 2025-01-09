package com.egelirli.creditchallenge.entity;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;


@Entity
public class Customer {
    
    //@GeneratedValue
	@Id
    private Long id;

	private String name;
    private String surname;
    private BigDecimal creditLimit;
    private BigDecimal usedCreditLimit;
    
    
    @OneToMany(mappedBy = "customer")
    private List<Loan> loanList;
  
 
	public Long getId() {
		return id;
	}
 
    public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public BigDecimal getCreditLimit() {
		return creditLimit;
	}
	public void setCreditLimit(BigDecimal creditLimit) {
		this.creditLimit = creditLimit;
	}
	public BigDecimal getUsedCreditLimit() {
		return usedCreditLimit;
	}
	public void setUsedCreditLimit(BigDecimal usedCreditLimit) {
		this.usedCreditLimit = usedCreditLimit;
	}

	///////////////////////////////
	// Loans
	///////////////////////////////
	public List<Loan> getLoanList() {
		return loanList;
	}

	public void setLoanList(List<Loan> loanList) {
		this.loanList = loanList;
	}
	
	
	
	@Override
	public String toString() {
		return "Customer [id=" + id + ", name=" + name + ", surname=" + surname + ", creditLimit=" + creditLimit
				+ ", usedCreditLimit=" + usedCreditLimit + "]";
	}


	
    
   	
}
