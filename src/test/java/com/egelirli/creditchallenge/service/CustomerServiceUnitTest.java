package com.egelirli.creditchallenge.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

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
import com.egelirli.creditchallenge.dto.PayLoanRequestDto;
import com.egelirli.creditchallenge.dto.PaymentResponse;
import com.egelirli.creditchallenge.entity.Customer;
import com.egelirli.creditchallenge.entity.Loan;
import com.egelirli.creditchallenge.exception.ResourceNotFoundException;

@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerServiceUnitTest {

	private static Logger logger = 
			LoggerFactory.getLogger(LoanServiceUnitTest.class);
	

	@Autowired
	private LoanService loanService;

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
	public void testAddLoanWithValidValues() {
		
		long customerId = 1;
		logger.debug("In testAddLoanWithValidValues");
		
		StringBuilder retMsg = new StringBuilder();
		Loan ret;
		try {
			Customer customer = customerService.findCustomer(customerId);
			BigDecimal usedCreditLimitOrg =  customer.getUsedCreditLimit();
			BigDecimal loanAmount = new BigDecimal("10000");
			logger.debug("In testAddLoanWithValidValues - "
					+ "customer : {}  loanAmount : {}",customer,loanAmount );
			
			
			LoanRequestDto  loanReqDto = LoanRequestDto.builder().
								customerId(customerId).loanAmount(loanAmount).
								interestRate(0.2f).numOfInstallments(6).
								build();
			ret = loanService.requestLoan(loanReqDto, retMsg);
			logger.debug("In testAddLoanWithValidValues - retMsg : {}", retMsg);
			
			Customer customerMod = customerService.findCustomer(customerId);
			logger.debug("In testAddLoanWithValidValues -  after loan - "
					+ "customer : {}  ",customerMod );
			
			assertTrue(ret != null);
			assertTrue((customerMod.getUsedCreditLimit()).
								equals(usedCreditLimitOrg.add(loanAmount)));
		} catch (ResourceNotFoundException e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testPayLoan() {
		
		logger.debug("In testPayLoan ");
		long customerId = 1;
		
		StringBuilder retMsg = new StringBuilder();
		try {
			
			BigDecimal loanAmount =  new BigDecimal("12000");
			int numOfInstallments = 6;
			BigDecimal installPrincipal = loanAmount.divide(BigDecimal.valueOf(numOfInstallments));
			
			
			LoanRequestDto  loanReqDto = LoanRequestDto.builder().
					customerId(customerId).loanAmount(loanAmount).
					interestRate(0.2f).numOfInstallments(numOfInstallments).
					build();
			Loan loan =  loanService.requestLoan(loanReqDto, retMsg);
			Customer customer = customerService.findCustomer(customerId);
			PayLoanRequestDto payLoanDto =  PayLoanRequestDto.builder().
													customerId(customerId).
													loanId(loan.getLoanId()).
													paymentAmount(new BigDecimal("5000")).
													paymentDate(null).
													build();
																		
			PaymentResponse response =loanService.payLoanInstallment(payLoanDto);

			logger.debug("In testPayLoan - response : {}", response);
			assertTrue(response.getNumOfInstallmentsPaid() == 2);
			Customer customerMod = customerService.findCustomer(customerId);
			assertTrue((customerMod.getUsedCreditLimit()).
					    equals(customer.getUsedCreditLimit().subtract
					    		(installPrincipal.multiply(
					    			  BigDecimal.valueOf(response.getNumOfInstallmentsPaid())))));
			
		} catch (ResourceNotFoundException e) {
			fail(e.getMessage()); 
		}
		
	}

	
}
