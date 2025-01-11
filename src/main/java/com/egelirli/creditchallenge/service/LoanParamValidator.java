package com.egelirli.creditchallenge.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.egelirli.creditchallenge.config.LoanServiceConfig;
import com.egelirli.creditchallenge.dto.LoanRequestDto;
import com.egelirli.creditchallenge.entity.Customer;
import com.egelirli.creditchallenge.exception.ResourceNotFoundException;
import com.egelirli.creditchallenge.repository.CustomerRepository;

@Component
public class LoanParamValidator {
	private Logger logger = LoggerFactory.getLogger(LoanParamValidator.class);
	
	private CustomerRepository customerRepo;
	
	private  LoanServiceConfig config; 
	
	
	private LoanParamValidator(CustomerRepository customerRepo, 
							   LoanServiceConfig config) {
		
		this.customerRepo = customerRepo;
		this.config = config;
				
	}

	public boolean validateLoanParams(
			LoanRequestDto requestLoanDto, StringBuilder returnMessage) 
										throws ResourceNotFoundException {
		
//	public boolean validateLoanParams(
//						Long customerId, BigDecimal loanAmount, float interestRate,
//						int numOfInstallements, StringBuilder returnMessage) 
//												throws ResourceNotFoundException {

		logger.debug(
				"In validateParams - requestLoanDto : {} ",requestLoanDto.toString());

		if (returnMessage == null) {
			throw new IllegalArgumentException("returnMessage == null");
		}

		Long customerId = requestLoanDto.getCustomerId(); 
		
		Optional<Customer> customerOpt = customerRepo.findById(customerId);
		if (customerOpt.isEmpty()) {
			String msg = String.format("Customer (Id:  %s)  not exists!", customerId);
			logger.warn("In validateParams - Customer({}) not found! ", customerId);
			returnMessage.append(msg);
			//return false;
			throw new ResourceNotFoundException(msg); 
		}
		
		Customer customer = customerOpt.get();

		BigDecimal loanAmount = requestLoanDto.getLoanAmount(); 
		if (loanAmount.compareTo(customer.getCreditLimit().
						subtract(customer.getUsedCreditLimit())) > 0) {
			String msg = String.format("Customer(%s) does not have enough limit - "
					+ "Loan amount : %s!", customerId,loanAmount);
			logger.warn("In validateParams - Customer({}) not found! ", customerId);
			returnMessage.append(msg);
			return false;
			//throw new ResourceNotFoundException(msg);
		}

		Float interestRate = requestLoanDto.getInterestRate();
		if (interestRate < config.getMinInterestRate() 
					|| interestRate > config.getMaxInterestRate()) {
			String msg = String.format("Interest rate must between {%s} "
				+ "and {%s} ", config.getMinInterestRate(),	config.getMaxInterestRate());
			logger.warn("In validateParams - Interest rate {} is not in "
					+ "allowed range ({} - {})! ", interestRate,
						config.getMinInterestRate(), config.getMaxInterestRate());
			returnMessage.append(msg);
			return false;

		}

		Integer numOfInstallments = requestLoanDto.getNumOfInstallments();
		if (!config.getNumberOfInstallements().contains(numOfInstallments)) {
			String msg = String.format("Number Of installments must be one of %s ",
													config.getNumberOfInstallements());
			logger.warn("In validateParams - numOfInstallements is not allowed {} ! ", 
					numOfInstallments);
			returnMessage.append(msg);
			return false;

		}

		return true;
	}


}
