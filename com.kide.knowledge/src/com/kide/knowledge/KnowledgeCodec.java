package com.kide.knowledge;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

final class KnowledgeCodec {
    static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private KnowledgeCodec() { }

    static String canonicalJson(KnowledgeDataset dataset) {
        return GSON.toJson(dataset);
    }

    static String sha256(KnowledgeDataset dataset) {
        return sha256Text(canonicalJson(dataset));
    }

    static String sha256Text(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
