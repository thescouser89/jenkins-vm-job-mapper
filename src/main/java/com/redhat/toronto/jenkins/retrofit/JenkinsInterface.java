package com.redhat.toronto.jenkins.retrofit;

import com.redhat.toronto.jenkins.model.DetailedBuild;
import com.redhat.toronto.jenkins.model.DetailedJob;
import com.redhat.toronto.jenkins.model.Jenkins;
import com.redhat.toronto.jenkins.model.JenkinsComputers;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

public interface JenkinsInterface {
    @GET("api/json")
    Call<Jenkins> getJenkins();

    @GET("job/{name}/api/json")
    Call<DetailedJob> getDetailedJob(@Path("name") String jobName);

    @GET("job/{jobName}/{jobId}/api/json")
    Call<DetailedBuild> getDetailedBuild(@Path("jobName") String jobName, @Path("jobId") int jobId);

    @GET("computer/api/json")
    Call<JenkinsComputers> getAllVms();
}
