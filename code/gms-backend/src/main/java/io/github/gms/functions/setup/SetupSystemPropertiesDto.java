package io.github.gms.functions.setup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.gms.functions.systemproperty.SystemPropertyDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SetupSystemPropertiesDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 9131525391851289031L;

    private List<SystemPropertyDto> properties;
}
