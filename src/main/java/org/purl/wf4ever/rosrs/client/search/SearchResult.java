package org.purl.wf4ever.rosrs.client.search;

import java.util.ArrayList;
import java.util.List;

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
}
