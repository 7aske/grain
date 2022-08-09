package com._7aske.grain.web.view;

import com._7aske.grain.GrainApp;
import com._7aske.grain.core.component.Grain;
import com._7aske.grain.core.configuration.Configuration;
import com._7aske.grain.core.context.ApplicationContext;
import com._7aske.grain.core.context.ApplicationContextImpl;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.session.Session;
import com._7aske.grain.security.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ViewResolverProviderTest {
	static class TestApp extends GrainApp {}

	@Grain
	static class TestViewResolver implements ViewResolver {
		@Override
		public void resolve(View view, HttpRequest request, HttpResponse response, Session session, Authentication authentication) {
			// nothing
		}
	}

	ApplicationContext applicationContext;
	@BeforeEach
	void setUp() {
		applicationContext = new ApplicationContextImpl(TestApp.class.getPackageName(), Configuration.createDefault());
	}

	@Test
	void resolve() {
		ViewResolver resolver = applicationContext.getGrain(ViewResolverProvider.class);
		assertNotNull(resolver);
	}
}