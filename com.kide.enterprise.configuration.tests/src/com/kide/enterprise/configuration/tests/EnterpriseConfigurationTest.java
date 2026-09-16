package com.kide.enterprise.configuration.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import org.junit.Test;

import com.kide.enterprise.configuration.ConfigurationException;
import com.kide.enterprise.configuration.ConfigurationLayer;
import com.kide.enterprise.configuration.ConfigurationValue;
import com.kide.enterprise.configuration.EnterpriseConfiguration;
import com.kide.enterprise.configuration.SecretReference;
import com.kide.enterprise.configuration.SecretResolver;
import com.kide.enterprise.configuration.SecretResolverRegistry;
import com.kide.enterprise.configuration.SecretValue;

public class EnterpriseConfigurationTest {

    @Test
    public void higherLayersOverrideLowerLayersWithProvenance() throws Exception {
        Path root = Files.createTempDirectory("kide-e03-");
        try {
            Path installation = write(root.resolve("installation.properties"), "service.endpoint=https://install.example\n");
            Path user = write(root.resolve("user.properties"), "service.endpoint=https://user.example\n");
            Path workspace = write(root.resolve("workspace.properties"), "service.endpoint=https://workspace.example\n");
            Path project = write(root.resolve("project.properties"), "service.endpoint=https://project.example\n");

            EnterpriseConfiguration config = EnterpriseConfiguration.load(
                    Collections.singletonMap("service.endpoint", "https://default.example"),
                    installation, user, workspace, project, new SecretResolverRegistry());

            assertEquals("https://project.example", config.get("service.endpoint", null));
            ConfigurationValue value = config.find("service.endpoint").get();
            assertEquals(ConfigurationLayer.PROJECT, value.layer());
            assertEquals(project.toAbsolutePath().normalize(), value.source());
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void sensitiveLiteralIsRejectedWithoutEchoingSecret() throws Exception {
        Path root = Files.createTempDirectory("kide-e03-");
        String literal = "do-not-echo-this-secret";
        try {
            Path workspace = write(root.resolve("workspace.properties"), "provider.apiKey=" + literal + "\n");
            try {
                EnterpriseConfiguration.load(Collections.emptyMap(), null, null, workspace, null,
                        new SecretResolverRegistry());
                fail("Expected literal secret rejection");
            } catch (ConfigurationException e) {
                assertTrue(e.getMessage().contains("provider.apiKey"));
                assertFalse(e.getMessage().contains(literal));
            }
        } finally {
            deleteTree(root);
        }
    }

    @Test
    public void secretReferenceResolvesOnlyThroughSecretApi() {
        SecretResolverRegistry registry = new SecretResolverRegistry().register(new SecretResolver() {
            @Override
            public String provider() {
                return "test";
            }

            @Override
            public SecretValue resolve(String key) {
                assertEquals("primary", key);
                return new SecretValue("expected-secret".toCharArray());
            }
        });
        EnterpriseConfiguration config = EnterpriseConfiguration.load(
                Collections.singletonMap("provider.password", "secret://test/primary"),
                null, null, null, null, registry);

        try {
            config.get("provider.password", null);
            fail("Normal configuration API must not return secret references as values");
        } catch (ConfigurationException expected) {
            assertTrue(expected.getMessage().contains("getSecret"));
        }

        SecretValue secret = config.getSecret("provider.password");
        assertEquals("[REDACTED]", secret.toString());
        assertArrayEquals("expected-secret".toCharArray(), secret.copy());
        secret.close();
        try {
            secret.copy();
            fail("Closed secret must not remain readable");
        } catch (IllegalStateException expected) {
            // expected
        }
    }

    @Test
    public void secretReferenceGrammarRejectsEmbeddedCredentialsAndTraversal() {
        assertEquals("secure", SecretReference.parse("secret://secure/build-signing").provider());
        assertEquals("BUILD_TOKEN", SecretReference.parse("secret://env/BUILD_TOKEN").key());
        assertInvalid("secret://user:password@secure/key");
        assertInvalid("secret://secure/a/b");
        assertInvalid("https://secure/key");
    }

    private static void assertInvalid(String value) {
        try {
            SecretReference.parse(value);
            fail("Expected invalid reference: " + value);
        } catch (ConfigurationException expected) {
            // expected
        }
    }

    private static Path write(Path path, String content) throws IOException {
        Files.writeString(path, content);
        return path;
    }

    private static void deleteTree(Path root) throws IOException {
        if (root == null || !Files.exists(root)) {
            return;
        }
        try (java.util.stream.Stream<Path> paths = Files.walk(root)) {
            paths.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
