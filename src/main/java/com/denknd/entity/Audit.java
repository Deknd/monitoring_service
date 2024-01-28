package com.denknd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Audit {
    private Long auditId;
    private User user;
    private String operation;
    private OffsetDateTime operationTime;
}
