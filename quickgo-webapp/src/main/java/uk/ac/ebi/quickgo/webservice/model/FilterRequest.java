package uk.ac.ebi.quickgo.webservice.model;

import java.util.List;

/**
 * @author Tony Wardell
 * Date: 07/10/2015
 * Time: 15:42
 * Created with IntelliJ IDEA.
 */
public interface FilterRequest {
    int getRows();

    List<Filter> getList();

    boolean isSlim();

    int getPage();

    String getFormat();

    int getLimit();
}
