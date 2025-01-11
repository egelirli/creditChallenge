package com.egelirli.creditchallenge.security;

import java.util.Collection;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.egelirli.creditchallenge.exception.NotAuthorizedException;

@Configuration
public class SecurityConfiguration {
	private static Logger logger = 
			LoggerFactory.getLogger(SecurityConfiguration.class);
	
	@Bean
	public UserDetailsService  createUserDetailsManager() {
		
		Function<String, String> passwdEncoder = 
					input ->  passwordEncoder().encode(input);
		
		
		UserDetails userDetails1 = createNewUserDetail(passwdEncoder, "admin", "admin","ADMIN");
		UserDetails userDetails2 = createNewUserDetail(passwdEncoder, "111", "111", "CUSTOMER");
		UserDetails userDetails3 = createNewUserDetail(passwdEncoder, "222", "222", "CUSTOMER");
		
		return new InMemoryUserDetailsManager(userDetails1, userDetails2,userDetails3);	 
	}


	private UserDetails createNewUserDetail(
			             Function<String, String> passwdEncoder, 
			             String username, 
			             String password,
			             String role) {
		
		UserDetails userDetails = User.builder()
				 .passwordEncoder(passwdEncoder)
				 .username(username)
				 .password(password)
				 .roles(role)
				 .build();
		return userDetails;
	}
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	
	@Bean
	protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http.csrf(AbstractHttpConfigurer::disable);
	    http.httpBasic(Customizer.withDefaults());
	    http.authorizeHttpRequests(registery -> registery
	        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
	        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**","/swagger-ui.html").permitAll()
	        .requestMatchers("/**").permitAll())
//	        .requestMatchers("/**").hasRole("ADMIN")
//	    	.requestMatchers("/**").hasRole("CUSTOMER"))
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
	    http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
	    http.formLogin(AbstractAuthenticationFilterConfigurer::permitAll);
	    return http.build();
	  }	
	
	
	
	
}
