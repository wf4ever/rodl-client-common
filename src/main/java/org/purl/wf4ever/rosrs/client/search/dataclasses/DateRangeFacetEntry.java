package org.purl.wf4ever.rosrs.client.search.dataclasses;

import java.util.Date;

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
        this.name = name;

        for (Object object : rangeFacet.getCounts()) {
            Count count = (Count) object;
            Date to = null;
            DateTime timeBegin = new DateTime(count.getValue());
            DateTime timeEnd = new DateTime(count.getValue());
            timeEnd = timeEnd.plusMonths(3);
            String query = fieldName + ":[" + count.getValue() + " TO " + count.getValue() + rangeFacet.getGap() + "]";
            values.add(new FacetValue(
                    timeBegin.toString().substring(0, 7) + " - " + timeEnd.toString().substring(0, 7),
                    count.getCount(), fieldName, query));
        }
    }
}
