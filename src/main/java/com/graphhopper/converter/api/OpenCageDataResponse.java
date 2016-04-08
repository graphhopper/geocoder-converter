package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 *
 * @author Peter Karich
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenCageDataResponse {

    public String documentation;
    public List<OpenCageDataEntry> results;
    public Status status;

    @Override
    public String toString() {
        return "results:" + results.size();
    }
}
