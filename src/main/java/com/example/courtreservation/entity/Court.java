package com.example.courtreservation.entity;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
@Entity
public class Court {
    @Id
    private int id;
    @ManyToOne
    @JoinColumn(name="court_surface_id")
    private CourtSurface cs;

    @Column
    private String description;

    public String toString(){
        return "Court: id=" + id + ", cs: [" + cs + "], description: " + description;
    }

    public Court() {}
    public Court(int id, CourtSurface cs, String description) {
        this.id = id;
        this.cs = cs;
        this.description = description;
    }
}
