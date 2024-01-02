package com._7aske.grain.web.http.codec.json;

import com._7aske.grain.web.http.HttpRequest;
import com._7aske.grain.web.http.HttpResponse;
import com._7aske.grain.web.requesthandler.middleware.Middleware;

import java.util.Objects;

import static com._7aske.grain.web.http.ContentType.APPLICATION_JSON;
import static com._7aske.grain.web.http.HttpHeaders.CONTENT_TYPE;

@Deprecated
public class JsonMiddleware implements Middleware {

	@Override
	public void handle(HttpRequest req, HttpResponse res) {
		if (Objects.equals(req.getHeader(CONTENT_TYPE), APPLICATION_JSON)) {
			JsonParser parser = new JsonParser();
//			req.setBody(parser.parse((String) req.getBody()));
		}
	}
}
