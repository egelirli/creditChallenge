package com.egelirli.creditchallenge.service;

import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.egelirli.creditchallenge.dto.LoanRequestDto;
import com.egelirli.creditchallenge.entity.Customer;
import com.egelirli.creditchallenge.entity.Loan;
import com.egelirli.creditchallenge.entity.LoanInstallment;
import com.egelirli.creditchallenge.exception.ResourceNotFoundException;

@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class InstallmentServiceUnitTest {
	private static Logger logger = 
			LoggerFactory.getLogger(InstallmentServiceUnitTest.class);

	@Autowired
	private LoanService loanService;
	
	@Autowired
	private InstallmentService installmentService;

	@Autowired
	private CustomerService customerService;
	
	@BeforeAll
	public static void executeBeforeAll() {
	     logger.debug("In onceExecutedBeforeAll");
		
	}
	
	@BeforeEach
	public void beforeEachMethod() {
		logger.debug("In beforeEachMethod");
		loanService.deleteAll();
		customerService.deleteAllCustomers();
		addCustomers();
	}
	
	
	private void addCustomers() {
		Customer customer = new Customer();
		customer.setId(1L);
		customer.setName("Ali");
		customer.setSurname("Kaya");
		customer.setCreditLimit(new BigDecimal(100000.0));
		customer.setUsedCreditLimit(new BigDecimal(0));
		customerService.addCustomer(customer);

		Customer customer2 = new Customer();
		customer2.setId(2L);
		customer2.setName("Ahmet");
		customer2.setSurname("Ay");
		customer2.setCreditLimit(new BigDecimal(200000.0));
		customer2.setUsedCreditLimit(new BigDecimal(0));
		customerService.addCustomer(customer2);
		
	}

	@Test
	public void testGetListOfInstallements() {
		
		logger.error("In testGetListOfInstallements ");
		
		long customerId = 1;
		StringBuilder retMsg = new StringBuilder();
		
		try {
			LoanRequestDto  loanReqDto = LoanRequestDto.builder().
					customerId(customerId).loanAmount(new BigDecimal("10000")).
					interestRate(0.2f).numOfInstallments(6).
					build();
			Loan loan = loanService.requestLoan(loanReqDto, retMsg);			
			if(loan != null) {
				List<LoanInstallment> list = installmentService.getInstallmentlistForLoan(loan.getLoanId());
				logger.debug("In testGetListOfLoanInstallments - list(size: {}) : {}",list.size(), list);
				assert(list.size() == 6);
			}else {
				fail("Could not add loan");
			}
		} catch (ResourceNotFoundException e) {
			fail("Exception : " + e.getMessage()); 
		}
		
	}
	
	@Test
	public void testGetInstallmentlistForLoanWithPaidStatus() {
		
		logger.error("In testGetInstallmentlistForLoanWithPaidStatus ");
		
		long customerId = 1;
		StringBuilder retMsg = new StringBuilder();
		
		try {
			LoanRequestDto  loanReqDto = LoanRequestDto.builder().
					customerId(customerId).loanAmount(new BigDecimal("10000")).
					interestRate(0.2f).numOfInstallments(6).
					build();
			Loan loan =  loanService.requestLoan(loanReqDto, retMsg);
			if(loan != null) {
				List<LoanInstallment> list = installmentService.
						getInstallmentlistForLoanWithPaidStatus(loan.getLoanId(),false);
				logger.debug("In testGetInstallmentlistForLoanWithPaidStatus - "
											+ "list(size: {}) : {}",list.size(), list);
				assert(list.size() == 6);
			}else {
				fail("Could not add loan");
			}
		} catch (ResourceNotFoundException e) {
			fail("Exception : " + e.getMessage()); 
		}
		
	}
	
	
	
}
