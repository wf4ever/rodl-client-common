package org.purl.wf4ever.rosrs.client.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.RangeFacet;

public class SearchResult {

    private List<Facet> facetsList;
    private List<FoundRO> ROsList;


    public SearchResult() {
        facetsList = new ArrayList<Facet>();
        setROsList(new ArrayList<FoundRO>());
    }


    public List<Facet> getFactesList() {
        return facetsList;
    }


    public List<FoundRO> getROsList() {
        return ROsList;
    }


    public void setROsList(List<FoundRO> rOsList) {
        ROsList = rOsList;
    }


    public void appendFacet(FacetField field, String name) {
        facetsList.add(new Facet(field, name));
    }


    public void appendFacet(RangeFacet rangeFacet, String name) {
        facetsList.add(new RangeFacetField(rangeFacet, name));
    }
}