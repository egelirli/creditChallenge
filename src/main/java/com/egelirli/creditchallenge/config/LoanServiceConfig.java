package com.egelirli.creditchallenge.config;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "loan-param")
@Component
public class LoanServiceConfig {
	private final String  INSTALLMENTS_LIST_SEPERATOR = ",";
	private Logger logger = LoggerFactory.getLogger(LoanServiceConfig.class);
	private String  numberOfInstallments;
	private float   minInterestRate;
	private float   maxInterestRate;
	
	private List<Integer> listInstallments = new ArrayList<Integer>(); 
	
	
	public List<Integer> getNumberOfInstallements() {
		
		return listInstallments;
	}
	public void setNumberOfInstallments(String numberOfInstallments) {
		this.numberOfInstallments = numberOfInstallments;
		setListInstallments();		
	}
	private void setListInstallments() {
	    if(numberOfInstallments == null) {
		  logger.error("In setListInstallments -  numberOfInstallments = null!");
		  return;
		}
	    
	    
	    String[] listStr = numberOfInstallments.split(INSTALLMENTS_LIST_SEPERATOR);
	    for (String str : listStr) {
	    	listInstallments.add(Integer.parseInt(str.strip()));
		}
	    
	    logger.debug("In setListInstallments : {} ",listInstallments);
	}

	public float getMinInterestRate() {
		return minInterestRate;
	}
	public void setMinInterestRate(float minInterestRate) {
		this.minInterestRate = minInterestRate;
		logger.debug("In setMinInterestRate - minInterestRate : {}", minInterestRate);
	}
	public float getMaxInterestRate() {
		return maxInterestRate;
	}
	public void setMaxInterestRate(float maxInterestRate) {
		this.maxInterestRate = maxInterestRate;
		logger.debug("In setMaxInterestRate - maxInterestRate : {}", maxInterestRate);
	}
	
	
	
}
