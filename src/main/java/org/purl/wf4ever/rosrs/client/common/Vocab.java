package org.purl.wf4ever.rosrs.client.common;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;

public class Vocab
{

	static final String RO_NAMESPACE = "http://purl.org/wf4ever/ro#";

	static final String ROEVO_NAMESPACE = "http://purl.org/wf4ever/roevo#";

	static final String ORE_NAMESPACE = "http://www.openarchives.org/ore/terms/";

	static final String AO_NAMESPACE = "http://purl.org/ao/";

	static final String WFPROV_NAMESPACE = "http://purl.org/wf4ever/wfprov#";

	static final String WFDESC_NAMESPACE = "http://purl.org/wf4ever/wfdesc#";

	static final String WF4EVER_NAMESPACE = "http://purl.org/wf4ever/wf4ever#";

	public static final OntModel model = (OntModel) ModelFactory
			.createOntologyModel(OntModelSpec.OWL_LITE_MEM_RDFS_INF).read(RO_NAMESPACE).read(ORE_NAMESPACE)
			.read(AO_NAMESPACE).read(WFPROV_NAMESPACE).read(WFDESC_NAMESPACE).read(WF4EVER_NAMESPACE).read(DCTerms.NS)
			.read(ROEVO_NAMESPACE);

	public static final Resource roResource = model.createResource(RO_NAMESPACE + "Resource");

	public static final Resource researchObject = model.createResource(RO_NAMESPACE + "ResearchObject");

	public static final Resource aggregatedAnnotation = model.createResource(RO_NAMESPACE + "AggregatedAnnotation");

	public static final Resource foafAgent = model.createResource("http://xmlns.com/foaf/0.1/Agent");

	public static final Resource snapshotRO = model.createResource(ROEVO_NAMESPACE + "SnapshotRO");

	public static final Resource liveRO = model.createResource(ROEVO_NAMESPACE + "LiveRO");

	public static final Resource archivedRO = model.createResource(ROEVO_NAMESPACE + "ArchivedRO");

	public static final OntProperty foafName = model.createOntProperty("http://xmlns.com/foaf/0.1/name");

	public static final OntProperty foafPrimaryTopic = model
			.createOntProperty("http://xmlns.com/foaf/0.1/primaryTopic");

	public static final OntProperty filesize = model.createOntProperty("http://purl.org/wf4ever/ro#filesize");

	public static final OntProperty aggregates = model.createOntProperty(Vocab.ORE_NAMESPACE + "aggregates");

	public static final OntProperty describes = model.createOntProperty(Vocab.ORE_NAMESPACE + "describes");

	public static final OntProperty annotatesAggregatedResource = model.createOntProperty(Vocab.RO_NAMESPACE
			+ "annotatesAggregatedResource");

	public static final OntProperty aoBody = model.createOntProperty(Vocab.AO_NAMESPACE + "body");

	public static final OntProperty hasSubProcess = model.createOntProperty(Vocab.WFDESC_NAMESPACE + "hasSubProcess");

	public static final OntProperty isSnapshotOf = model.createOntProperty(Vocab.ROEVO_NAMESPACE + "isSnapshotOf");

	public static final OntProperty hasPreviousVersion = model.createOntProperty(Vocab.ROEVO_NAMESPACE
			+ "hasPreviousVersion");

	public static final OntProperty derivedFrom = model.createOntProperty(Vocab.ROEVO_NAMESPACE + "derivedFrom");

	static {
		model.add(DCTerms.references, OWL.inverseOf, DCTerms.isReferencedBy);
		model.add(DCTerms.isReferencedBy, OWL.inverseOf, DCTerms.references);
		model.add(DCTerms.replaces, OWL.inverseOf, DCTerms.isReplacedBy);
		model.add(DCTerms.isReplacedBy, OWL.inverseOf, DCTerms.replaces);
		model.add(DCTerms.requires, OWL.inverseOf, DCTerms.isRequiredBy);
		model.add(DCTerms.isRequiredBy, OWL.inverseOf, DCTerms.requires);
	}

}
