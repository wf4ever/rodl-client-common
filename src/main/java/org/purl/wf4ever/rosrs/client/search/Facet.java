package org.purl.wf4ever.rosrs.client.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;

public class Facet {

    protected String fieldName;
    protected String name;
    protected FacetField field;
    protected List<FacetValue> values;


    public Facet(FacetField field) {
        this.setField(field);
        this.values = new ArrayList<FacetValue>();
    }


    public String getFieldName() {
        return fieldName;
    }


    public void setFieldName(String indexName) {
        this.fieldName = indexName;
    }


    public FacetField getField() {
        return field;
    }


    public void setField(FacetField field) {
        this.field = field;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

}
