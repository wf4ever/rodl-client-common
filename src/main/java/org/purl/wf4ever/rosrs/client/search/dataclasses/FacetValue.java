package org.purl.wf4ever.rosrs.client.search.dataclasses;

import java.io.Serializable;

public class FacetValue implements Serializable {

    private String label;
    private Integer count;
    private String paramName;
    private String query;


    public FacetValue(String label, Integer count, String paramName, String query) {
        this.label = label;
        this.count = count;
        this.setParamName(paramName);
        this.setQuery(query);
    }


    public Integer getCount() {
        return count;
    }


    public void setCount(Integer count) {
        this.count = count;
    }


    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public String getParamName() {
        return paramName;
    }


    public void setParamName(String paramName) {
        this.paramName = paramName;
    }


    public String getQuery() {
        return query;
    }


    public void setQuery(String query) {
        this.query = query;
    }
}
