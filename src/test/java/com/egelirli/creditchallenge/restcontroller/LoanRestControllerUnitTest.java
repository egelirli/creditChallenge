package com.egelirli.creditchallenge.restcontroller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.egelirli.creditchallenge.service.LoanService;

//@EnableAutoConfiguration
//@RunWith(SpringRunner.class)

//@WebMvcTest(controllers = LoanRestController.class)
//@AutoConfigureMockMvc(addFilters = false)

@SpringBootTest
@AutoConfigureMockMvc
public class LoanRestControllerUnitTest {

	private static String LOANS_REQUEST_URL = 
			"http://localhost:8080/loans/request";

	private static String LOANS_LIST_URL = 
			"http://localhost:8080/loans/list/111";
	
	private static String LOAN_PAY_URL = 
			"http://localhost:8080/loans/pay";

	
	@MockBean
	private LoanService loanService;
	
	@Autowired
	private MockMvc mockMvc;
	
	//////////////////////////////////
	//
	// Authorization Tests
	//
	/////////////////////////////////
	@Test
	void loanRequestAuthorization_405Scenario_NoUser() throws Exception {
		RequestBuilder requestBuilder = 
				MockMvcRequestBuilders.get(LOANS_REQUEST_URL).
				                     accept(MediaType.APPLICATION_JSON);
		
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		assertEquals(405, mvcResult.getResponse().getStatus());
	}	

	@Test
	void loanRequestAuthorization_405Scenario_Customer222() throws Exception {
		RequestBuilder requestBuilder = 
				MockMvcRequestBuilders.get(LOANS_REQUEST_URL).
									 headers(getAuthorizedHeader("222", "222" )).
				                     accept(MediaType.APPLICATION_JSON);
		
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		assertEquals(405, mvcResult.getResponse().getStatus());
	}	
	
	
	@Test
	void loanListAuthorization_200Scenario_Admin() throws Exception {
		RequestBuilder requestBuilder = 
				MockMvcRequestBuilders.get(LOANS_LIST_URL).
									 headers(getAuthorizedHeader("admin", "admin" )).
				                     accept(MediaType.APPLICATION_JSON);
		
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		assertEquals(200, mvcResult.getResponse().getStatus());
	}	
	
	@Test
	void loanListAuthorization_200Scenario_Customer111() throws Exception {
		RequestBuilder requestBuilder = 
				MockMvcRequestBuilders.get(LOANS_LIST_URL).
									 headers(getAuthorizedHeader("111", "111" )).
				                     accept(MediaType.APPLICATION_JSON);
		
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		assertEquals(200, mvcResult.getResponse().getStatus());
	}	


	
	@Test
	void loanRequest_200Scenario_Customer111() throws Exception {
		RequestBuilder requestBuilder = 
				MockMvcRequestBuilders.get(LOANS_REQUEST_URL).
									 headers(getAuthorizedHeader("111", "111" )).
				                     accept(MediaType.APPLICATION_JSON);
		
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		
		assertEquals(200, mvcResult.getResponse().getStatus());
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
