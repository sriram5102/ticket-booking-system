package com.cloudbees.interview.ticket.booking.repository;

import com.cloudbees.interview.ticket.booking.dao.SectionDao;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISectionRepository extends CrudRepository<SectionDao,Integer> {
}
