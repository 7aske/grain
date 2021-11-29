package com._7aske.grain.security.config.rule;

import com._7aske.grain.http.HttpRequest;
import com._7aske.grain.security.Authentication;
import com._7aske.grain.security.Authority;
import com._7aske.grain.security.context.SecurityContextHolder;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RuleUrlPatternMatcher {
	private final Set<Rule> rules;

	public RuleUrlPatternMatcher(Set<Rule> rules) {
		this.rules = rules;
	}

	public boolean matches(HttpRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Optional<Rule> matching = rules.stream()
				.filter(r -> r.matches(request.getPath(), request.getMethod()))
				// We use the length of the pattern as a way to determine
				// the narrowness of the match. We want the narrowest possible
				// match
				.max(Comparator.comparingInt(r -> r.getPattern().length()));


		if (matching.isPresent()) {
			Rule rule = matching.get();
			if (authentication == null && rule.isAuthenticationRequired()) {
				return false;
			}

			if (authentication != null && rule.isAuthenticationRequired()) {
				List<String> roles = authentication.getAuthorities().stream().map(Authority::getName).collect(Collectors.toList());
				return rule.getRolesRequired().stream().anyMatch(roles::contains);
			}
		}

		return true;
	}
}
