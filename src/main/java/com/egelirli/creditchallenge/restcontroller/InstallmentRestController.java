package com.egelirli.creditchallenge.restcontroller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.egelirli.creditchallenge.entity.LoanInstallment;
import com.egelirli.creditchallenge.exception.ResourceNotFoundException;
import com.egelirli.creditchallenge.service.InstallmentService;

@RestController
public class InstallmentRestController {
	private Logger logger = LoggerFactory.getLogger(LoanRestController.class);
	
	private InstallmentService instService;
	
	
	public InstallmentRestController(InstallmentService instService) {
		this.instService = instService;
	}
	
	@GetMapping("/installments/{loanId}")
	List<LoanInstallment> getLoansForCustomer(@PathVariable Long loanId) 
			throws ResourceNotFoundException {
	    logger.debug("In getLoansForCustomer - loanId : {}", loanId);
		return instService.getInstallmentlistForLoan(loanId);
	}
	
	
}
