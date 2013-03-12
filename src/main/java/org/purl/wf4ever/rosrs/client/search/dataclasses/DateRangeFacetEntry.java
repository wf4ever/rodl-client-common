package org.purl.wf4ever.rosrs.client.search.dataclasses;

import org.apache.solr.client.solrj.response.RangeFacet;
import org.apache.solr.client.solrj.response.RangeFacet.Count;
import org.joda.time.DateTime;

public class DateRangeFacetEntry extends FacetEntry {

    private RangeFacet rangeFacet;


    public DateRangeFacetEntry(FacetEntry f) {
        super();
    }


    public DateRangeFacetEntry(RangeFacet rangeFacet, String name) {
        super();
        this.fieldName = rangeFacet.getName();
        this.rangeFacet = rangeFacet;
        this.readableName = name;

        for (Object object : rangeFacet.getCounts()) {
            Count count = (Count) object;
            values.add(new FacetValue(calculateLabel(count), count.getCount(), fieldName, calculateQuery(count,
                rangeFacet.getGap().toString())));
        }
    }


    private String calculateQuery(Count count, String gap) {
        return fieldName + ":[" + count.getValue() + " TO " + count.getValue() + gap + "]";
    }


    private String calculateLabel(Count count) {
        DateTime timeBegin = new DateTime(count.getValue());
        DateTime timeEnd = new DateTime(count.getValue());
        timeEnd = timeEnd.plusMonths(3);
        return timeBegin.toString().substring(0, 7) + " - " + timeEnd.toString().substring(0, 7);
    }
}
