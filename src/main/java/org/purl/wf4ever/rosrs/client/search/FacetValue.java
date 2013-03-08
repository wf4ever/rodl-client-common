package org.purl.wf4ever.rosrs.client.search;

public class FacetValue {

    private String label;
    private Integer count;
    private String query;


    public FacetValue(String label, Integer count, String query) {
        this.label = label;
        this.count = count;
        this.query = query;
    }


    public Integer getCount() {
        return count;
    }


    public void setCount(Integer count) {
        this.count = count;
    }


    public String getQuery() {
        return query;
    }


    public void setQuery(String query) {
        this.query = query;
    }


    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }
}
