package foo.bar.annotations.readers;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ FIELD })
@Retention(RUNTIME)
public @interface Range {

	String startField();

	String startDatePattern() default "";

	String endField();

	String endDatePattern() default "";

}
