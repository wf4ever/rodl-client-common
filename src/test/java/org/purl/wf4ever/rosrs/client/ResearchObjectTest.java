package org.purl.wf4ever.rosrs.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import org.purl.wf4ever.rosrs.client.evo.EvoType;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Test the ResearchObject class.
 * 
 * @author piotrekhol
 * 
 */
public class ResearchObjectTest extends BaseTest {

    /** A test HTTP mock server. */
    @Rule
    public static final WireMockRule WIREMOCK_RULE = new WireMockRule(8089); // No-args constructor defaults to port 8080

    /** RO that will be mapped to local resources. */
    private static final URI RO_PREFIX = URI.create("http://example.org/ro1/");

    /** Some RO available by HTTP. */
    private static final URI MOCK_RO = URI.create("http://localhost:8089/ro1/");

    /** Some RO available by HTTP. */
    private static final URI MOCK_MANIFEST = URI.create("http://localhost:8089/ro1/.ro/manifest.rdf");

    /** A loaded RO. */
    private static ResearchObject ro1;


    /**
     * Prepare a loaded RO.
     * 
     * @throws Exception
     *             when the test data cannot be loaded
     */
    @BeforeClass
    public static final void setUpBeforeClass()
            throws Exception {
        BaseTest.setUpBeforeClass();
        rosrs = new ROSRService(URI.create("http://localhost:8089/"), TOKEN);
        ro1 = new ResearchObject(RO_PREFIX, rosrs);
        ro1.load();
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
        InputStream manifest = getClass().getClassLoader().getResourceAsStream("ro1/.ro/manifest.rdf");
        // here we configure the mock HTTP server
        stubFor(get(urlEqualTo("/ro1/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(303).withHeader("Content-Type", MediaType.TEXT_PLAIN)
                    .withHeader("Location", MOCK_MANIFEST.toString())));
        stubFor(get(urlEqualTo("/ro1/.ro/manifest.rdf")).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(manifest))));
    }


    /**
     * Test that an initial RO is not loaded.
     */
    @Test
    public final void testResearchObject() {
        ResearchObject ro = new ResearchObject(RO_PREFIX, null);
        Assert.assertFalse(ro.isLoaded());
    }


    /**
     * Test that you can create and delete an RO in ROSRS.
     * 
     * @throws ROSRSException
     *             ROSRS returned an unexpected response.
     */
    @Test
    public final void testCreateDelete()
            throws ROSRSException {
        stubFor(post(urlEqualTo("/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/rdf+xml")
                    .withHeader("Location", MOCK_RO.toString())));
        stubFor(delete(urlEqualTo("/ro1/")).willReturn(aResponse().withStatus(204)));

        ResearchObject ro = ResearchObject.create(rosrs, "JavaClientTest");
        verify(postRequestedFor(urlMatching("/")).withHeader("Slug", equalTo("JavaClientTest")).withHeader("Accept",
            equalTo("application/rdf+xml")));

        ro.delete();
        verify(deleteRequestedFor(urlMatching("/ro1/")));
    }


    /**
     * Test the example RO URI.
     */
    @Test
    public final void testGetUri() {
        Assert.assertEquals(RO_PREFIX, ro1.getUri());
    }


    /**
     * Test the ROSRS client is saved.
     */
    @Test
    public final void testGetRosrs() {
        Assert.assertNotNull(ro1.getRosrs());
        Assert.assertEquals(URI.create("http://localhost:8089/"), ro1.getRosrs().getRosrsURI());
    }


    /**
     * Test that the example RO says it's loaded.
     */
    @Test
    public final void testIsLoaded() {
        Assert.assertTrue(ro1.isLoaded());
    }


    /**
     * Test that an RO can be loaded over HTTP.
     * 
     * @throws ROSRSException
     *             connection problem
     * @throws ROException
     *             remote manifest is incorrect
     */
    @Test
    public final void testLoad()
            throws ROSRSException, ROException {
        ResearchObject ro = new ResearchObject(MOCK_RO, rosrs);
        Assert.assertFalse(ro.isLoaded());
        ro.load();
        Assert.assertTrue(ro.isLoaded());
    }


    /**
     * Test ro:Resources identified.
     */
    @Test
    public final void testGetResources() {
        Set<Resource> ex = new HashSet<>();
        ex.add(new Resource(ro1, RO_PREFIX.resolve("res1.txt"), RO_PREFIX.resolve("proxies/1"), URI
                .create("http://test1.myopenid.com"), new DateTime(2011, 12, 02, 15, 02, 10, DateTimeZone.UTC)));
        ex.add(new Resource(ro1, RO_PREFIX.resolve("res2"), RO_PREFIX.resolve("proxies/2"), URI
                .create("http://test2.myopenid.com"), new DateTime(2011, 12, 02, 15, 02, 11, DateTimeZone.UTC)));
        ex.add(new Resource(ro1, RO_PREFIX.resolve("res3"), RO_PREFIX.resolve("proxies/5"), URI
                .create("http://test2.myopenid.com"), new DateTime(2011, 12, 02, 15, 02, 11, DateTimeZone.UTC)));
        Set<Resource> res = new HashSet<>();
        res.addAll(ro1.getResources().values());
        Assert.assertEquals(ex, res);
    }


    /**
     * Test ro:Folders identified.
     * 
     * @throws ROSRSException
     *             unexpected response from the server
     */
    @Test
    public final void testGetResourcesWithoutFolders()
            throws ROSRSException {
        List<Resource> ex = new ArrayList<>();
        ex.add(new Resource(ro1, RO_PREFIX.resolve("res3"), RO_PREFIX.resolve("proxies/5"), URI
                .create("http://test2.myopenid.com"), new DateTime(2011, 12, 02, 15, 02, 11, DateTimeZone.UTC)));
        List<Resource> res = ro1.getResourcesWithoutFolders();
        Assert.assertEquals(ex, res);
    }


    /**
     * Test ro:Folders identified.
     */
    @Test
    public final void testGetFolders() {
        Set<Folder> folders = new HashSet<>();
        folders.add(new Folder(ro1, RO_PREFIX.resolve("folder1/"), RO_PREFIX.resolve("proxies/3"), RO_PREFIX
                .resolve("folder1.ttl"), URI.create("http://test3.myopenid.com"), new DateTime(2011, 12, 02, 15, 02,
                12, DateTimeZone.UTC), true));
        folders.add(new Folder(ro1, RO_PREFIX.resolve("folder1/folder2/"), RO_PREFIX.resolve("proxies/4"), RO_PREFIX
                .resolve("folder2.ttl"), URI.create("http://test3.myopenid.com"), new DateTime(2011, 12, 02, 15, 02,
                12, DateTimeZone.UTC), false));
        Set<Folder> res = new HashSet<>();
        res.addAll(ro1.getFolders().values());
        Assert.assertEquals(folders, res);
    }


    /**
     * Test ro:AggregatedAnnotations identified.
     */
    @Test
    public final void testGetAnnotations() {
        Multimap<URI, Annotation> ex = HashMultimap.<URI, Annotation> create();
        Annotation an1 = new Annotation(ro1, RO_PREFIX.resolve(".ro/annotations/1"), RO_PREFIX.resolve("body1.rdf"),
                RO_PREFIX, URI.create("http://test.myopenid.com"), new DateTime(2012, 12, 11, 12, 06, 53, 551,
                        DateTimeZone.UTC));
        Annotation an2 = new Annotation(ro1, RO_PREFIX.resolve(".ro/annotations/2"),
                URI.create("http://example.org/externalbody1.rdf"), RO_PREFIX.resolve("res1.txt"),
                URI.create("http://test.myopenid.com"), new DateTime(2012, 12, 11, 12, 06, 53, 551, DateTimeZone.UTC));
        Set<URI> targets = new HashSet<>();
        targets.add(RO_PREFIX.resolve("folder1/"));
        targets.add(RO_PREFIX.resolve("res2"));
        Annotation an3 = new Annotation(ro1, RO_PREFIX.resolve(".ro/annotations/3"), RO_PREFIX.resolve("body2.rdf"),
                targets, URI.create("http://test.myopenid.com"), new DateTime(2012, 12, 11, 12, 06, 53, 551,
                        DateTimeZone.UTC));
        Annotation an4 = new Annotation(ro1, RO_PREFIX.resolve(".ro/annotations/4"), RO_PREFIX.resolve("body3.rdf"),
                RO_PREFIX.resolve("folder1/"), URI.create("http://test.myopenid.com"), new DateTime(2012, 12, 11, 12,
                        06, 53, 551, DateTimeZone.UTC));
        ex.put(RO_PREFIX, an1);
        ex.put(RO_PREFIX.resolve("res1.txt"), an2);
        ex.put(RO_PREFIX.resolve("folder1/"), an3);
        ex.put(RO_PREFIX.resolve("res2"), an3);
        ex.put(RO_PREFIX.resolve("folder1/"), an4);

        Assert.assertEquals(ex, ro1.getAllAnnotations());
    }


    /**
     * Test dcterms:creator.
     */
    @Test
    public final void testCreator() {
        Assert.assertEquals(URI.create("http://test.myopenid.com"), ro1.getCreator());
    }


    /**
     * Test RO dcterms:created.
     */
    @Test
    public final void testGetCreated() {
        //2011-12-02T16:01:10Z
        Assert.assertEquals(new DateTime(2011, 12, 02, 16, 01, 10, DateTimeZone.UTC), ro1.getCreated());
    }


    /**
     * Test RO dcterms:title taken from an annotation.
     */
    @Test
    public final void testGetTitle() {
        Assert.assertEquals("The rocking RO", ro1.getTitle());
    }


    /**
     * Test RO dcterms:description taken from an annotation.
     */
    @Test
    public final void testGetDescription() {
        Assert.assertEquals("This RO rocks.", ro1.getDescription());
    }


    /**
     * Test RO evo class taken from an annotation.
     */
    @Test
    public final void testGetEvoType() {
        Assert.assertEquals(EvoType.SNAPSHOT, ro1.getEvoType());
    }
}
