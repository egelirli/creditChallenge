package com.egelirli.creditchallenge.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.egelirli.creditchallenge.config.LoanServiceConfig;
import com.egelirli.creditchallenge.dto.PayLoanRequestDto;
import com.egelirli.creditchallenge.dto.PaymentResponse;
import com.egelirli.creditchallenge.entity.Customer;
import com.egelirli.creditchallenge.entity.Loan;
import com.egelirli.creditchallenge.entity.LoanInstallment;
import com.egelirli.creditchallenge.exception.ResourceNotFoundException;
import com.egelirli.creditchallenge.repository.CustomerRepository;
import com.egelirli.creditchallenge.repository.LoanRepository;


@Service
@Transactional
public class LoanService {
	
	
	private Logger logger = LoggerFactory.getLogger(LoanService.class);
	
	private CustomerRepository customerRepo;
	
	private LoanRepository loanRepo;
	
	private InstallmentService installService;
		
	
	@Autowired
	private LoanParamValidator loanParamValidator;

	private CustomerService customerService;
	
	public LoanService( CustomerRepository customerRepo,
						LoanRepository loanRepo,
						CustomerService customerService,
						InstallmentService installService ) {
		
		this.customerRepo = customerRepo;
		this.loanRepo = loanRepo;
		this.customerService = customerService;
		this.installService = installService;
	}
	/**
	 *  Creates a new loan for a given customer, amount, interest rate and 
	 *  number of installments 
	 * @param customerId
	 * @param loanAmount
	 * @param interestRate
	 * @param numOfInstallements
	 * @param returnMessage : Return message
	 * @return Loan if created Loan successfully, null otherwise
	 * @throws ResourceNotFoundException
	 */
	public Loan addLoan(Long customerId,
						   BigDecimal loanAmount,
						   float interestRate,
						   int numOfInstallments,
						   StringBuilder returnMessage) 
								   throws ResourceNotFoundException {
		Loan loan = null;
		
		logger.debug("In addLoan - customerId : {} loanAmount : {} "
				+ "interestRate: {} numOfInstallments : {}",
					customerId, loanAmount, interestRate, numOfInstallments);
		
		if(!loanParamValidator.validateLoanParams(
				customerId,loanAmount, interestRate, numOfInstallments, returnMessage)) {
			return null;
		}
		
		//(Optional<Customer> customer = customerRepo.findById(customerId);
		
		BigDecimal totalAmount = 
				calculateTotalAmount(loanAmount, numOfInstallments, interestRate);
		if(totalAmount != null) {
			loan =  createAndSaveLoan(customerRepo.findById(customerId).get(), 
											loanAmount, numOfInstallments);
			List<LoanInstallment> list = 
					installService.createInstallments(loan, totalAmount, numOfInstallments);
			loan.setInstallmentList(list);
			customerService.processLoanAdded(customerId, loanAmount);
		}else {
			logger.error("In addLoan - totalAmount =null!");
			returnMessage.append("totalAmount =null!");
		}
		
		
		return loan;
	}
	
	public List<Loan> getLoanlistForCustomer(Long customerId) 
									throws ResourceNotFoundException{
		logger.debug("In getLoanlistForCustomer - customerId: {}", customerId);
		
		customerRepo.findById(customerId)
					.orElseThrow(() -> new ResourceNotFoundException(" Customer Not Found " + customerId ) );
		return this.loanRepo.findByCustomerId(customerId);
	}
	

    public List<Loan> getLoanlistForCustomerWithPaidStatus(
    						Long customerId, boolean paidStatus ) 
    									throws ResourceNotFoundException{

    	logger.debug("In getLoanlistForCustomerWithPayStatus - "
    							+ "customerId: {} isPaid: {}", customerId,paidStatus);
		
		customerRepo.findById(customerId)
		    .orElseThrow(() -> new ResourceNotFoundException(" Customer Not Found " + customerId ) );
		return this.loanRepo.findByCustomerIdAndIsPaid(customerId,paidStatus );
    }	
	
   
	public PaymentResponse payLoanInstallment(PayLoanRequestDto payLoanDto) 
											throws ResourceNotFoundException{
		logger.debug("In payLoanInstallment - "
					+ "payLoanDto: {} ",payLoanDto);
    	Loan loan = 
		 loanRepo.findById(payLoanDto.getLoanId())
				.orElseThrow(() -> new ResourceNotFoundException(
								" Loan Not Found " + payLoanDto.getLoanId() ) );
    	
    	List<LoanInstallment> list =  
    			installService.getInstallmentlistForLoanWithPaidStatus(
    												payLoanDto.getLoanId(), false);
    	
    	
    	LocalDate paymentDay = LocalDate.now();
    	if(payLoanDto.getPaymentDate() != null) {
    		paymentDay = payLoanDto.getPaymentDate();
    	}
    	
    	PaymentResponse response = payLoanInstallment(
    				payLoanDto.getCustomerId(), list, payLoanDto.getPaymentAmount(),  paymentDay);
   
    	customerService.processInstallmentPaid(
						payLoanDto.getCustomerId(), loan,   response.getNumOfInstallmentsPaid());
    	
    	if(response.getNumOfInstallmentsPaid() >= list.size()) {
    		response.setLoanPaidCompletely(true);
    		loan.setIsPaid(true);
    		loanRepo.save(loan);
    	}else {
    		response.setLoanPaidCompletely(false);
    	}
    	
    	
		return response;
    	
	}

    
    private PaymentResponse payLoanInstallment(
    		long customerId, 
    		List<LoanInstallment> installList, 
    		BigDecimal paymentAmount,
			LocalDate paymentDay) {
    	
    	logger.debug("In payLoanInstallment - "
				+ "customerId: {}  paymentAmount : {} paymentDay : {}", 
										customerId,paymentAmount,paymentDay);

    	PaymentResponse response = new PaymentResponse();
    	BigDecimal remainingPaymentAmount = paymentAmount; 
    	BigDecimal totalAmountSpent = new BigDecimal(0);
    	int numberOfInstPaid = 0;
    	for (LoanInstallment loanInstallment : installList) {
    		 logger.trace("In payLoanInstallment -  "
    		 		+ "remainingPaymentAmount : {}  loanInstallment : {}", 
    		 							remainingPaymentAmount, loanInstallment);
			if(remainingPaymentAmount.compareTo(loanInstallment.getAmount()) >= 0) {
				 Period diff = Period.between(paymentDay, loanInstallment.getDueDate() );
				 int diffMonths = diff.getYears()*12 + diff.getMonths(); 
				 logger.trace("In payLoanInstallment -  diffMonths : {}", diffMonths);
				 if(diffMonths < 3  ) {
					 remainingPaymentAmount = 
							 remainingPaymentAmount.subtract(loanInstallment.getAmount());
					 
					 totalAmountSpent = totalAmountSpent.add(loanInstallment.getAmount());
					 installService.modifyLoanInstallmentAsPaid(
								loanInstallment,paymentDay,loanInstallment.getAmount() );
					 numberOfInstPaid++;
				 }else { //diffMonths > 3
					break;
				 }
			}else {
				break;
			}
		}
    	
    	response.setNumOfInstallmentsPaid(numberOfInstPaid);
    	response.setTotalAmountSpent(totalAmountSpent);
    	
    	
    	return response;
    }
    
	private Loan createAndSaveLoan(
			Customer customer, BigDecimal loanAmount, int numOfInstallments) {
		
		Loan loan = new Loan();
		loan.setCustomer(customer);
		loan.setLoanAmount(loanAmount);
		loan.setNumberOfInstallment(numOfInstallments);
		loan.setCreateDate(new Date(System.currentTimeMillis()));
		loan.setIsPaid(false);
		loanRepo.save(loan);
		
		logger.debug("In createAndSaveLoan  saved the loan - "
				+ "customerId : {} loanAmount : {}  numOfInstallments : {}",
						customer.getId(), loanAmount, numOfInstallments);

		return loan;
	}

	private BigDecimal calculateTotalAmount(
			 BigDecimal loanAmount, int numOfInstallements, float interestRate) {
		
		logger.debug("In calculateTotalAmount loanAmount {} "
						+ "numOfInstallements : {}  interestRate: {}", 
								loanAmount,numOfInstallements, interestRate,interestRate);
//		BigDecimal totalInterest = 
//				loanAmount.multiply(BigDecimal.valueOf(numOfInstallements*interestRate)); 
//		BigDecimal total = loanAmount.add(totalInterest);
		BigDecimal total = loanAmount.multiply(BigDecimal.valueOf(1 + interestRate));
		
		logger.debug("In calculateTotalAmount returns {}", total);
		
		return total;
	}
	public void deleteAll() {
	   logger.debug("In deleteAll ");
	   loanRepo.deleteAll(); 	
	}
	
	
	
	
	
}
