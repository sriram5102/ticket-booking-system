package com.cloudbees.interview.ticket.booking.domain;

import lombok.Data;

import java.util.List;

@Data
public class Section {
    String sectionName;
    List<Seat> seats;
}
