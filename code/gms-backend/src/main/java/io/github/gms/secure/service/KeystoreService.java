package io.github.gms.secure.service;

import org.springframework.web.multipart.MultipartFile;

import io.github.gms.common.abstraction.AbstractCrudService;
import io.github.gms.secure.dto.DownloadFileResponseDto;
import io.github.gms.secure.dto.GetSecureValueDto;
import io.github.gms.secure.dto.IdNamePairListDto;
import io.github.gms.secure.dto.KeystoreDto;
import io.github.gms.secure.dto.KeystoreListDto;
import io.github.gms.secure.dto.SaveEntityResponseDto;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface KeystoreService extends AbstractCrudService<SaveKeystoreRequestDto, SaveEntityResponseDto, KeystoreDto, KeystoreListDto>, CountService {

	SaveEntityResponseDto save(String model, MultipartFile file);

	String getValue(GetSecureValueDto dto);

	IdNamePairListDto getAllKeystoreNames();
	
	IdNamePairListDto getAllKeystoreAliasNames(Long keystoreId);

	DownloadFileResponseDto downloadKeystore(Long keystoreId);
}
