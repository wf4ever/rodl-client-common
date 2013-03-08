package org.purl.wf4ever.rosrs.client.search;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.joda.time.DateTime;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.exception.SearchException;

/**
 * An implementation connecting to the Solr instance in RODL. Note that the response schema is hardcoded.
 * 
 * @author piotrekhol
 * 
 */
public class SolrSearchServer implements SearchServer, Serializable {

    /** id. */
    private static final long serialVersionUID = -276599078305951556L;

    /** Field for the RO URI in the response from Solr. */
    private static final String FIELD_RO_URI = "ro_uri";

    /** Solr instance. */
    private transient HttpSolrServer server;

    /** Solr server URI, necessary for reinitializing the instance. */
    private URI solrUri;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(SolrSearchServer.class);


    /**
     * Constructor.
     * 
     * @param solrUri
     *            URI for the Solr instance, for example http://sandbox.wf4ever-project.org/solr/
     */
    public SolrSearchServer(URI solrUri) {
        this.solrUri = solrUri;
    }


    /**
     * Get the solr server instance, creating it if necessary.
     * 
     * @return the solr server
     */
    private HttpSolrServer getServer() {
        if (server == null) {
            server = new HttpSolrServer(solrUri.toString());
        }
        return server;
    }


    @Override
    public SearchResult search(String queryString)
            throws SearchException {
        return search(queryString, 0, DEFAULT_MAX_RESULTS);
    }


    @Override
    public boolean supportsPagination() {
        return true;
    }


    @Override
    public SearchResult search(String queryString, int offset, int limit)
            throws SearchException {
        try {

            SolrQuery query = new SolrQuery(SolrQueryBuilder.escapeQueryString(queryString))
                    .setRows(DEFAULT_MAX_RESULTS);

            query.addFacetField("evo_type");
            query.addFacetField("creator");
            query.addNumericRangeFacet("annotations_size", 0, 100, 10);
            query.addNumericRangeFacet("resources_size", 0, 200, 20);
            query.addDateRangeFacet("created", DateTime.now().minusYears(5).toDate(), DateTime.now().toDate(), "1YEAR");

            QueryResponse response = getServer().query(query);

            SolrDocumentList results = response.getResults();
            List<FoundRO> searchResults = getROsList(results);

            //response.getFacetDate("created");
            response.getFacetField("creator");
            response.getFacetField("evo_type");
            response.getFacetRanges().get(0);

            SearchResult result = new SearchResult();
            result.setROsList(searchResults);

            return result;

        } catch (SolrServerException e) {
            throw new SearchException("Exception when performing a Solr query", e);
        }
    }


    /**
     * Get ROs list from solr document.
     * 
     * @param list
     * @return
     */
    private List<FoundRO> getROsList(SolrDocumentList list) {
        List<FoundRO> searchResults = new ArrayList<>();
        for (SolrDocument document : list) {
            URI researchObjectUri = URI.create(document.getFieldValue(FIELD_RO_URI).toString());
            ResearchObject researchObject = new ResearchObject(researchObjectUri, null);
            FoundRO searchResult = new FoundRO(researchObject, -1);
            searchResults.add(searchResult);
        }
        return searchResults;
    }
}
