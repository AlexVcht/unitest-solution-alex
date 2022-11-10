package com.kindredgroup.unibetlivetest.service;

import com.kindredgroup.unibetlivetest.model.entity.Customer;
import com.kindredgroup.unibetlivetest.model.exception.CustomException;
import com.kindredgroup.unibetlivetest.model.types.ExceptionType;
import com.kindredgroup.unibetlivetest.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer findCustomerByPseudo(String pseudo) {
        return customerRepository.getCustomerByPseudo(pseudo)
                .orElseThrow(() -> new CustomException(format("customer %s not found", pseudo), ExceptionType.CUSTOMER_NOT_FOUND));
    }

    public List<Customer> findCustomerByIds(List<Long> customerIds) {
        return customerRepository.getCustomerByIdIn(customerIds);
    }

    public List<Customer> saveCustomers(List<Customer> customerUpdated) {
        return customerRepository.saveAll(customerUpdated);
    }
}
