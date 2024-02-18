package com.cloudbees.interview.ticket.booking.domain;

import lombok.Data;

@Data
public class Status {
    int statusCode;
    String statusDescription;
    CrudOperation crudOperation;
}
