//package task.worker;
//
//import java.nio.file.Path;
//import java.text.MessageFormat;
//import java.util.Objects;
//import java.util.Optional;
//import parameter.matcher.ParameterAndValueAssigneeType;
//import task.SystemResourceFinder3;
//
//public class SystemResourceFileFindTaskWorker2 implements EndPointTaskWorker2 {
//    private final SystemResourceFinder3 systemResourceFinder3;
//    private final String resourcePath;
//
//    public SystemResourceFileFindTaskWorker2(SystemResourceFinder3 systemResourceFinder3, String resourcePath) {
//        Objects.requireNonNull(systemResourceFinder3);
//        if (Objects.isNull(resourcePath) || resourcePath.isBlank()) {
//            throw new RuntimeException("does not exist resourcePath.");
//        }
//        this.systemResourceFinder3 = systemResourceFinder3;
//        this.resourcePath = resourcePath;
//    }
//
//    @Override
//    public ParameterAndValueAssigneeType[] getParameterTypeInfos() {
//        return new ParameterAndValueAssigneeType[0];
//    }
//
//    @Override
//    public EndPointWorkerResult execute(Object[] params) {
//        Optional<Path> optionalFoundResource = systemResourceFinder3.findFile(resourcePath);
//
//        if (optionalFoundResource.isEmpty()) {
//            throw new RuntimeException(MessageFormat.format("Fail to find resource. ResourcePath : `{}`", resourcePath));
//        }
//
//        Path foundResource = optionalFoundResource.get();
//        WorkerResultType workerResultType = WorkerResultType.findByPath(foundResource);
//        return new EndPointWorkerResult(workerResultType, foundResource);
//    }
//}