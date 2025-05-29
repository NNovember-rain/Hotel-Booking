package com.NotificationService.entity;

import com.NotificationService.enums.NotificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification extends BaseEntity{
    @Column
    String title;

    @Column
    String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    NotificationStatus status;

    @Column(nullable = false)
    Long profileId;

    @Column(nullable = false)
    String userId;

}
