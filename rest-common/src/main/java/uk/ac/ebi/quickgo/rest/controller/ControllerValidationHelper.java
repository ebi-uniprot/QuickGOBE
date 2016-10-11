package uk.ac.ebi.quickgo.rest.controller;

import java.util.List;

/**
 * Contains common validation logic used by REST controllers.
 *
 * Created 18/04/16
 * @author Edd
 */
public interface ControllerValidationHelper {
    /**
     * Checks the validity of a list of IDs in CSV format.
     * @param ids a list of IDs in CSV format
     * @throws IllegalArgumentException is thrown if an ID is not valid, or if
     * number of IDs listed is greater than the maximum permissible number of results.
     */
    List<String> validateCSVIds(String ids);

    void validateRequestedPages(int requestedPages);

    /**
     * Checks whether the requested number of results is valid.
     * @param requestedResultsSize the number of results being requested
     * @throws IllegalArgumentException if the number is greater than the maximum permissible number of results.
     */
    void validateRequestedResults(int requestedResultsSize);

    List<String> csvToList(String csv);
}
