package org.purl.wf4ever.rosrs.client.accesscontrol;

/** User role. */
public enum Role {

    /** Can read, edit and grant permissions. */
    OWNER,
    /** Can read and edit. */
    EDITOR,
    /** Can read. */
    REDAER;

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    };
}
