package uk.ac.ebi.quickgo.rest.controller;

import java.util.Set;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created 20/12/16
 * @author Edd
 */
//@Configuration
public class WebFilterConfig {

//    @Bean
    public WebMvcConfigurer corsConfigurer(MyCorsFilter myCorsFilter) {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                myCorsFilter.getFilters()
                        .forEach(filterInfo ->
                                registry.addMapping(filterInfo.getPath())
                                        .allowCredentials(filterInfo.getAllowCredentials())
                                        .allowedHeaders(setToArray(filterInfo.getAllowHeaders()))
                                        .allowedMethods(setToArray(filterInfo.getAllowMethods()))
                                        .allowedOrigins(setToArray(filterInfo.getAllowOrigins()))
                                        .exposedHeaders(setToArray(filterInfo.getExposeHeaders())));
            }
        };
    }

    private String[] setToArray(Set<String> info) {
        return info.toArray(new String[info.size()]);
    }
}
