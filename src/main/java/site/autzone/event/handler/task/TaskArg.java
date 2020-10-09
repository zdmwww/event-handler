package site.autzone.event.handler.task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(TaskArgs.class)
public @interface TaskArg {
	/**
	 * 是否必须的参数
	 * @return
	 */
    boolean required() default false;
    
	/**
	 * 参数在Item中的编码
	 * @return
	 */
    String argCode() default "";

    /**
     * 参数描述
     */
    String description() default "";

    /**
     * 参数示例
     * @return
     */
    String[] sampleValues() default {};
}
