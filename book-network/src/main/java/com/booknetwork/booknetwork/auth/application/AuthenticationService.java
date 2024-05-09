package com.booknetwork.booknetwork.auth.application;

import com.booknetwork.booknetwork.auth.domain.AuthenticationRequest;
import com.booknetwork.booknetwork.auth.domain.AuthenticationResponse;
import com.booknetwork.booknetwork.auth.domain.RegistrationRequest;
import com.booknetwork.booknetwork.email.application.EmailService;
import com.booknetwork.booknetwork.email.domain.EmailTemplateName;
import com.booknetwork.booknetwork.role.domain.RoleRepository;
import com.booknetwork.booknetwork.security.JwtService;
import com.booknetwork.booknetwork.user.domain.Token;
import com.booknetwork.booknetwork.user.domain.TokenRepository;
import com.booknetwork.booknetwork.user.domain.User;
import com.booknetwork.booknetwork.user.domain.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder  passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${application.security.mail.frontend.activation-url}")
    private String activationUrl;


    public void register(RegistrationRequest request) throws MessagingException{
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER WAS NOT  initialized"));

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
        );

        var claims = new HashMap<String,Object>();
        var user = (User) auth.getPrincipal();
        claims.put("fullName", user.getFullname());
        var jwtToken = jwtService.generateToken(claims,user);


        return AuthenticationResponse.builder()
                .token(jwtToken).build();
    }



    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        //Send email
        emailService.sendSimpleMail(
                user.getEmail(),
                user.getFullname(),
                EmailTemplateName.ACTIVE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"

        );



    }

    private String generateAndSaveActivationToken(User user) {
        //Generate token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepository.save(token);

        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();

        for(int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length()); //0 to 9
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();

    }


    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(()-> new RuntimeException("Token not found"));

        if(LocalDateTime.now().isAfter(savedToken.getExpireTime())){
            sendValidationEmail(savedToken.getUser());
            throw  new RuntimeException("Token is expired, A new token has been generated");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);

        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}
