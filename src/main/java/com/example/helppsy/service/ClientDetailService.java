package com.example.helppsy.service;

import com.example.helppsy.entity.Client;
import com.example.helppsy.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientDetailService implements UserDetailsService {

    private ClientRepository clientRepository;

    @Autowired
    public ClientDetailService(ClientRepository clientRepository){
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Client> client = clientRepository.findByEmail(username);
        if (client.isEmpty()){
            throw new UsernameNotFoundException("username not found");
        }

        return build(client.get());
    }

    public Client loadClientById(int id){
        return clientRepository.findById(id).orElse(null);
    }

    public static Client build(Client client){
        List<GrantedAuthority> authorities = client.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());

        return new Client(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPassword(),
                client.isStatus(),
                authorities
        );
    }
}
