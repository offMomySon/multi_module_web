package processor;

import config.Config;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mapper.ClassAnnotationDetector;
import mapper.TaskIndicator;
import mapper.TaskMapper;
import mapper.marker.Controller;
import mapper.marker.RequestMapping;
import vo.HttpMethod;

/***
 * 역할.
 * http request 를 지속적으로 수신하고 thread 에 worker 를 할당함으로써 서비스를 수행하는 역할.
 */
@Slf4j
public class HttpService {
    private final ThreadPoolExecutor threadPoolExecutor;
    private final ServerSocket serverSocket;

    //    TODO file scan 으로 class 들을 찾으면 되지않나?
    private final Class<?> primaryClazz;
    private final String controllerPackage;

    public HttpService(Class<?> appClass, String controllerPackage) {
        try {
            this.threadPoolExecutor = new ThreadPoolExecutor(Config.INSTANCE.getMaxConnection(),
                                                             Config.INSTANCE.getMaxConnection(),
                                                             Config.INSTANCE.getKeepAliveTime(),
                                                             TimeUnit.MILLISECONDS,
                                                             new LinkedBlockingQueue<>(Config.INSTANCE.getWaitConnection()));
            this.serverSocket = new ServerSocket(Config.INSTANCE.getPort());

            this.primaryClazz = appClass;
            this.controllerPackage = controllerPackage;

        } catch (IOException e) {
            throw new RuntimeException(MessageFormat.format("fail to active server. Reason : `{0}`", e.getCause()), e);
        }
    }

    public void start() {
        log.info("start server.");

        TaskMapper taskMapper = createTaskMapper(this.primaryClazz, this.controllerPackage);

        while (true) {
            try {
                log.info("Ready client connection..");
                Socket socket = serverSocket.accept();

                log.info("load worker to thread.");
                threadPoolExecutor.execute(HttpWorker.create(socket.getInputStream(), socket.getOutputStream()));
            } catch (IOException e) {
                throw new RuntimeException(MessageFormat.format("I/O fail. Reason : `{0}`", e.getCause()));
            }
        }
    }

    public static TaskMapper createTaskMapper(Class<?> _clazz, String packageName) {
        BufferedReader resourceReader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(
                _clazz.getResourceAsStream("/" + packageName.replaceAll("[.]", "/")))
            )
        );

        List<ClassAnnotationDetector> allClasses = resourceReader.lines()
            .peek(l -> log.info("line : {}", l))
            .filter(isClass())
            .map(parseClassName())
            .map(generatePackageClassName(packageName))
            .map(HttpService::getClass)
            .map(ClassAnnotationDetector::new)
            .collect(Collectors.toUnmodifiableList());

        Set<ClassAnnotationDetector> controllerDetector = allClasses.stream()
            .filter(a -> a.isAnnotatedOnClass(Controller.class))
            .collect(Collectors.toUnmodifiableSet());

        Map<TaskIndicator, Method> totalMethodMapper = new HashMap<>();
        for (ClassAnnotationDetector detector : controllerDetector) {
            Optional<Set<String>> optionalControllerUrls = detector.findAnnotationOnClass(RequestMapping.class)
                .map(RequestMapping::value)
                .map(Set::of);

            if (optionalControllerUrls.isEmpty()) {
                continue;
            }

            Set<String> controllerUrls = optionalControllerUrls.get();

            for (Method method : detector.findMethod(RequestMapping.class)) {
                Optional<RequestMapping> optionalRequestMappingMethod = detector.findAnnotationOnMethod(method, RequestMapping.class);

                if (optionalRequestMappingMethod.isEmpty()) {
                    continue;
                }

                RequestMapping requestMapping = optionalRequestMappingMethod.get();

                List<String> taskUrls = Arrays.stream(requestMapping.value()).collect(Collectors.toUnmodifiableList());
                List<HttpMethod> taskHttpMethods = Arrays.stream(requestMapping.method()).collect(Collectors.toUnmodifiableList());

                Set<TaskIndicator> taskIndicators = createMethodIndicator(taskUrls, taskHttpMethods);

                Set<TaskIndicator> fullUrlTaskIndicators = prevAppendUrlToMethodIndicators(controllerUrls, taskIndicators);

                Map<TaskIndicator, Method> methodMapper = fullUrlTaskIndicators.stream()
                    .map(taskIndicator -> Map.entry(taskIndicator, method))
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue, (it1, it2) -> it1));

                totalMethodMapper.putAll(methodMapper);
            }
        }

        totalMethodMapper.forEach((key, value) -> log.info("key : {}, value : {}", key, value));

        return new TaskMapper(totalMethodMapper);
    }

    private static Set<TaskIndicator> prevAppendUrlToMethodIndicators(Set<String> urls, Set<TaskIndicator> taskIndicators) {
        Set<TaskIndicator> newTaskIndicators = new HashSet<>();
        for (String url : urls) {
            for (TaskIndicator taskIndicator : taskIndicators) {
                newTaskIndicators.add(taskIndicator.prevAppendUrl(url));
            }
        }
        return Collections.unmodifiableSet(newTaskIndicators);
    }

    private static Set<TaskIndicator> createMethodIndicator(List<String> taskUrls, List<HttpMethod> taskHttpMethods) {
        Set<TaskIndicator> taskIndicators = new HashSet<>();
        for (String taskUrl : taskUrls) {
            for (HttpMethod httpMethod : taskHttpMethods) {
                taskIndicators.add(new TaskIndicator(taskUrl, httpMethod));
            }
        }

        return Collections.unmodifiableSet(taskIndicators);
    }

    private static Function<String, String> generatePackageClassName(String packageName) {
        return s -> packageName + "." + s;
    }

    private static Function<String, String> parseClassName() {
        return className -> className.substring(0, className.lastIndexOf('.'));
    }

    private static Predicate<String> isClass() {
        return resource -> resource.endsWith(".class");
    }

    private static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("class not found.");
        }
    }
}
