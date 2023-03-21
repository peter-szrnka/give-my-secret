package io.github.gms.secure.service.impl;

import io.github.gms.common.enums.MdcParameter;
import io.github.gms.common.exception.GmsException;
import io.github.gms.secure.dto.KeystoreAliasDto;
import io.github.gms.secure.dto.SaveKeystoreRequestDto;
import io.github.gms.secure.entity.UserEntity;
import io.github.gms.secure.repository.KeystoreRepository;
import io.github.gms.secure.repository.UserRepository;
import io.github.gms.secure.service.KeystoreFileService;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Stream;

import static io.github.gms.common.util.Constants.ENTITY_NOT_FOUND;

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
	@Autowired
	private UserRepository userRepository;
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
		try {
			KeyStore ks = KeyStore.getInstance(dto.getType().name());

			char[] password = dto.getCredential().toCharArray();
			ks.load(null, password);

			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
			keyPairGen.initialize(2048);
			KeyPair keyPair = keyPairGen.generateKeyPair();
			X509Certificate certificate = generateCertificate(keyPair);

			for (KeystoreAliasDto alias : dto.getAliases()) {
				ks.setKeyEntry(alias.getAlias(),
						keyPair.getPrivate(),
						alias.getAliasCredential().toCharArray(),
						new X509Certificate[] { certificate });
			}

			String newKeystoreName = UUID.randomUUID() + "." + dto.getType().getFileExtension();
			FileOutputStream fos = new FileOutputStream(keystoreTempPath + newKeystoreName, false);
			ks.store(fos, password);
			fos.close();

			return newKeystoreName;
		} catch (Exception e) {
			throw new GmsException(e);
		}
	}

	private boolean deleteTempKeystoreFile(Path path) {
		return path.toFile().delete();
	}

	private Path getFileNameIfNotExists(Path path) {
		return repository.findByFileName(path.toFile().getName()) != null ? null : path;
	}

	private X509Certificate generateCertificate(KeyPair keyPair) throws Exception {
		final Instant now = Instant.now();
		final Date notBefore = Date.from(now);
		final Date until = GregorianCalendar.from(ZonedDateTime.now().plusYears(1L)).getTime();

		UserEntity user = userRepository.findById(getUserId()).orElseThrow(() -> new GmsException(ENTITY_NOT_FOUND));

		final ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WITHRSA").build(keyPair.getPrivate());
		final X500Name x500Name = new X500Name("CN=" + user.getName() + ",O=" + user.getName() + ",L=NA,ST=NA");
		final X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(x500Name,
				BigInteger.valueOf(now.toEpochMilli()), notBefore, until, x500Name, keyPair.getPublic());
		return new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider())
				.getCertificate(certificateBuilder.build(contentSigner));
	}

	private Long getUserId() {
		return Long.parseLong(MDC.get(MdcParameter.USER_ID.getDisplayName()));
	}
}