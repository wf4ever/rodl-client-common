package org.purl.wf4ever.rosrs.client.evo;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * A test class for the RO evo service.
 * 
 * @author piotrekhol
 * 
 */
public class ROEVOServiceTest extends BaseTest {

    /** A test HTTP mock server. */
    @Rule
    public static final WireMockRule WIREMOCK_RULE = new WireMockRule(8089); // No-args constructor defaults to port 8080


    /**
     * Create a new snapshot job, verify the correct response.
     */
    @Test
    public final void testCreateSnapshotNoFinalize() {
        JobStatus status = roevo.createSnapshot(ro.getUri(), "ro1-copy", false);
        Assert.assertEquals(JobStatus.State.RUNNING, status.getState());
        Assert.assertEquals(ro.getUri(), status.getCopyfrom());
        Assert.assertNotNull(status.getTarget());
    }


    /**
     * Get a job status, verify it has correct data.
     */
    @Test
    public final void testGetJobStatus() {
        JobStatus status = roevo.createSnapshot(ro.getUri(), "ro1-copy", false);
        Assert.assertEquals(JobStatus.State.RUNNING, status.getState());
        Assert.assertEquals(ro.getUri(), status.getCopyfrom());
        Assert.assertNotNull(status.getTarget());

        JobStatus status2 = roevo.getStatus(status.getUri());
        Assert.assertEquals(status.getTarget(), status2.getTarget());
        Assert.assertEquals(status.getCopyfrom(), status2.getCopyfrom());
        Assert.assertEquals(status.getUri(), status2.getUri());
        Assert.assertEquals(status.isFinalize(), status2.isFinalize());
    }

}
