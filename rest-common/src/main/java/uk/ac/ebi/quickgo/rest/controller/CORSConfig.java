package uk.ac.ebi.quickgo.rest.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static uk.ac.ebi.quickgo.rest.controller.FilterProperties.*;

/**
 * Created 21/12/16
 * @author Edd
 */
@Configuration
@ConfigurationProperties(prefix = "cors")
public class CORSConfig {

    @NestedConfigurationProperty
    private List<FilterProperties> filters = new ArrayList<>();

    public List<FilterProperties> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterProperties> filters) {
        this.filters = filters;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        if (!filters.isEmpty()) {
            filters.forEach(
                    filter -> {
                        CorsConfiguration config = new CorsConfiguration();
                        filter.getAllowHeaders().forEach(config::addAllowedHeader);
                        filter.getAllowOrigins().forEach(config::addAllowedOrigin);
                        filter.getExposeHeaders().forEach(config::addExposedHeader);
                        filter.getAllowMethods().forEach(config::addAllowedMethod);
                        config.setMaxAge(filter.getMaxAge());
                        config.setAllowCredentials(filter.getAllowCredentials());
                        source.registerCorsConfiguration(filter.getPath(), config);
                    }
            );
        } else {
            CorsConfiguration config = new CorsConfiguration();
            DEFAULT_ACCESS_CONTROL_ALLOW_HEADERS.forEach(config::addAllowedHeader);
            DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN.forEach(config::addAllowedOrigin);
            DEFAULT_EXPOSE_HEADERS.forEach(config::addExposedHeader);
            DEFAULT_ACCESS_CONTROL_ALLOW_METHODS.forEach(config::addAllowedMethod);
            config.setMaxAge(DEFAULT_ACCESS_CONTROL_MAX_AGE);
            config.setAllowCredentials(DEFAULT_ACCESS_CONTROL_ALLOW_CREDENTIALS);
            source.registerCorsConfiguration(FilterProperties.DEFAULT_PATH, config);
        }
        return new CorsFilter(source);
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean(CorsFilter corsFilter) {
        FilterRegistrationBean bean = new FilterRegistrationBean(corsFilter);
        bean.setOrder(0);
        return bean;
    }
}
