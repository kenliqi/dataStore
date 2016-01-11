package ib.data;

/**
 * Created by qili on 21/11/2015.
 */
public enum Exchange {
    SHA("China", "SHA", "SHA"),
    SHE("China", "SHE", "SHA"),
    NYSE("USA", "NYSE", "NYSE"),
    NASDAQ("USA", "NASDAQ", "NASD"),
    AMEX("USA", "AMEX", "NYSEMKT");

    public String region;
    public String name;
    public String urlName;

    Exchange(String region, String name, String urlName) {
        this.region = region;
        this.name = name;
        this.urlName = urlName;
    }

}
