package org.purl.wf4ever.rosrs.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
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
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import pl.psnc.dl.wf4ever.vocabulary.ORE;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.Scenario;

/**
 * Test the {@link Folder} methods.
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

    /** Some folder entry available by HTTP. */
    private static final URI MOCK_FOLDER_ENTRY = URI.create("http://localhost:8089/folder/entries/1");

    /** Some RO available by HTTP. */
    private static final URI MOCK_RO = URI.create("http://localhost:8089/ro1/");

    /** Some resource available by HTTP. */
    private static final URI MOCK_FOLDER_PROXY = URI.create("http://localhost:8089/folderproxy");

    /** Some resource available by HTTP. */
    private static final URI MOCK_SUBFOLDER_PROXY = URI.create("http://localhost:8089/subfolderproxy");

    /** Some folder resource map available by HTTP. */
    private static final URI MOCK_SUBFOLDER_RMAP = URI.create("http://localhost:8089/folder/subfolder/rmap.rdf");

    /** Some resource available by HTTP. */
    private static final URI MOCK_RESOURCE = URI.create("http://localhost:8089/res1.txt");

    /** Some resource available by HTTP. */
    private static final URI MOCK_RESOURCE_PROXY = URI.create("http://localhost:8089/resproxy");

    /** Some RO available by HTTP. */
    private static final URI MOCK_MANIFEST = URI.create("http://localhost:8089/ro1/.ro/manifest.rdf");

    /** Some RO available by HTTP. */
    private static final URI MOCK_SUBFOLDER = URI.create("http://localhost:8089/folder/subfolder/");

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
        rosrs = new ROSRService(URI.create("http://localhost:8089/"), TOKEN);
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
        //        super.setUp();
        // this is what the mock HTTP server will return
        InputStream rmap = getClass().getClassLoader().getResourceAsStream("folders/rmap.rdf");
        InputStream manifest = getClass().getClassLoader().getResourceAsStream("ro1/.ro/manifest.rdf");
        InputStream subfolderRmap = getClass().getClassLoader().getResourceAsStream("folders/subfolder_rmap.rdf");
        // here we configure the mock HTTP server
        WireMock.resetAllScenarios();
        stubFor(get(urlEqualTo("/folder/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(303).withHeader("Content-Type", MediaType.TEXT_PLAIN)
                    .withHeader("Location", MOCK_RMAP.toString())));
        stubFor(get(urlEqualTo("/folder/rmap.rdf")).whenScenarioStateIs(Scenario.STARTED).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(rmap))));
        stubFor(get(urlEqualTo("/ro1/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(303).withHeader("Content-Type", MediaType.TEXT_PLAIN)
                    .withHeader("Location", MOCK_MANIFEST.toString())));
        stubFor(get(urlEqualTo("/ro1/.ro/manifest.rdf")).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(manifest))));
        stubFor(get(urlEqualTo("/folder/subfolder/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(303).withHeader("Content-Type", MediaType.TEXT_PLAIN)
                    .withHeader("Location", MOCK_SUBFOLDER_RMAP.toString())));
        stubFor(get(urlEqualTo("/folder/subfolder/rmap.rdf")).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(subfolderRmap))));
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
     * @throws IOException
     *             if test data can't be loaded
     */
    @Test
    public final void testCreateResearchObjectString()
            throws ROSRSException, IOException {
        // this is what the mock HTTP server will return
        InputStream response = getClass().getClassLoader().getResourceAsStream("folders/response.rdf");
        stubFor(post(urlEqualTo("/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/rdf+xml")
                    .withHeader("Location", MOCK_RO.toString())));
        stubFor(delete(urlEqualTo("/ro1/")).willReturn(aResponse().withStatus(204)));
        stubFor(post(urlEqualTo("/ro1/")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/rdf+xml")
                    .withHeader("Location", MOCK_FOLDER_PROXY.toString())
                    .withHeader("Link", "<" + MOCK_FOLDER + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                    .withHeader("Link", "<" + MOCK_RMAP + ">; rel=\"" + ORE.isDescribedBy.toString() + "\"")
                    .withBody(IOUtils.toByteArray(response))));
        stubFor(delete(urlEqualTo("/folder/")).willReturn(aResponse().withStatus(204)));

        ResearchObject ro = ResearchObject.create(rosrs, "ro1");
        Folder f = Folder.create(ro, "folder/");
        Assert.assertNotNull(f);
        f.delete();
        verify(postRequestedFor(urlMatching("/ro1/")).withHeader("Content-Type",
            equalTo("application/vnd.wf4ever.folder")));
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
        // this is what the mock HTTP server will return
        InputStream folderResponse = getClass().getClassLoader().getResourceAsStream("folders/response.rdf");
        InputStream resResponse = getClass().getClassLoader().getResourceAsStream("resources/response.rdf");
        InputStream rmapWithEntry = getClass().getClassLoader().getResourceAsStream("folders/rmap_with_entry.rdf");
        //ro
        stubFor(post(urlEqualTo("/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/rdf+xml")
                    .withHeader("Location", MOCK_RO.toString())));
        stubFor(delete(urlEqualTo("/ro1/")).willReturn(aResponse().withStatus(204)));
        //folder
        stubFor(post(urlEqualTo("/ro1/")).withHeader("Content-Type", equalTo("application/vnd.wf4ever.folder"))
                .willReturn(
                    aResponse().withStatus(201).withHeader("Content-Type", "application/rdf+xml")
                            .withHeader("Location", MOCK_FOLDER_PROXY.toString())
                            .withHeader("Link", "<" + MOCK_FOLDER + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                            .withHeader("Link", "<" + MOCK_RMAP + ">; rel=\"" + ORE.isDescribedBy.toString() + "\"")
                            .withBody(IOUtils.toByteArray(folderResponse))));
        stubFor(delete(urlEqualTo("/folder/")).willReturn(aResponse().withStatus(204)));
        //resource
        stubFor(post(urlEqualTo("/ro1/")).withHeader("Content-Type", equalTo("text/plain")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "text/plain")
                    .withHeader("Location", MOCK_RESOURCE_PROXY.toString())
                    .withHeader("Link", "<" + MOCK_RESOURCE + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                    .withBody(IOUtils.toByteArray(resResponse))));
        stubFor(delete(urlEqualTo("/res1.txt")).willReturn(aResponse().withStatus(204)));
        //folder entry
        stubFor(post(urlEqualTo("/folder/")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/vnd.wf4ever.folderentry")
                    .withHeader("Location", MOCK_FOLDER_ENTRY.toString())
                    .withHeader("Link", "<" + MOCK_RESOURCE + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                    .withBody(IOUtils.toByteArray(folderResponse))).willSetStateTo("Folder entry added"));
        //resource map with entry
        stubFor(get(urlEqualTo("/folder/rmap.rdf")).whenScenarioStateIs("Folder entry added").willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(rmapWithEntry))));

        ResearchObject ro = ResearchObject.create(rosrs, "ro1");
        Folder f = Folder.create(ro, "folder/");
        Assert.assertNotNull(f);

        f.load(false);
        Resource res = null;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("resources/res1.txt")) {
            res = Resource.create(ro, "folder/res1.txt", in, "text/plain");
            Assert.assertNotNull(res);
        }

        FolderEntry entry = f.addEntry(res, "res1.txt");
        Assert.assertTrue(f.getFolderEntries().containsValue(entry));

        Folder f2 = new Folder(ro, f.getUri(), f.getProxyUri(), f.getResourceMap(), null, null, f.isRootFolder());
        f2.load(false);
        Assert.assertTrue(f2.getFolderEntries().containsValue(entry));

        verify(postRequestedFor(urlMatching("/ro1/")).withHeader("Content-Type",
            equalTo("application/vnd.wf4ever.folder")));

        res.delete();
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
        // this is what the mock HTTP server will return
        InputStream folderResponse = getClass().getClassLoader().getResourceAsStream("folders/response.rdf");
        InputStream subolderResponse = getClass().getClassLoader()
                .getResourceAsStream("folders/subfolder_response.rdf");
        InputStream resResponse = getClass().getClassLoader().getResourceAsStream("resources/response.rdf");
        InputStream rmapWithSubfolder = getClass().getClassLoader().getResourceAsStream(
            "folders/rmap_with_subfolder.rdf");
        //ro
        stubFor(post(urlEqualTo("/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/rdf+xml")
                    .withHeader("Location", MOCK_RO.toString())));
        stubFor(delete(urlEqualTo("/ro1/")).willReturn(aResponse().withStatus(204)));
        //folder
        stubFor(post(urlEqualTo("/ro1/"))
                .withHeader("Content-Type", equalTo("application/vnd.wf4ever.folder"))
                .withHeader("Slug", equalTo("folder/"))
                .willReturn(
                    aResponse().withStatus(201).withHeader("Content-Type", "application/rdf+xml")
                            .withHeader("Location", MOCK_FOLDER_PROXY.toString())
                            .withHeader("Link", "<" + MOCK_FOLDER + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                            .withHeader("Link", "<" + MOCK_RMAP + ">; rel=\"" + ORE.isDescribedBy.toString() + "\"")
                            .withBody(IOUtils.toByteArray(folderResponse))));
        stubFor(delete(urlEqualTo("/folder/")).willReturn(aResponse().withStatus(204)));
        //resource
        stubFor(post(urlEqualTo("/ro1/")).withHeader("Content-Type", equalTo("text/plain")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "text/plain")
                    .withHeader("Location", MOCK_RESOURCE_PROXY.toString())
                    .withHeader("Link", "<" + MOCK_RESOURCE + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                    .withBody(IOUtils.toByteArray(resResponse))));
        stubFor(delete(urlEqualTo("/res1.txt")).willReturn(aResponse().withStatus(204)));
        //subfolder
        stubFor(post(urlEqualTo("/ro1/"))
                .withHeader("Content-Type", equalTo("application/vnd.wf4ever.folder"))
                .withHeader("Slug", equalTo("/folder/subfolder/"))
                .willReturn(
                    aResponse()
                            .withStatus(201)
                            .withHeader("Content-Type", "application/rdf+xml")
                            .withHeader("Location", MOCK_SUBFOLDER_PROXY.toString())
                            .withHeader("Link", "<" + MOCK_SUBFOLDER + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                            .withHeader("Link",
                                "<" + MOCK_SUBFOLDER_RMAP + ">; rel=\"" + ORE.isDescribedBy.toString() + "\"")
                            .withBody(IOUtils.toByteArray(subolderResponse))));
        stubFor(delete(urlEqualTo("/folder/subfolder/")).willReturn(aResponse().withStatus(204)));
        //subfolder entry
        stubFor(post(urlEqualTo("/folder/"))
                .withRequestBody(matching(".*:proxyFor rdf:resource=\"" + MOCK_SUBFOLDER + "\".*"))
                .willReturn(
                    aResponse().withStatus(201).withHeader("Content-Type", "application/vnd.wf4ever.folderentry")
                            .withHeader("Location", MOCK_FOLDER_ENTRY.toString())
                            .withHeader("Link", "<" + MOCK_SUBFOLDER + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                            .withBody(IOUtils.toByteArray(folderResponse))).willSetStateTo("Subfolder added"));
        //resource map with entry
        stubFor(get(urlEqualTo("/folder/rmap.rdf")).whenScenarioStateIs("Subfolder added").willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(rmapWithSubfolder))));

        ResearchObject ro = ResearchObject.create(rosrs, "ro1");

        Folder f = Folder.create(ro, "folder/");
        Assert.assertNotNull(f);
        f.load(false);

        FolderEntry entry = f.addSubFolder("subfolder/");
        Assert.assertEquals(f.getUri().resolve("subfolder/"), entry.getResourceUri());
        Assert.assertTrue(f.getFolderEntries().containsValue(entry));

        Folder f2 = new Folder(ro, f.getUri(), f.getProxyUri(), f.getResourceMap(), null, null, f.isRootFolder());
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
