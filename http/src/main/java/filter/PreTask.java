package filter;

public interface PreTask {
    String getName();
    PreTaskWorker getFilterWorker();
    boolean isMatchUrl(String requestUrl);
}
