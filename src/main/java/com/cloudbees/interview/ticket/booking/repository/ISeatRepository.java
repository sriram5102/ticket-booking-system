package com.cloudbees.interview.ticket.booking.repository;

import com.cloudbees.interview.ticket.booking.dao.SeatDao;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISeatRepository extends CrudRepository<SeatDao,Integer> {
}
