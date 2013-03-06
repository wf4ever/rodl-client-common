package org.purl.wf4ever.rosrs.client.search;

import java.util.List;

import org.purl.wf4ever.rosrs.client.exception.SearchException;

public interface SearchServer {

    List<SearchResult> search(String query)
            throws SearchException;

}
