package com.restaurant.doantotnghiep.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendReservationPaymentEmail(
            String to,
            String name,
            String branch,
            String table,
            String time,
            String code,
            double amount) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("branch", branch);
            context.setVariable("table", table);
            context.setVariable("time", time);
            context.setVariable("code", code);
            context.setVariable("amount", amount);

            String html = templateEngine.process("email/reservation", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Xác nhận đặt bàn");
            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
}