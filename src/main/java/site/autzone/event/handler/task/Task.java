package site.autzone.event.handler.task;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Task {
	/**
	 * Job描述信息
	 * @return
	 */
    String description();
    /**
     * ConsumerKey
     * @return
     */
    String consumerKey() default "";
    
    /**
     * Job创建时间
     * @return
     */
    String created() default "";
}
