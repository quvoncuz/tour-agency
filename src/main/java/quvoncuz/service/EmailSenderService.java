package quvoncuz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSenderImpl mailSender;

    private void emailSender(String toMail, String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("quvoncuz@mail.ru");
        mailMessage.setTo(toMail);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        mailSender.send(mailMessage);
    }

    @Async
    public void sendPriceChangeNotification(String to, String tourName,
                                            Long oldPrice, Long newPrice) {
        String subject = "Tur narxi o'zgardi!";
        String text = String.format("""
                Hurmatli mijoz!
                
                Siz buyurtma bergan "%s" turi narxi o'zgardi:
                
                Eski narx:  %s $
                Yangi narx: %s $
                
                Tasdiqlash:   http://localhost:8080/bookings/waiting
                """, tourName, oldPrice, newPrice);

        emailSender(to, subject, text);
    }
}
