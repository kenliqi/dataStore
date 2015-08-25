package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by qili on 25/08/2015.
 * <p>
 * Mark the class to be storable
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface entity {
    /**
     * Whether we need to store this entity into database
     *
     * @return
     */
    boolean persisted() default true;
}
