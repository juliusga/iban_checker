package com.task;

public class Iban {
    private String countryCode;
    private String bbanPart;
    private int lenght;

    public Iban(String iban)
    {
        countryCode = iban.substring(0, 2);
        bbanPart = iban.substring(2);
        lenght = iban.length();
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getBbanPart() {
        return bbanPart;
    }

    public int getLenght() {
        return lenght;
    }
}
