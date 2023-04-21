package com.example.helppsy.controller;

import com.example.helppsy.entity.Psychologist;
import com.example.helppsy.repository.PsyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private PsyRepository psyRepository;

    @Autowired
    public AdminController(PsyRepository psyRepository){
        this.psyRepository = psyRepository;
    }

    @GetMapping("/psy")
    public List<Psychologist> psychologists(){
        return psyRepository.findAll()
                .stream().filter(e -> !e.isStatus())
                .collect(Collectors.toList());
    }

    @GetMapping("/psy/{id}")
    public Psychologist condidatPage(@PathVariable("id") int id){
        Optional<Psychologist> psy = psyRepository.findById(id);
        if(psy.isEmpty()){
            return null;
        }
        return psy.get();
    }

    @PostMapping("/psy/{id}")
    public String savePsy(@PathVariable("id") int id,
                          @RequestBody Psychologist psychologist){
        Optional<Psychologist> psychologistOptional = psyRepository.findById(id);
        if (psychologistOptional.isEmpty())
            return "not id";
        if (psychologist.isStatus()){
            Psychologist psychologistDB = psychologistOptional.get();
            psychologistDB.setStatus(true);
            psyRepository.save(psychologistDB);
        } else {
            psyRepository.deleteById(id);
        }
        return "ok";
    }
}
