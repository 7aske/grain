package com._7aske.grain.web.controller.response.impl;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.security.context.SecurityContextHolder;
import com._7aske.grain.web.controller.response.AbstractResponseWriterSupport;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.handler.RequestHandler;
import com._7aske.grain.web.view.View;
import com._7aske.grain.web.view.ViewResolver;
import com._7aske.grain.web.view.ViewResolverProvider;

import java.io.IOException;

@Grain
public class ViewResponseWriter extends AbstractResponseWriterSupport<View> {
    private final ViewResolverProvider viewResolver;

    public ViewResponseWriter(ViewResolverProvider viewResolver) {
        super(View.class);
        this.viewResolver = viewResolver;
    }

    @Override
    public void writeInternal(View returnValue, HttpRequest request, HttpResponse response, RequestHandler handler) throws IOException {
        viewResolver.resolve(returnValue, request, response, request.getSession(), SecurityContextHolder.getContext().getAuthentication());
    }
}
