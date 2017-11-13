package foo.bar.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD})
@Retention(RUNTIME)
public @interface Unique {

	String uk();
	
	String[] fields() default {};

	String message() default "Unique Exception Message";

}
