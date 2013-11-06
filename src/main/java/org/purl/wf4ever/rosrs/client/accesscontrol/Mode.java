package org.purl.wf4ever.rosrs.client.accesscontrol;

/**
 * Research Object access mode.
 * 
 * @author pejot
 * 
 */
public enum Mode {

    /** Visible for everyone. */
    PUBLIC,
    /** Visible for user with special permissions. */
    PRIVATE;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    };
}
