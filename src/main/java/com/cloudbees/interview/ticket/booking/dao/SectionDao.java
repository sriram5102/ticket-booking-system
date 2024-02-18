package com.cloudbees.interview.ticket.booking.dao;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "section")
@Entity
@ToString(exclude = {"trainDao"})
public class SectionDao implements Serializable {
    @Id
    @Column(name = "section_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    int sectionId;
    @Column(name = "train_id", insertable = false, updatable = false)
    int trainId;
    @Column(name = "section_name")
    String sectionName;
    @Column(name = "seats_left")
    int seatsLeft;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "train_id", nullable = false)
    TrainDao trainDao;
    @OneToMany(cascade = CascadeType.ALL)
    @JsonManagedReference
    List<SeatDao> seats = new ArrayList<>();
}