package org.purl.wf4ever.rosrs.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.purl.wf4ever.rosrs.client.evo.BaseTest;
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

    /** RO that will be mapped to local resources. */
    private static final URI RO_PREFIX = URI.create("http://example.org/ro1/");

    /** Annotation that will be mapped to local resources. */
    private static final URI ANN_PREFIX = URI.create("http://example.org/ro1/.ro/annotations/1");

    /** Annotation body that will be mapped to local resources. */
    private static final URI BODY_PREFIX = URI.create("http://example.org/ro1/body1.rdf");

    /** Some RO available by HTTP. */
    private static final URI MOCK_RO = URI.create("http://localhost:8089/ro1/");

    /** Some annotation available by HTTP. */
    private static final URI MOCK_ANNOTATION = URI.create("http://localhost:8089/ann");

    /** Some annotation available by HTTP. */
    private static final URI MOCK_ANNOTATION_PROXY = URI.create("http://localhost:8089/annproxy");

    /** Some annotation body available by HTTP. */
    private static final URI MOCK_BODY = URI.create("http://localhost:8089/body.rdf");

    /** Some annotation available by HTTP. */
    private static final URI MOCK_TARGET = URI.create("http://example.org/ROs/1/");

    /** A loaded RO. */
    private static ResearchObject ro1;

    /** A loaded annotation. */
    private static Annotation an1;


    /**
     * Prepare a loaded RO.
     * 
     * @throws Exception
     *             when loading the RO fails
     */
    @BeforeClass
    public static void setUpBeforeClass()
            throws Exception {
        BaseTest.setUpBeforeClass();
        rosrs = new ROSRService(URI.create("http://localhost:8089/"), TOKEN);
        ro1 = new ResearchObject(RO_PREFIX, rosrs);
        ro1.load();
        an1 = new Annotation(ro1, ANN_PREFIX, BODY_PREFIX, RO_PREFIX, URI.create("http://test.myopenid.com"),
                new DateTime(2011, 12, 02, 16, 01, 10, DateTimeZone.UTC));
        an1.load();
    }


    /**
     * Prepare the HTTP server mockup.
     * 
     * @throws Exception
     *             if the super method throws it
     */
    @Before
    public void setUp()
            throws Exception {
        //        super.setUp();
        // this is what the mock HTTP server will return
        InputStream body = getClass().getClassLoader().getResourceAsStream("annotations/body.rdf");
        // here we configure the mock HTTP server
        stubFor(get(urlEqualTo("/ann")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(303).withHeader("Content-Type", MediaType.TEXT_PLAIN)
                    .withHeader("Location", MOCK_BODY.toString())));
        stubFor(get(urlEqualTo("/body.rdf")).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(body))));
    }


    /**
     * Test the constructor.
     */
    @Test
    public final void testAnnotationResearchObjectUriUriSetOfURIURIDateTime() {
        Set<URI> targets = new HashSet<>();
        targets.add(RO_PREFIX);
        Annotation annotation = new Annotation(ro1, ANN_PREFIX, BODY_PREFIX, targets,
                URI.create("http://test.myopenid.com"), new DateTime(2011, 12, 02, 16, 01, 10, DateTimeZone.UTC));
        Assert.assertFalse(annotation.isLoaded());
    }


    /**
     * Test the constructor.
     */
    @Test
    public final void testAnnotationResearchObjectUriUriUriUriDateTime() {
        Annotation annotation = new Annotation(ro1, ANN_PREFIX, BODY_PREFIX, RO_PREFIX,
                URI.create("http://test.myopenid.com"), new DateTime(2011, 12, 02, 16, 01, 10, DateTimeZone.UTC));
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
        // this is what the mock HTTP server will return
        InputStream response = getClass().getClassLoader().getResourceAsStream("annotations/response.rdf");
        stubFor(post(urlEqualTo("/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/rdf+xml")
                    .withHeader("Location", MOCK_RO.toString())));
        stubFor(delete(urlEqualTo("/ro1/")).willReturn(aResponse().withStatus(204)));
        stubFor(post(urlEqualTo("/ro1/")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/vnd.wf4ever.annotation")
                    .withHeader("Location", MOCK_ANNOTATION_PROXY.toString())
                    .withHeader("Link", "<" + MOCK_ANNOTATION + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                    .withBody(IOUtils.toByteArray(response))));
        stubFor(delete(urlEqualTo("/ann")).willReturn(aResponse().withStatus(204)));

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
        Annotation an = new Annotation(ro1, MOCK_ANNOTATION, MOCK_BODY, MOCK_TARGET, null, null);
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
        Assert.assertEquals(ANN_PREFIX, an1.getUri());
    }


    /**
     * The correct body URI is returned.
     */
    @Test
    public final void testGetBody() {
        Assert.assertEquals(BODY_PREFIX, an1.getBody());
    }


    /**
     * The correct creator is returned.
     */
    @Test
    public final void testGetCreator() {
        Assert.assertEquals(URI.create("http://test.myopenid.com"), an1.getCreator());
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
        targets.add(RO_PREFIX);
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
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("ro1/body1.rdf")) {
            ex.read(in, BODY_PREFIX.toString());
        }

        Model res = ModelFactory.createDefaultModel();
        try (InputStream in = IOUtils.toInputStream(body)) {
            res.read(in, BODY_PREFIX.toString());
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

        Annotation annotation = new Annotation(ro1, MOCK_ANNOTATION, MOCK_BODY, MOCK_TARGET, null, null);
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
        Annotation annotation = new Annotation(ro1, MOCK_ANNOTATION, MOCK_BODY, MOCK_TARGET, null, null);
        annotation.getStatements();
    }
}
