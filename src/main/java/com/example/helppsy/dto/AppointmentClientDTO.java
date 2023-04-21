package com.example.helppsy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentClientDTO {
    private int id;
    private Date date;
    private boolean status;
    private int psyId;
    private String psyName;
    private String link;
}
