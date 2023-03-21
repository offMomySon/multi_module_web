package mapper;

import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import vo.HttpMethod;

public class JavaMethodResolver2 {
    private static final String PATH_DELIMITER = "/";
    private static final String WILD_CARD = "**";

    private final HttpMethod httpMethod;
    private final String url;
    private final Method javaMethod;

    public JavaMethodResolver2(@NonNull HttpMethod httpMethod, @NonNull String url, @NonNull Method javaMethod) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.javaMethod = javaMethod;
    }

    public Optional<Method> resolve(String otherUrl) {
        if (Objects.isNull(otherUrl) || otherUrl.isEmpty() || otherUrl.isBlank()) {
            return Optional.empty();
        }

        if (doesNotMatch(otherUrl)) {
            return Optional.empty();
        }

        return Optional.of(javaMethod);
    }

    public boolean doesNotMatch(String otherUrl) {
        return !match(otherUrl);
    }

    public boolean match(String otherUrl) {
        if (Objects.isNull(otherUrl) || otherUrl.isEmpty() || otherUrl.isBlank()) {
            return false;
        }
        otherUrl = URI.create(otherUrl).getPath();
        otherUrl = Paths.get(otherUrl).normalize().toString();

        String[] splitThisPaths = this.url.split(PATH_DELIMITER);
        String[] splitOtherPahts = otherUrl.split(PATH_DELIMITER);

        Deque<String> thisPaths = new ArrayDeque<>();
        Deque<String> otherPaths = new ArrayDeque<>();
        for (String thisPath : splitThisPaths) {
            thisPaths.addLast(thisPath);
        }
        for (String otherPath : splitOtherPahts) {
            otherPaths.addLast(otherPath);
        }

        return doMatch(thisPaths, otherPaths);
    }

    private boolean doMatch(Deque<String> thisPaths, Deque<String> otherPaths) {
        boolean skipUntilMatchPath = false;

        for (String otherPath : otherPaths) {
            if(thisPaths.size() == 0){
                return true;
            }
            String thisPath = thisPaths.peekFirst();

            if(skipUntilMatchPath && Objects.equals(thisPath, otherPath)){
                thisPaths.removeFirst();
                skipUntilMatchPath = false;
                continue;
            }

            if(skipUntilMatchPath && !Objects.equals(thisPath, otherPath)){
                thisPaths.removeFirst();
                continue;
            }

            if (Objects.equals(WILD_CARD, thisPath)) {
                thisPaths.removeFirst();
                skipUntilMatchPath = true;
                continue;
            }


            if (!Objects.equals(otherPath, thisPath)) {
                thisPaths.removeFirst();
                return false;
            }
        }

        return true;
    }

}
