package org.purl.wf4ever.rosrs.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.purl.wf4ever.rosrs.client.exception.ObjectNotLoadedException;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import pl.psnc.dl.wf4ever.vocabulary.ORE;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * A test of the annotation class.
 * 
 * @author piotrekhol
 * 
 */
public class AnnotationTest extends BaseTest {

    /** A test HTTP mock server. */
    @Rule
    public static final WireMockRule WIREMOCK_RULE = new WireMockRule(8089); // No-args constructor defaults to port 8080

    /** Annotation proxy. */
    private static final URI MOCK_ANNOTATION_PROXY = URI.create("http://localhost:8089/annproxy");

    /** Annotation target. */
    private static final URI MOCK_TARGET = URI.create("http://localhost:8089/ro1/");

    /** A loaded annotation. */
    private Annotation an1;


    /**
     * Prepare the HTTP server mockup.
     * 
     * @throws Exception
     *             if the super method throws it
     */
    @Before
    public void setUp()
            throws Exception {
        super.setUp();
        setUpAnnotationCreateDelete();

        an1 = new Annotation(ro1, MOCK_ANNOTATION, MOCK_BODY, Collections.singleton(MOCK_RO), PERSON, new DateTime(
                2011, 12, 02, 16, 01, 10, DateTimeZone.UTC));
        an1.load();
    }


    /**
     * Configure WireMock to create and delete annotations.
     * 
     * @throws IOException
     *             if the test resources are not available
     */
    protected void setUpAnnotationCreateDelete()
            throws IOException {
        // this is what the mock HTTP server will return
        InputStream response = getClass().getClassLoader().getResourceAsStream("annotations/response.rdf");
        stubFor(post(urlEqualTo("/ro1/")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/vnd.wf4ever.annotation")
                    .withHeader("Location", MOCK_ANNOTATION_PROXY.toString())
                    .withHeader("Link", "<" + MOCK_ANNOTATION + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                    .withBody(IOUtils.toByteArray(response))));
        stubFor(delete(urlEqualTo("/ann")).willReturn(aResponse().withStatus(204)));
    }


    /**
     * Test the constructor.
     */
    @Test
    public final void testAnnotationResearchObjectUriUriSetOfURIURIDateTime() {
        Set<URI> targets = new HashSet<>();
        targets.add(MOCK_RO);
        Annotation annotation = new Annotation(ro1, MOCK_ANNOTATION, MOCK_BODY, targets, PERSON, new DateTime(2011, 12,
                02, 16, 01, 10, DateTimeZone.UTC));
        Assert.assertFalse(annotation.isLoaded());
    }


    /**
     * Test the constructor.
     */
    @Test
    public final void testAnnotationResearchObjectUriUriUriUriDateTime() {
        Annotation annotation = new Annotation(ro1, MOCK_ANNOTATION, MOCK_BODY, Collections.singleton(MOCK_RO), PERSON,
                new DateTime(2011, 12, 02, 16, 01, 10, DateTimeZone.UTC));
        Assert.assertFalse(annotation.isLoaded());
    }


    /**
     * Create an RO and add an annotation to it, then delete both.
     * 
     * @throws ROSRSException
     *             unexpected server response
     * @throws ROException
     *             the manifest is incorrect
     * @throws IOException
     *             the test response can't be read
     */
    @Test
    public final void testCreateDelete()
            throws ROSRSException, ROException, IOException {
        ResearchObject ro = ResearchObject.create(rosrs, "ro1");
        Annotation an = Annotation.create(ro, MOCK_BODY, MOCK_TARGET);
        Assert.assertNotNull(an);
        verify(postRequestedFor(urlMatching("/ro1/")).withHeader("Content-Type",
            equalTo("application/vnd.wf4ever.annotation")));

        an.delete();
        ro.delete();
    }


    /**
     * Load an annotation body over HTTP.
     * 
     * @throws ROSRSException
     *             unexpected server response
     * @throws IOException
     *             error serializing an annotation body
     */
    @Test
    public final void testLoad()
            throws ROSRSException, IOException {
        Annotation an = new Annotation(ro1, MOCK_ANNOTATION, MOCK_BODY, Collections.singleton(MOCK_TARGET), null, null);
        Assert.assertFalse(an.isLoaded());
        an.load();
        Assert.assertTrue(an.isLoaded());
    }


    /**
     * Test get RO.
     */
    @Test
    public final void testGetResearchObject() {
        Assert.assertEquals(ro1, an1.getResearchObject());
    }


    /**
     * The correct URI is returned.
     */
    @Test
    public final void testGetUri() {
        Assert.assertEquals(MOCK_ANNOTATION, an1.getUri());
    }


    /**
     * The correct body URI is returned.
     */
    @Test
    public final void testGetBody() {
        Assert.assertEquals(MOCK_BODY, an1.getBody());
    }


    /**
     * The correct creator is returned.
     */
    @Test
    public final void testGetAuthor() {
        Assert.assertEquals(PERSON, an1.getAuthor());
    }


    /**
     * The correct creation date is returned.
     */
    @Test
    public final void testGetCreated() {
        Assert.assertEquals(new DateTime(2011, 12, 02, 16, 01, 10, DateTimeZone.UTC), an1.getCreated());
    }


    /**
     * The correct list of targets is returned.
     */
    @Test
    public final void testGetTargets() {
        Set<URI> targets = new HashSet<>();
        targets.add(MOCK_RO);
        Assert.assertEquals(targets, an1.getTargets());
    }


    /**
     * The correct loaded flag is returned.
     */
    @Test
    public final void testIsLoaded() {
        Assert.assertTrue(an1.isLoaded());
    }


    /**
     * The correct annotation body serialization is returned.
     * 
     * @throws IOException
     *             error serializing the annotation body
     */
    @Test
    public final void testGetBodySerializedAsString()
            throws IOException {
        String body = an1.getBodySerializedAsString();
        Assert.assertNotNull(body);

        Model ex = ModelFactory.createDefaultModel();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("ro1/body.rdf")) {
            ex.read(in, MOCK_BODY.toString());
        }

        Model res = ModelFactory.createDefaultModel();
        try (InputStream in = IOUtils.toInputStream(body)) {
            res.read(in, MOCK_BODY.toString());
        }
        Assert.assertTrue(ex.isIsomorphicWith(res));
    }


    /**
     * The list of annotation statement should be fill up when the object is loaded.
     * 
     * @throws ROSRSException
     *             unexpected server response when downloading the body
     * @throws IOException
     *             error copying streams
     */
    @Test
    public final void testGetStatement()
            throws ROSRSException, IOException {

        Annotation annotation = new Annotation(ro1, MOCK_ANNOTATION, MOCK_BODY, Collections.singleton(MOCK_TARGET),
                null, null);
        annotation.load();
        Assert.assertNotNull(annotation.getStatements());
    }


    /**
     * When not loaded are accessed, ObjectNotLoadedException is thrown.
     * 
     * @throws ROSRSException
     *             unexpected server response when downloading the body
     * @throws IOException
     *             error copying streams
     */
    @Test(expected = ObjectNotLoadedException.class)
    public final void testGetNotLoadedStatement()
            throws ROSRSException, IOException {
        Annotation annotation = new Annotation(ro1, MOCK_ANNOTATION, MOCK_BODY, Collections.singleton(MOCK_TARGET),
                null, null);
        annotation.getStatements();
    }
}
