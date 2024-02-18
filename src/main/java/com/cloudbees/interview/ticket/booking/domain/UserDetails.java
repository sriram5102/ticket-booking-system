package com.cloudbees.interview.ticket.booking.domain;

import lombok.Data;

@Data
public class UserDetails {
    String firstName;
    String lastName;
    String email;
    int seatNo;
}
