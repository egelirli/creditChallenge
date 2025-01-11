package com.egelirli.creditchallenge.service;

import static org.junit.Assert.assertTrue;
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
import com.egelirli.creditchallenge.dto.PayLoanRequestDto;
import com.egelirli.creditchallenge.dto.PaymentResponse;
import com.egelirli.creditchallenge.entity.Customer;
import com.egelirli.creditchallenge.entity.Loan;
import com.egelirli.creditchallenge.exception.ResourceNotFoundException;

@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class LoanServiceUnitTest {
	private static Logger logger = 
			LoggerFactory.getLogger(LoanServiceUnitTest.class);
	

	@Autowired
	private LoanService loanService;

	@Autowired
	private CustomerService customerService;
	
	
	@BeforeAll
	public static void executeBeforeAll() {
		System.out.println("******* IN executeBeforeAll ****");
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
			BigDecimal loanAmount = new BigDecimal("10000");
			
			LoanRequestDto  loanReqDto = LoanRequestDto.builder().
					customerId(customerId).loanAmount(loanAmount).
					interestRate(0.2f).numOfInstallments(6).
					build();
			ret = loanService.requestLoan(loanReqDto, retMsg);
			//ret = loanService.requestLoan(customerId, loanAmount  , 0.2f, 6, retMsg);
			logger.debug("In testAddLoanWithValidValues - retMsg : {}", retMsg);
			
			Customer customerMod = customerService.findCustomer(customerId);
			logger.debug("In testAddLoanWithValidValues -  after loan - "
					+ "customer : {}  ",customerMod );
			
			assertTrue(ret != null);
			
			//assertTrue((customerMod.getCreditLimit()).equals(creditLimitOrg.subtract(loanAmount)));
		} catch (ResourceNotFoundException e) {
			fail(e.getMessage());
		}
		
	}

	@Test
	public void testWithInterestRateAboveRange() {
		
		long customerId = 1;
		logger.debug("In testWithInterestRateAboveRange");		
		StringBuilder retMsg = new StringBuilder();
		Loan ret;
		try {
			LoanRequestDto  loanReqDto = LoanRequestDto.builder().
					customerId(customerId).loanAmount(new BigDecimal("10000")).
					interestRate(0.8f).numOfInstallments(6).
					build();
			ret = loanService.requestLoan(loanReqDto, retMsg);
			logger.debug("In testWithInterestRateAboveRange retMsg: {}",retMsg);
			assertTrue(ret == null);
		} catch (ResourceNotFoundException e) {
			fail(e.getMessage());
		}
		
	}

	@Test
	public void testWithCustomerIdInvalid() {
		
		long customerId = 3;
		
		StringBuilder retMsg = new StringBuilder();
		Loan ret;
		try {
			LoanRequestDto  loanReqDto = LoanRequestDto.builder().
					customerId(customerId).loanAmount(new BigDecimal("10000")).
					interestRate(0.2f).numOfInstallments(6).
					build();
			ret = loanService.requestLoan(loanReqDto, retMsg);
			logger.debug("In testWithCustomerIdInvalid - retMsg : {}", retMsg.toString());
			assertTrue(ret == null);
		} catch (ResourceNotFoundException e) {
			assertTrue(true);
		}
		
	}
	

	@Test
	public void testGetListOfLoans() {
		
		logger.debug("In testGetListOfLoan ");
		long customerId = 1;
		
		
		try {
			StringBuilder retMsg = new StringBuilder();
			LoanRequestDto  loanReqDto = LoanRequestDto.builder().
					customerId(customerId).loanAmount(new BigDecimal("10000")).
					interestRate(0.2f).numOfInstallments(6).
					build();
			loanService.requestLoan(loanReqDto, retMsg);
			//loanService.requestLoan(customerId, new BigDecimal("10000") , 0.2f, 6, retMsg);

			LoanRequestDto  loanReqDto2 = LoanRequestDto.builder().
					customerId(customerId).loanAmount(new BigDecimal("20000")).
					interestRate(0.3f).numOfInstallments(9).
					build();
			loanService.requestLoan(loanReqDto2, retMsg);
			List<Loan> list = loanService.getLoanlistForCustomer(customerId);
			logger.debug("In testGetListOfLoan - list : {}", list);
			assert(list.size() == 2);
		} catch (ResourceNotFoundException e) {
			fail(e.getMessage()); 
		}
		
	}

	@Test
	public void testGetLoanlistForCustomerWithPaidStatus() {
		
		logger.debug("In testGetLoanlistForCustomerWithPaidStatus ");
		long customerId = 1;
		
		StringBuilder retMsg = new StringBuilder();
		
		try {
			LoanRequestDto  loanReqDto = LoanRequestDto.builder().
					customerId(customerId).loanAmount(new BigDecimal("10000")).
					interestRate(0.2f).numOfInstallments(6).
					build();
			loanService.requestLoan(loanReqDto, retMsg);
			
			
			LoanRequestDto  loanReqDto2 = LoanRequestDto.builder().
					customerId(customerId).loanAmount(new BigDecimal("20000")).
					interestRate(0.3f).numOfInstallments(9).
					build();
			loanService.requestLoan(loanReqDto2, retMsg);
			List<Loan> list = loanService.getLoanlistForCustomerWithPaidStatus(customerId, false);
			logger.debug("In testGetListOfLoan - list : {}", list);
			assert(list.size() == 2);
			List<Loan> list2 = loanService.getLoanlistForCustomerWithPaidStatus(customerId, true);
			assert(list2.size() == 0);

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
			
			LoanRequestDto  loanReqDto = LoanRequestDto.builder().
					customerId(customerId).loanAmount(new BigDecimal("10000")).
					interestRate(0.2f).numOfInstallments(6).
					build();
			Loan loan = loanService.requestLoan(loanReqDto, retMsg);
			PayLoanRequestDto payLoanDto =  PayLoanRequestDto.builder().
					customerId(customerId).
					loanId(loan.getLoanId()).
					paymentAmount(new BigDecimal("5000")).
					paymentDate(null).
					build();

			
			PaymentResponse response = 
					loanService.payLoanInstallment(payLoanDto );
			logger.debug("In testPayLoan - response : {}", response);
			assert(response.getNumOfInstallmentsPaid() == 2);
			
			PaymentResponse response2 = 
					loanService.payLoanInstallment(payLoanDto );
			logger.debug("In testPayLoan - response2 : {}", response2);
			assert(response2.getNumOfInstallmentsPaid() == 1);
			assert(!response.isLoanPaidCompletely());
			
		} catch (ResourceNotFoundException e) {
			fail(e.getMessage()); 
		}
		
	}
	
	
	

}
