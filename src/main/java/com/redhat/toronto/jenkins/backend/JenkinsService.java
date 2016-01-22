package com.redhat.toronto.jenkins.backend;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.redhat.toronto.jenkins.model.Build;
import com.redhat.toronto.jenkins.model.Computer;
import com.redhat.toronto.jenkins.model.DetailedJob;
import com.redhat.toronto.jenkins.model.Jenkins;
import com.redhat.toronto.jenkins.model.JenkinsComputers;
import com.redhat.toronto.jenkins.model.Job;
import com.redhat.toronto.jenkins.retrofit.JenkinsInterface;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

@ApplicationScoped
public class JenkinsService {
    private static final String JENKINS_SERVER = "http://tovarich2.usersys.redhat.com/jenkins/";

    private Map<String, Set<String>> jobVmsMap = new HashMap<String, Set<String>>();
    private Map<String, Set<String>> vmJobsMap = new HashMap<String, Set<String>>();
    private List<String> unusedJobs;


    @PostConstruct
    private void init() throws IOException {
        update();
    }

    @Schedule(persistent = false)
    public void update() throws IOException {
        Map<String, Set<String>> tempJobVmMap = new HashMap<String, Set<String>>();
        Map<String, Set<String>> tempVmJobMap = new HashMap<String, Set<String>>();

        JenkinsInterface service = getJenkinsInterfaceInstance();
        Call<Jenkins> jenkins = service.getJenkins();

        List<String> tempUnusedJobs = generateAllJobs(service);

        for (Job job : jenkins.execute().body().getJobs()) {
            // filter jobs so that we only pick IVT jobs
            if (!job.getName().startsWith("IVT") || job.getName().endsWith("ALL")) {
                continue;
            }

            Set<String> vms = new HashSet<String>();

            for (Build build : getAllBuilds(service, job.getName())) {
                String vmName = getVmName(service, job.getName(), build.getNumber());
                vms.add(vmName);

                // if vm name not yet in map
                if (tempVmJobMap.get(vmName) == null) {
                    Set<String> jobs = new HashSet<String>();
                    jobs.add(job.getName());
                    tempVmJobMap.put(vmName, jobs);
                } else {
                    tempVmJobMap.get(vmName).add(job.getName());
                    tempUnusedJobs.remove(vmName);
                }
            }
            tempJobVmMap.put(job.getName(), vms);
        }
        jobVmsMap = tempJobVmMap;
        vmJobsMap = tempVmJobMap;
        unusedJobs = tempUnusedJobs;
    }

    private JenkinsInterface getJenkinsInterfaceInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(JENKINS_SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(JenkinsInterface.class);
    }

    private String getVmName(JenkinsInterface service, String jobName, int buildNumber) throws IOException {
        return service.getDetailedBuild(jobName, buildNumber).execute().body().getBuiltOn();
    }

    private List<Build> getAllBuilds(JenkinsInterface service, String jobName) throws IOException {
        Call<DetailedJob> detailedJob = service.getDetailedJob(jobName);
        // get all builds for a particular job
        return detailedJob.execute().body().getBuilds();
    }

    private List<String> generateAllJobs(JenkinsInterface service) throws IOException {
        List<String>  allJobs = new LinkedList<String>();

        JenkinsComputers computers = service.getAllVms().execute().body();
        for (Computer computer: computers.getComputer()) {
            allJobs.add(computer.getDisplayName());
        }

        return allJobs;
    }

    public Map<String, Set<String>> getJobVmsMap() {
        return jobVmsMap;
    }

    public Map<String, Set<String>> getVmJobsMap() {
        return vmJobsMap;
    }

    public List<String> getUnusedJobs() {
        return unusedJobs;
    }
}
