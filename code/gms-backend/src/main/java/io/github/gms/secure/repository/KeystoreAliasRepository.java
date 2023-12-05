package io.github.gms.secure.repository;

import io.github.gms.secure.dto.IdNamePairDto;
import io.github.gms.secure.entity.KeystoreAliasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Repository
public interface KeystoreAliasRepository extends JpaRepository<KeystoreAliasEntity, Long> {

	List<KeystoreAliasEntity> findAllByKeystoreId(Long keystoreId);

	void deleteByKeystoreId(Long keystoreId);

	Optional<KeystoreAliasEntity> findByIdAndKeystoreId(Long id, Long keystoreId);

	@Query("select new io.github.gms.secure.dto.IdNamePairDto(a.id,a.alias) from KeystoreAliasEntity a where a.keystoreId = :keystoreId")
	List<IdNamePairDto> getAllAliasNames(@Param("keystoreId") Long keystoreId);
}