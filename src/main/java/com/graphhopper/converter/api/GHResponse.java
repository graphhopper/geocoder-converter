package com.graphhopper.converter.api;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Peter Karich
 */
public class GHResponse {

    private List<String> copyrights = new ArrayList<String>(5);
    private List<GHEntry> hits;

    public GHResponse() {
        this(5);
    }

    public GHResponse(int no) {
        hits = new ArrayList<>(no);
    }

    public void setCopyrights(List<String> copyrights) {
        this.copyrights = copyrights;
    }

    public List<String> getCopyrights() {
        return copyrights;
    }

    public GHResponse addCopyright(String cr) {
        copyrights.add(cr);
        return this;
    }

    public void setHits(List<GHEntry> hits) {
        this.hits = hits;
    }

    public void add(GHEntry entry) {
        hits.add(entry);
    }

    public List<GHEntry> getHits() {
        return hits;
    }
}
