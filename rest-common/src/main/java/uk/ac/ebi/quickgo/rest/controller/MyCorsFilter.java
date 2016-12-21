package uk.ac.ebi.quickgo.rest.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Created 20/12/16
 * @author Edd
 */
//@Component
//@ConfigurationProperties(prefix = "cors")
public class MyCorsFilter {

    @NestedConfigurationProperty
    private List<FilterProperties> filters = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public List<FilterProperties> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterProperties> filters) {
        this.filters = filters;
    }
}
