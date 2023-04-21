package com.example.helppsy.controller;

import com.example.helppsy.dto.ClientDTO;
import com.example.helppsy.entity.Client;
import com.example.helppsy.entity.RefreshToken;
import com.example.helppsy.payload.request.LoginRequest;
import com.example.helppsy.payload.request.RegisterRequest;
import com.example.helppsy.payload.request.TokenRefreshRequest;
import com.example.helppsy.payload.response.JWTTokenSuccessResponse;
import com.example.helppsy.payload.response.TokenRefreshResponse;
import com.example.helppsy.repository.ClientRepository;
import com.example.helppsy.repository.RefreshTokenRepository;
import com.example.helppsy.security.JWTTokenProvider;
import com.example.helppsy.security.SecurityConstants;
import com.example.helppsy.service.ClientService;
import com.example.helppsy.service.RefreshTokenService;
import com.example.helppsy.validations.ClientValidator;
import com.example.helppsy.validations.ResponseErrorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
@PreAuthorize("permitAll()")
public class AuthorizeConroller {
    private JWTTokenProvider jwtTokenProvider;
    private AuthenticationManager authenticationManager;
    private ResponseErrorValidation responseErrorValidation;
    private ClientService clientService;
    private ClientValidator clientValidator;
    private RefreshTokenService refreshTokenService;

    @Autowired
    public AuthorizeConroller(JWTTokenProvider jwtTokenProvider,
                              AuthenticationManager authenticationManager,
                              ResponseErrorValidation responseErrorValidation,
                              ClientService clientService,
                              ClientValidator clientValidator,
                              RefreshTokenService refreshTokenService){
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.responseErrorValidation = responseErrorValidation;
        this.clientService = clientService;
        this.clientValidator = clientValidator;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/registration")
    public ResponseEntity<Object> newClient(@RequestBody @Valid RegisterRequest client,
                                               BindingResult bindingResult){
        clientValidator.validate(client, bindingResult);

        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors))
            return errors;

        ClientDTO clientDTO = clientService.createClient(client);

        return ResponseEntity.ok(clientDTO);
    }

    @GetMapping("/activate/{code}")
    public ResponseEntity<Object> activate(@PathVariable String code){
        boolean isActivated = clientService.activateClient(code);

        if(!isActivated){
            return new ResponseEntity<>(isActivated, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(isActivated, HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        System.out.println("11111111");
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors))
            return errors;
        System.out.println("222222");

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));

        System.out.println("33333333");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Client client = (Client) authentication.getPrincipal();
        String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(client);

        System.out.println("4444444");
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(client.getId());
        ResponseCookie jwtRefreshCookie = jwtTokenProvider.generateRefreshJwtCookie(refreshToken.getToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(new JWTTokenSuccessResponse(true, jwt, refreshToken.getToken()));
    }



    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.getJwtRefreshFromCookies(request);

        if ((refreshToken != null) && (refreshToken.length() > 0)) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getClient)
                    .map(client -> {
                        String token = "Bearer " + jwtTokenProvider.generateToken(client);
                        refreshTokenService.deleteByUserId(client.getId());

                        RefreshToken refToken = refreshTokenService.createRefreshToken(client.getId());
                        ResponseCookie jwtRefreshCookie = jwtTokenProvider.generateRefreshJwtCookie(refToken.getToken());

                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                                .body(new JWTTokenSuccessResponse(true, token, refToken.getToken()));
                    })
                    .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
        }

        return ResponseEntity.badRequest().body("Refresh Token is empty!");
    }

//    @PostMapping("/refreshtoken")
//    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
//        String requestRefreshToken = request.getRefreshToken();
//
//        return refreshTokenService.findByToken(requestRefreshToken)
//                .map(refreshTokenService::verifyExpiration)
//                .map(RefreshToken::getClient)
//                .map(client -> {
//                    String token = "Bearer " + jwtTokenProvider.generateToken(client);
//                    refreshTokenService.deleteByUserId(client.getId());
//                    String refToken = refreshTokenService.createRefreshToken(client.getId()).getToken();
//                    return ResponseEntity.ok(new TokenRefreshResponse(token, refToken));
//                })
//                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
//    }
}

