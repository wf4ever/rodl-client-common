package org.purl.wf4ever.rosrs.client.evo;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.purl.wf4ever.rosrs.client.ROSRService;
import org.purl.wf4ever.rosrs.client.ResearchObject;

import com.github.tomakehurst.wiremock.client.WireMock;

/**
 * A test class for the RO evo service.
 * 
 * @author piotrekhol
 * 
 */
public class BaseTest {

    /** ro evo service. */
    protected static ROEVOService roevo;

    /** ROSR service. */
    protected static ROSRService rosrs;

    /** RODL access token. */
    protected static final String TOKEN = "1cec3d40-4c6c-4bb8-8527-cbd8776c6327";

    /** RODL URI for testing. */
    protected static final URI RODL_URI = URI.create("http://sandbox.wf4ever-project.org/rodl/");

    /** ROs to delete after a test. */
    protected static List<ResearchObject> rosToDelete = new ArrayList<>();

    /** The Live RO. */
    protected ResearchObject ro;

    /** Some RO available by HTTP. */
    protected static final URI MOCK_RO = URI.create("http://localhost:8089/ro1/");

    /** Some RO available by HTTP. */
    protected static final URI MOCK_SNAPSHOT = URI.create("http://localhost:8089/ro1-copy/");

    /** Some RO available by HTTP. */
    protected static final URI MOCK_JOB_STATUS = URI.create("http://localhost:8089/jobs/1");


    /**
     * Create the Live RO.
     * 
     * @throws Exception
     *             unexpected response from RODL
     */
    @Before
    public void setUp()
            throws Exception {
        WireMock.resetAllScenarios();
        setUpServiceDesc();
        setUpRoSnapshot();
        setUpGetStatus();

        rosrs = new ROSRService(URI.create("http://localhost:8089/foo/"), null);
        roevo = new ROEVOService(URI.create("http://localhost:8089/"), null);
        ro = new ResearchObject(MOCK_RO, rosrs);
    }


    /**
     * Configure WireMock to handle snapshotting the RO.
     * 
     * @throws IOException
     *             if the test resources are not available
     */
    protected void setUpServiceDesc()
            throws IOException {
        InputStream serviceDesc = getClass().getClassLoader().getResourceAsStream("evo/service-desc.rdf");
        stubFor(get(urlEqualTo("/")).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/rdf+xml")
                    .withBody(IOUtils.toByteArray(serviceDesc))));
    }


    /**
     * Configure WireMock to handle snapshotting the RO.
     * 
     * @throws IOException
     *             if the test resources are not available
     */
    protected void setUpRoSnapshot()
            throws IOException {
        InputStream jobRunning = getClass().getClassLoader().getResourceAsStream("evo/status-running.json");
        stubFor(post(urlEqualTo("/copy/")).willReturn(
            aResponse().withStatus(201).withHeader("Content-Type", "application/json")
                    .withHeader("Location", MOCK_JOB_STATUS.toString()).withBody(IOUtils.toByteArray(jobRunning)))
                .willSetStateTo("Job created"));
        stubFor(delete(urlEqualTo("/jobs/1")).willReturn(aResponse().withStatus(204)));
    }


    /**
     * Configure WireMock to handle snapshotting the RO.
     * 
     * @throws IOException
     *             if the test resources are not available
     */
    protected void setUpGetStatus()
            throws IOException {
        InputStream jobRunning = getClass().getClassLoader().getResourceAsStream("evo/status-running.json");
        stubFor(get(urlEqualTo("/jobs/1")).willReturn(
            aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                    .withBody(IOUtils.toByteArray(jobRunning))).willSetStateTo("Job checked #1"));
        InputStream jobDone = getClass().getClassLoader().getResourceAsStream("evo/status-done.json");
        stubFor(get(urlEqualTo("/jobs/1"))
                .whenScenarioStateIs("Job checked #1")
                .willReturn(
                    aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                            .withBody(IOUtils.toByteArray(jobDone))).willSetStateTo("Job done"));
    }
}
