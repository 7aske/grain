package com._7aske.grain.web.http;

import com._7aske.grain.annotation.NotNull;
import com._7aske.grain.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com._7aske.grain.web.server.HttpRequestReader.*;

public class HttpHeader {
    private String name;
    private String value = "";
    private Map<String, String> parameters = new HashMap<>();

    public HttpHeader() {
    }

    public static HttpHeader parse(String header) {
        HttpHeader requestHeader = new HttpHeader();

        String[] headerParts = HEADER_SEPARATOR_REGEX.split(header, 2);
        if (headerParts.length == 0) {
            return null;
        }
        if (headerParts.length == 1) {
            requestHeader.name = headerParts[0].trim();
            return requestHeader;
        }

        String headerName = headerParts[0].trim();
        String headerValue = headerParts[1].trim();
        String[] valueParts = HEADER_PARAMETER_SEPARATOR_REGEX.split(headerValue);
        String actualValue = valueParts[0];

        for (int i = 1; i < valueParts.length; i++) {
            String[] paramParts = URL_ENCODED_VALUE_SEPARATOR.split(valueParts[i], 2);
            if (paramParts.length != 2) {
                requestHeader.parameters.put(paramParts[0], "");
            } else {
                String stripped = StringUtils.stripQuotes(paramParts[1]);
                requestHeader.parameters.put(paramParts[0], stripped);
            }
        }

        requestHeader.name = headerName;
        requestHeader.value = actualValue;

        return requestHeader;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public @NotNull String getParameter(String name) {
        return parameters.getOrDefault(name, "");
    }

    public Optional<String> getOptionalParameter(String name) {
        return Optional.ofNullable(parameters.getOrDefault(name, null));
    }

    @Override
    public String toString() {
        return name + ": " + value + (parameters.isEmpty() ? "" : "; " + parameters);
    }
}
