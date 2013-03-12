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
        this.readableName = name;
        for (Object object : rangeFacet.getCounts()) {
            Count count = (Count) object;
            values.add(new FacetValue(calcualteName(count), count.getCount(), fieldName, calculateQuery(count)));
        }
    }


    private String calculateQuery(Count count) {
        Integer from = new Integer(count.getValue());
        Integer to = from + new Integer(rangeFacet.getGap().toString());
        return fieldName + ":[" + from.toString() + " TO " + to.toString() + "]";
    }


    private String calcualteName(Count count) {
        Integer from = new Integer(count.getValue());
        Integer to = from + new Integer(rangeFacet.getGap().toString());
        return from.toString() + " - " + to.toString();
    }
}
