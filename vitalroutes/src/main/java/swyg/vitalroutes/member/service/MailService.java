package swyg.vitalroutes.member.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendEmail(String name, String email, String profile, String url) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("VitalRoutes 비밀번호 재설정 링크입니다");
        message.setText(setContent(name, profile, url), "utf-8", "html");
        mailSender.send(message);
    }

    public String setContent(String name, String profile, String url) {
        Context context = new Context();
        context.setVariable("name", "Hi, " + name);
        context.setVariable("profile", profile);
        context.setVariable("url", url);
        return templateEngine.process("mailTemplate", context);
    }

}
