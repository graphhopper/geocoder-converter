package com.graphhopper.converter.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Xuejing Dong
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetToolKitResponse {

    public String code;
    public List<NetToolKitAddressEntry> results;
    public Status status;

    @Override
    public String toString() {
        return "results: " + results.size();
    }

}
