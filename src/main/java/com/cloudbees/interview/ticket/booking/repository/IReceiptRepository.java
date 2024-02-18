package com.cloudbees.interview.ticket.booking.repository;

import com.cloudbees.interview.ticket.booking.dao.ReceiptDao;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReceiptRepository extends CrudRepository<ReceiptDao,String> {
}
