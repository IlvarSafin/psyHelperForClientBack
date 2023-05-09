package com.example.helppsy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PsychologistDTO{
    private int id;
    private String name;
    private double estimation;
    private String description;
    private String sex;
    private Double price;
    private byte[] photo;

    private List<ReviewDTO> reviewsCl;
    private List<AppointmentClientDTO> appointmentsCl;
}
