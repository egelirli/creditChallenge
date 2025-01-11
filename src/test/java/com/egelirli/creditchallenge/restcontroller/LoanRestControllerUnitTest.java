package com.egelirli.creditchallenge.restcontroller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.egelirli.creditchallenge.dto.LoanRequestDto;
import com.egelirli.creditchallenge.entity.Customer;
import com.egelirli.creditchallenge.entity.Loan;
import com.egelirli.creditchallenge.service.LoanService;

//@EnableAutoConfiguration
//@RunWith(SpringRunner.class)
//@WebMvcTest(controllers = LoanRestController.class)
//@AutoConfigureMockMvc(addFilters = false)

@SpringBootTest
@AutoConfigureMockMvc
public class LoanRestControllerUnitTest {
	
	private Logger logger = 
			LoggerFactory.getLogger(LoanRestControllerUnitTest.class);
	
	
	private static final long authCustomerId = 111;
	private static final String authCustomerPasswd = "111";
	private static final BigDecimal customerCreditLimit = new BigDecimal(100000);
	
	private static final long notAuthCustomerId = 222;
	private static final String notAuthCustomerPasswd = "222";
	
	private static final String LOANS_REQUEST_URL = 
			"http://localhost:8080/loans/request";

	private static final String LOANS_LIST_URL = 
			"http://localhost:8080/loans/list/111";
	
	private static final String LOAN_PAY_URL = 
			"http://localhost:8080/loans/pay";

	
	//@MockBean
	@MockitoBean
	private LoanService loanService;
	
	@Autowired
	private MockMvc mockMvc;
	
	//////////////////////////////////
	// Authorization Tests
	/////////////////////////////////
	@Test
	void loanListtAuthorization_403Scenario_NoUser() throws Exception {
		logger.debug("In loanRequestAuthorization_405Scenario_NoUser ");

	  RequestBuilder requestBuilder = 
				MockMvcRequestBuilders.get(LOANS_LIST_URL).
				                     accept(MediaType.APPLICATION_JSON);
		
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		assertEquals(403, mvcResult.getResponse().getStatus());
	}	

	@Test
	void loanRequestAuthorization_403Scenario_Customer222() throws Exception {
		logger.debug("In loanRequestAuthorization_405Scenario_Customer222 ");
		
		
		String requestBody = """
				{
				    "customerId": 111,
				    "loanAmount": 20000.0,
				    "interestRate": 0.2,
				  	"numOfInstallments": 6
				}
				""";

		RequestBuilder requestBuilder = 
				MockMvcRequestBuilders.post(LOANS_REQUEST_URL).
									headers(getAuthorizedHeader(
											notAuthCustomerId+"", notAuthCustomerPasswd )).
									 content(requestBody).
				                     accept(MediaType.APPLICATION_JSON);
		
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		assertEquals(403, mvcResult.getResponse().getStatus());
	}	

	@Test
	void loanRequestAuthorization_401Scenario_Customer111_WrongPassword() throws Exception {
		logger.debug("In loanRequestAuthorization_401Scenario_Customer111_WrongPassword ");
	
		String requestBody = """
				{
				    "customerId": 111,
				    "loanAmount": 20000.0,
				    "interestRate": 0.2,
				  	"numOfInstallments": 6
				}
				""";
		RequestBuilder requestBuilder = 
				MockMvcRequestBuilders.post(LOANS_REQUEST_URL).
									headers(getAuthorizedHeader(
										authCustomerId+"", "dsffd" )).
									 content(requestBody).
				                     accept(MediaType.APPLICATION_JSON);
		
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		assertEquals(401, mvcResult.getResponse().getStatus());
	}	
	
	
	@Test
	void loanListAuthorization_200Scenario_Admin() throws Exception {
		logger.debug("In loanListAuthorization_200Scenario_Admin ");
		RequestBuilder requestBuilder = 
				MockMvcRequestBuilders.get(LOANS_LIST_URL).
									 headers(getAuthorizedHeader("admin", "admin" )).
				                     accept(MediaType.APPLICATION_JSON);
		
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		assertEquals(200, mvcResult.getResponse().getStatus());
	}	
	
	@Test
	void loanListAuthorization_200Scenario_Customer111() throws Exception {
		logger.debug("In loanListAuthorization_200Scenario_Customer111 ");

		RequestBuilder requestBuilder = 
				MockMvcRequestBuilders.get(LOANS_LIST_URL).
				 headers(getAuthorizedHeader(
						 	authCustomerId+"", authCustomerPasswd )).
				                     accept(MediaType.APPLICATION_JSON);
		
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		assertEquals(200, mvcResult.getResponse().getStatus());
	}	


	//////////////////////////////////
	// Loan Request Tests
	/////////////////////////////////

	@Test
	void loanRequest_200Scenario_Customer111() throws Exception {

		logger.debug("In loanRequest_200Scenario_Customer111 ");		
		
		String requestBody = """
			{
			    "customerId": 111,
			    "loanAmount": 20000.0,
			    "interestRate": 0.2,
			  	"numOfInstallments": 6
			}
			""";
	
		
		RequestBuilder requestBuilder = 
				MockMvcRequestBuilders.post(LOANS_REQUEST_URL).
									 headers(getAuthorizedHeader(
											 	authCustomerId+"", authCustomerPasswd )).
									 content(requestBody).
									 accept(MediaType.APPLICATION_JSON);
		
	  
		BigDecimal loanAmount = new BigDecimal("10000");
		float interestRate = 0.2f;
		int numOfInstallments = 6;
		
//		LoanRequestDto  loanReqDto = LoanRequestDto.builder().
//				customerId(authCustomerId).
//				loanAmount(loanAmount).
//				interestRate(interestRate).
//				numOfInstallments(numOfInstallments).
//				build();
//	   StringBuilder retMsg = new StringBuilder();
	   Customer customer = Customer.builder().
			   					id(authCustomerId).
			   					name("Aaa").
			   					surname("Bbb").
			   					creditLimit(customerCreditLimit).
			   					usedCreditLimit(loanAmount).
			   					loanList(null).
			   					build();
			   
	    Loan retLoan = Loan.builder().
	        createDate(Date.valueOf("2025-01-11")).
	        customer(customer).
		    installmentList(new ArrayList<>()).
		    isPaid(false).
		    numberOfInstallment(numOfInstallments).
		    loanAmount(loanAmount).build();
	    
	    
		//when(loanService.requestLoan(loanReqDto, retMsg)).thenReturn(retLoan);
	  when(loanService.requestLoan(any(), any())).thenReturn(retLoan);
		
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		assertEquals(200, mvcResult.getResponse().getStatus());
		
		String expectedResponse = """
						{"loan":
							{
							"loanId":null,
							"loanAmount":10000,
							"numberOfInstallment":6,
							"createDate":"2025-01-11",
							"isPaid":false,
							"installmentList":[]
							},
						  "replyMessage":""
						}						
				"""; 

		logger.info("In loanRequest_200Scenario_Customer111 - response: {}",
									mvcResult.getResponse().getContentAsString());		
		
		JSONAssert.assertEquals(expectedResponse, 
					mvcResult.getResponse().getContentAsString(), false);
	}	
	
	
	
	private HttpHeaders getAuthorizedHeader(String userId, String password ) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Authorization",  "Basic " + encodeUserPassword(userId, password));
		
		return headers;
	}	
	
	private String encodeUserPassword(String user, String password) {
		String  userPassword = user+":"+password;
		byte[] encBytes =  Base64.getEncoder().encode(userPassword.getBytes());
		return new String(encBytes);
	}
	
	
}
