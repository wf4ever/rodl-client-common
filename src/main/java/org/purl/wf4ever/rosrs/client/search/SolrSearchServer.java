package org.purl.wf4ever.rosrs.client.search;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.joda.time.DateTime;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.exception.SearchException;
import org.purl.wf4ever.rosrs.client.search.dataclasses.FoundRO;
import org.purl.wf4ever.rosrs.client.search.dataclasses.SearchResult;
import org.purl.wf4ever.rosrs.client.search.dataclasses.solr.DateRangeFacetEntry;
import org.purl.wf4ever.rosrs.client.search.dataclasses.solr.FacetEntry;
import org.purl.wf4ever.rosrs.client.search.dataclasses.solr.RangeFacetEntry;

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
    @SuppressWarnings("unused")
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
    public boolean supportsPagination() {
        return true;
    }


    @Override
    public SearchResult search(String queryString, Integer offset, Integer limit, Map<String, SortOrder> sortFields)
            throws SearchException {
        try {
            SolrQuery query = new SolrQuery(queryString);
            if (offset != null) {
                query.setStart(offset);
            }
            if (limit != null) {
                query.setRows(limit);
            }
            if (sortFields != null) {
                for (String key : sortFields.keySet()) {
                    ORDER order = sortFields.get(key) == SortOrder.DESC ? ORDER.desc : ORDER.asc;
                    query.addSortField(key, order);
                }
            }
            addFacetFields(query);
            QueryResponse response = getServer().query(query);
            return prepareSearchResult(response);

        } catch (SolrServerException e) {
            throw new SearchException("Exception when performing a Solr query", e);
        }
    }


    @Override
    public SearchResult search(String queryString)
            throws SearchException {
        return search(queryString, null, null, null);
    }


    /**
     * Add new facet field.
     * 
     * @param query
     *            query
     */
    //TODO maybe read from solr.configuration.properties or something like this?
    private void addFacetFields(SolrQuery query) {
        query.addFacetField("evo_type");
        query.addFacetField("creator");
        query.addNumericRangeFacet("annotations_size", 0, 100, 10);
        query.addNumericRangeFacet("resources_size", 0, 200, 20);
        query.addDateRangeFacet("created", DateTime.now().minusYears(2).toDate(), DateTime.now().toDate(), "+3MONTH");
    }


    /**
     * Pull up solr query results.
     * 
     * @param response
     *            solr response
     * @return SearchResult
     */
    //TODO the same maybe read from solr.configuration.properties or something like this?
    //TODO maybe intervals should be calculated dynamically if they are not configurable? 
    private SearchResult prepareSearchResult(QueryResponse response) {
        SearchResult result = new SearchResult();
        SolrDocumentList results = response.getResults();
        List<FoundRO> searchResults = getROsList(results);
        result.addFacet(new FacetEntry(response.getFacetField("creator"), "Creators", false));
        result.addFacet(new FacetEntry(response.getFacetField("evo_type"), "RO status"));
        result.addFacet(new RangeFacetEntry(response.getFacetRanges().get(0), "Number of annotations"));
        result.addFacet(new RangeFacetEntry(response.getFacetRanges().get(1), "Number of resources"));
        result.addFacet(new DateRangeFacetEntry(response.getFacetRanges().get(2), "Created"));
        result.setROsList(searchResults);
        return result;
    }


    /**
     * Get ROs list from solr document.
     * 
     * @param list
     *            list of solr documents
     * @return list of found ROs
     */
    private List<FoundRO> getROsList(SolrDocumentList list) {
        List<FoundRO> searchResults = new ArrayList<>();
        for (SolrDocument document : list) {
            URI researchObjectUri = URI.create(document.getFieldValue(FIELD_RO_URI).toString());
            ResearchObject researchObject = new ResearchObject(researchObjectUri, null);
            DateTime created = new DateTime((Date) document.get("created"));
            @SuppressWarnings("unchecked")
            List<String> creators = (List<String>) document.get("creator");
            FoundRO foundRO = new FoundRO(researchObject, -1, (int) document.get("resources_size"),
                    (int) document.get("annotations_size"), (String) document.get("evo_type"), created, creators);
            searchResults.add(foundRO);
        }
        return searchResults;
    }
}
