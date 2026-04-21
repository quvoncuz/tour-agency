package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quvoncuz.service.EmailSenderService;
import quvoncuz.service.NotificationService;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {


    private final EmailSenderService emailSenderService;

    @Override
    public void sendNotificationForTourUpdate(String to, String tourName, Long old, Long newP) {
        emailSenderService.sendPriceChangeNotification(to, tourName, old, newP);
    }
}
