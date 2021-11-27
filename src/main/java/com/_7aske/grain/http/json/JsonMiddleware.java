package com._7aske.grain.http.json;

import com._7aske.grain.component.Grain;
import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.http.session.Session;
import com._7aske.grain.requesthandler.middleware.Middleware;

import java.util.Objects;

import static com._7aske.grain.http.HttpContentType.APPLICATION_JSON;
import static com._7aske.grain.http.HttpHeaders.CONTENT_TYPE;

@Grain
public class JsonMiddleware implements Middleware {

	@Override
	public boolean handle(HttpRequest req, HttpResponse res, Session session) {
		if (Objects.equals(req.getHeader(CONTENT_TYPE), APPLICATION_JSON)) {
			JsonParser deserializer = new JsonParser((String) req.getBody());
			req.setBody(deserializer.parse());
		}
		return false;
	}
}
