package com.egelirli.creditchallenge.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.egelirli.creditchallenge.entity.Loan;
import com.egelirli.creditchallenge.entity.LoanInstallment;
import com.egelirli.creditchallenge.exception.ResourceNotFoundException;
import com.egelirli.creditchallenge.repository.InstallmentRepository;
import com.egelirli.creditchallenge.repository.LoanRepository;
import com.egelirli.creditchallenge.util.FirstBusinessDayFinder;

@Service
@Transactional
public class InstallmentService {
	private Logger logger = LoggerFactory.getLogger(InstallmentService.class);

	private InstallmentRepository installRepo;
	
	private LoanRepository loanRepo;

	public InstallmentService(
			LoanRepository loanRepo,
			InstallmentRepository installRepo) {

		this.installRepo = installRepo;
		this.loanRepo = loanRepo;
	}

	public List<LoanInstallment> createInstallments(Loan loan, BigDecimal totalAmount, int numOfInstallements) {
		logger.debug("In createInstallments - totalAmount {} " + "numOfInstallements : {}", totalAmount,
				numOfInstallements);
		
		
		BigDecimal monthlyPayment = totalAmount.divide(
				      BigDecimal.valueOf(numOfInstallements), RoundingMode.HALF_DOWN);

		LocalDate today = LocalDate.now();
		ArrayList<LoanInstallment> retList = new  ArrayList<LoanInstallment>();
		for (int i = 1; i <= numOfInstallements; i++) {
			LocalDate dueDate = FirstBusinessDayFinder.getFirstBusinessDay(
												today.plusMonths(i).getYear(),
												today.plusMonths(i).getMonthValue());

			LoanInstallment installment = new LoanInstallment();
			installment.setLoan(loan);
			installment.setAmount(monthlyPayment);
			installment.setPaidAmount(BigDecimal.valueOf(0));
			installment.setDueDate(dueDate);
			installment.setIsPaid(false);
			installRepo.save(installment);
			retList.add(installment);
			logger.debug(
					"In createInstallments - created installment totalAmount {} "
							+ "numOfInstallements : {} monthlyPayment : {} dueDate: {} ",
					totalAmount, numOfInstallements, monthlyPayment, dueDate);

		}
		
		return retList;
	}

	public List<LoanInstallment> getInstallmentlistForLoan(Long loanId) 
											throws ResourceNotFoundException {
		logger.debug("In getInstallmentlistForLoan - loanId: {}", loanId);

		loanRepo.findById(loanId)
				.orElseThrow(() -> new ResourceNotFoundException(" Loan Not Found: " + loanId));
		return this.installRepo.findByLoanLoanId(loanId);
	}
	
	
	public List<LoanInstallment> getInstallmentlistForLoanWithPaidStatus(
													Long loanId, boolean isPaid)
															throws ResourceNotFoundException {
		
		logger.debug("In getInstallmentlistForLoanWithPaidStatus - "
									+ "loanId: {} isPaid:{}", loanId,isPaid);

		loanRepo.findById(loanId)
				.orElseThrow(() -> new ResourceNotFoundException(" Loan Not Found: " + loanId));
		return this.installRepo.findByLoanLoanIdAndIsPaidOrderByDueDate(loanId,isPaid);
		
	}

	public void modifyLoanInstallmentAsPaid(
							LoanInstallment loanInstallment, 
							LocalDate paymentDate, 
							BigDecimal paidAmount) {

		logger.debug("In modifyLoanInstallmentAsPaid - Id: {} ", loanInstallment.getId() );
		
		loanInstallment.setPaidAmount(paidAmount);
		loanInstallment.setPaymentDate(paymentDate);
		loanInstallment.setIsPaid(true);
		
		installRepo.save(loanInstallment);
		
	}

}
