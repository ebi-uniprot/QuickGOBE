package uk.ac.ebi.quickgo.rest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created 21/12/16
 * @author Edd
 */
@Component
//@EnableWebMvc
@ConfigurationProperties(prefix = "cors")
public class CORSConfigurer extends WebMvcConfigurerAdapter {

    @NestedConfigurationProperty
    private List<FilterProperties> filters = new ArrayList<>();

    public List<FilterProperties> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterProperties> filters) {
        this.filters = filters;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        filters.forEach(filterInfo ->
                registry.addMapping(filterInfo.getPath())
                        .allowCredentials(filterInfo.getAllowCredentials())
                        .allowedHeaders(toArray(filterInfo.getAllowHeaders()))
                        .allowedMethods(toArray(filterInfo.getAllowMethods()))
                        .allowedOrigins(toArray(filterInfo.getAllowOrigins()))
                        .exposedHeaders(toArray(filterInfo.getExposeHeaders())));
        System.out.println(registry);
    }

    private String[] toArray(Set<String> info) {
        return info.toArray(new String[info.size()]);
    }

}
