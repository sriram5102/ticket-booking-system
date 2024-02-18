package com.cloudbees.interview.ticket.booking.dao;

import com.cloudbees.interview.ticket.booking.domain.SeatStatus;
import com.cloudbees.interview.ticket.booking.domain.SeatType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@Table(name = "seat")
@Entity
@ToString(exclude = {"sectionDao"})
@JsonIgnoreProperties(value = {"userDao"})
public class SeatDao implements Serializable {
    @Id
    @Column(name = "seat_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    int seatId;
    @Column(name = "seat_status")
    String seatStatus;
    @Column(name = "section_id", insertable = false, updatable = false)
    int sectionId;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "section_id",nullable = false)
    SectionDao sectionDao;
    @OneToOne(cascade = CascadeType.ALL)
    UserDao userDao;
}
