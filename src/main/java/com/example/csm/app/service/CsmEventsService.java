package com.example.csm.app.service;

import com.example.csm.app.dto.CsmEmployeeEvent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public interface CsmEventsService {
    void processBatch(@NotEmpty List<@Valid CsmEmployeeEvent> items);
}
