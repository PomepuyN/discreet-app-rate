package fr.nicolaspomepuy.discreetapprate;

/**
 * Created by nicolas on 06/03/14.
 */
public enum  RetryPolicy {
    /**
     * Will retry each time initial count has been triggered
     * Ex: if initial is set to 3, it will be shown on the 3rd, 6th, 9th, ... times
     */
    INCREMENTAL,
    /**
     * Will retry exponentially to be less intrusive
     * Ex: if initial is set to 3, it will be shown on the 3rd, 6th, 12th, ... times
     */
    EXPONENTIAL,
    /**
     * Will never retry
     */
    NONE;
}
