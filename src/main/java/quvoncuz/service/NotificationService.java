package quvoncuz.service;

public interface NotificationService {

    void sendNotificationForTourUpdate(String to, String tourName, Long old, Long newP);
}
