package com.yahoo.ycsb.generator;

import java.util.UUID;

import org.bson.types.ObjectId;

/**
 * Unique ID Generator for Pi resources.
 * 
 */
public class UniqueIdGenerator {

    /**
     * Generates an incrementing unique id string.
     * 
     * @return A Unique ID incremented from the previous.
     */
    public static String generateIncrementing() {
        return new ObjectId().toString();
    }

    /**
     * Generate a randomly generated unique id string. Wraps a call to {@link UUID#randomUUID()} followed by a removal
     * of all "-" characters from the output string.
     * 
     * @return A Unique ID randomly generated.
     */
    public static String generateRandom() {
        String fullUUID = UUID.randomUUID().toString();
        String trimUUID = fullUUID.replaceAll("-", "");
        return trimUUID;
    }
}