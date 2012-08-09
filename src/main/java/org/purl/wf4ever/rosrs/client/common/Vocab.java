package org.purl.wf4ever.rosrs.client.common;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * Class aggregating the RO model vocabulary.
 * 
 * @author piotrekhol
 * 
 */
public final class Vocab {

    /** RO ontology namespace. */
    static final String RO_NAMESPACE = "http://purl.org/wf4ever/ro#";

    /** ROEVO ontology namespace. */
    static final String ROEVO_NAMESPACE = "http://purl.org/wf4ever/roevo#";

    /** ORE ontology namespace. */
    static final String ORE_NAMESPACE = "http://www.openarchives.org/ore/terms/";

    /** Annotation Ontology namespace. */
    static final String AO_NAMESPACE = "http://purl.org/ao/";

    /** wfprov ontology namespace. */
    static final String WFPROV_NAMESPACE = "http://purl.org/wf4ever/wfprov#";

    /** wfdesc ontology namespace. */
    static final String WFDESC_NAMESPACE = "http://purl.org/wf4ever/wfdesc#";

    /** wf4ever ontology namespace. */
    static final String WF4EVER_NAMESPACE = "http://purl.org/wf4ever/wf4ever#";

    /** A model with all the ontologies. */
    public static final OntModel MODEL = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM_RDFS_INF);

    static {
        try {
            MODEL.read(RO_NAMESPACE).read(ORE_NAMESPACE).read(AO_NAMESPACE).read(WFPROV_NAMESPACE)
                    .read(WFDESC_NAMESPACE).read(WF4EVER_NAMESPACE).read(DCTerms.NS).read(ROEVO_NAMESPACE);
        } catch (JenaException e) {
            Logger.getLogger(Vocab.class).error("Error when loading the model", e);
        }
        MODEL.add(DCTerms.references, OWL.inverseOf, DCTerms.isReferencedBy);
        MODEL.add(DCTerms.isReferencedBy, OWL.inverseOf, DCTerms.references);
        MODEL.add(DCTerms.replaces, OWL.inverseOf, DCTerms.isReplacedBy);
        MODEL.add(DCTerms.isReplacedBy, OWL.inverseOf, DCTerms.replaces);
        MODEL.add(DCTerms.requires, OWL.inverseOf, DCTerms.isRequiredBy);
        MODEL.add(DCTerms.isRequiredBy, OWL.inverseOf, DCTerms.requires);
    }

    /** ro:Resource. */
    public static final Resource RO_RESOURCE = MODEL.createResource(RO_NAMESPACE + "Resource");

    /** ro:ResearchObject. */
    public static final Resource RO_RESEARCH_OBJECT = MODEL.createResource(RO_NAMESPACE + "ResearchObject");

    /** ro:AggregatedAnnotation. */
    public static final Resource RO_AGGREGATED_ANNOTATION = MODEL.createResource(RO_NAMESPACE + "AggregatedAnnotation");

    /** foaf:Agent. */
    public static final Resource FOAF_AGENT = MODEL.createResource("http://xmlns.com/foaf/0.1/Agent");

    /** roevo:SnapshotRO. */
    public static final Resource ROEVO_SNAPSHOT_RO = MODEL.createResource(ROEVO_NAMESPACE + "SnapshotRO");

    /** roevo:LiveRO. */
    public static final Resource ROEVO_LIVE_RO = MODEL.createResource(ROEVO_NAMESPACE + "LiveRO");

    /** roevo:ArchivedRO. */
    public static final Resource ROEVO_ARCHIVED_RO = MODEL.createResource(ROEVO_NAMESPACE + "ArchivedRO");

    /** ore:Proxy. */
    public static final Resource ORE_PROXY = MODEL.createResource(Vocab.ORE_NAMESPACE + "Proxy");

    /** foaf:name. */
    public static final OntProperty FOAF_NAME = MODEL.createOntProperty("http://xmlns.com/foaf/0.1/name");

    /** foaf:primaryTopic. */
    public static final OntProperty FOAF_PRIMARY_TOPIC = MODEL
            .createOntProperty("http://xmlns.com/foaf/0.1/primaryTopic");

    /** ro:filesize (a fake one). */
    public static final OntProperty FILESIZE = MODEL.createOntProperty("http://purl.org/wf4ever/ro#filesize");

    /** ore:aggregates. */
    public static final OntProperty ORE_AGGREGATES = MODEL.createOntProperty(Vocab.ORE_NAMESPACE + "aggregates");

    /** ore:describes. */
    public static final OntProperty ORE_DESCRIBES = MODEL.createOntProperty(Vocab.ORE_NAMESPACE + "describes");

    /** ore:proxyFor. */
    public static final OntProperty ORE_PROXY_FOR = MODEL.createOntProperty(Vocab.ORE_NAMESPACE + "proxyFor");

    /** ro:annotatesAggregatedResource. */
    public static final OntProperty RO_ANNOTATES_AGGREGATED_RESOURCE = MODEL.createOntProperty(Vocab.RO_NAMESPACE
            + "annotatesAggregatedResource");

    /** ao:body. */
    public static final OntProperty AO_BODY = MODEL.createOntProperty(Vocab.AO_NAMESPACE + "body");

    /** wfdesc:hasSubProcess. */
    public static final OntProperty WFDESC_HAS_SUBPROCESS = MODEL.createOntProperty(Vocab.WFDESC_NAMESPACE
            + "hasSubProcess");

    /** roevo:isSnapshotOf. */
    public static final OntProperty ROEVO_IS_SNAPSHOT_OF = MODEL.createOntProperty(Vocab.ROEVO_NAMESPACE
            + "isSnapshotOf");

    /** roevo:hasPreviousVersion. */
    public static final OntProperty ROEVO_HAS_PREVIOUS_VERSION = MODEL.createOntProperty(Vocab.ROEVO_NAMESPACE
            + "hasPreviousVersion");

    /** roevo:derivedFrom. */
    public static final OntProperty ROEVO_DERIVED_FROM = MODEL.createOntProperty(Vocab.ROEVO_NAMESPACE + "derivedFrom");


    /**
     * Private constructor.
     */
    private Vocab() {
        // nope
    }
}
