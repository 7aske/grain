package com._7aske.grain.security.authentication;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.http.HttpResponse;
import com._7aske.grain.security.Authentication;

public interface AuthenticationEntryPoint {
	Authentication authenticate(HttpRequest request, HttpResponse response) throws SecurityException;
}
