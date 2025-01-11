package com.egelirli.creditchallenge.security;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.egelirli.creditchallenge.exception.NotAuthorizedException;

@Component
public class UserAuthorizer {
	private static Logger logger = 
			LoggerFactory.getLogger(UserAuthorizer.class);

	
	public boolean checkIfUserAuthorized(
			UserDetails userDetails, Long customerId) 
							throws NotAuthorizedException {
		
		boolean isAuthorized = false;
		
		if(userDetails == null || customerId == null) {
			logger.warn("In checkUserAthorized - userDetails or customerId is null!");
			throw new NotAuthorizedException("userDetails or customerId is null!");
		}
		
		logger.info("In isUserAthorized - userName : {} customerId : {}",
				userDetails.getUsername(),customerId);
		
		if(userDetails.getUsername().trim().equals(customerId+"")){
			isAuthorized = true;
		}else {
		 	Collection<? extends GrantedAuthority> list =  userDetails.getAuthorities();
		 	for (GrantedAuthority authority  : list) {
				if(authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN")) {
					isAuthorized = true; 
				}
			}
		}
		
		logger.info("In isUserAthorized - userName : {} customerId : {} isAuthorized: {}",
				userDetails.getUsername(),customerId,isAuthorized);
		
		if(!isAuthorized){
			logger.error("In requestLoan - not authorized :userDetails {}",userDetails);
			throw new NotAuthorizedException("User not authorized");
		}
		
		return isAuthorized;
	}

	
}
