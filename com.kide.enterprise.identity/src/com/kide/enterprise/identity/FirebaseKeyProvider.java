package com.kide.enterprise.identity;

import java.security.PublicKey;

@FunctionalInterface
public interface FirebaseKeyProvider {
    PublicKey key(String keyId);
}
