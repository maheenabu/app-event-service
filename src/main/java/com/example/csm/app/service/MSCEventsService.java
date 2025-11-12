package com.example.csm.app.service;

import com.example.csm.app.dto.MSCEmployeeEvent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public interface MSCEventsService {
    void processBatch(@NotEmpty List<@Valid MSCEmployeeEvent> items);
}
