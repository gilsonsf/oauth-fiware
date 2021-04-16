package com.gsf.executor.api.util;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

public class Utilities {

    private static LocalDateTime endCountDate;

    public static boolean isTimeToHack() {

        if (nonNull(endCountDate)) {
            boolean isTimeToHack = LocalDateTime.now().isAfter(endCountDate);

            if (isTimeToHack) {
                endCountDate = LocalDateTime.now().plusSeconds(10);
                return true;
            }

        } else {
            endCountDate = LocalDateTime.now().plusSeconds(10);
        }
        return false;
    }


}
