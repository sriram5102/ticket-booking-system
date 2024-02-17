package com.cloudbees.interview.ticket.booking.domain;

import lombok.Data;

import java.util.List;

@Data
public class Train {
    int trainNo;
    String trainName;
    Station fromStation;
    Station toStation;
    List<Section> sections;
}
