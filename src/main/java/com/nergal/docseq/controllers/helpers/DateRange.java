package com.nergal.docseq.controllers.helpers;

import java.time.LocalDateTime;

public class DateRange {

    int year = LocalDateTime.now().getYear();
    
    LocalDateTime initialDateTime = LocalDateTime.of(year, 1, 1, 0, 0, 0);
    LocalDateTime endDateTime = LocalDateTime.of(year, 12, 31, 23, 59, 59);

    public LocalDateTime getInitialDateTime() {
        return initialDateTime;
    }
    
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }
}
