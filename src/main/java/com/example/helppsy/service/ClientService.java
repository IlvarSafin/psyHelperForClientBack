package com.example.helppsy.service;

import com.example.helppsy.dto.AppointmentClientDTO;
import com.example.helppsy.dto.ClientDTO;
import com.example.helppsy.entity.Appointment;
import com.example.helppsy.entity.Client;
import com.example.helppsy.entity.Psychologist;
import com.example.helppsy.entity.enums.ERole;
import com.example.helppsy.payload.request.RegisterRequest;
import com.example.helppsy.repository.AppointmentRepository;
import com.example.helppsy.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private ClientRepository clientRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private AppointmentService appointmentService;
    private AppointmentRepository appointmentRepository;
    private MailSender mailSender;

    @Autowired
    public ClientService(ClientRepository clientRepository,
                         AppointmentService appointmentService,
                         BCryptPasswordEncoder bCryptPasswordEncoder,
                         MailSender mailSender,
                         AppointmentRepository appointmentRepository){
        this.clientRepository = clientRepository;
        this.appointmentService = appointmentService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.mailSender = mailSender;
        this.appointmentRepository = appointmentRepository;
    }

    public ClientDTO createClient(RegisterRequest registerRequest){
        if (!clientCheck(registerRequest)){
            throw new RuntimeException("Client not right!");
        }
        Client client = new Client();
        client.setEmail(registerRequest.getEmail());
        client.setName(registerRequest.getName());
        client.setPhoto(null);
        client.setAppointments(new ArrayList<>());
        client.getRoles().add(ERole.ROLE_USER);
        client.setPassword(bCryptPasswordEncoder.encode(registerRequest.getPassword()));
        client.setActivationCode(UUID.randomUUID().toString());
        clientRepository.save(client);

        String message = String.format(
                "Hello, %s! \n" +
                        "Welcome to psyHelper. Please, visit next link: http://localhost:4200/activate/%s",
                client.getUsername(), client.getActivationCode()
        );
        mailSender.send(client.getEmail(), "Activation code", message);
        return clientToClientDTO(client);
    }

    private boolean clientCheck(RegisterRequest client){
        return (client.getName() != null &&
                client.getEmail() != null &&
                client.getPassword() != null &&
                client.getPassword().equals(client.getConfirmedPassword()));
    }

    public Client getClientByPrincipal(Principal principal){
        String email = principal.getName();

        return clientRepository.
                findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Client not found with login " + email));
    }

    public Client getCurrentClient(Principal principal){
        return getClientByPrincipal(principal);
    }

    public Client updateClient(ClientDTO clientDTO){
        Client client = clientRepository.findById(clientDTO.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Client not found " + clientDTO.getEmail()));

        client.setName(clientDTO.getName());

        clientRepository.save(client);
        return client;
    }

    public List<Appointment> appointments(Principal principal){
        return getClientByPrincipal(principal).getAppointments()
                .stream().filter(e -> !e.isStatus())
                .collect(Collectors.toList());
    }

    public List<Appointment> currentAppointments(Principal principal){
        long twoHoursInMillieSeconds = 7200000;
        List<Appointment> appointments = getClientByPrincipal(principal).getAppointments();
        appointments.forEach(e -> {
            if ((e.getDate().getTime() + twoHoursInMillieSeconds) <= System.currentTimeMillis()){
                e.setStatus(false);
                appointmentRepository.save(e);
            }
        });

        return appointments.stream()
                .filter(e -> e.isStatus())
                .collect(Collectors.toList());
    }

    public byte[] uploadPhoto(MultipartFile file, Principal principal) throws IOException {
        Client client = getCurrentClient(principal);

        client.setPhoto(file.getBytes());
        clientRepository.save(client);

        return file.getBytes();
    }

    public ClientDTO deletePhoto(Principal principal){
        Client client = getCurrentClient(principal);
        client.setPhoto(null);
        return clientToClientDTO(clientRepository.save(client));
    }

    public ClientDTO clientToClientDTO(Client client){
        List<Appointment> appointments = client.getAppointments();
        List<AppointmentClientDTO> appointmentClientDTOs = null;
        if (appointments != null) {
            appointmentClientDTOs = appointments.stream()
                    .map(a -> appointmentService.appointmentToAppointmentClDto(a)).toList();
        }
        return new ClientDTO(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPassword(),
                client.getPhoto(),
                appointmentClientDTOs
        );
    }

    public boolean activateClient(String code) {
        Client client = clientRepository.findByActivationCode(code)
                .orElseThrow(() -> new UsernameNotFoundException("NOT Client with code"));
        client.setActivationCode(null);
        client.setStatus(true);

        clientRepository.save(client);
        return client.isStatus();
    }

    public List<Psychologist> psysByDate(){
        List<Appointment> appointments = appointmentRepository.findAll()
                .stream().filter(Appointment::isStatus).toList();

        Collections.sort(appointments, (a, b) -> (int) (a.getDate().getTime() - b.getDate().getTime()));

        return appointments.stream()
                .map(Appointment::getPsychologist).distinct().collect(Collectors.toList());
    }
}
