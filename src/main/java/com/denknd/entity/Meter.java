package com.denknd.entity;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public class Meter {
    private Long id;
    private String serialNumber;
    private OffsetDateTime installationDate;
    private OffsetDateTime lastCheckDate;
    private String meterModel;
}
