package com.example.helppsy.service;

import com.example.helppsy.dto.AppointmentClientDTO;
import com.example.helppsy.entity.Appointment;
import com.example.helppsy.entity.Client;
import com.example.helppsy.entity.Psychologist;
import com.example.helppsy.repository.AppointmentRepository;
import com.example.helppsy.repository.PsyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {
    private AppointmentRepository appointmentRepository;
    private PsyRepository psyRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              PsyRepository psyRepository){
        this.appointmentRepository = appointmentRepository;
        this.psyRepository = psyRepository;
    }

    public AppointmentClientDTO createAppointment(AppointmentClientDTO appointmentClientDTO, Client client, int id){
        Psychologist psychologist = psyRepository.findById(id).
                orElseThrow(() -> new UsernameNotFoundException("Psy not found with id " + id));
        Appointment appointment = appointmentDTOToAppointment(appointmentClientDTO, client);
        appointment.setStatus(true);
        appointment.setClient(client);
        appointment.setPsychologist(psychologist);
        appointmentRepository.save(appointment);
        return appointmentToAppointmentClDto(appointment);
    }

    public AppointmentClientDTO appointmentToAppointmentClDto(Appointment appointment){

        return new AppointmentClientDTO(
                appointment.getId(),
                appointment.getDate(),
                appointment.isStatus(),
                appointment.getPsychologist().getId(),
                appointment.getPsychologist().getName(),
                appointment.getLink()
        );
    }

    public Appointment appointmentDTOToAppointment(AppointmentClientDTO appointmentClientDTO,
                                                   Client client){
        int psyId = appointmentClientDTO.getPsyId();
        Psychologist psychologist = psyRepository.findById(psyId)
                .orElseThrow(() -> new UsernameNotFoundException("Psy with id: " + psyId + " not found"));
        return new Appointment(
                appointmentClientDTO.getId(),
                appointmentClientDTO.getDate(),
                appointmentClientDTO.isStatus(),
                appointmentClientDTO.getLink(),
                psychologist,
                client
        );
    }


}
