package com._7aske.grain.web.controller.response.impl;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.web.controller.response.AbstractResponseWriterSupport;
import com._7aske.grain.web.http.ContentType;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;
import org.apache.jasper.tagplugins.jstl.core.Out;

import java.io.IOException;
import java.io.OutputStream;

import static com._7aske.grain.web.http.HttpHeaders.CONTENT_TYPE;

@Grain
public class StringResponseWriter extends AbstractResponseWriterSupport<String> {
    /**
     * Prefix that signalizes that the response is actually a redirect
     * rather than a response with string body.
     */
    private static final String REDIRECT_PREFIX = "redirect:";

    protected StringResponseWriter() {
        super(String.class);
    }

    @Override
    public void writeInternal(String returnValue, HttpRequest request, HttpResponse response, RequestHandler handler) throws IOException {
            if (returnValue.startsWith(REDIRECT_PREFIX)) {
                response.sendRedirect(returnValue.substring(REDIRECT_PREFIX.length()));
            } else {
                try (OutputStream outputStream = response.getOutputStream()) {
                    outputStream.write(returnValue.getBytes());
                    if (response.getHeader(CONTENT_TYPE) == null)
                        response.setHeader(CONTENT_TYPE, ContentType.TEXT_PLAIN);
            }
        }
    }
}
