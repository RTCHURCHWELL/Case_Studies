package com.fullsail.cerberus.helpers;

public class Formatter {
    String input;

    public Formatter(String input) {
        this.input = input;
    }

    public String getFormatted(){
        return input.replaceAll(" ", "_");
    }

    public String getUnformatted(){
        return input.replaceAll("_", " ");
    }
}
