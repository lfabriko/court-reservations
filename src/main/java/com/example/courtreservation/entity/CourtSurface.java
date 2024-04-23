package com.example.courtreservation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "court_surface")
public class CourtSurface {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="cost_in_minutes")
    private int costInMinutes;

    public CourtSurface() {}
    public CourtSurface(int id, int costInMinutes) {
        this.id = id;
        this.costInMinutes = costInMinutes;
    }

    public String toString(){
        return "Court surface: id=" + id + ", costInMinutes=" + costInMinutes;
    }
}
