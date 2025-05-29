package com.HistoryService.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaveHistoryEvent {
    private Long bookingId;
    private Long profileId;
    // Getters and setters
}
