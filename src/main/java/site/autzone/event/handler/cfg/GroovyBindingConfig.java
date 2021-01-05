package site.autzone.event.handler.cfg;

import groovy.lang.Binding;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 绑定spring beans
 * @author xiaowj
 *
 */
@Configuration
public class GroovyBindingConfig implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Bean("groovyBinding")
    public Binding groovyBinding() {
        Map<String, Object> beanMap = applicationContext.getBeansOfType(Object.class);
        return new Binding(beanMap);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}