package com.cloudbees.interview.ticket.booking.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RemoveUserRequest {
    int trainNo;
    LocalDate travelDate;
    User user;
}
