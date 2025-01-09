package com.egelirli.creditchallenge.dto;

import com.egelirli.creditchallenge.entity.Loan;

import lombok.Getter;
import lombok.Setter;


@Getter 
@Setter
public class LoanRequestReplyDto {
	private Loan loan;
	private String replyMessage;
	
}
