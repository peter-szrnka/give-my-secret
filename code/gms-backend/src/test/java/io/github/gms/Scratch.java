package io.github.gms;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static io.github.gms.common.util.Constants.LDAP_CRYPT_PREFIX;

class Scratch {
    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(LDAP_CRYPT_PREFIX + bCryptPasswordEncoder.encode("Test1234"));
    }
}