package site.autzone.event.handler.swagger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 支持多个包配置，多个包通过英文逗号隔开
 * @author xiaowj
 *
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {
    @Autowired
    private SwaggerConfigProperties scp;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
            .apis(SwaggerConfig.basePackage(scp.getPackageScan()))
            .paths(PathSelectors.any())
            .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title(scp.getTitle())
            .description(scp.getDescription())
            .version(scp.getVersion())
            .build();
    }
    
    public static Predicate<RequestHandler> basePackage(final String basePackage) {
      return new Predicate<RequestHandler>() {

          @Override
          public boolean apply(RequestHandler input) {
              return declaringClass(input).transform(handlerPackage(basePackage)).or(true);
          }
      };
  }

  private static Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
      return new Function<Class<?>, Boolean>() {
          @Override
          public Boolean apply(Class<?> input) {
              for (String strPackage : basePackage.split(",")) {
                  boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                  if (isMatch) {
                      return true;
                  }
              }
              return false;
          }
      };
  }

  @SuppressWarnings("deprecation")
  private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
      return Optional.fromNullable(input.declaringClass());
  }
}
