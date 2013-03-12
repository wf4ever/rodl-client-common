package org.purl.wf4ever.rosrs.client.search.dataclasses;

import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.client.solrj.response.RangeFacet.Count;

public class RangeFacetEntry extends FacetEntry {

    private RangeFacet rangeFacet;


    public RangeFacetEntry(FacetEntry f) {
        super();
    }


    public RangeFacetEntry(RangeFacet rangeFacet, String name) {
        super();
        this.fieldName = rangeFacet.getName();
        this.rangeFacet = rangeFacet;
        this.name = name;
        for (Object object : rangeFacet.getCounts()) {
            Count count = (Count) object;
            Integer from = new Integer(count.getValue());
            Integer to = from + new Integer(rangeFacet.getGap().toString());

            String query = fieldName + ":[" + from.toString() + " TO " + to.toString() + "]";
            values.add(new FacetValue(from.toString() + " - " + to.toString(), count.getCount(), fieldName, query));
        }
    }
}
