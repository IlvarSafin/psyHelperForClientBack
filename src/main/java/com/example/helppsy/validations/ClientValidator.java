package com.example.helppsy.validations;

import com.example.helppsy.entity.Client;
import com.example.helppsy.payload.request.RegisterRequest;
import com.example.helppsy.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ClientValidator implements Validator {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientValidator(ClientRepository clientRepository){
        this.clientRepository = clientRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Client.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegisterRequest client = (RegisterRequest) target;
        if (clientRepository.findByEmail(client.getEmail()).isPresent()){
            errors.rejectValue("email", "", "Сlient with this email exists");
        }
    }
}
