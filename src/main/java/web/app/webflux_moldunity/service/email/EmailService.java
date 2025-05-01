package web.app.webflux_moldunity.service.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class EmailService {
    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender mailSender) {
        this.emailSender = mailSender;
    }

    public Mono<Void> sendEmail(String to, String from, String subject, String body) {
        return Mono.fromRunnable(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(body);
            emailSender.send(message);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
