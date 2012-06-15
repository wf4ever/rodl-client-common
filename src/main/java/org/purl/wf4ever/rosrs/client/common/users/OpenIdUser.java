package org.purl.wf4ever.rosrs.client.common.users;

import java.io.Serializable;

/**
 * OpenID user with all supported attributes.
 * 
 * @author Piotr Hołubowicz
 * 
 */
public class OpenIdUser implements Serializable {

    /** id. */
    private static final long serialVersionUID = -2834908356868001273L;

    /** OpenID. */
    private String openId;

    /** first and last name. */
    private String fullName;

    /** first name. */
    private String firstName;

    /** last name. */
    private String lastName;

    /** e-mail. */
    private String emailAddress;

    /** country. */
    private String country;

    /** language. */
    private String language;


    public String getOpenId() {
        return openId;
    }


    public void setOpenId(String openId) {
        this.openId = openId;
    }


    public String getFullName() {
        return fullName;
    }


    public void setFullName(String fullname) {
        this.fullName = fullname;
    }


    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getEmailAddress() {
        return emailAddress;
    }


    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }


    public String getCountry() {
        return country;
    }


    public void setCountry(String country) {
        this.country = country;
    }


    public String getLanguage() {
        return language;
    }


    public void setLanguage(String language) {
        this.language = language;
    }

}
