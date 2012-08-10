package org.purl.wf4ever.rosrs.client.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.scribe.model.Token;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.DoesNotExistException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * A client of ROSR 6 API.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public final class ROSRService {

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(ROSRService.class);

    /** Annotation MIME type. */
    public static final String ANNOTATION_MIME_TYPE = "application/vnd.wf4ever.annotation";

    /** Proxy MIME type. */
    public static final String PROXY_MIME_TYPE = "application/vnd.wf4ever.proxy";


    /**
     * Private constructor.
     */
    private ROSRService() {
        //nope
    }


    /**
     * Create a Research Object.
     * 
     * @param rodlURI
     *            RODL URI
     * @param roId
     *            RO ID, will be URL encoded
     * @param dLibraToken
     *            RODL access token
     * @return response from RODL, remember to close it after use
     */
    public static ClientResponse createResearchObject(URI rodlURI, String roId, Token dLibraToken) {
        Client client = Client.create();
        WebResource webResource = client.resource(rodlURI.toString()).path("ROs");
        return webResource.header("Authorization", "Bearer " + dLibraToken.getToken()).header("Slug", roId)
                .type("text/plain").post(ClientResponse.class);
    }


    /**
     * Delete a Research Object.
     * 
     * @param researchObjectURI
     *            RO URI
     * @param dLibraToken
     *            RODL access token
     * @return response from RODL, remember to close it after use
     */
    public static ClientResponse deleteResearchObject(URI researchObjectURI, Token dLibraToken) {
        Client client = Client.create();
        WebResource webResource = client.resource(researchObjectURI.toString());
        return webResource.header("Authorization", "Bearer " + dLibraToken.getToken()).delete(ClientResponse.class);
    }


    /**
     * Get a web resource.
     * 
     * @param resourceURI
     *            resource URI
     * @return a resource input stream, remember to close it after use
     */
    public static InputStream getResource(URI resourceURI) {
        Client client = Client.create();
        WebResource webResource = client.resource(resourceURI.toString());
        return webResource.get(InputStream.class);
    }


    /**
     * Get a HEAD response to an RODL resource.
     * 
     * @param resource
     *            resource URI
     * @return RODL response
     */
    public static ClientResponse getResourceHead(URI resource) {
        Client client = Client.create();
        WebResource webResource = client.resource(resource.toString());
        return webResource.head();
    }


    /**
     * Create a new resource in RODL.
     * 
     * @param researchObject
     *            research object URI
     * @param resourcePath
     *            path to the resource
     * @param content
     *            content input stream
     * @param contentType
     *            MIME type for the request
     * @param dLibraToken
     *            RODL access token
     * @return response from RODL, remember to close it after use
     */
    public static ClientResponse createResource(URI researchObject, String resourcePath, InputStream content,
            String contentType, Token dLibraToken) {
        Client client = Client.create();
        WebResource webResource = client.resource(researchObject.toString());
        if (!contentType.equals(PROXY_MIME_TYPE)) {
            return webResource.header("Authorization", "Bearer " + dLibraToken.getToken()).header("Slug", resourcePath)
                    .type(contentType).post(ClientResponse.class, content);
        } else {
            URI resource = researchObject.resolve(resourcePath);
            aggregateResource(researchObject, resource, dLibraToken);
            return updateResource(resource, content, contentType, dLibraToken);

        }
    }


    /**
     * Aggregate an external resource in RO.
     * 
     * @param researchObject
     *            research object URI
     * @param resource
     *            external resource URI
     * @param dLibraToken
     *            RODL access token
     * @return response from RODL, remember to close it after use
     */
    public static ClientResponse aggregateResource(URI researchObject, URI resource, Token dLibraToken) {
        Client client = Client.create();
        WebResource webResource = client.resource(researchObject.toString());
        OntModel model = ModelFactory.createOntologyModel();
        Individual proxy = model.createIndividual(Vocab.ORE_PROXY);
        Resource proxyFor = model.createResource(resource.toString());
        model.add(proxy, Vocab.ORE_PROXY_FOR, proxyFor);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        model.write(out);
        return webResource.header("Authorization", "Bearer " + dLibraToken.getToken()).type("application/rdf+xml")
                .post(ClientResponse.class, new ByteArrayInputStream(out.toByteArray()));
    }


    /**
     * Update an existing resource in RODL.
     * 
     * @param resourceURI
     *            resource URI
     * @param content
     *            content input stream
     * @param contentType
     *            MIME type for the request
     * @param dLibraToken
     *            RODL access token
     * @return response from RODL, remember to close it after use
     */
    public static ClientResponse updateResource(URI resourceURI, InputStream content, String contentType,
            Token dLibraToken) {
        Client client = Client.create();
        WebResource webResource = client.resource(resourceURI.toString());
        return webResource.header("Authorization", "Bearer " + dLibraToken.getToken()).type(contentType)
                .put(ClientResponse.class, content);
    }


    //    /**
    //     * Create a new RDF resource in RODL.
    //     * 
    //     * @param bodyURI
    //     *            resource URI
    //     * @param statements
    //     *            list of Jena statements that should make the content
    //     * @param dLibraToken
    //     *            RODL access token
    //     * @return response from RODL, remember to close it after use
    //     */
    //    public static InputStream create(URI bodyURI, List<Statement> statements, Token dLibraToken) {
    //        OntModel body = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
    //        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
    //        if (statements != null) {
    //            for (Statement statement : statements) {
    //                body.add(statement);
    //            }
    //        }
    //        body.write(out2);
    //        return updateResource(bodyURI, new ByteArrayInputStream(out2.toByteArray()), "application/rdf+xml", dLibraToken);
    //    }

    /**
     * Delete a resource from RODL.
     * 
     * @param resourceURI
     *            resource URI
     * @param dLibraToken
     *            RODL access token
     * @return response from RODL, remember to close it after use
     */
    public static ClientResponse deleteResource(URI resourceURI, Token dLibraToken) {
        Client client = Client.create();
        WebResource webResource = client.resource(resourceURI.toString());
        return webResource.header("Authorization", "Bearer " + dLibraToken.getToken()).delete(ClientResponse.class);
    }


    /**
     * Return data about a RODL user.
     * 
     * @param rodlURI
     *            rodl URI
     * @param userURI
     *            URI of the user in RODL
     * @return RDF graph input stream
     */
    public static InputStream getUser(URI rodlURI, URI userURI) {
        Client client = Client.create();
        WebResource webResource = client.resource(rodlURI.toString()).path("users")
                .path(Base64.encodeBase64URLSafeString(userURI.toString().getBytes()));
        return webResource.get(InputStream.class);
    }


    /**
     * Return data about the access token owner.
     * 
     * @param rodlURI
     *            RODL URI
     * @param dLibraToken
     *            RODL access token
     * @return RDF graph input stream
     */
    public static InputStream getWhoAmi(URI rodlURI, Token dLibraToken) {
        Client client = Client.create();
        WebResource webResource = client.resource(rodlURI.toString()).path("whoami");
        return webResource.header("Authorization", "Bearer " + dLibraToken.getToken()).get(InputStream.class);
    }


    /**
     * Return a list of all ROs.
     * 
     * @param rodlURI
     *            RODL URI
     * @return a list of RO URIs
     * @throws URISyntaxException
     *             if the URIs returned by RODL are incorrect
     */
    public static List<URI> getROList(URI rodlURI)
            throws URISyntaxException {
        return getROList(rodlURI, null);
    }


    /**
     * Return a list of ROs. If the access token is not null, returns a list of ROs belonging to the access token owner.
     * Otherwise returns a list of all ROs.
     * 
     * @param rodlURI
     *            RODL URI
     * @param dLibraToken
     *            RODL access token
     * @return a list of RO URIs
     * @throws URISyntaxException
     *             if the URIs returned by RODL are incorrect
     */
    public static List<URI> getROList(URI rodlURI, Token dLibraToken)
            throws URISyntaxException {
        Client client = Client.create();
        WebResource webResource = client.resource(rodlURI.toString()).path("ROs");
        String response;
        if (dLibraToken == null) {
            response = webResource.get(String.class);
        } else {
            response = webResource.header("Authorization", "Bearer " + dLibraToken.getToken()).get(String.class);
        }
        List<URI> uris = new ArrayList<URI>();
        for (String s : response.split("[\\r\\n]+")) {
            if (!s.isEmpty()) {
                uris.add(new URI(s));
            }
        }
        return uris;
    }


    /**
     * Create an annotation in RODL using an existing resource as the annotation body.
     * 
     * @param researchObject
     *            RO URI
     * @param targets
     *            annotated resources URIs
     * @param bodyURI
     *            annotation body URI
     * @param dLibraToken
     *            RODL access token
     * @return RODL response
     */
    public static ClientResponse addAnnotation(URI researchObject, List<URI> targets, URI bodyURI, Token dLibraToken) {
        Client client = Client.create();
        WebResource webResource = client.resource(researchObject.toString());
        OntModel model = ModelFactory.createOntologyModel();
        Individual annotation = model.createIndividual(Vocab.RO_AGGREGATED_ANNOTATION);
        Resource body = model.createResource(bodyURI.toString());
        model.add(annotation, Vocab.AO_BODY, body);
        for (URI targetURI : targets) {
            Resource target = model.createResource(targetURI.toString());
            model.add(annotation, Vocab.RO_ANNOTATES_AGGREGATED_RESOURCE, target);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        model.write(out);
        return webResource.header("Authorization", "Bearer " + dLibraToken.getToken()).type(ANNOTATION_MIME_TYPE)
                .post(ClientResponse.class, new ByteArrayInputStream(out.toByteArray()));
    }


    /**
     * Aggregate a new resource in RO using it as an annotation body.
     * 
     * @param researchObject
     *            RO URI
     * @param targets
     *            annotated resources URIs
     * @param bodyPath
     *            annotation body path
     * @param content
     *            content input stream
     * @param contentType
     *            MIME type for the request
     * @param dLibraToken
     *            RODL access token
     * @return RODL response
     */
    public static ClientResponse addAnnotation(URI researchObject, List<URI> targets, String bodyPath,
            InputStream content, String contentType, Token dLibraToken) {
        if (!ANNOTATION_MIME_TYPE.equals(contentType)) {
            Client client = Client.create();
            WebResource webResource = client.resource(researchObject.toString());
            Builder builder = webResource.header("Authorization", "Bearer " + dLibraToken.getToken())
                    .header("Slug", bodyPath).type(contentType);
            for (URI target : targets) {
                builder = builder.header("Link",
                    String.format("<%s>; rel=\"http://purl.org/ao/annotates\"", target.toString()));
            }
            return builder.post(ClientResponse.class, content);
        } else {
            URI resource = researchObject.resolve(bodyPath);
            addAnnotation(researchObject, targets, resource, dLibraToken);
            return updateResource(resource, content, contentType, dLibraToken);
        }
    }


    /**
     * Delete an annotation and its annotation body, if exists.
     * 
     * @param annURI
     *            annotation URI
     * @param dLibraToken
     *            RODL access token
     * @return RODL response to uploading the manifest
     */
    public static ClientResponse deleteAnnotationAndBody(URI annURI, Token dLibraToken) {
        Client client = Client.create();
        client.setFollowRedirects(false);
        ClientResponse response = client.resource(annURI.toString()).get(ClientResponse.class);
        if (response.getClientResponseStatus().getStatusCode() == HttpStatus.SC_SEE_OTHER) {
            client.resource(response.getLocation()).header("Authorization", "Bearer " + dLibraToken.getToken())
                    .delete();
        }
        return client.resource(annURI).header("Authorization", "Bearer " + dLibraToken.getToken())
                .delete(ClientResponse.class);
    }


    /**
     * Checks if it is possible to create an RO with workspace "default" and version "v1".
     * 
     * @param rodlURI
     *            RODL URI
     * @param roId
     *            RO id
     * @return true if the RO id is free, false otherwise
     * @throws URISyntaxException
     *             if the URIs returned by RODL are not correct
     */
    public static boolean isRoIdFree(URI rodlURI, String roId)
            throws URISyntaxException {
        //FIXME there should be a way to implement this without getting the list of all URIs
        List<URI> ros = getROList(rodlURI);
        URI ro = new URI(rodlURI.getScheme(), rodlURI.getHost(), rodlURI.getPath() + "ROs/" + roId + "/", null);
        return !ros.contains(ro);
    }


    /**
     * Create a Jena {@link OntModel} of a manifest.
     * 
     * @param researchObjectURI
     *            RO URI
     * @return the OntModel
     */
    public static OntModel createManifestModel(URI researchObjectURI) {
        URI manifestURI = researchObjectURI.resolve(".ro/manifest.rdf");
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
        try {
            model.read(manifestURI.toString());
        } catch (DoesNotExistException e) {
            // do nothing, model will be empty
            LOG.trace("The manifest does not exist for RO: " + researchObjectURI, e);
        }
        if (model.isEmpty()) {
            // HACK for old ROs
            manifestURI = researchObjectURI.resolve(".ro/manifest");
            model.read(manifestURI.toString());
        }
        return model;
    }


    /**
     * Create a Jena {@link OntModel} of a manifest and its annotations.
     * 
     * @param researchObjectURI
     *            RO URI
     * @return the OntModel
     */
    public static OntModel createManifestAndAnnotationsModel(URI researchObjectURI) {
        URI manifestURI = researchObjectURI.resolve(".ro/manifest.trig");
        NamedGraphSet graphset = new NamedGraphSetImpl();
        graphset.read(manifestURI.toString() + "?original=manifest.rdf", "TRIG");
        if (graphset.countQuads() == 0) {
            // HACK for old ROs
            graphset.read(manifestURI.toString() + "?original=manifest", "TRIG");
        }
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM,
            graphset.asJenaModel(researchObjectURI.resolve(".ro/manifest.rdf").toString()));
        model.add(Vocab.MODEL);
        return model;
    }


    /**
     * Generate a path for an annotation body of a resource. The template is ["ro"|resource_name] + "-" + random_string.
     * 
     * @param targetPath
     *            the annotation body target relative to the RO URI. null means the RO itself
     * @return an annotation body path relative to the RO URI
     */
    public static String createAnnotationBodyPath(String targetPath) {
        String targetName;
        if (targetPath == null || targetPath.isEmpty()) {
            targetName = "ro";
        } else {
            String[] segments = targetPath.split("/");
            targetName = segments[segments.length - 1];
        }
        String randomBit = "" + Math.abs(UUID.randomUUID().getLeastSignificantBits());

        return ".ro/" + targetName + "-" + randomBit + ".rdf";
    }

}
