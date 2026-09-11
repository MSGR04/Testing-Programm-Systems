package ru.ostrovok.tests;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Execution(ExecutionMode.CONCURRENT)
@ParameterizedTest(name = "Браузер: {0}")
@MethodSource("ru.ostrovok.tests.BaseTest#browsers")
public @interface BrowserTest {
}