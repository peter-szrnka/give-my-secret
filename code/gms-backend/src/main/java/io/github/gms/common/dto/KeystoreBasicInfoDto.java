package io.github.gms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeystoreBasicInfoDto {

    private Long id;
    private Long userId;
    private String filename;
}
