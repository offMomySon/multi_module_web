package com.main.task.policy;

import java.util.Optional;

public  interface ParameterValuePolicy {
    Optional<Object> getValue();
}
