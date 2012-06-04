package org.purl.wf4ever.rosrs.client.common;

import java.net.URI;
import java.util.Calendar;
import java.util.UUID;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;

public class ROService {

    /**
     * Delete the annotation from the manifest. Does not delete the annotation body.
     * 
     * In the future should be done by the RODL and supported via ROSR API.
     * 
     * @param manifest
     *            the Jena model of the manifest
     * @param annotationURI
     *            the annotation URI
     */
    public static URI deleteAnnotationFromManifest(OntModel manifest, URI annURI) {
        Individual ann = manifest.getIndividual(annURI.toString());
        if (ann == null) {
            throw new IllegalArgumentException("Annotation URI is not valid");
        }
        Resource body = ann.getPropertyResourceValue(Vocab.aoBody);

        manifest.removeAll(ann, null, null);
        manifest.removeAll(null, null, ann);
        if (body != null) {
            return URI.create(body.getURI());
        } else {
            return null;
        }
    }


    /**
     * Delete the blank node annotation from the manifest. Does not delete the annotation body.
     * 
     * In the future should be done by the RODL and supported via ROSR API.
     * 
     * @param manifest
     *            the Jena model of the manifest
     * @param annId
     *            the annotation id
     */
    public static URI deleteAnnotationFromManifest(OntModel manifest, AnonId annId) {
        Resource ann = manifest.createResource(annId);
        Resource body = ann.getPropertyResourceValue(Vocab.aoBody);

        manifest.removeAll(ann, null, null);
        manifest.removeAll(null, null, ann);
        if (body != null) {
            return URI.create(body.getURI());
        } else {
            return null;
        }
    }


    /**
     * Add an annotation to the manifest. You need to add the annotation body separately, after uploading the manifest
     * to RODL.
     * 
     * In the future should be done by the RODL and supported via ROSR API.
     * 
     * @param manifest
     *            the Jena model of the manifest
     * @param researchObjectURI
     *            research object URI
     * @param annURI
     *            the annotation URI
     * @param targetURI
     *            annotation target.
     * @param bodyURI
     *            the URI of the annotation body that will be uploaded later
     * @param userURI
     *            the URI representing the author of the annotation
     */
    public static void addAnnotationToManifestModel(OntModel manifest, URI researchObjectURI, URI annURI,
            URI targetURI, URI bodyURI, URI userURI) {
        Individual ann = manifest.createIndividual(annURI.toString(), Vocab.aggregatedAnnotation);
        ann.addProperty(Vocab.annotatesAggregatedResource, manifest.createResource(targetURI.toString()));
        ann.addProperty(Vocab.aoBody, manifest.createResource(bodyURI.toString()));
        ann.addProperty(DCTerms.created, manifest.createTypedLiteral(Calendar.getInstance()));
        Resource agent = manifest.createResource(userURI.toString());
        ann.addProperty(DCTerms.creator, agent);
        Individual ro = manifest.createResource(researchObjectURI.toString()).as(Individual.class);
        ro.addProperty(Vocab.aggregates, ann);
    }


    /**
     * 
     * @param manifest
     * @param researchObjectURI
     * @return i.e. http://sandbox.wf4ever-project.org/rosrs5/ROs/.ro/manifest.rdf#ann217/52 a272f1 -864f-4a42
     *         -89ff-2501a739d6f0
     */
    public static URI createAnnotationURI(OntModel manifest, URI researchObjectURI) {
        URI ann = null;
        do {
            ann = researchObjectURI.resolve(".ro/manifest.rdf#" + UUID.randomUUID().toString());
        } while (manifest != null && manifest.containsResource(manifest.createResource(ann.toString())));
        return ann;
    }


    /**
     * Generate a URI for an annotation body of a resource. The URI template is ["ro"|resource_name] + "-" +
     * random_string.
     * 
     * @param researchObjectURI
     *            research object URI
     * @param targetURI
     *            the annotation body target URI
     * @return an annotation body URI
     */
    public static URI createAnnotationBodyURI(URI researchObjectURI, URI targetURI) {
        String targetName;
        if (targetURI.equals(researchObjectURI))
            targetName = "ro";
        else
            targetName = targetURI.resolve(".").relativize(targetURI).toString();
        String randomBit = "" + Math.abs(UUID.randomUUID().getLeastSignificantBits());

        return researchObjectURI.resolve(".ro/" + targetName + "-" + randomBit + ".rdf");
    }

}
