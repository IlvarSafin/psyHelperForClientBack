package com.example.helppsy.controller;

import com.example.helppsy.dto.*;
import com.example.helppsy.entity.Appointment;
import com.example.helppsy.entity.Client;
import com.example.helppsy.entity.Psychologist;
import com.example.helppsy.entity.Review;
import com.example.helppsy.exception.ClientNotFoundException;
import com.example.helppsy.repository.AppointmentRepository;
import com.example.helppsy.repository.PsyRepository;
import com.example.helppsy.security.JWTTokenProvider;
import com.example.helppsy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/client/")
@CrossOrigin
public class ClientController {
    private AppointmentRepository appointmentRepository;
    private PsyRepository psyRepository;
    private ClientService clientService;
    private AppointmentService appointmentService;
    private PsyService psyService;
    private ReviewService reviewService;
    private RefreshTokenService refreshTokenService;
    private JWTTokenProvider jwtTokenProvider;

    @Autowired
    public ClientController(AppointmentRepository appointmentRepository,
                            PsyRepository psyRepository,
                            ClientService clientService,
                            AppointmentService appointmentService,
                            PsyService psyService,
                            ReviewService reviewService,
                            RefreshTokenService refreshTokenService,
                            JWTTokenProvider jwtTokenProvider){
        this.appointmentRepository = appointmentRepository;
        this.psyRepository = psyRepository;
        this.clientService = clientService;
        this.appointmentService = appointmentService;
        this.psyService = psyService;
        this.reviewService = reviewService;
        this.refreshTokenService = refreshTokenService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("myProfile")
    public ResponseEntity<ClientDTO> clientInfo(Principal principal){
        return new ResponseEntity<>(clientService.clientToClientDTO(clientService.getClientByPrincipal(principal)), HttpStatus.OK);
    }

    @PostMapping("myProfile")
    public ResponseEntity<Object> clientInit(@RequestBody ClientDTO clientDTO){
        Client client = clientService.updateClient(clientDTO);
        return new ResponseEntity<>(clientService.clientToClientDTO(client), HttpStatus.OK);
    }

    @PostMapping("uploadPhoto")
    public ResponseEntity<Object> uploadImageToClient(@RequestParam("photo")MultipartFile file,
                                                      Principal principal) throws IOException {
        byte[] photo = clientService.uploadPhoto(file, principal);

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @PostMapping("deletePhoto")
    public ResponseEntity<Object> deleteProfilePhoto(Principal principal){
        return new ResponseEntity<>(clientService.deletePhoto(principal), HttpStatus.OK);
    }

    @GetMapping("psys")
    public ResponseEntity<List<PsychologistDTO>> allPsys(){
        System.out.println("allpsys1111");
        List<PsychologistDTO> psys = psyRepository
                .findAll().stream().filter(Psychologist::isStatus)
                .map(e -> psyService.psychologistToPsychologistDTO(e))
                .collect(Collectors.toList());
        return new ResponseEntity<>(psys, HttpStatus.OK);
    }

    @GetMapping("psy/{id}")
    public ResponseEntity<PsychologistDTO> choicePsy(@PathVariable int id){
        Psychologist psy = psyRepository.findById(id)
                .orElseThrow(()-> new UsernameNotFoundException("Psy with id: " + id + " not found"));
        PsychologistDTO psychologistDTO = psyService.psychologistToPsychologistDTO(psy);
        return new ResponseEntity<>(psychologistDTO, HttpStatus.OK);
    }

    @PostMapping("psy/{id}")
    public ResponseEntity<Object> createAppoint(@RequestBody AppointmentClientDTO appointmentClientDTO,
                                                    @PathVariable("id") int id,
                                                    Principal principal){
        System.out.println("createAppoint");
        return new ResponseEntity<>(appointmentService.createAppointment(appointmentClientDTO, clientService.getCurrentClient(principal), id),
                HttpStatus.OK);
    }

    @GetMapping("currentAppointments")
    public ResponseEntity<List<AppointmentClientDTO>> currentApppoints(Principal principal){
        return new ResponseEntity<>(clientService.currentAppointments(principal).stream()
                .map(e -> appointmentService.appointmentToAppointmentClDto(e))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("finishApps")
    public ResponseEntity<List<AppointmentClientDTO>> closeApppoints(Principal principal){
        return new ResponseEntity<>(clientService.appointments(principal).stream()
                .map(e -> appointmentService.appointmentToAppointmentClDto(e))
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("closeApp/{id}")
    public  ResponseEntity<Object> concretApp(@PathVariable("id") int id){
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Appointment with id: " + id + " not found"));

        if (appointment.isStatus())
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        AppointmentClientDTO appointmentClientDTO = appointmentService.appointmentToAppointmentClDto(appointment);
        appointmentClientDTO.setPsyId(appointment.getPsychologist().getId());
        return new ResponseEntity<>(appointmentClientDTO, HttpStatus.OK);
    }

    @PostMapping("closeApp/{id}")
    public ResponseEntity<Object> reviewSave(@PathVariable("id") int id,
                                                 @RequestBody ReviewDTO reviewDTO,
                                                 Principal principal){
        System.out.println("1111111111 " + reviewDTO);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(ClientNotFoundException::new);

        if (appointment.isStatus()){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        System.out.println("222222222222 ");

        Psychologist psychologist = appointment.getPsychologist();
        Review review = reviewService.createReview(psychologist, reviewDTO, principal);
        //TODO
        appointmentRepository.delete(appointment);

        return new ResponseEntity<>(reviewService.reviewToReviewDTO(review), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Principal principal) {
        refreshTokenService.deleteByUserId(clientService.getCurrentClient(principal).getId());

        ResponseCookie jwtRefreshCookie = jwtTokenProvider.getCleanJwtRefreshCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body("OK");
    }


    @ExceptionHandler
    private ResponseEntity<ClientErrorResponse> notFoundId(ClientNotFoundException cnfe){
        return new ResponseEntity<>(new ClientErrorResponse("Not Found By Id!",
                System.currentTimeMillis()), HttpStatus.NOT_FOUND);
    }
}
