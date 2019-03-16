package com.example.wikifry.testandy.models;

public class BTDevice {

    private String name;
    private String macAddress;

    public BTDevice(String name, String macAddress)
    {
        this.name = name;
        this.macAddress = macAddress;
    }

    public String getName()
    {
        return this.name;
    }

    public String getMacAddress()
    {
        return this.macAddress;
    }
}
