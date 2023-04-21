package com.example.helppsy.service;

import com.example.helppsy.dto.ReviewDTO;
import com.example.helppsy.entity.Psychologist;
import com.example.helppsy.entity.Review;
import com.example.helppsy.repository.ClientRepository;
import com.example.helppsy.repository.PsyRepository;
import com.example.helppsy.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class ReviewService {
    private final ClientService clientService;
    private final ClientRepository clientRepository;
    private final ReviewRepository reviewRepository;
    private final PsyRepository psyRepository;

    @Autowired
    public ReviewService(ClientService clientService,
                         ClientRepository clientRepository,
                         ReviewRepository reviewRepository,
                         PsyRepository psyRepository){
        this.clientService = clientService;
        this.clientRepository = clientRepository;
        this.reviewRepository = reviewRepository;
        this.psyRepository = psyRepository;
    }

    public ReviewDTO reviewToReviewDTO(Review review){
        return new ReviewDTO(
                review.getId(),
                review.getText(),
                review.getEstimation(),
                review.getClient().getId(),
                review.getClient().getName()
        );
    }

    public Review reviewDtoToReview(ReviewDTO reviewDTO,
                                    Principal principal,
                                    Psychologist psychologist){
        return new Review(
                reviewDTO.getId(),
                reviewDTO.getText(),
                reviewDTO.getEstimation(),
                clientService.getCurrentClient(principal),
                psychologist
        );
    }

    public Review createReview(Psychologist psychologist, ReviewDTO reviewDTO, Principal principal){
        Review review = reviewDtoToReview(reviewDTO, principal, psychologist);
        System.out.println("333333333 "+review);
        review.setClient(clientService.getCurrentClient(principal));
        review.setPsychologist(psychologist);

        Review savedReview = reviewRepository.save(review);

        double newEstimations = (psychologist.getEstimation() * psychologist.getReviews().size() + reviewDTO.getEstimation()) / (psychologist.getReviews().size() + 1);
        psychologist.setEstimation(newEstimations);
        psyRepository.save(psychologist);

        return savedReview;
    }

}
