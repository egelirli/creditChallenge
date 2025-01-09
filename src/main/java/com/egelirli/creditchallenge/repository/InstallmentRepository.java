package com.egelirli.creditchallenge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.egelirli.creditchallenge.entity.LoanInstallment;

public interface InstallmentRepository extends JpaRepository<LoanInstallment, Long> {
	List<LoanInstallment> findByLoanLoanId(Long loanId);
	List<LoanInstallment> findByLoanLoanIdAndIsPaidOrderByDueDate(Long loanId, boolean isPaid);
}
