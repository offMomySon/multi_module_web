package beanContainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import mapper.AnnotationUtils;
import mapper.marker.Component;

@Slf4j
public class BeanContainerCreator {

    public Map<Class<?>, Object> create(List<Class<?>> clazzs) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Map<Class<?>, Object> container = new HashMap<>();

        List<Class<?>> componentClazzs = clazzs.stream()
            .filter(c -> AnnotationUtils.exist(c, Component.class))
            .collect(Collectors.toUnmodifiableList());

        for (Class<?> componentClazz : componentClazzs) {
            container = instantiate(componentClazz, container);
        }

        return container;
    }


    private static Map<Class<?>, Object> instantiate(Class<?> clazz, Map<Class<?>, Object> container)
        throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Class<?>> memberClazzes = Arrays.stream(clazz.getDeclaredFields())
            .map(f -> f.getType())
            .filter(c -> AnnotationUtils.exist(c, Component.class))
            .collect(Collectors.toUnmodifiableList());

        // member class 가 존재하지 않으면 instance 를 생성한다.
        if (memberClazzes.isEmpty()) {
            Constructor<?> constructor = clazz.getConstructor();
            Object instance = constructor.newInstance();
            container.put(clazz, instance);
            return container;
        }

        // 하위의 모든 class 의 instance 를 생성한다.
        for (Class<?> memberClazz : memberClazzes) {
            container = instantiate(memberClazz, container);
        }

        // 하위
        Object[] memberObjects = memberClazzes.stream()
            .map(container::get)
            .toArray();
        Constructor<?> constructor = clazz.getConstructor(memberClazzes.toArray(Class<?>[]::new));
        Object instance = constructor.newInstance(memberObjects);

        container.put(clazz, instance);

        return container;
    }
}

















