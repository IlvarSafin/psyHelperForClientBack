package com.example.helppsy.dto;

import com.example.helppsy.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
    private int id;
    private String name;
    private String email;
    private String password;
    private byte[] photo;

    private List<AppointmentClientDTO> appointmentsCl;
}
