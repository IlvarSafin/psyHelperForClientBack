package com.example.helppsy.service;

import com.example.helppsy.dto.AppointmentClientDTO;
import com.example.helppsy.dto.PsychologistDTO;
import com.example.helppsy.dto.ReviewDTO;
import com.example.helppsy.entity.Appointment;
import com.example.helppsy.entity.Psychologist;
import com.example.helppsy.repository.AppointmentRepository;
import com.example.helppsy.repository.PsyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PsyService {
    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;
    private final ReviewService reviewService;
    private final PsyRepository psyRepository;

    @Autowired
    public PsyService(AppointmentService appointmentService,
                      ReviewService reviewService,
                      AppointmentRepository appointmentRepository,
                      PsyRepository psyRepository){
        this.appointmentService = appointmentService;
        this.reviewService = reviewService;
        this.appointmentRepository = appointmentRepository;
        this.psyRepository = psyRepository;
    }

    public PsychologistDTO psychologistToPsychologistDTO(Psychologist psychologist){
        List<ReviewDTO> reviewDTOs = psychologist.getReviews().stream()
                .map(reviewService::reviewToReviewDTO).toList();
        List<AppointmentClientDTO> appointmentClientDTOs = psychologist.getAppointments()
                .stream().map(appointmentService::appointmentToAppointmentClDto)
                .toList();

        return new PsychologistDTO(
                psychologist.getId(),
                psychologist.getName(),
                psychologist.getEstimation(),
                psychologist.getDescription(),
                psychologist.getSex(),
                psychologist.getPrice(),
                psychologist.getPhoto(),
                reviewDTOs,
                appointmentClientDTOs
        );
    }

    public List<Psychologist> psysByDate(){
        List<Psychologist> psychologists = new ArrayList<>(psyRepository.findAll()
                .stream()
                .filter(e -> e.getAppointments().isEmpty())
                .toList());

        List<Appointment> appointments = new ArrayList<>(appointmentRepository.findAll()
                .stream().filter(Appointment::isStatus).toList());

        appointments.sort((a, b) -> (int) (a.getDate().getTime() - b.getDate().getTime()));

        List<Psychologist> appPsys = appointments.stream()
                .map(Appointment::getPsychologist).distinct().toList();

        psychologists.addAll(appPsys);

        return psychologists;
    }
}
