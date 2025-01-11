package com.egelirli.creditchallenge.restcontroller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Base64;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.egelirli.creditchallenge.entity.Customer;
import com.egelirli.creditchallenge.service.CustomerService;
import com.egelirli.creditchallenge.service.LoanService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LoansIntegrationTest {

	private Logger logger = 
			LoggerFactory.getLogger(LoanRestControllerUnitTest.class);
	
	
	private static final long authCustomerId = 111;
	private static final String authCustomerPasswd = "111";
	
	private static final String LOANS_REQUEST_URL = "/loans/request";
	private static final String LOANS_LIST_URL = "/loans/list/111";

	@Autowired
	private TestRestTemplate template;
	
	@Autowired
	private LoanService loanService;

	@Autowired
	private CustomerService customerService;

	@BeforeAll
	public static void executeBeforeAll() {
		
	}

	@BeforeEach
	public void beforeEachMethod() {
		logger.debug("In beforeEachMethod");
		loanService.deleteAll();
	}
	
	
	@Test
	void loanRequest_basicScenario_Customer111() throws Exception {

		logger.debug("In loanRequest_Scenario_Customer111 ");		
		
		loanRequest_Customer111();
		loanList_Customer111();
		
	}	

	void loanList_Customer111() throws Exception {

		logger.debug("In loanList_Customer111 ");		

		//Now list
		
		HttpHeaders headers = getAuthorizedHeader(authCustomerId+"", authCustomerPasswd);
		HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
		ResponseEntity<String> responseEntity 
			= template.exchange(LOANS_LIST_URL, HttpMethod.GET, httpEntity, String.class);
		
		logger.info("In loanRequestAndList_basicScenario_Customer111 - response: {}",
				responseEntity.getBody());		
		
		assertTrue(responseEntity.getStatusCode().is2xxSuccessful());

		String expectedResponse = """
				[{
					"loanId":1,"loanAmount":20000.00,"numberOfInstallment":6,"isPaid":false,
					"installmentList":[
					    {"id":1,"amount":4000.00,"paidAmount":0.00,"paymentDate":null,"isPaid":false},
					    {"id":2,"amount":4000.00,"paidAmount":0.00,"paymentDate":null,"isPaid":false},
					    {"id":3,"amount":4000.00,"paidAmount":0.00,"paymentDate":null,"isPaid":false},
					    {"id":4,"amount":4000.00,"paidAmount":0.00,"paymentDate":null,"isPaid":false},
					    {"id":5,"amount":4000.00,"paidAmount":0.00,"paymentDate":null,"isPaid":false},
					    {"id":6,"amount":4000.00,"paidAmount":0.00,"paymentDate":null,"isPaid":false}
					  ]
				}]
				""";		
		
		JSONAssert.assertEquals(expectedResponse, responseEntity. getBody(), false);
	}	
	

	private void loanRequest_Customer111() throws Exception {

		logger.debug("In loanRequest_Customer111 ");		
		
		String requestBody = """
			{
			    "customerId": 111,
			    "loanAmount": 20000.0,
			    "interestRate": 0.2,
			  	"numOfInstallments": 6
			}
			""";
	    
		HttpHeaders headers = getAuthorizedHeader(authCustomerId+"", authCustomerPasswd);
		HttpEntity<String> httpEntity = new HttpEntity<String>(requestBody, headers);
		ResponseEntity<String> responseEntity 
			= template.exchange(LOANS_REQUEST_URL, HttpMethod.POST, httpEntity, String.class);
		
		logger.info("In loanRequest_Customer111 - response: {}",
				responseEntity.getBody());		
		
		assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
		String expectedResponse = """
						{"loan":
							{
							"loanId":1,
							"loanAmount":20000,
							"numberOfInstallment":6,
							"isPaid":false
							},
						  "replyMessage":""
						}						
				"""; 

		
		JSONAssert.assertEquals(expectedResponse, responseEntity. getBody(), false);
	
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
