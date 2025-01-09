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
import com.egelirli.creditchallenge.exception.ResourceNotFoundException;
import com.egelirli.creditchallenge.service.LoanService;

@RestController
public class LoanRestController {
	
	private Logger logger = LoggerFactory.getLogger(LoanRestController.class);
	
	private LoanService loanService;
	
	
	public LoanRestController(LoanService loanService) {
		this.loanService = loanService;
	}

	@PostMapping("/loans")
	public LoanRequestReplyDto requestLoan(@RequestBody LoanRequestDto addLoanDto,
						@AuthenticationPrincipal UserDetails userDetails)
						    		 throws ResourceNotFoundException {
		
		
		StringBuilder msg = new  StringBuilder();
		
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
	public PaymentResponse addLoan(@RequestBody PayLoanRequestDto payLoanDto)
						    		 throws ResourceNotFoundException {

			return  loanService.payLoanInstallment(
					   payLoanDto.getCustomerId(),
					   payLoanDto.getLoanId(), 
					   payLoanDto.getPaymentAmount() );
			
	}
	
	
	@GetMapping("/loans/{customerId}")
	List<Loan> getLoansForCustomer(@PathVariable Long customerId) 
			throws ResourceNotFoundException {
	    
		return loanService.getLoanlistForCustomer(customerId);
	}
	
	
	
}
