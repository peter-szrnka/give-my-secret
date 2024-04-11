package io.github.gms.functions.gdpr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchUserOperationDto {

    private String requestId;
    private Set<Long> userIds;
}
