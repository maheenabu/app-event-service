package com.example.msc.app.service;

import com.example.msc.app.dto.MSCEmployeeEvent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public interface MSCEventsService {
    void processBatch(@NotEmpty List<@Valid MSCEmployeeEvent> items);
}
