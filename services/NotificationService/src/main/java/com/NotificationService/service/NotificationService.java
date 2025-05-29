package com.NotificationService.service;

import com.NotificationService.events.NotificationEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static com.mysql.cj.util.TimeUtil.DATE_FORMATTER;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    String fromEmailAddress;

    @KafkaListener(topics = "notification.send", groupId = "notification-service-group")
    public void handleRoomNotification(String message) {
        try {
            NotificationEvent event = objectMapper.readValue(message, NotificationEvent.class);

            String recipientEmail = event.getEmail();
            String emailSubject;
            String emailBody;
            String formattedCheckIn = event.getCheckInDate() != null ? event.getCheckInDate().format(DATE_FORMATTER) : "N/A";
            String formattedCheckOut = event.getCheckOutDate() != null ? event.getCheckOutDate().format(DATE_FORMATTER) : "N/A";
            String price = event.getPrice()+"đ";

            emailSubject = "Thông báo cập nhật trạng thái phòng bạn quan tâm";
            emailBody = String.format(
                    "Chào bạn,\n\n" +
                            "Phòng #%s tại khách sạn #%s mà bạn theo dõi hiện đã TRỐNG.\n" +
                            "Thông tin chi tiết:\n" +
                            "- Ngày nhận phòng: %s\n" +
                            "- Ngày trả phòng: %s\n\n" +
                            "- Giá phòng: %s\n\n" +
                            "Vui lòng truy cập hệ thống để biết thêm chi tiết và đặt phòng nếu bạn muốn.\n\n" +
                            "Trân trọng !",
                    event.getRoomNumber(), event.getHotelName(), formattedCheckIn, formattedCheckOut,price
            );

            sendActualEmail(recipientEmail, emailSubject, emailBody);

        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON message to RoomStatusEvent: {}", message, e);
        } catch (Exception e) {
            log.error("An unexpected error occurred while processing notification for message [{}]: {}", message, e.getMessage(), e);
        }
    }

    private void sendActualEmail(String to, String subject, String emailBody) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmailAddress);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(emailBody);

            javaMailSender.send(mailMessage);
            log.info("Email sent successfully to {} with subject: '{}'", to, subject);
        } catch (MailException e) {
            log.error("Failed to send email to {}. Subject: '{}'. Error: {}", to, subject, e.getMessage(), e);
        }
    }
    
}
