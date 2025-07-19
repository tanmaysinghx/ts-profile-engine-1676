package com.ts.ts_profile_engine_1676.util;

public class ProfileIdGenerator {

    private static final String PREFIX = "PID";
    private static final int ID_LENGTH = 5; // Length of numeric part e.g. 00001

    public static String generateNextProfileId(String lastProfileId) {
        if (lastProfileId == null || !lastProfileId.startsWith(PREFIX)) {
            return PREFIX + String.format("%0" + ID_LENGTH + "d", 1);
        }

        String numberPart = lastProfileId.substring(PREFIX.length());
        int number = Integer.parseInt(numberPart);
        int nextNumber = number + 1;

        return PREFIX + String.format("%0" + ID_LENGTH + "d", nextNumber);
    }
}
