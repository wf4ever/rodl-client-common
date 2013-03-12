package org.purl.wf4ever.rosrs.client.search.dataclasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.RangeFacet;

public class SearchResult implements Serializable {

    private List<FacetEntry> facetsList;
    private List<FoundRO> ROsList;


    public SearchResult() {
        facetsList = new ArrayList<FacetEntry>();
        setROsList(new ArrayList<FoundRO>());
    }


    public List<FacetEntry> getFactesList() {
        return facetsList;
    }


    public List<FoundRO> getROsList() {
        return ROsList;
    }


    public void setROsList(List<FoundRO> rOsList) {
        ROsList = rOsList;
    }


    public void appendFacet(FacetField field, String name) {
        facetsList.add(new FacetEntry(field, name));
    }


    public void appendFacet(RangeFacet rangeFacet, String name) {
        facetsList.add(new RangeFacetEntry(rangeFacet, name));
    }


    public void appendDateFacet(RangeFacet rangeFacet, String name) {
        facetsList.add(new DateRangeFacetEntry(rangeFacet, name));
    }
}