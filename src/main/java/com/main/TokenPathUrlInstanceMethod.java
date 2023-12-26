package com.main;

import lombok.Getter;
import lombok.NonNull;
import matcher.PathMatcher;
import matcher.path.PathUrl;

@Getter
public class TokenPathUrlInstanceMethod {
    private final PathMatcher.Token token;
    private final PathUrl pathUrl;
    private final InstanceMethod instanceMethod;

    public TokenPathUrlInstanceMethod(@NonNull PathMatcher.Token token, @NonNull PathUrl pathUrl, @NonNull InstanceMethod instanceMethod) {
        this.token = token;
        this.pathUrl = pathUrl;
        this.instanceMethod = instanceMethod;
    }
}
