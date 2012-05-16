package org.purl.wf4ever.rosrs.client.common;

/**
 * 
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.Token;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.DoesNotExistException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * @author Piotr Hołubowicz
 * 
 */
public class ROSRService
{

	private static final Logger log = Logger.getLogger(ROSRService.class);


	public static ClientResponse createResearchObject(URI rodlURI, String roId, Token dLibraToken)
	{
		Client client = Client.create();
		WebResource webResource = client.resource(rodlURI.toString()).path("ROs");
		return webResource.header("Authorization", "Bearer " + dLibraToken.getToken()).type("text/plain")
				.post(ClientResponse.class, roId);
	}


	public static ClientResponse deleteResearchObject(URI researchObjectURI, Token dLibraToken)
	{
		Client client = Client.create();
		WebResource webResource = client.resource(researchObjectURI.toString());
		return webResource.header("Authorization", "Bearer " + dLibraToken.getToken()).delete(ClientResponse.class);
	}


	public static InputStream getResource(URI resourceURI)
	{
		Client client = Client.create();
		WebResource webResource = client.resource(resourceURI.toString());
		return webResource.get(InputStream.class);
	}


	public static ClientResponse uploadResource(URI resourceURI, InputStream content, String contentType,
			Token dLibraToken)
	{
		Client client = Client.create();
		WebResource webResource = client.resource(resourceURI.toString());
		return webResource.header("Authorization", "Bearer " + dLibraToken.getToken()).type(contentType)
				.put(ClientResponse.class, content);
	}


	public static ClientResponse uploadResource(URI bodyURI, List<Statement> statements, Token dLibraToken)
	{
		OntModel body = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		ByteArrayOutputStream out2 = new ByteArrayOutputStream();
		if (statements != null) {
			for (Statement statement : statements) {
				body.add(statement);
			}
		}
		body.write(out2);
		return uploadResource(bodyURI, new ByteArrayInputStream(out2.toByteArray()), "application/rdf+xml", dLibraToken);
	}


	public static ClientResponse deleteResource(URI resourceURI, Token dLibraToken)
	{
		Client client = Client.create();
		WebResource webResource = client.resource(resourceURI.toString());
		return webResource.header("Authorization", "Bearer " + dLibraToken.getToken()).delete(ClientResponse.class);
	}


	public static InputStream getUser(URI rodlURI, URI userURI)
	{
		Client client = Client.create();
		WebResource webResource = client.resource(rodlURI.toString()).path("users")
				.path(Base64.encodeBase64URLSafeString(userURI.toString().getBytes()));
		return webResource.get(InputStream.class);
	}


	public static InputStream getWhoAmi(URI rodlURI, Token dLibraToken)
		throws URISyntaxException
	{
		Client client = Client.create();
		WebResource webResource = client.resource(rodlURI.toString()).path("whoami");
		return webResource.header("Authorization", "Bearer " + dLibraToken.getToken()).get(InputStream.class);
	}


	public static List<URI> getROList(URI rodlURI)
		throws MalformedURLException, URISyntaxException
	{
		return getROList(rodlURI, null);
	}


	public static List<URI> getROList(URI rodlURI, Token dLibraToken)
		throws MalformedURLException, URISyntaxException
	{
		Client client = Client.create();
		WebResource webResource = client.resource(rodlURI.toString()).path("ROs");
		String response;
		if (dLibraToken == null) {
			response = webResource.get(String.class);
		}
		else {
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
	 * Creates an annotation in ROSRS
	 * 
	 * @param researchObjectURI
	 * @param targetURI
	 * @param userURI
	 * @param dLibraToken
	 * @throws OAuthException
	 * @throws URISyntaxException
	 */
	public static ClientResponse addAnnotation(URI baseURI, URI researchObjectURI, URI annURI, URI targetURI,
			URI bodyURI, URI userURI, Token dLibraToken)
		throws URISyntaxException
	{
		OntModel manifest = createManifestModel(researchObjectURI);
		ROService.addAnnotationToManifestModel(manifest, researchObjectURI, annURI, targetURI, bodyURI, userURI);
		return uploadManifestModel(researchObjectURI, manifest, dLibraToken);
	}


	public static ClientResponse deleteAnnotationAndBody(URI researchObjectURI, URI annURI, Token dLibraToken)
		throws IllegalArgumentException, URISyntaxException
	{
		OntModel manifest = createManifestModel(researchObjectURI);

		URI bodyURI = ROService.deleteAnnotationFromManifest(manifest, annURI);
		try {
			deleteResource(bodyURI, dLibraToken);
		}
		catch (Exception e) {
			log.warn("Problem with deleting annotation body: " + e.getMessage());
		}
		return uploadManifestModel(researchObjectURI, manifest, dLibraToken);
	}


	/**
	 * Checks if it is possible to create an RO with workspace "default" and version "v1"
	 * 
	 * @param roId
	 * @return
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws Exception
	 */
	public static boolean isRoIdFree(URI rodlURI, String roId)
		throws URISyntaxException, MalformedURLException
	{
		//FIXME there should be a way to implement this without getting the list of all URIs
		List<URI> ros = getROList(rodlURI);
		URI ro = new URI(rodlURI.getScheme(), rodlURI.getHost(), rodlURI.getPath() + "ROs/" + roId + "/", null);
		return !ros.contains(ro);
	}


	/**
	 * @param researchObjectURI
	 * @return
	 */
	public static OntModel createManifestModel(URI researchObjectURI)
	{
		URI manifestURI = researchObjectURI.resolve(".ro/manifest.rdf");
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
		try {
			model.read(manifestURI.toString());
		}
		catch (DoesNotExistException e) {
			// do nothing, model will be empty
		}
		if (model.isEmpty()) {
			// HACK for old ROs
			manifestURI = researchObjectURI.resolve(".ro/manifest");
			model.read(manifestURI.toString());
		}
		return model;
	}


	/**
	 * @param researchObjectURI
	 * @return
	 */
	public static OntModel createManifestAndAnnotationsModel(URI researchObjectURI)
	{
		URI manifestURI = researchObjectURI.resolve(".ro/manifest.trig");
		NamedGraphSet graphset = new NamedGraphSetImpl();
		graphset.read(manifestURI.toString() + "?original=manifest.rdf", "TRIG");
		if (graphset.countQuads() == 0) {
			// HACK for old ROs
			graphset.read(manifestURI.toString() + "?original=manifest", "TRIG");
		}
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_LITE_MEM,
			graphset.asJenaModel(researchObjectURI.resolve(".ro/manifest.rdf").toString()));
		model.add(Vocab.model);
		return model;
	}


	public static ClientResponse uploadManifestModel(URI researchObjectURI, OntModel manifest, Token dLibraToken)
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		manifest.write(out);
		return uploadResource(researchObjectURI.resolve(".ro/manifest.rdf"),
			new ByteArrayInputStream(out.toByteArray()), "application/rdf+xml", dLibraToken);
	}

}
