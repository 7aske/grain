package com._7aske.grain.web.http.multipart;

import java.io.InputStream;
import java.util.Collection;

public interface Part {
    String getContentType();

    String getName();

    String getFileName();

    long getSize();

    String getHeader(String name);

    Collection<String> getHeaderNames();

    InputStream getInputStream();
}
