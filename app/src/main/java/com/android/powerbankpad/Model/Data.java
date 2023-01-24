package com.android.powerbankpad.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {
    @SerializedName("asn")
    @Expose
    private String asn;
    @SerializedName("logo_url")
    @Expose
    private String logoUrl;
    @SerializedName("ads_url")
    @Expose
    private List<String> adsUrl = null;

    public String getAsn() {
        return asn;
    }

    public void setAsn(String asn) {
        this.asn = asn;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public List<String> getAdsUrl() {
        return adsUrl;
    }

    public void setAdsUrl(List<String> adsUrl) {
        this.adsUrl = adsUrl;
    }

}
