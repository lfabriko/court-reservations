package com.example.courtreservation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;

import java.time.Instant;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@SoftDelete
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="court_id")
    private Court court;

    @ManyToOne// @Fetch(SELECT) //(cascade = {CascadeType.PERSIST})
    @JoinColumn(name="my_user_id")
    private MyUser myUser;

    @Column
    private String desc;

    @Column//(name="start_date")
    private LocalDateTime startDate;
    @Column//(name="end_date")
    private LocalDateTime endDate;

    //@Column//(name="kind_of_game")
    //private String kindOfGame;

    @Enumerated(EnumType.ORDINAL)
    private KindOfGame kindOfGame;

    @Column
    private long totalCost;

    @CreationTimestamp
    private Instant createdOn;

    public String toString(){
        return "Reservation: id=" + id + ", desc = " + desc + ", court = " + court + ", myuser = " + myUser;
    }
}
