package org.purl.wf4ever.rosrs.client.search;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.exception.SearchException;

public class SolrSearchServer implements SearchServer {

    private static final String FIELD_RO_URI = "ro_uri";

    /** Solr instance. */
    private HttpSolrServer server;


    public SolrSearchServer(URI solrUri) {
        server = new HttpSolrServer(solrUri.toString());
    }


    @Override
    public List<SearchResult> search(String query)
            throws SearchException {
        try {
            QueryResponse response = server.query(new SolrQuery(query));
            SolrDocumentList allResults = response.getResults();
            List<SolrDocument> results = allResults.subList(0, DEFAULT_MAX_RESULTS);
            List<SearchResult> searchResults = new ArrayList<>();
            for (SolrDocument document : results) {
                URI researchObjectUri = URI.create(document.getFieldValue(FIELD_RO_URI).toString());
                ResearchObject researchObject = new ResearchObject(researchObjectUri, null);
                SearchResult searchResult = new SearchResult(researchObject, -1);
                searchResults.add(searchResult);
            }
            return searchResults;
        } catch (SolrServerException e) {
            throw new SearchException("Exception when performing a Solr query", e);
        }
    }
}
