package com.egelirli.creditchallenge.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.egelirli.creditchallenge.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer,Long>{

}
