package com.denknd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypeMeter {
    private Long typeMeterId;
    private String typeCode;
    private String typeDescription;
    private String metric;
}
