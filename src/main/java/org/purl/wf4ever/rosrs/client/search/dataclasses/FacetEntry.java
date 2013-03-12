package org.purl.wf4ever.rosrs.client.search.dataclasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;

public class FacetEntry implements Serializable {

    protected String fieldName;
    protected String readableName;
    protected List<FacetValue> values;


    public FacetEntry() {
        this.values = new ArrayList<FacetValue>();
    }


    public FacetEntry(FacetField field, String name) {
        this();
        this.readableName = name;
        this.fieldName = field.getName();
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


    public String getName() {
        return readableName;
    }

}
