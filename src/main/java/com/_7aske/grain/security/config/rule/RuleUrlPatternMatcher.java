package com._7aske.grain.security.config.rule;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.Authority;
import com._7aske.grain.security.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RuleUrlPatternMatcher {
	private final List<Rule> rules;

	public RuleUrlPatternMatcher(List<Rule> rules) {
		this.rules = new ArrayList<>(rules);
		// @Refactor figure out how to more deterministically
		// order rules
		Collections.reverse(this.rules);
	}

	public boolean matches(HttpRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Optional<Rule> matching = rules.stream()
				.filter(r -> r.matches(request.getPath(), request.getMethod()))
				.findFirst();


		if (matching.isPresent()) {
			Rule rule = matching.get();
			if (authentication == null && rule.isAuthenticationRequired()) {
				return false;
			}

			if (authentication != null && rule.isAuthenticationRequired()) {
				List<String> roles = authentication.getAuthorities().stream().map(Authority::getName).collect(Collectors.toList());
				return rule.getRolesRequired().isEmpty() || rule.getRolesRequired().stream().anyMatch(roles::contains);
			}
		}

		return true;
	}
}
