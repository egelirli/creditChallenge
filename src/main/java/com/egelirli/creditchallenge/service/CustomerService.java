package com.egelirli.creditchallenge.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.egelirli.creditchallenge.entity.Customer;
import com.egelirli.creditchallenge.entity.Loan;
import com.egelirli.creditchallenge.exception.ResourceNotFoundException;
import com.egelirli.creditchallenge.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

//@RequiredArgsConstructor
@Service
@Transactional
public class CustomerService {
	private Logger logger = LoggerFactory.getLogger(CustomerService.class);

	private CustomerRepository customerRepo;

	public CustomerService(CustomerRepository customerRepo) {
		this.customerRepo = customerRepo;
	}
	
	public boolean addCustomer(Customer customer) {

		logger.debug("In addCustomer - customer {}", customer);
		customerRepo.save(customer);
		return true;
		
	}
	
	public void deleteAllCustomers() {

		logger.debug("In deleteAllCustomers");
		customerRepo.deleteAll();
	}
	
	
	public void processLoanAdded(
			Long customerId, BigDecimal loanAmount) throws ResourceNotFoundException {
		
		logger.debug("In processLoanAdded - customerId: {} loanAmount : {}", customerId, loanAmount);

		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException(" Customer Not Found " + customerId));
		
		logger.debug("In processLoanAdded -  customer: {} ", customer);
		//customer.setCreditLimit((customer.getCreditLimit()).subtract(loanAmount));
		customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(loanAmount));
		customerRepo.save(customer);

		logger.debug("In processLoanAdded - modified customer: {} ", customer);

	}

	public void processInstallmentPaid(long customerId, 
									   Loan loan, 
									   int numberOfInstPaid) 
											   	throws ResourceNotFoundException {

		logger.debug("In processInstallmentPaid - customerId: {} loanAmount : {} "
				+ "numberOfInstPaid : {}", customerId, loan.getLoanAmount(),numberOfInstPaid);

		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException(" Customer Not Found " + customerId));

		
		BigDecimal paidPrincipalAmount = 
					loan.getLoanAmount().divide(
						 BigDecimal.valueOf(loan.getNumberOfInstallment()), RoundingMode.HALF_DOWN).
								multiply(BigDecimal.valueOf(numberOfInstPaid));
		
		
		customer.setUsedCreditLimit(customer.getUsedCreditLimit().subtract(paidPrincipalAmount));
		customerRepo.save(customer);

		logger.debug("In processInstallmentPaid - modified customer: {} ", customer);

	}

	public Customer findCustomer(long customerId) throws ResourceNotFoundException {
		return  customerRepo.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException(" Customer Not Found " + customerId));
	}

}
