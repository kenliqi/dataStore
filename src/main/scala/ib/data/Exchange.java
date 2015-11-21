package ib.data;

/**
 * Created by qili on 21/11/2015.
 */
public enum Exchange {
    NYSE("USA", "NYSE"),
    NASDAQ("USA", "NASDAQ"),
    AMEX("USA", "AMEX");

    private String region;
    private String name;

    Exchange(String region, String name) {
        this.region = region;
        this.name = name;
    }

}
