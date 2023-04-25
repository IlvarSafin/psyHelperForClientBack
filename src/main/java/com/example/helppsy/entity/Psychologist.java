package com.example.helppsy.entity;

import com.example.helppsy.entity.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "psychologist")
public class Psychologist{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "estimation")
    private double estimation;
    @Column(name = "status")
    private boolean status;
    @Column(name = "email")
    @NotEmpty(message = "Email should not be empty")
    @Email
    private String email;
    @NotEmpty(message = "Password should not be empty")
    @Size(min = 8, message = "Password should be bigger 8 characters")
    @Column(name = "password")
    private String password;
    @Column(name = "description")
    private String description;
    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    @Column(name="photo")
    private byte[] photo;
    @Column(name="activation_code")
    private String activationCode;
    @Column(name = "registered")
    private boolean registered;

    @OneToMany(mappedBy = "psychologist")
    private List<Review> reviews;

    @OneToMany(mappedBy = "psychologist")
    private List<Appointment> appointments;

    @ElementCollection(targetClass = ERole.class)
    @CollectionTable(name = "psy_role",
            joinColumns = @JoinColumn(name = "psy_id"))
    private Set<ERole> roles = new HashSet<>();

    @OneToMany(mappedBy = "psychologist")
    private List<Certificate> certificates;
}
