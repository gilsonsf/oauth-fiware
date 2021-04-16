package com.gsf.executor.api;

public enum AttackTypes {

    NONE(0),
    REDIRECT_307(1),
    MIXUP(2),
    CSRF(3);

    private final int id;

    AttackTypes(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }


}
