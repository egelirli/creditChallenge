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
import com.egelirli.creditchallenge.security.UserAuthorizer;
import com.egelirli.creditchallenge.service.LoanService;

@RestController
public class LoanRestController {
	
	private Logger logger = LoggerFactory.getLogger(LoanRestController.class);
	
	private LoanService loanService;
	
	private UserAuthorizer userAuthorizer;
	
	public LoanRestController(LoanService loanService, 
			UserAuthorizer userAuthorizer) {
		this.loanService = loanService;
		this.userAuthorizer = userAuthorizer;
	}

	@PostMapping("/loans/request")
	public LoanRequestReplyDto requestLoan(
						@RequestBody LoanRequestDto requestLoanDto,
						@AuthenticationPrincipal UserDetails userDetails)
							 throws ResourceNotFoundException, NotAuthorizedException {
		
		
		StringBuilder msg = new  StringBuilder();
		
		logger.info("In requestLoan - userDetails : {} customerId : {}",
					userDetails,requestLoanDto.getCustomerId());
		
		
		userAuthorizer.checkIfUserAuthorized(userDetails, requestLoanDto.getCustomerId());
		
		Loan loan = 
			   loanService.requestLoan(requestLoanDto, msg);
		if(loan == null) {
			logger.warn("In requestLoan - loan == null! msg : {} ", msg.toString());
		}
		
		LoanRequestReplyDto reply = new LoanRequestReplyDto();
		reply.setLoan(loan);
		reply.setReplyMessage(msg.toString());
		
		logger.info("In requestLoan - reply : {} ",reply);
		
		return reply;
	}
	

	@PostMapping("/loans/pay")
	public PaymentResponse payLoan(
							@RequestBody PayLoanRequestDto payLoanDto,
							@AuthenticationPrincipal UserDetails userDetails)
					 throws ResourceNotFoundException, NotAuthorizedException {

	
		userAuthorizer.checkIfUserAuthorized(userDetails, payLoanDto.getCustomerId());
		return  loanService.payLoanInstallment(payLoanDto);

	}
	
	
	@GetMapping("/loans/list/{customerId}")
	List<Loan> getLoansForCustomer(@PathVariable Long customerId,
								   @AuthenticationPrincipal UserDetails userDetails) 
			throws ResourceNotFoundException, NotAuthorizedException {
	    
		userAuthorizer.checkIfUserAuthorized(userDetails, customerId);
		return loanService.getLoanlistForCustomer(customerId);
	}
	
	
	
}
