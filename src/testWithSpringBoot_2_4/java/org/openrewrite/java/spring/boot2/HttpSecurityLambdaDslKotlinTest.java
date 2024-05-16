package org.openrewrite.java.spring.boot2;

import org.junit.jupiter.api.Test;
import org.openrewrite.kotlin.KotlinParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.kotlin.Assertions.kotlin;

class HttpSecurityLambdaDslKotlinTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new HttpSecurityLambdaDsl())
          .parser(KotlinParser.builder()
            .classpath("spring-beans", "spring-context", "spring-boot", "spring-security", "spring-web", "tomcat-embed", "spring-core"));
    }

    @Test
    void handleDisableChain() {
        rewriteRun(
          //language=kotlin
          kotlin(
            """
              import org.springframework.security.config.annotation.web.builders.HttpSecurity
              import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
              import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
              
              @EnableWebSecurity
              class ConventionalSecurityConfig : WebSecurityConfigurerAdapter() {
              
                  fun configure(http: HttpSecurity) {
                      http.csrf().disable()
                  }
              }
              """,
            """
              import org.springframework.security.config.annotation.web.builders.HttpSecurity
              import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
              import org.springframework.security.web.SecurityFilterChain
              
              @EnableWebSecurity
              class ConventionalSecurityConfig {
              
                  fun configure(http: HttpSecurity): SecurityFilterChain {
                      http.csrf { it.disable() }
                      return http.build()
                  }
              }
              """
          )
        );
    }
}
