package io.github.gms.functions.systemproperty;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.gms.common.enums.PropertyType;
import io.github.gms.common.enums.SystemPropertyCategory;
import io.github.gms.common.types.Sensitive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

import static io.github.gms.common.util.Constants.DATE_FORMAT;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemPropertyDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 9035787175353459355L;
	
	private String key;
	@Sensitive
	private String value;
	private PropertyType type;
	private SystemPropertyCategory category;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private ZonedDateTime lastModified;
	private boolean factoryValue;
}