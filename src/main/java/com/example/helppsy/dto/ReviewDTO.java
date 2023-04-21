package com.example.helppsy.dto;

import com.example.helppsy.entity.Client;
import com.example.helppsy.entity.Psychologist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private int id;
    private String text;
    private int estimation;
    private int clientId;
    private String nameClient;
}
