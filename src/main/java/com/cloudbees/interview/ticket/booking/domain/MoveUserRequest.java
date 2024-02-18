package com.cloudbees.interview.ticket.booking.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MoveUserRequest {
    int trainNo;
    LocalDate travelDate;
    User user;
    String targetSectionName;
    int targetSeatId;
}
