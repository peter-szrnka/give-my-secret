package io.github.gms.functions.keystore;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.gms.common.enums.EntityStatus;
import io.github.gms.common.enums.KeystoreType;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static io.github.gms.common.util.Constants.DATE_FORMAT;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeystoreDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -6962129766459594155L;

	private Long id;
	private Long userId;
	private EntityStatus status;
	private String name;
	private String fileName;
	private KeystoreType type;
	private String description;
	private String credential;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
	private ZonedDateTime creationDate;
	private List<KeystoreAliasDto> aliases = new ArrayList<>();
}
