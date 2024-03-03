package io.github.gms.common.model;

import lombok.Getter;

/**
 * @author Peter Szrnka
 * @since 1.0
 */
@Getter
public enum EnabledAlgorithm {
    SHA1WITHRSA("SHA1WITHRSA"),
    SHA224WITHRSA("SHA224WITHRSA"),
    SHA256WITHRSA("SHA256WITHRSA"),
    SHA384WITHRSA("SHA384WITHRSA"),
    SHA512WITHRSA("SHA512WITHRSA"),
    SHA512_224_WITHRSA("SHA512(224)WITHRSA"),
    SHA512_256_WITHRSA("SHA512(256)WITHRSA"),
    SHA1WITHRSAANDMGF1("SHA1WITHRSAANDMGF1"),
    SHA224WITHRSAANDMGF1("SHA224WITHRSAANDMGF1"),
    SHA256WITHRSAANDMGF1("SHA256WITHRSAANDMGF1"),
    SHA384WITHRSAANDMGF1("SHA384WITHRSAANDMGF1"),
    SHA512WITHRSAANDMGF1("SHA512WITHRSAANDMGF1");

    private EnabledAlgorithm(String displayName) {
        this.displayName = displayName;
    }

    private final String displayName;

    public static EnabledAlgorithm getByName(String name) {
        try {
            return EnabledAlgorithm.valueOf(name);
        } catch (Exception e) {
            return EnabledAlgorithm.SHA256WITHRSA;
        }
    }
}
