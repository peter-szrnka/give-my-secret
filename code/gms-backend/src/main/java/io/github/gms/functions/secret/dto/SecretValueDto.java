package io.github.gms.functions.secret.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecretValueDto {

    private Long keystoreId;
    private Long keystoreAliasId;
    private Map<String, String> secretValues;
}
