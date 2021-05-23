package com.gsf.executor.api.util;

import com.gsf.executor.api.enums.AttackTypes;

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

    public static AttackTypes getAttackTypesById(int id) {
        for (AttackTypes type : AttackTypes.values()) {
            if (type.getId() == id) return type;
        }
        return AttackTypes.NONE;
    }


}
