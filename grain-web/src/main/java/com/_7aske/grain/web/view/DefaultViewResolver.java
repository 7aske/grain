package com._7aske.grain.web.view;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.exception.GrainRuntimeException;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.session.Session;

import java.io.IOException;
import java.io.OutputStream;

@Grain
@Order(Order.LOWEST_PRECEDENCE)
public class DefaultViewResolver implements ViewResolver {
    @Override
    public void resolve(View view, HttpRequest request, HttpResponse response, Session session, Authentication authentication) {
        populateImplicitObjects(view, request, response, session, authentication, null);

        response.setHeader("Content-Type", view.getContentType());
        response.setContentLength(view.getContent().length());
        try (OutputStream outputStream = response.getOutputStream()) {
            byte[] output = view.getContent().getBytes();
            outputStream.write(view.getContent().getBytes());
            response.setContentType(view.getContentType());
            response.setContentLength(output.length);
            outputStream.flush();
        } catch (IOException e) {
            throw new GrainRuntimeException(e);
        }
    }
}
