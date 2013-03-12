package org.purl.wf4ever.rosrs.client.search.dataclasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;

public class FacetEntry implements Serializable {

    protected String fieldName;
    protected String name;
    protected List<FacetValue> values;
    private FacetField field;


    public FacetEntry() {
        this.values = new ArrayList<FacetValue>();
    }


    public FacetEntry(FacetField field, String name) {
        this();
        this.setField(field);
        this.fieldName = field.getName();
        this.name = name;
        this.values = new ArrayList<FacetValue>();
        if (field != null) {
            for (Count count : field.getValues()) {
                String query = fieldName + ":" + "\"" + count.getName() + "\"";
                values.add(new FacetValue(count.getName(), (int) count.getCount(), fieldName, query));
            }
        }

    }


    public List<FacetValue> getValues() {
        return this.values;
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
