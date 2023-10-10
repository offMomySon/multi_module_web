package task;

public interface PostTask {
    String getName();
    PostTaskWorker getFilterWorker();
    boolean isMatchUrl(String requestUrl);
}
