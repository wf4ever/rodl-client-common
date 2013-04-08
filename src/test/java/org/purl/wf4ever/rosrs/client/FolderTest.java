package org.purl.wf4ever.rosrs.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.purl.wf4ever.rosrs.client.evo.BaseTest;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Test the {@link} methods.
 * 
 * @author piotrekhol
 * 
 */
public class FolderTest extends BaseTest {

    /** A test HTTP mock server. */
    @Rule
    public static final WireMockRule WIREMOCK_RULE = new WireMockRule(8089); // No-args constructor defaults to port 8080

    /** RO that will be mapped to local resources. */
    private static final URI RO_PREFIX = URI.create("http://example.org/ro1/");

    /** Resource that will be mapped to local resources. */
    private static final URI FOLDER_URI = URI.create("http://example.org/ro1/folder1/");

    /** Proxy of the resource that will be mapped to local resources. */
    private static final URI PROXY_URI = URI.create("http://example.org/ro1/proxies/3");

    /** Resource map of the resource that will be mapped to local resources. */
    private static final URI RMAP_URI = URI.create("http://example.org/ro1/folder1.ttl");

    /** Some folder available by HTTP. */
    private static final URI MOCK_FOLDER = URI.create("http://localhost:8089/folder/");

    /** Some folder resource map available by HTTP. */
    private static final URI MOCK_RMAP = URI.create("http://localhost:8089/folder/rmap.rdf");

    /** A loaded RO. */
    private static ResearchObject ro1;

    /** A loaded folder. */
    private static Folder fol1;


    /**
     * Prepare a loaded folder.
     * 
     * @throws Exception
     *             when the test data can't be loaded
     */
    @BeforeClass
    public static final void setUpBeforeClass()
            throws Exception {
        BaseTest.setUpBeforeClass();
        ro1 = new ResearchObject(RO_PREFIX, rosrs);
        ro1.load();
        fol1 = new Folder(ro1, FOLDER_URI, PROXY_URI, RMAP_URI, URI.create("http://test3.myopenid.com"), new DateTime(
                2011, 12, 02, 15, 02, 12, DateTimeZone.UTC), true);
        fol1.load(false);
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
        super.setUp();
        // this is what the mock HTTP server will return
        InputStream rmap = getClass().getClassLoader().getResourceAsStream("folders/rmap.rdf");
        // here we configure the mock HTTP server
        stubFor(get(urlEqualTo("/folder/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(303).withHeader("Content-Type", MediaType.TEXT_PLAIN)
                    .withHeader("Location", MOCK_RMAP.toString())));
        stubFor(get(urlEqualTo("/folder/rmap.rdf")).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(rmap))));
    }


    /**
     * Test the constructor works without problems.
     */
    @Test
    public final void testFolder() {
        Folder f = new Folder(ro1, FOLDER_URI, PROXY_URI, RMAP_URI, URI.create("http://test3.myopenid.com"),
                new DateTime(2011, 12, 02, 15, 02, 12, DateTimeZone.UTC), true);
        Assert.assertFalse(f.isLoaded());
    }


    /**
     * Test that the folder can be created and deleted remotely.
     * 
     * @throws ROSRSException
     *             unexpected server response
     */
    @Test
    public final void testCreateResearchObjectString()
            throws ROSRSException {
        ResearchObject ro;
        try {
            ro = ResearchObject.create(rosrs, "JavaClientTest");
        } catch (ROSRSException e) {
            if (e.getStatus() == HttpStatus.SC_CONFLICT) {
                ro = new ResearchObject(rosrs.getRosrsURI().resolve("JavaClientTest/"), rosrs);
                ro.delete();
                ro = ResearchObject.create(rosrs, "JavaClientTest");
            } else {
                throw e;
            }
        }
        Folder f = Folder.create(ro, "folder1/");
        Assert.assertNotNull(f);
        f.delete();
        ro.delete();
    }


    /**
     * Test that you can load a folder resource map over HTTP.
     * 
     * @throws ROSRSException
     *             unexpected response from the server
     */
    @Test
    public final void testLoad()
            throws ROSRSException {
        Folder f = new Folder(ro1, MOCK_FOLDER, null, MOCK_RMAP, null, null, false);
        Assert.assertFalse(f.isLoaded());
        f.load(false);
        Assert.assertTrue(f.isLoaded());
    }


    /**
     * Test that you can add a folder entry and it gets saved both remotely and locally.
     * 
     * @throws ROSRSException
     *             unexpected server response
     * @throws IOException
     *             problem loading test data
     * @throws ROException
     *             invalid remote manifest
     */
    @Test
    public final void testAddEntry()
            throws ROSRSException, IOException, ROException {
        ResearchObject ro;
        try {
            ro = ResearchObject.create(rosrs, "JavaClientTest");
        } catch (ROSRSException e) {
            if (e.getStatus() == HttpStatus.SC_CONFLICT) {
                ro = new ResearchObject(rosrs.getRosrsURI().resolve("JavaClientTest/"), rosrs);
                ro.delete();
                ro = ResearchObject.create(rosrs, "JavaClientTest");
            } else {
                throw e;
            }
        }
        Folder f = Folder.create(ro, "folder1/");
        Assert.assertNotNull(f);
        f.load(false);
        Resource res = null;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("ro1/res1.txt")) {
            res = Resource.create(ro, "folder1/res1.txt", in, "text/plain");
            Assert.assertNotNull(res);
        }

        FolderEntry entry = f.addEntry(res, "res1.txt");
        Assert.assertTrue(f.getFolderEntries().containsValue(entry));

        Folder f2 = new Folder(ro, ro.getUri().resolve("folder1/"), f.getProxyUri(), f.getResourceMap(), null, null,
                f.isRootFolder());
        f2.load(false);
        Assert.assertTrue(f2.getFolderEntries().containsValue(entry));

        f.delete();
        ro.delete();
    }


    /**
     * Test that you can add a subfolder and it gets saved both remotely and locally.
     * 
     * @throws ROSRSException
     *             unexpected server response
     * @throws IOException
     *             problem loading test data
     * @throws ROException
     *             invalid remote manifest
     */
    @Test
    public final void testAddSubFolder()
            throws ROSRSException, IOException, ROException {
        ResearchObject ro;
        try {
            ro = ResearchObject.create(rosrs, "JavaClientTest");
        } catch (ROSRSException e) {
            if (e.getStatus() == HttpStatus.SC_CONFLICT) {
                ro = new ResearchObject(rosrs.getRosrsURI().resolve("JavaClientTest/"), rosrs);
                ro.delete();
                ro = ResearchObject.create(rosrs, "JavaClientTest");
            } else {
                throw e;
            }
        }
        Folder f = Folder.create(ro, "folder1/");
        Assert.assertNotNull(f);
        f.load(false);
        Resource res = null;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("ro1/res1.txt")) {
            res = Resource.create(ro, "folder1/res1.txt", in, "text/plain");
            Assert.assertNotNull(res);
        }

        FolderEntry entry = f.addSubFolder("folder2/");
        Assert.assertEquals(f.getUri().resolve("folder2/"), entry.getResourceUri());
        Assert.assertTrue(f.getFolderEntries().containsValue(entry));

        Folder f2 = new Folder(ro, ro.getUri().resolve("folder1/"), f.getProxyUri(), f.getResourceMap(), null, null,
                f.isRootFolder());
        f2.load(false);
        Assert.assertTrue(f2.getFolderEntries().containsValue(entry));

        f.delete();
        ro.delete();
    }


    /**
     * Test that you can get the resource map URI.
     */
    @Test
    public final void testGetResourceMap() {
        Assert.assertEquals(RMAP_URI, fol1.getResourceMap());
    }


    /**
     * Test can check if it's loaded.
     */
    @Test
    public final void testIsLoaded() {
        Assert.assertTrue(fol1.isLoaded());
    }


    /**
     * Test that you can check if it's a root folder.
     */
    @Test
    public final void testIsRootFolder() {
        Assert.assertTrue(fol1.isRootFolder());
    }


    /**
     * Test that you read correct folder entries.
     */
    @Test
    public final void testGetFolderEntries() {
        Set<FolderEntry> ex = new HashSet<>();
        URI entry1 = UriBuilder.fromUri(FOLDER_URI).fragment("entry1").build();
        ex.add(new FolderEntry(fol1, entry1, RO_PREFIX.resolve("res1.txt"), "res1.txt"));
        URI entry2 = UriBuilder.fromUri(FOLDER_URI).fragment("entry2").build();
        ex.add(new FolderEntry(fol1, entry2, RO_PREFIX.resolve("res2"), "res2"));
        URI entry3 = UriBuilder.fromUri(FOLDER_URI).fragment("entry3").build();
        ex.add(new FolderEntry(fol1, entry3, FOLDER_URI.resolve("folder2/"), "folder2"));
        Assert.assertEquals(ex, new HashSet<FolderEntry>(fol1.getFolderEntries().values()));
    }
}
