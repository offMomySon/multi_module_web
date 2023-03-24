package mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WildCardChecker {
    private static final String WILD_CARD = "**";

    private final List<String> paths;

    private WildCardChecker(List<String> paths) {
        this.paths = paths.stream().filter(s -> !Objects.isNull(s)).collect(Collectors.toUnmodifiableList());
    }

    public static WildCardChecker from(List<String> paths, int startIndex) {
        List<String> subList = paths.subList(startIndex, paths.size());

        return new WildCardChecker(subList);
    }

    public boolean doesNotStartWithWildCard() {
        return !startWithWildCard();
    }

    private boolean startWithWildCard() {
        if (paths.isEmpty()) {
            return false;
        }

        String firstPath = paths.get(0);

        boolean isWildCard = firstPath.equals(WILD_CARD);
        return isWildCard;
    }

    public boolean onlyExistWildCard() {
        if (doesNotStartWithWildCard()) {
            return false;
        }

        boolean onlyExistWildCard = paths.size() <= 1;

        return onlyExistWildCard;
    }
}
