package io.github.gms.functions.secret.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecretValueDto {

    private Long keystoreId;
    private Long keystoreAliasId;
    private String value;
}
