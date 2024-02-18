package com.cloudbees.interview.ticket.booking.repository;

import com.cloudbees.interview.ticket.booking.dao.TrainDao;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ITrainRepository  extends CrudRepository<TrainDao,Integer> {
    Optional<TrainDao> findByTrainNoAndTravelDate(int trainNo, LocalDate travelDate);
}