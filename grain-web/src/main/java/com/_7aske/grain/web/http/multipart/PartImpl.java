package com._7aske.grain.web.http.multipart;

import com._7aske.grain.web.http.HttpHeader;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com._7aske.grain.web.http.HttpHeaders.CONTENT_TYPE;

public class PartImpl implements Part {
    private String contentType;
    private String name;
    private String fileName;
    private long size;
    private InputStream inputStream;
    private final Map<String, String> headers = new HashMap<>();
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private PartImpl() {
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getContentType() {
        if (contentType != null) {
            return contentType;
        }

        return headers.get(CONTENT_TYPE);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public InputStream getInputStream() {
        if (inputStream == null) {
            inputStream = new BufferedInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        }
        return inputStream;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public static final class Builder {
        PartImpl part;

        private Builder() {
            part = new PartImpl();
        }

        public Builder withHeader(String name, String value) {
            part.headers.put(name, value);
            return this;
        }

        public Builder withHeaders(Map<String, String> headers) {
            part.headers.putAll(headers);
            return this;
        }

        public Builder withHttpHeaders(Map<String, HttpHeader> headers) {
            for (Map.Entry<String, HttpHeader> kv : headers.entrySet()) {
                part.headers.put(kv.getKey(), kv.getValue().getValue());
            }
            return this;
        }

        public Builder withContentType(String contentType) {
            part.contentType = contentType;
            return this;
        }

        public Builder withFileName(String name) {
            part.fileName = name;
            return this;
        }

        public Builder withName(String name) {
            part.name = name;
            return this;
        }

        public Builder withSize(long size) {
            part.size = size;
            return this;
        }

        public Builder withContent(byte[] data) {
            return withContent(data, 0, data.length);
        }

        public Builder withContent(byte[] data, int len) {
            return withContent(data, 0, len);
        }

        public Builder withContent(byte[] data, int off, int len) {
            part.outputStream.write(data, off, len);
            part.size = len;
            return this;
        }

        public Builder withContent(int[] data) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4);
            for (int i : data) {
                byteBuffer.putInt(i);
            }
            byte[] byteData = byteBuffer.array();
            part.outputStream.writeBytes(byteData);
            part.size = byteData.length;
            return this;
        }

        public PartImpl build() {
            return part;
        }

    }
}
