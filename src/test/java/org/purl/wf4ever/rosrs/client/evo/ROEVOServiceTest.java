package org.purl.wf4ever.rosrs.client.evo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.purl.wf4ever.rosrs.client.ROSRService;
import org.purl.wf4ever.rosrs.client.ResearchObject;
import org.purl.wf4ever.rosrs.client.exception.ROException;
import org.purl.wf4ever.rosrs.client.exception.ROSRSException;

/**
 * A test class for the RO evo service.
 * 
 * @author piotrekhol
 * 
 */
public class ROEVOServiceTest {

    /** ro evo service. */
    private static ROEVOService roevo;

    /** ROSR service. */
    private static ROSRService rosrs;

    /** RODL access token. */
    private static final String TOKEN = "32801fc0-1df1-4e34-b";

    /** RODL URI for testing. */
    private static final URI RODL_URI = URI.create("http://sandbox.wf4ever-project.org/rodl/");

    /** ROs to delete after a test. */
    private static List<ResearchObject> rosToDelete = new ArrayList<>();

    /** The Live RO. */
    private ResearchObject ro;


    /**
     * Prepare an ROEVO service.
     * 
     * @throws ROException
     *             example RO has incorrect data
     * @throws ROSRSException
     *             could not load the example RO
     */
    @BeforeClass
    public static final void setUpBeforeClass()
            throws ROSRSException, ROException {
        rosrs = new ROSRService(RODL_URI.resolve("ROs/"), TOKEN);
        roevo = new ROEVOService(RODL_URI.resolve("evo/"), TOKEN);
    }


    /**
     * Create the Live RO.
     * 
     * @throws ROSRSException
     *             unexpected response from RODL
     */
    @Before
    public final void setUp()
            throws ROSRSException {
        ro = ResearchObject.create(rosrs, "ROEVOServiceTest");
        rosToDelete.add(ro);
    }


    /**
     * Remove all ROs created in the tests.
     * 
     * @throws ROSRSException
     *             unexpected response from RODL
     */
    @After
    public void tearDown()
            throws ROSRSException {
        for (ResearchObject roToDelete : rosToDelete) {
            roToDelete.delete();
        }
    }


    /**
     * Create a new snapshot job, verify the correct response.
     */
    @Test
    public final void testCreateSnapshotNoFinalize() {
        JobStatus status = roevo.createSnapshot(ro.getUri(), "TestSnapshot", false);
        Assert.assertEquals(JobStatus.State.RUNNING, status.getState());
        Assert.assertEquals(ro.getUri(), status.getCopyfrom());
        Assert.assertNotNull(status.getTarget());
        rosToDelete.add(new ResearchObject(ro.getUri().resolve(status.getTarget()), rosrs));
    }

}
