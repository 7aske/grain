package com._7aske.grain.web.view;

import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.component.Order;
import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.http.session.Session;
import com._7aske.grain.security.Authentication;

import java.util.List;

@Grain
@Order(Integer.MIN_VALUE)
public class ViewResolverProvider implements ViewResolver {
	private final List<ViewResolver> viewResolvers;

	public ViewResolverProvider(List<ViewResolver> viewResolvers) {
		this.viewResolvers = viewResolvers;
	}

	@Override
	public void resolve(View view, HttpRequest request, HttpResponse response, Session session, Authentication authentication) {
		for (ViewResolver viewResolver : viewResolvers) {
			if (viewResolver.supports(view)) {
				viewResolver.resolve(view, request, response, session, authentication);
				return;
			}
		}
	}
}
