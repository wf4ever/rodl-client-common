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
import org.junit.Rule;
import org.junit.Test;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

import pl.psnc.dl.wf4ever.vocabulary.ORE;

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

    /** Some folder available by HTTP. */
    private static final URI MOCK_FOLDER = URI.create("http://localhost:8089/folder/");

    /** Some folder resource map available by HTTP. */
    private static final URI MOCK_RMAP = URI.create("http://localhost:8089/folder/rmap.rdf");

    /** Some folder entry available by HTTP. */
    private static final URI MOCK_FOLDER_ENTRY = URI.create("http://localhost:8089/folder/entries/1");

    /** Some resource available by HTTP. */
    private static final URI MOCK_FOLDER_PROXY = URI.create("http://localhost:8089/folderproxy");

    /** Some resource available by HTTP. */
    private static final URI MOCK_SUBFOLDER_PROXY = URI.create("http://localhost:8089/subfolderproxy");

    /** Some folder resource map available by HTTP. */
    private static final URI MOCK_SUBFOLDER_RMAP = URI.create("http://localhost:8089/folder/subfolder/rmap.rdf");

    /** Some RO available by HTTP. */
    private static final URI MOCK_SUBFOLDER = URI.create("http://localhost:8089/folder/subfolder/");

    /** A loaded RO. */
    private static ResearchObject ro1;

    /** A loaded folder. */
    private static Folder fol1;


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
        setUpResourceMaps();
        setUpFolderCreateDelete();

        ro1 = new ResearchObject(MOCK_RO, rosrs);
        ro1.load();
        fol1 = new Folder(ro1, MOCK_FOLDER, MOCK_FOLDER_PROXY, MOCK_RMAP, URI.create("http://test3.myopenid.com"),
                new DateTime(2011, 12, 02, 15, 02, 12, DateTimeZone.UTC), true);
        fol1.load(false);
    }


    protected void setUpResourceMaps()
            throws IOException {
        InputStream rmap = getClass().getClassLoader().getResourceAsStream("folders/rmap.rdf");
        stubFor(get(urlEqualTo("/folder/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(303).withHeader("Content-Type", MediaType.TEXT_PLAIN)
                    .withHeader("Location", MOCK_RMAP.toString())));
        stubFor(get(urlEqualTo("/folder/rmap.rdf")).whenScenarioStateIs(Scenario.STARTED).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(rmap))));

        InputStream subfolderRmap = getClass().getClassLoader().getResourceAsStream("folders/subfolder_rmap.rdf");
        stubFor(get(urlEqualTo("/folder/subfolder/")).withHeader("Accept", equalTo("application/rdf+xml")).willReturn(
            aResponse().withStatus(303).withHeader("Content-Type", "application/rdf+xml")
                    .withHeader("Location", MOCK_SUBFOLDER_RMAP.toString())));
        stubFor(get(urlEqualTo("/folder/subfolder/rmap.rdf")).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(subfolderRmap))));
    }


    protected void setUpFolderCreateDelete()
            throws IOException {
        InputStream response = getClass().getClassLoader().getResourceAsStream("folders/response.rdf");
        stubFor(post(urlEqualTo("/ro1/")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/rdf+xml")
                    .withHeader("Location", MOCK_FOLDER_PROXY.toString())
                    .withHeader("Link", "<" + MOCK_FOLDER + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                    .withHeader("Link", "<" + MOCK_RMAP + ">; rel=\"" + ORE.isDescribedBy.toString() + "\"")
                    .withBody(IOUtils.toByteArray(response))));
        stubFor(delete(urlEqualTo("/folder/")).willReturn(aResponse().withStatus(204)));
    }


    protected void setUpEntryCreateDelete()
            throws IOException {
        //folder entry
        InputStream folderResponse = getClass().getClassLoader().getResourceAsStream("folders/response.rdf");
        stubFor(post(urlEqualTo("/folder/")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/vnd.wf4ever.folderentry")
                    .withHeader("Location", MOCK_FOLDER_ENTRY.toString())
                    .withHeader("Link", "<" + MOCK_RESOURCE + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                    .withBody(IOUtils.toByteArray(folderResponse))).willSetStateTo("Folder entry added"));
        //resource map with entry
        InputStream rmapWithEntry = getClass().getClassLoader().getResourceAsStream("folders/rmap_with_entry.rdf");
        stubFor(get(urlEqualTo("/folder/rmap.rdf")).whenScenarioStateIs("Folder entry added").willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(rmapWithEntry))));
    }


    protected void setUpSubfolderEntryCreateDelete()
            throws IOException {
        //subfolder entry
        InputStream folderResponse = getClass().getClassLoader().getResourceAsStream("folders/response.rdf");
        stubFor(post(urlEqualTo("/folder/"))
                .withRequestBody(matching(".*:proxyFor rdf:resource=\"" + MOCK_SUBFOLDER + "\".*"))
                .willReturn(
                    aResponse().withStatus(201).withHeader("Content-Type", "application/vnd.wf4ever.folderentry")
                            .withHeader("Location", MOCK_FOLDER_ENTRY.toString())
                            .withHeader("Link", "<" + MOCK_SUBFOLDER + ">; rel=\"" + ORE.proxyFor.toString() + "\"")
                            .withBody(IOUtils.toByteArray(folderResponse))).willSetStateTo("Subfolder added"));
        //resource map with entry
        InputStream rmapWithSubfolder = getClass().getClassLoader().getResourceAsStream(
            "folders/rmap_with_subfolder.rdf");
        stubFor(get(urlEqualTo("/folder/rmap.rdf")).whenScenarioStateIs("Subfolder added").willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(rmapWithSubfolder))));
    }


    protected void setUpSubfolderCreateDelete()
            throws IOException {
        InputStream subolderResponse = getClass().getClassLoader()
                .getResourceAsStream("folders/subfolder_response.rdf");
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
    }


    /**
     * Test the constructor works without problems.
     */
    @Test
    public final void testFolder() {
        Folder f = new Folder(ro1, MOCK_FOLDER, MOCK_FOLDER_PROXY, MOCK_RMAP, URI.create("http://test3.myopenid.com"),
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
        setUpEntryCreateDelete();
        ResearchObject ro = ResearchObject.create(rosrs, "ro1");
        Folder f = Folder.create(ro, "folder/");
        Assert.assertNotNull(f);

        f.load(false);
        Resource res = null;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("resources/res1.txt")) {
            res = Resource.create(ro, "ro1/res1.txt", in, "text/plain");
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
        setUpSubfolderCreateDelete();
        setUpSubfolderEntryCreateDelete();

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
        Assert.assertEquals(MOCK_RMAP, fol1.getResourceMap());
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
        URI entry1 = UriBuilder.fromUri(MOCK_FOLDER).fragment("entry1").build();
        ex.add(new FolderEntry(fol1, entry1, MOCK_RO.resolve("res1.txt"), "res1.txt"));
        URI entry2 = UriBuilder.fromUri(MOCK_FOLDER).fragment("entry2").build();
        ex.add(new FolderEntry(fol1, entry2, MOCK_RO.resolve("res2"), "res2"));
        URI entry3 = UriBuilder.fromUri(MOCK_FOLDER).fragment("entry3").build();
        ex.add(new FolderEntry(fol1, entry3, MOCK_FOLDER.resolve("folder2/"), "folder2"));
        Assert.assertEquals(ex, new HashSet<FolderEntry>(fol1.getFolderEntries().values()));
    }
}
