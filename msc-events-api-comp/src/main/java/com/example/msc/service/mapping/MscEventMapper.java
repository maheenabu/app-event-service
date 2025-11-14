package com.example.msc.service.mapping;

import com.example.msc.model.EventHeader;
import com.example.msc.model.FormMSCEvent;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Component
public class MscEventMapper {

    public MscEventContext toContext(FormMSCEvent event) {
        EventHeader header = event.getHeader();
        Map<String, Object> dynamicBody =
                Optional.ofNullable(event.getAdditionalProperties()).orElse(Collections.emptyMap());

        return new MscEventContext(
                header.getEventStatus(),
                header.getDelChanId(),
                dynamicBody
        );
    }
}
