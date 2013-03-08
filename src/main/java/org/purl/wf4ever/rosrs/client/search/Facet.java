package org.purl.wf4ever.rosrs.client.search;

import org.apache.solr.client.solrj.response.FacetField;

public class Facet {

    private String indexName;
    private String name;
    private FacetField field;


    public Facet(FacetField field) {
        this.setField(field);
    }


    public String getIndexName() {
        return indexName;
    }


    public void setIndexName(String indexName) {
        this.indexName = indexName;
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
