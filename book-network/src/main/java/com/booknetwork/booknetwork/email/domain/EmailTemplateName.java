package com.booknetwork.booknetwork.email.domain;

import lombok.Getter;

@Getter
public enum EmailTemplateName {

    ACTIVE_ACCOUNT("activate_account");


    private final String name;

    EmailTemplateName(String name) {
        this.name = name;
    }

}
