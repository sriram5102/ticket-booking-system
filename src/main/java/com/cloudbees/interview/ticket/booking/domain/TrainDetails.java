package com.cloudbees.interview.ticket.booking.domain;

import com.cloudbees.interview.ticket.booking.dao.SeatDao;
import com.cloudbees.interview.ticket.booking.dao.SectionDao;
import com.cloudbees.interview.ticket.booking.dao.TrainDao;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class TrainDetails {
    String sectionName;
    int trainNo;
    @JsonIgnore
    TrainDao trainDao;
    SeatDao seatDao;
}
