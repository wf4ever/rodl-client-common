package org.purl.wf4ever.rosrs.client.evo;

import java.io.Serializable;
import java.net.URI;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Job status as JSON.
 * 
 * @author piotrekhol
 * 
 */
@XmlRootElement
public class JobStatus implements Serializable {

    /** id. */
    private static final long serialVersionUID = 4168448397936150150L;


    /**
     * The job state.
     * 
     * @author piotrekhol
     * 
     */
    public enum State {
        /** The job has started and is running. */
        RUNNING,
        /** The job has finished successfully. */
        DONE,
        /** The job has been cancelled by the user. */
        CANCELLED,
        /** The job has failed gracefully. */
        FAILED,
        /** There has been an unexpected error during conversion. */
        SERVICE_ERROR;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        };
    }


    /** job URI. */
    private URI uri;

    /** RO to copy from. */
    private URI copyfrom;

    /** Target RO evolution status. */
    private EvoType type;

    /** Finalize? */
    private boolean finalize;

    /** Target RO URI. */
    private String target;

    /** job state. */
    private State state;

    /** Justification of the current state, useful in case of error. */
    private String reason;

    /** ROEVO client. */
    private ROEVOService roevo;


    /**
     * Default empty constructor.
     */
    public JobStatus() {

    }


    /**
     * Constructor.
     * 
     * @param copyfrom
     *            RO to copy from
     * @param type
     *            Target RO evolution status
     * @param finalize
     *            Finalize?
     */
    public JobStatus(URI copyfrom, EvoType type, boolean finalize) {
        setCopyfrom(copyfrom);
        setType(type);
        setFinalize(finalize);
    }


    @XmlTransient
    public ROEVOService getRoevo() {
        return roevo;
    }


    public void setRoevo(ROEVOService roevo) {
        this.roevo = roevo;
    }


    public URI getUri() {
        return uri;
    }


    public void setUri(URI uri) {
        this.uri = uri;
    }


    public URI getCopyfrom() {
        return copyfrom;
    }


    public void setCopyfrom(URI copyfrom) {
        this.copyfrom = copyfrom;
    }


    public EvoType getType() {
        return type;
    }


    public void setType(EvoType type) {
        this.type = type;
    }


    public boolean isFinalize() {
        return finalize;
    }


    public void setFinalize(boolean finalize) {
        this.finalize = finalize;
    }


    public String getTarget() {
        return target;
    }


    public void setTarget(String target) {
        this.target = target;
    }


    @XmlElement(name = "status")
    public State getState() {
        return state;
    }


    public void setState(State state) {
        this.state = state;
    }


    public String getReason() {
        return reason;
    }


    public void setReason(String reason) {
        this.reason = reason;
    }


    /**
     * Reload the properties from the ROEVO server.
     */
    public void refresh() {
        JobStatus status = roevo.getStatus(uri);
        this.copyfrom = status.getCopyfrom();
        this.finalize = status.isFinalize();
        this.reason = status.getReason();
        this.target = status.getTarget();
        this.type = status.getType();
        this.state = status.getState();
    }

}
