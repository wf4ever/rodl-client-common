package org.purl.wf4ever.rosrs.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import pl.psnc.dl.wf4ever.vocabulary.AO;
import pl.psnc.dl.wf4ever.vocabulary.ORE;
import pl.psnc.dl.wf4ever.vocabulary.RO;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

/**
 * A client of ROSR 6 API.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class ROSRService implements Serializable {

    /** id. */
    private static final long serialVersionUID = 8005409732875360705L;

    /** Logger. */
    private static final Logger LOG = Logger.getLogger(ROSRService.class);

    /** Annotation MIME type. */
    public static final String ANNOTATION_MIME_TYPE = "application/vnd.wf4ever.annotation";

    /** Proxy MIME type. */
    public static final String PROXY_MIME_TYPE = "application/vnd.wf4ever.proxy";

    /** ro:Folder MIME type. */
    public static final String FOLDER_MIME_TYPE = "application/vnd.wf4ever.folder";

    /** ro:FolderEntry MIME type. */
    public static final String FOLDER_ENTRY_MIME_TYPE = "application/vnd.wf4ever.folderentry";

    /** ROSRS URI. */
    private URI rosrsURI;

    /** RODL access token. */
    private String token;

    /** web client. */
    private transient Client client;


    /**
     * Constructor.
     * 
     * @param rosrsUri
     *            ROSRS URI
     * @param token
     *            RODL access token
     */
    public ROSRService(URI rosrsUri, String token) {
        this.rosrsURI = rosrsUri;
        this.token = token;
    }


    /**
     * Return an HTTP client, creating it if necessary.
     * 
     * @return an HTTP client
     */
    private Client getClient() {
        if (client == null) {
            client = Client.create();
        }
        return client;
    }


    public URI getRosrsURI() {
        return rosrsURI;
    }


    public String getToken() {
        return token;
    }


    /**
     * Create a Research Object.
     * 
     * @param roId
     *            RO ID, will be URL encoded
     * @return response from RODL, remember to close it after use
     * @throws ROSRSException
     *             when the response code is not 201 Created
     */
    public ClientResponse createResearchObject(String roId)
            throws ROSRSException {
        WebResource webResource = getClient().resource(rosrsURI.toString());
        ClientResponse response = webResource.header("Authorization", "Bearer " + token).header("Slug", roId)
                .type("text/plain").accept("application/rdf+xml").post(ClientResponse.class);
        if (response.getStatus() == HttpStatus.SC_CREATED) {
            return response;
        } else {
            throw new ROSRSException("Creating the RO failed", response);
        }
    }


    /**
     * Delete a Research Object.
     * 
     * @param researchObjectURI
     *            RO URI
     * @return response from RODL, remember to close it after use
     * @throws ROSRSException
     *             when the response code is neither 204 nor 404
     */
    public ClientResponse deleteResearchObject(URI researchObjectURI)
            throws ROSRSException {
        WebResource webResource = getClient().resource(researchObjectURI.toString());
        ClientResponse response = webResource.header("Authorization", "Bearer " + token).delete(ClientResponse.class);
        if (response.getStatus() == HttpStatus.SC_NO_CONTENT || response.getStatus() == HttpStatus.SC_NOT_FOUND) {
            return response;
        } else {
            throw new ROSRSException("Deleting the RO failed", response);
        }
    }


    /**
     * Get a web resource.
     * 
     * @param resourceURI
     *            resource URI
     * @param accept
     *            acceptable MIME type or null
     * @return a resource input stream, remember to close it after use
     * @throws ROSRSException
     *             when the response code is not 2xx
     */
    public ClientResponse getResource(URI resourceURI, String accept)
            throws ROSRSException {
        WebResource webResource = getClient().resource(resourceURI.toString());
        LOG.debug("Start loading " + resourceURI + " time: " + DateTime.now());
        ClientResponse response = webResource.accept(accept).get(ClientResponse.class);
        LOG.debug("Ended loading " + resourceURI + " time: " + DateTime.now());
        if (response.getStatus() == HttpStatus.SC_OK) {
            return response;
        } else {
            throw new ROSRSException("Getting the resource failed", response);
        }
    }


    /**
     * Get a HEAD response to an RODL resource asking for RDF/XML.
     * 
     * @param resource
     *            resource URI
     * @return RODL response
     * @throws ROSRSException
     *             when the response code is neither 200
     */
    public ClientResponse getResourceHead(URI resource)
            throws ROSRSException {
        WebResource webResource = getClient().resource(resource.toString());
        ClientResponse response = webResource.accept("application/rdf+xml").head();
        if (response.getStatus() == HttpStatus.SC_OK) {
            return response;
        } else {
            throw new ROSRSException("Getting the resource head failed", response);
        }
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
     * @return response from RODL, remember to close it after use
     * @throws ROSRSException
     *             when the response code is neither 201 nor 409
     */
    public ClientResponse aggregateInternalResource(URI researchObject, String resourcePath, InputStream content,
            String contentType)
            throws ROSRSException {
        WebResource webResource = getClient().resource(researchObject.toString());
        if (!contentType.equals(PROXY_MIME_TYPE)) {
            ClientResponse response = webResource.header("Authorization", "Bearer " + token)
                    .header("Slug", resourcePath).type(contentType).post(ClientResponse.class, content);
            if (response.getStatus() == HttpStatus.SC_CREATED || response.getStatus() == HttpStatus.SC_CONFLICT) {
                return response;
            } else {
                throw new ROSRSException("Creating the resource failed", response);
            }
        } else {
            URI resource = researchObject.resolve(resourcePath);
            aggregateExternalResource(researchObject, resource);
            return updateResource(resource, content, contentType);

        }
    }


    /**
     * Aggregate an external resource in RO.
     * 
     * @param researchObject
     *            research object URI
     * @param resource
     *            external resource URI
     * @return response from RODL, remember to close it after use
     * @throws ROSRSException
     *             when the response code is not 201
     */
    public ClientResponse aggregateExternalResource(URI researchObject, URI resource)
            throws ROSRSException {
        WebResource webResource = getClient().resource(researchObject.toString());
        OntModel model = ModelFactory.createOntologyModel();
        Individual proxy = model.createIndividual(ORE.Proxy);
        Resource proxyFor = model.createResource(resource.toString());
        model.add(proxy, ORE.proxyFor, proxyFor);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        model.write(out);
        ClientResponse response = webResource.header("Authorization", "Bearer " + token)
                .type("application/vnd.wf4ever.proxy")
                .post(ClientResponse.class, new ByteArrayInputStream(out.toByteArray()));
        if (response.getStatus() == HttpStatus.SC_CREATED) {
            return response;
        } else {
            throw new ROSRSException("Aggregating the resource failed", response);
        }
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
     * @return response from RODL, remember to close it after use
     * @throws ROSRSException
     *             when the response code is not 200
     */
    public ClientResponse updateResource(URI resourceURI, InputStream content, String contentType)
            throws ROSRSException {
        WebResource webResource = getClient().resource(resourceURI.toString());
        ClientResponse response = webResource.header("Authorization", "Bearer " + token).type(contentType)
                .put(ClientResponse.class, content);
        if (response.getStatus() == HttpStatus.SC_OK) {
            return response;
        } else {
            throw new ROSRSException("Updating the resource failed", response);
        }
    }


    /**
     * Delete a resource from RODL.
     * 
     * @param resourceURI
     *            resource URI
     * @return response from RODL, remember to close it after use
     * @throws ROSRSException
     *             when the response code is not 204 nor 404
     */
    public ClientResponse deleteResource(URI resourceURI)
            throws ROSRSException {
        WebResource webResource = getClient().resource(resourceURI.toString());
        ClientResponse response = webResource.header("Authorization", "Bearer " + token).delete(ClientResponse.class);
        if (response.getStatus() == HttpStatus.SC_NO_CONTENT || response.getStatus() == HttpStatus.SC_NOT_FOUND) {
            return response;
        } else {
            throw new ROSRSException("Deleting the resource failed", response);
        }
    }


    /**
     * Return a list of ROs, either a list of ROs belonging to the access token owner or a list of all ROs.
     * 
     * @param all
     *            include all ROs
     * @return a list of RO URIs
     * @throws URISyntaxException
     *             if the URIs returned by RODL are incorrect
     * @throws ROSRSException
     *             when the response code is not 2xx
     */
    public List<URI> getROList(boolean all)
            throws URISyntaxException, ROSRSException {
        WebResource webResource = getClient().resource(rosrsURI.toString());
        String response;
        try {
            if (all) {
                response = webResource.get(String.class);
            } else {
                response = webResource.header("Authorization", "Bearer " + token).get(String.class);
            }
        } catch (UniformInterfaceException e) {
            throw new ROSRSException(e.getLocalizedMessage(), e.getResponse());
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
     * @return RODL response
     * @throws ROSRSException
     *             when the response code is not 201
     */
    public ClientResponse addAnnotation(URI researchObject, Collection<URI> targets, URI bodyURI)
            throws ROSRSException {
        WebResource webResource = getClient().resource(researchObject.toString());
        OntModel model = ModelFactory.createOntologyModel();
        Individual annotation = model.createIndividual(RO.AggregatedAnnotation);
        Resource body = model.createResource(bodyURI.toString());
        model.add(annotation, AO.body, body);
        for (URI targetURI : targets) {
            Resource target = model.createResource(targetURI.toString());
            model.add(annotation, AO.annotatesResource, target);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        model.write(out);
        ClientResponse response = webResource.header("Authorization", "Bearer " + token).type(ANNOTATION_MIME_TYPE)
                .post(ClientResponse.class, new ByteArrayInputStream(out.toByteArray()));
        if (response.getStatus() == HttpStatus.SC_CREATED) {
            return response;
        } else {
            throw new ROSRSException("Creating the annotation failed", response);
        }
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
     * @return RODL response
     * @throws ROSRSException
     *             when the response code is not 201 or 409 (or 200 in case of aggregating an annotation description)
     */
    public ClientResponse addAnnotation(URI researchObject, Set<URI> targets, String bodyPath, InputStream content,
            String contentType)
            throws ROSRSException {
        if (!ANNOTATION_MIME_TYPE.equals(contentType)) {
            WebResource webResource = getClient().resource(researchObject.toString());
            Builder builder = webResource.header("Authorization", "Bearer " + token).type(contentType);
            if (bodyPath != null) {
                builder = builder.header("Slug", bodyPath);
            }
            for (URI target : targets) {
                builder = builder.header("Link",
                    String.format("<%s>; rel=\"http://purl.org/ao/annotatesResource\"", target.toString()));
            }
            ClientResponse response = builder.post(ClientResponse.class, content);
            if (response.getStatus() == HttpStatus.SC_CREATED || response.getStatus() == HttpStatus.SC_CONFLICT) {
                return response;
            } else {
                throw new ROSRSException("Creating the resource failed", response);
            }
        } else {
            URI resource = researchObject.resolve(bodyPath);
            addAnnotation(researchObject, targets, resource);
            return updateResource(resource, content, contentType);
        }
    }


    /**
     * Delete an annotation and its annotation body, if exists.
     * 
     * @param annURI
     *            annotation URI
     * @return RODL response to uploading the manifest
     * @throws ROSRSException
     *             when the response code is not 204 or 404
     */
    public ClientResponse deleteAnnotationAndBody(URI annURI)
            throws ROSRSException {
        getClient().setFollowRedirects(false);
        try {
            ClientResponse response = getClient().resource(annURI.toString()).get(ClientResponse.class);
            if (response.getClientResponseStatus().getStatusCode() == HttpStatus.SC_SEE_OTHER) {
                ClientResponse bodyResponse = getClient().resource(response.getLocation())
                        .header("Authorization", "Bearer " + token).delete(ClientResponse.class);
                if (bodyResponse.getStatus() != HttpStatus.SC_NO_CONTENT) {
                    LOG.warn("Unexpected response when deleting the annotation body: " + bodyResponse.toString());
                }
            }
            response = getClient().resource(annURI).header("Authorization", "Bearer " + token)
                    .delete(ClientResponse.class);
            if (response.getStatus() == HttpStatus.SC_NO_CONTENT || response.getStatus() == HttpStatus.SC_NOT_FOUND) {
                return response;
            } else {
                throw new ROSRSException("Deleting the annotation failed", response);
            }
        } finally {
            getClient().setFollowRedirects(true);
        }
    }


    /**
     * Create a new folder in the Research Object.
     * 
     * @param researchObject
     *            research object URI
     * @param path
     *            folder path
     * @return response from server
     * @throws ROSRSException
     *             server returned other code than 201 Created or 409 Conflict
     */
    public ClientResponse createFolder(URI researchObject, String path)
            throws ROSRSException {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
        model.createIndividual(RO.Folder);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        model.write(out, "RDF/XML");
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

        WebResource webResource = getClient().resource(researchObject.toString());
        ClientResponse response = webResource.header("Authorization", "Bearer " + token).header("Slug", path)
                .type(FOLDER_MIME_TYPE).post(ClientResponse.class, in);
        if (response.getStatus() == HttpStatus.SC_CREATED || response.getStatus() == HttpStatus.SC_CONFLICT) {
            return response;
        } else {
            throw new ROSRSException("Creating the folder failed", response);
        }
    }


    /**
     * Create a new folder entry in a folder.
     * 
     * @param folder
     *            folder URI
     * @param resource
     *            resource to be added to the folder
     * @param name
     *            name of the resource in the folder
     * @return response from server
     * @throws ROSRSException
     *             server returned other code than 201 Created or 409 Conflict
     */
    public ClientResponse addFolderEntry(URI folder, URI resource, String name)
            throws ROSRSException {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
        Individual entry = model.createIndividual(RO.FolderEntry);
        entry.addProperty(ORE.proxyFor, model.createResource(resource.toString()));
        if (name != null) {
            entry.addProperty(RO.entryName, model.createLiteral(name));
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        model.write(out, "RDF/XML");
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

        WebResource webResource = getClient().resource(folder.toString());
        ClientResponse response = webResource.header("Authorization", "Bearer " + token).type(FOLDER_ENTRY_MIME_TYPE)
                .post(ClientResponse.class, in);
        if (response.getStatus() == HttpStatus.SC_CREATED || response.getStatus() == HttpStatus.SC_CONFLICT) {
            return response;
        } else {
            throw new ROSRSException("Creating the folder entry failed", response);
        }
    }

}
