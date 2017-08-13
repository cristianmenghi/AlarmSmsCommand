package com.lovelyfatbears.thoniorf.alarmsmscommander;

/**
 * Created by thoniorf on 8/11/17.
 */

public class Alarm {
    protected String name;
    protected String number;
    protected String password;

    protected String[] codes = {"ARM", "DISARM", "CHECK"};

    Alarm(String number, String password, String name) {
        this.number = number;
        this.password = password;
        this.name = name;
    }
}
