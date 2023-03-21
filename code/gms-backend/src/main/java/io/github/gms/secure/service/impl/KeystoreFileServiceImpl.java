package io.github.gms.secure.service.impl;

import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.service.KeystoreFileService;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Slf4j
@Service
public class KeystoreFileServiceImpl implements KeystoreFileService {
	
	static {	
		Security.setProperty("crypto.policy", "unlimited");
		Security.addProvider(new BouncyCastleProvider());
	}

	@Autowired
	private KeystoreRepository repository;

	@Value("${config.location.keystoreTemp.path}")
	private String keystoreTempPath;

	@Override
	public long deleteTempKeystoreFiles()  {
		try (Stream<Path> fileList = Files.list(Paths.get(keystoreTempPath)).parallel()) {
			return fileList.filter(path -> !path.toFile().isDirectory())
					.map(this::getFileNameIfNotExists)
					.filter(Objects::nonNull)
					.map(this::deleteTempKeystoreFile)
					.filter(Boolean.TRUE::equals)
					.count();
		} catch (Exception e) {
			throw new GmsException(e);
		}
	}

	@Override
	public String generate(SaveKeystoreRequestDto dto) {
		return "generated.jks";
	}

	private boolean deleteTempKeystoreFile(Path path) {
		return path.toFile().delete();
	}

	private Path getFileNameIfNotExists(Path path) {
		return repository.findByFileName(path.toFile().getName()) != null ? null : path;
	}
}