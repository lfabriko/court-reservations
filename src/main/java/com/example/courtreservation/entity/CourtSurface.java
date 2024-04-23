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

    /*@OneToMany(cascade = CascadeType.ALL)//mappedBy = "court_surface", mappedBy = "id",
    private Set<Court> courts;*/

    public CourtSurface() {}
    public CourtSurface(int id, int costInMinutes) {
        this.id = id;
        this.costInMinutes = costInMinutes;//tady asi nema byt set courts
    }

    public String toString(){
        return "Court surface: id=" + id + ", costInMinutes=" + costInMinutes;
    }
}
