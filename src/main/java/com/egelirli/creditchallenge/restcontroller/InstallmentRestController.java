package com.egelirli.creditchallenge.restcontroller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.egelirli.creditchallenge.entity.LoanInstallment;
import com.egelirli.creditchallenge.exception.NotAuthorizedException;
import com.egelirli.creditchallenge.exception.ResourceNotFoundException;
import com.egelirli.creditchallenge.security.SecurityConfiguration;
import com.egelirli.creditchallenge.service.InstallmentService;

@RestController
public class InstallmentRestController {
	private Logger logger = LoggerFactory.getLogger(LoanRestController.class);
	
	private InstallmentService instService;
	
	private SecurityConfiguration securityConfig;
	
	public InstallmentRestController(
					InstallmentService instService,
					SecurityConfiguration securityConfig) {
		this.instService = instService;
		
		this.securityConfig = securityConfig;
	}
	
	@GetMapping("/installments/{loanId}")
	List<LoanInstallment> getLoansForCustomer(
						@PathVariable Long loanId,
						@AuthenticationPrincipal UserDetails userDetails) 
			throws ResourceNotFoundException, NotAuthorizedException {
	    logger.debug("In getLoansForCustomer - loanId : {}", loanId);
	    securityConfig.checkUserAthorized(userDetails, loanId);
		return instService.getInstallmentlistForLoan(loanId);
	}
	
	
}
