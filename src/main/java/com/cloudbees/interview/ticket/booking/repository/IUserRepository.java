package com.cloudbees.interview.ticket.booking.repository;

import com.cloudbees.interview.ticket.booking.dao.UserDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<UserDao,Integer> {
}
