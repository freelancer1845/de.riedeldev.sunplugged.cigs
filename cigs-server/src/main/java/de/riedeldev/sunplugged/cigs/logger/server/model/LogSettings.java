package de.riedeldev.sunplugged.cigs.logger.server.model;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface LogSettings {

	int csvPosition() default 0;

	boolean createChart() default true;

	String nameToDisplay() default "";
}
