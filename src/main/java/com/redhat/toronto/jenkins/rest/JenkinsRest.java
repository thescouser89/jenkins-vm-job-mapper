package com.redhat.toronto.jenkins.rest;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import com.redhat.toronto.jenkins.backend.JenkinsService;

@Path("/jenkins")
public class JenkinsRest {

    @Inject
    private JenkinsService jenkinsService;

    @GET @Path("job_vms_map")
    @Produces(MediaType.APPLICATION_JSON)
    public Response jobVmsMap() {
        return Response.ok(jenkinsService.getJobVmsMap()).build();
    }

    @GET @Path("vm_jobs_map")
    @Produces(MediaType.APPLICATION_JSON)
    public Response vmsJobMap() {
        return Response.ok(jenkinsService.getVmJobsMap()).build();
    }

    @GET @Path("unused_vms")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unusedVms() {
        return Response.ok(jenkinsService.getUnusedJobs()).build();
    }
}
