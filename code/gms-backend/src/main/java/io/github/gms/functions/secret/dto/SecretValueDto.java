package io.github.gms.functions.secret.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecretValueDto {

    private Long keystoreId;
    private Long keystoreAliasId;
    private String value;
}
