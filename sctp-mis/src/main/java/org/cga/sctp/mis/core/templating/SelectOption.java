package org.cga.sctp.mis.core.templating;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for rendering &lt;option&gt; elements.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelectOption {

    /**
     * @return The name of the field or method from where to get the unique value. For example "propertyName" or "methodName()"
     */
    String value();

    /**
     * @return The name of the field or method from where to get the display text. For example "propertyName" or "methodName()"
     */
    String text();
}
