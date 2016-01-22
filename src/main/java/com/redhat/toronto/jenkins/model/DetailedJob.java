package com.redhat.toronto.jenkins.model;

import java.util.List;

public class DetailedJob {
    private List<Build> builds;

    public List<Build> getBuilds() {
        return builds;
    }

    public void setBuilds(List<Build> builds) {
        this.builds = builds;
    }
}
