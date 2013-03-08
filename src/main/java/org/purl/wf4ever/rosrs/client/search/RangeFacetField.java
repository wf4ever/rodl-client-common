package org.purl.wf4ever.rosrs.client.search;

import org.apache.solr.client.solrj.response.RangeFacet;

public class RangeFacetField extends Facet {

    private RangeFacet rangeFacet;


    public RangeFacetField(Facet f) {
        super();
    }


    public RangeFacetField(RangeFacet rangeFacet, String name) {
        super();
        this.rangeFacet = rangeFacet;
        this.name = name;
    }
}
