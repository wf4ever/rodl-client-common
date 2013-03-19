package org.purl.wf4ever.rosrs.client.search.dataclasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.purl.wf4ever.rosrs.client.search.dataclasses.solr.FacetEntry;

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


    public List<FacetEntry> getFacetsList() {
        return facetsList;
    }


    public List<FoundRO> getROsList() {
        return rosList;
    }


    public void setROsList(List<FoundRO> rOsList) {
        rosList = rOsList;
    }


    /**
     * Add a new facet.
     * 
     * @param facetEntry
     *            the facet to add
     */
    public void addFacet(FacetEntry facetEntry) {
        facetsList.add(facetEntry);
    }

}
