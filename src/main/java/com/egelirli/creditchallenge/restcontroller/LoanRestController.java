package com.egelirli.creditchallenge.restcontroller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.egelirli.creditchallenge.dto.LoanRequestDto;
import com.egelirli.creditchallenge.dto.LoanRequestReplyDto;
import com.egelirli.creditchallenge.dto.PayLoanRequestDto;
import com.egelirli.creditchallenge.dto.PaymentResponse;
import com.egelirli.creditchallenge.entity.Loan;
import com.egelirli.creditchallenge.exception.NotAuthorizedException;
import com.egelirli.creditchallenge.exception.ResourceNotFoundException;
import com.egelirli.creditchallenge.security.SecurityConfiguration;
import com.egelirli.creditchallenge.service.LoanService;

@RestController
public class LoanRestController {
	
	private Logger logger = LoggerFactory.getLogger(LoanRestController.class);
	
	private LoanService loanService;
	
	private SecurityConfiguration securityConfig;
	
	public LoanRestController(LoanService loanService, 
							  SecurityConfiguration securityConfig) {
		this.loanService = loanService;
		this.securityConfig = securityConfig;
	}

	@PostMapping("/loans")
	public LoanRequestReplyDto requestLoan(
						@RequestBody LoanRequestDto addLoanDto,
						@AuthenticationPrincipal UserDetails userDetails)
							 throws ResourceNotFoundException, NotAuthorizedException {
		
		
		StringBuilder msg = new  StringBuilder();
		
		logger.info("In requestLoan - userDetails : {} customerId : {}",
					userDetails,addLoanDto.getCustomerId());
		
		
		securityConfig.checkUserAthorized(userDetails, addLoanDto.getCustomerId());
		
		//BigDecimal loanAmountBig = new  BigDecimal(loanAmount);
		Loan loan = 
			   loanService.addLoan(
					   addLoanDto.getCustomerId(),
					   addLoanDto.getLoanAmount(), 
					   addLoanDto.getInterestRate() ,
					   addLoanDto.getNumOfInstallments(),
					   msg );
		if(loan == null) {
			logger.warn(msg.toString());
		}
		
		LoanRequestReplyDto reply = new LoanRequestReplyDto();
		reply.setLoan(loan);
		reply.setReplyMessage(msg.toString());
		
		return reply;
	}
	

	@PostMapping("/loans/pay")
	public PaymentResponse payLoan(
							@RequestBody PayLoanRequestDto payLoanDto,
							@AuthenticationPrincipal UserDetails userDetails)
					 throws ResourceNotFoundException, NotAuthorizedException {

	
		securityConfig.checkUserAthorized(userDetails, payLoanDto.getCustomerId());
		return  loanService.payLoanInstallment(payLoanDto);

//		return  loanService.payLoanInstallment(
//					   payLoanDto.getCustomerId(),
//					   payLoanDto.getLoanId(), 
//					   payLoanDto.getPaymentAmount() );
			
	}
	
	
	@GetMapping("/loans/{customerId}")
	List<Loan> getLoansForCustomer(@PathVariable Long customerId,
								   @AuthenticationPrincipal UserDetails userDetails) 
			throws ResourceNotFoundException, NotAuthorizedException {
	    
		securityConfig.checkUserAthorized(userDetails, customerId);
		return loanService.getLoanlistForCustomer(customerId);
	}
	
	
	
}
