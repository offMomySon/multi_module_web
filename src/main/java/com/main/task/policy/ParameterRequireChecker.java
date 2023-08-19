package com.main.task.policy;

import java.util.Optional;

public  interface ParameterRequireChecker {
    void check(Optional<Object> parameterValue);
}
