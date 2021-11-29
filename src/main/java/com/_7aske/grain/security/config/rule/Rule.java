package com._7aske.grain.security.config.rule;

import com._7aske.grain.http.HttpMethod;
import com._7aske.grain.util.HttpPathUtil;

import java.util.*;

public class Rule {
	private Set<HttpMethod> httpMethods;
	private String pattern;
	private Set<String> rolesRequired;
	private boolean authenticationRequired;

	Rule(Set<HttpMethod> methods, String pattern, Set<String> rolesRequired, boolean authenticationRequired) {
		this.httpMethods = methods;
		this.pattern = pattern;
		this.rolesRequired = rolesRequired;
		this.authenticationRequired = authenticationRequired;
	}

	Rule() {

	}

	public Set<HttpMethod> getHttpMethods() {
		return httpMethods;
	}

	void setHttpMethods(Set<HttpMethod> methods) {
		this.httpMethods = methods;
	}

	public String getPattern() {
		return pattern;
	}

	void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Set<String> getRolesRequired() {
		return rolesRequired;
	}

	void setRolesRequired(Set<String> rolesRequired) {
		this.rolesRequired = rolesRequired;
	}

	public boolean isAuthenticationRequired() {
		return authenticationRequired;
	}

	void setAuthenticationRequired(boolean authenticationRequired) {
		this.authenticationRequired = authenticationRequired;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Rule rule = (Rule) o;
		return pattern.equals(rule.pattern);
	}

	@Override
	public int hashCode() {
		return Objects.hash(httpMethods, pattern, rolesRequired, authenticationRequired);
	}

	public boolean matches(String path, HttpMethod method) {
		if (this.httpMethods.size() != 0 && !this.httpMethods.contains(method)) {
			return false;
		}

		return HttpPathUtil.antMatching(pattern, path) && httpMethods.contains(method);
	}

	public static final class Builder {
		private Set<HttpMethod> methods;
		private String pattern;
		private Set<String> rolesRequired;
		private boolean authenticationRequired;
		private final Set<Rule> rules;

		public Builder() {
			this.rules = new HashSet<>();
			this.resetFields();
		}

		private void resetFields() {
			this.pattern = null;
			this.methods = new HashSet<>();
			this.rolesRequired = new HashSet<>();
			this.authenticationRequired = true;
		}

		public Builder authenticated() {
			authenticationRequired = true;
			return this;
		}

		public Builder unauthenticated() {
			authenticationRequired = false;
			return this;
		}

		public Builder method(HttpMethod... methods) {
			if (!this.methods.isEmpty())
				throw new IllegalStateException("HttpMethods already set");
			this.methods.addAll(List.of(methods));
			return this;
		}

		public Builder urlPattern(String pattern) {
			if (this.pattern != null)
				throw new IllegalStateException("Pattern already set");
			this.pattern = pattern;
			return this;
		}

		public Builder roles(String... roles) {
			if (!this.rolesRequired.isEmpty())
				throw new IllegalStateException("Roles already set");
			this.rolesRequired.addAll(List.of(roles));
			return this;
		}

		public Builder and() {
			this.rules.add(new Rule(methods, pattern, rolesRequired, authenticationRequired));
			resetFields();
			return this;
		}

		public List<Rule> build() {
			this.and();
			ArrayList<Rule> result = new ArrayList<>(this.rules);
			this.rules.clear();
			return result;
		}
	}
}
