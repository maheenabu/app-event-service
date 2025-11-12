package com.example.csm.app.service;

import com.example.csm.app.dto.MSCEmployeeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class MSCEventsServiceImpl implements MSCEventsService {
    private static final Logger log = LoggerFactory.getLogger(MSCEventsServiceImpl.class);

    @Override
    public void processBatch(List<MSCEmployeeEvent> items) {
        items.forEach(e -> log.info("Processing empId={} formId={} session={} status={} delivery={} extras={}",
                e.getEmpId(), e.getFormId(), e.getSvpSessionId(), e.getEventStatus(),
                e.getDeliveryChannelId(), e.getExtras()));
    }
}
