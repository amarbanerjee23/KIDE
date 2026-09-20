package com.kide.languageserver.gateway;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class LspMessageFraming {
    private static final int MAX_HEADER_BYTES = 8192;

    private LspMessageFraming() { }

    public static void writeJson(OutputStream out, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        byte[] header = ("Content-Length: " + body.length + "\r\n\r\n")
                .getBytes(StandardCharsets.US_ASCII);
        synchronized (out) {
            out.write(header);
            out.write(body);
            out.flush();
        }
    }

    public static String readJson(InputStream raw, int maxBodyBytes) throws IOException {
        InputStream in = raw;
        int contentLength = -1;
        int headerBytes = 0;
        while (true) {
            String line = readLine(in, MAX_HEADER_BYTES - headerBytes);
            headerBytes += line.length() + 2;
            if (headerBytes > MAX_HEADER_BYTES) throw new IOException("LSP header exceeds limit");
            if (line.isEmpty()) break;
            int colon = line.indexOf(':');
            if (colon <= 0) throw new IOException("Malformed LSP header");
            String name = line.substring(0, colon).trim();
            String value = line.substring(colon + 1).trim();
            if ("Content-Length".equalsIgnoreCase(name)) {
                try {
                    contentLength = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new IOException("Invalid LSP Content-Length", e);
                }
            }
        }
        if (contentLength < 0 || contentLength > maxBodyBytes) {
            throw new IOException("LSP body length is outside configured limit");
        }
        byte[] body = in.readNBytes(contentLength);
        if (body.length != contentLength) throw new IOException("Unexpected EOF in LSP body");
        return new String(body, StandardCharsets.UTF_8);
    }

    private static String readLine(InputStream in, int remaining) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        boolean sawCr = false;
        while (buffer.size() <= remaining) {
            int value = in.read();
            if (value < 0) throw new IOException("Unexpected EOF in LSP header");
            if (sawCr) {
                if (value == '\n') return buffer.toString(StandardCharsets.US_ASCII);
                buffer.write('\r');
                sawCr = false;
            }
            if (value == '\r') sawCr = true;
            else buffer.write(value);
        }
        throw new IOException("LSP header line exceeds limit");
    }
}
