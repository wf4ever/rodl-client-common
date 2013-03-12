package org.purl.wf4ever.rosrs.client.search.dataclasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.purl.wf4ever.rosrs.client.search.dataclasses.solr.DateRangeFacetEntry;
import org.purl.wf4ever.rosrs.client.search.dataclasses.solr.FacetEntry;
import org.purl.wf4ever.rosrs.client.search.dataclasses.solr.RangeFacetEntry;

/**
 * Result of query.
 * 
 * @author pejot
 * 
 */
public class SearchResult implements Serializable {

    /** Serializtion. */
    private static final long serialVersionUID = 1L;
    /** List of facets. */
    private List<FacetEntry> facetsList;
    /** List of ROs. */
    private List<FoundRO> rosList;


    /**
     * Constructor.
     */
    public SearchResult() {
        facetsList = new ArrayList<FacetEntry>();
        setROsList(new ArrayList<FoundRO>());
    }


    public List<FacetEntry> getFactesList() {
        return facetsList;
    }


    public List<FoundRO> getROsList() {
        return rosList;
    }


    public void setROsList(List<FoundRO> rOsList) {
        rosList = rOsList;
    }


    /**
     * Add new facet.
     * 
     * @param field
     *            facet field
     * @param name
     *            human readable name
     */
    public void appendFacet(FacetField field, String name) {
        facetsList.add(new FacetEntry(field, name));
    }


    /**
     * Add new facet.
     * 
     * @param rangeFacet
     *            facet field
     * @param name
     *            human readable name
     */
    public void appendFacet(RangeFacet<?, ?> rangeFacet, String name) {
        facetsList.add(new RangeFacetEntry(rangeFacet, name));
    }


    /**
     * Add new facet.
     * 
     * @param rangeFacet
     *            facet field
     * @param name
     *            human readable name
     */
    public void appendDateFacet(RangeFacet<?, ?> rangeFacet, String name) {
        facetsList.add(new DateRangeFacetEntry(rangeFacet, name));
    }
}
