package com.cloudbees.interview.ticket.booking.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SectionViewRequest {
    String sectionName;
    int trainNo;
    LocalDate travelDate;
}
