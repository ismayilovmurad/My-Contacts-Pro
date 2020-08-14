package com.martiandeveloper.mycontactspro.model;

public class ContactClass {

    private String name;
    private String surName;
    private String primaryPhone;
    private String secondaryPhone;
    private String eMail;
    private String instagram;
    private String facebook;
    private String twitter;
    private String linkedin;
    private String snapchat;
    private String skype;
    private String website;

    public ContactClass(String name, String surName, String primaryPhone, String secondaryPhone, String eMail, String instagram, String facebook, String twitter, String linkedin, String snapchat, String skype, String website) {
        this.name = name;
        this.surName = surName;
        this.primaryPhone = primaryPhone;
        this.secondaryPhone = secondaryPhone;
        this.eMail = eMail;
        this.instagram = instagram;
        this.facebook = facebook;
        this.twitter = twitter;
        this.linkedin = linkedin;
        this.snapchat = snapchat;
        this.skype = skype;
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(String phone1) {
        this.primaryPhone = phone1;
    }

    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public void setSecondaryPhone(String phone2) {
        this.secondaryPhone = phone2;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getSnapchat() {
        return snapchat;
    }

    public void setSnapchat(String snapchat) {
        this.snapchat = snapchat;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
