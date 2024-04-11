package io.github.gms.functions.keystore;

import io.github.gms.common.abstraction.AbstractCrudService;
import io.github.gms.common.abstraction.BatchDeletionService;
import io.github.gms.functions.secret.GetSecureValueDto;
import io.github.gms.common.dto.IdNamePairListDto;
import io.github.gms.common.dto.SaveEntityResponseDto;
import io.github.gms.common.service.CountService;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
public interface KeystoreService extends AbstractCrudService<SaveKeystoreRequestDto, SaveEntityResponseDto, KeystoreDto, KeystoreListDto>, CountService,
		BatchDeletionService {

	SaveEntityResponseDto save(String model, MultipartFile file);

	String getValue(GetSecureValueDto dto);

	IdNamePairListDto getAllKeystoreNames();
	
	IdNamePairListDto getAllKeystoreAliasNames(Long keystoreId);

	DownloadFileResponseDto downloadKeystore(Long keystoreId);
}
