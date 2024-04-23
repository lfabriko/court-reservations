package com.example.courtreservation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class MyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column//(name="phone_number")
    private String phoneNumber;

    public String toString(){
        return "MyUser: id = " + id + ", phoneNum=" + phoneNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MyUser) {
            return false;
        }
        return this.phoneNumber.equals(((MyUser)obj).getPhoneNumber());
    }
}
