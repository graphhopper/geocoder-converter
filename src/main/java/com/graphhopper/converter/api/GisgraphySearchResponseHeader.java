package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author David Masclet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GisgraphySearchResponseHeader {

    private Integer status = 0;
    @JsonProperty("QTime")
    private Integer qTime = 0;
    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonProperty("QTime")
    public Integer getQTime() {
        return qTime;
    }

    @JsonProperty("QTime")
    public void setqTime(Integer qTime) {
        this.qTime = qTime;
    }




}
