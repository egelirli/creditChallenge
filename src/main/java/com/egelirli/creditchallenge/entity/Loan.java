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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Loan {

	@Setter(AccessLevel.NONE)
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

	@Override
	public String toString() {
		return "Loan [loanId=" + loanId + ", customerId=" + customer.getId() + ", loanAmount=" + loanAmount
				+ ", numberOfInstallement=" + numberOfInstallment + ", createDate=" + createDate + ", isPaid=" + isPaid
				+ "]";
	}  
    
	
	
}
