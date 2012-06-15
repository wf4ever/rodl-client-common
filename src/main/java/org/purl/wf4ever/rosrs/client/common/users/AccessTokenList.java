package org.purl.wf4ever.rosrs.client.common.users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a list of {@link AccessToken} which can be serialized and deserialized.
 * 
 * @author piotrekhol
 * 
 */
@XmlRootElement(name = "access-tokens")
public class AccessTokenList implements Serializable {

    /** id. */
    private static final long serialVersionUID = 4878049791239802255L;

    /** access tokens. */
    protected List<AccessToken> list = new ArrayList<AccessToken>();


    /**
     * Constructor.
     * 
     * @param list
     *            access tokens
     */
    public AccessTokenList(List<AccessToken> list) {
        this.list = list;
    }


    @XmlElement(name = "access-token")
    public List<AccessToken> getList() {
        return list;
    }
}
