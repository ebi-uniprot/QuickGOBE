/**
 * Provides the classes necessary to create and process client request filters.
 * <p/>
 * {@link uk.ac.ebi.quickgo.rest.search.filter.RequestFilter} instances are usually created by QuickGO REST endpoints
 * when a client would like to filter down results from a query that (s)he has supplied.
 * <p/>
 * The complexity of a request filter is based on the field(s) declared within the filter. Fields may:
 * <ul>
 *     <li>Exist in a table/collection of underlying data-source: It is most likely that no complex processing
 *     is necessary, and that the filter can be directly executed from within the data source.
 *     </li>
 *     <li>Not exist in table/collection of the underlying data-source: Using this field as a filter will
 *     require extra processing, usually in the form of a join between two tables/collections, or querying other
 *     services for the required information</li>
 * </ul>
 * The {@link uk.ac.ebi.quickgo.rest.search.filter.FilterConverterFactory} is the class responsible for deciding how a
 * {@link uk.ac.ebi.quickgo.rest.search.filter.RequestFilter} is to be processed. Note that the factory does not do
 * the actual processing, it delegates this task to {@link uk.ac.ebi.quickgo.rest.search.filter.FilterConverter}.
 * There exist several implementations of the {@link uk.ac.ebi.quickgo.rest.search.filter.FilterConverter} interface,
 * each one tries to obtain the required information in a different way, e.g. contact another service, create a join
 * between two tables/collections or create a simple filter query. Any extra configuration required by the converter
 * is fed to them via the {@link uk.ac.ebi.quickgo.rest.search.filter.FilterExecutionConfig}.
 *
 * @author Ricardo Antunes
 */
package uk.ac.ebi.quickgo.rest.search.request;