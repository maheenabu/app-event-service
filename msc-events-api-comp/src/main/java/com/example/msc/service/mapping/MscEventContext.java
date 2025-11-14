package com.example.msc.service.mapping;

import java.util.Map;

public class MscEventContext {

    private final String eventStatus;
    private final String delChanId;
    private final Map<String, Object> dynamicBody;

    public MscEventContext(String eventStatus, String delChanId, Map<String, Object> dynamicBody) {
        this.eventStatus = eventStatus;
        this.delChanId = delChanId;
        this.dynamicBody = dynamicBody;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public String getDelChanId() {
        return delChanId;
    }

    public Map<String, Object> getDynamicBody() {
        return dynamicBody;
    }
}
