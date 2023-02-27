package mapper;

/**
 * 매칭여부에 따라 boolean 값을 반환합니다.
 */
public interface Matcher {
    boolean match(Matcher otherMatcher);
}
