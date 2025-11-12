package com.example.csm.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;

public final class CsmEmployeeEvent {
    @NotBlank private final String eventStatus;
    @NotBlank private final String deliveryChannelId;
    @NotBlank private final String svpSessionId;
    @NotBlank private final String formId;
    @NotBlank private final String empId;
    @NotNull  private final Map<String, Object> extras;

    private CsmEmployeeEvent(Builder b) {
        this.eventStatus = b.eventStatus;
        this.deliveryChannelId = b.deliveryChannelId;
        this.svpSessionId = b.svpSessionId;
        this.formId = b.formId;
        this.empId = b.empId;
        this.extras = b.extras == null ? Collections.emptyMap() : Collections.unmodifiableMap(b.extras);
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String eventStatus, deliveryChannelId, svpSessionId, formId, empId;
        private Map<String, Object> extras;
        public Builder eventStatus(String v){ this.eventStatus=v; return this; }
        public Builder deliveryChannelId(String v){ this.deliveryChannelId=v; return this; }
        public Builder svpSessionId(String v){ this.svpSessionId=v; return this; }
        public Builder formId(String v){ this.formId=v; return this; }
        public Builder empId(String v){ this.empId=v; return this; }
        public Builder extras(Map<String,Object> v){ this.extras=v; return this; }
        public CsmEmployeeEvent build(){ return new CsmEmployeeEvent(this); }
    }

    public String getEventStatus(){ return eventStatus; }
    public String getDeliveryChannelId(){ return deliveryChannelId; }
    public String getSvpSessionId(){ return svpSessionId; }
    public String getFormId(){ return formId; }
    public String getEmpId(){ return empId; }
    public Map<String, Object> getExtras(){ return extras; }
}
