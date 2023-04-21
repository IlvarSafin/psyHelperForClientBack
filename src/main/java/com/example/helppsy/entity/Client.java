package com.example.helppsy.entity;

import com.example.helppsy.entity.enums.ERole;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "client")
public class Client implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @NotEmpty(message = "Name should not be empty")
    @Column(name = "name")
    private String name;
    @NotEmpty(message = "Email should not be empty")
    //@Size(min = 6, message = "Login should be bigger 8 characters")
    @Email
    @Column(name = "email")
    private String email;
    @Column(name = "activation_code")
    private String activationCode;
    @NotEmpty(message = "Password should not be empty")
    @Size(min = 8, message = "Password should be bigger 8 characters")
    @Column(name = "password")
    private String password;
    @Column(name = "status")
    private boolean status;
    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    @Column(name="photo")
    private byte[] photo;

    @ElementCollection(targetClass = ERole.class)
    @CollectionTable(name = "client_role",
            joinColumns = @JoinColumn(name = "client_id"))
    private Set<ERole> roles = new HashSet<>();

    @OneToMany(mappedBy = "client")
    private List<Appointment> appointments;

    @OneToOne(mappedBy = "client")
    private RefreshToken refreshToken;

    @Transient
    private Collection<? extends GrantedAuthority> authorities;


    /**
     * SECURITY
     */

    public Client(int id, String name, String email, String password, boolean status, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.status = status;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status;
    }

}
