package org.purl.wf4ever.rosrs.client.search;

import java.util.List;

import org.purl.wf4ever.rosrs.client.exception.SearchException;

/**
 * An interface for a service that performs search for ROs in RODL.
 * 
 * @author piotrekhol
 * 
 */
public interface SearchServer {

    /** The maximum number of results for a default, unparametrized query. */
    int DEFAULT_MAX_RESULTS = 20;


    /**
     * Find the research objects for a provided query. If there are no results, return an empty list. Return no more
     * than 20 results.
     * 
     * @param query
     *            a query, where keywords are separated by spaces
     * @return a list of results
     * @throws SearchException
     *             when the search finished with an exception
     */
    List<SearchResult> search(String query)
            throws SearchException;


    /**
     * True if the search server provides a search method with offset and limit parameters.
     * 
     * @return true if pagination is supported, false otherwise
     */
    boolean supportsPagination();


    /**
     * Find the research objects for a provided query. Return a list starting with the "offset" position on the list,
     * and containing no more than "limit" results.
     * 
     * @param query
     *            a query, where keywords are separated by spaces
     * @param offset
     *            how many first results to skip (0 to skip none)
     * @param limit
     *            the maximum number of results
     * @return a list of results
     * @throws SearchException
     *             when the search finished with an exception
     */
    List<SearchResult> search(String query, int offset, int limit)
            throws SearchException;
}
