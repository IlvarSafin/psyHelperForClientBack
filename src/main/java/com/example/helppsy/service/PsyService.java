package com.example.helppsy.service;

import com.example.helppsy.dto.AppointmentClientDTO;
import com.example.helppsy.dto.PsychologistDTO;
import com.example.helppsy.dto.ReviewDTO;
import com.example.helppsy.entity.Psychologist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PsyService {
    private final AppointmentService appointmentService;
    private final ReviewService reviewService;

    @Autowired
    public PsyService(AppointmentService appointmentService,
                      ReviewService reviewService){
        this.appointmentService = appointmentService;
        this.reviewService = reviewService;
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
                psychologist.getPhoto(),
                reviewDTOs,
                appointmentClientDTOs
        );
    }


}
