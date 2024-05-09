package com.booknetwork.booknetwork.email.application;

import com.booknetwork.booknetwork.email.domain.EmailTemplateName;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendSimpleMail(String to,
                               String username,
                               EmailTemplateName emailTemplate,
                               String confirmUrl,
                               String activationCode,
                               String subject) throws MessagingException {

        String templateName;
        if(emailTemplate == null){
            templateName = "confirm-email";
        }else {
            templateName = emailTemplate.getName();
        }

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name());

        Map<String, Object> properties = new HashMap<>();
        properties.put("username",username);
        properties.put("confirmationUrl",confirmUrl);
        properties.put("activation_code",activationCode);


        Context context = new Context();
        context.setVariables(properties);

        mimeMessageHelper.setFrom("juanuchdzib@gmail.com");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);

        String template = templateEngine.process(templateName, context);

        mimeMessageHelper.setText(template,true);
        javaMailSender.send(mimeMessage);

    }
}
