package superperk.hug.testcontainers.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableMongoContainer {
    String image() default "mongo:latest";
}