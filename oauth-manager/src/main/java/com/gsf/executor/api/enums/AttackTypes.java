package com.gsf.executor.api.enums;

public enum AttackTypes {

    NONE(0),
    MIXUP(1),
    CSRF(2);

    private final int id;

    AttackTypes(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }


}
