package io.github.gms.secure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeystoreListDto {

	private List<KeystoreDto> resultList;
}
