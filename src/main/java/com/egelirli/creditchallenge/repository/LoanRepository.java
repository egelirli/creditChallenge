package com.egelirli.creditchallenge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.egelirli.creditchallenge.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan,Long>{
	List<Loan> findByCustomerId(Long customerId);

	List<Loan> findByCustomerIdAndIsPaid(Long customerId, boolean paidStatus);
}
