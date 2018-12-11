package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation use to define a one to one relation between two models. Only the element owning the relation have access to the relation (unidirectional). The name property is for the relation field in database while the repository parameter stand for the Java model class to use to retrieve this relation.
 * @author Antoine FORET
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToOne {
    String name();
    Class repository();
}
