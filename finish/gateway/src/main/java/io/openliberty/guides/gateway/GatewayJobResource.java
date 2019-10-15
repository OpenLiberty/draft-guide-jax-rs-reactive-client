// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.gateway;

import java.util.concurrent.CompletionStage;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.List;
import java.util.LinkedList;
import org.apache.cxf.jaxrs.rx2.client.ObservableRxInvoker;
import org.apache.cxf.jaxrs.rx2.client.ObservableRxInvokerProvider;

import io.reactivex.rxjava3.core.*;
import io.reactivex.Observable;
//import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;
//import io.reactivex.rxjava3.core.Observable;

import javax.ws.rs.client.Client;

import io.openliberty.guides.gateway.client.JobClient;
import io.openliberty.guides.models.JobList;
import io.openliberty.guides.models.Job;
import io.openliberty.guides.models.JobResult;
import io.openliberty.guides.models.Jobs;

@RequestScoped
@Path("/jobs")
public class GatewayJobResource {

    @Inject
    private JobClient jobClient;

    /*@GET
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<JobList> getJobs() {
        return jobClient
            .getJobs()
            // tag::thenApplyAsync[]
            .thenApplyAsync((jobs) -> {
                return new JobList(jobs.getResults());
            })
            // end::thenApplyAsync[]
            // tag::exceptionally[]
            .exceptionally((ex) -> {
                // Respond with empty list on error
                return new JobList();
            });
            // end::exceptionally[]
    }*/
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JobList getJobs() {
        Observable<Jobs> obs = jobClient.getJobs();
            obs
                .observeOn(Schedulers.computation(), true)
                .subscribe((v) -> {
                    ((Jobs)v).getResults();
            });
            return new JobList();
        
    }

    /*@GET
    @Path("{jobId}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<JobResult> getJob(@PathParam("jobId") String jobId) {
        return jobClient.getJob(jobId);
    }*/

    @GET
    @Path("{jobId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Observable<JobResult> getJob(@PathParam("jobId") String jobId) {
        return Observable.defer(() -> {
            try {
                //return Observable.just(jobClient.getJob(jobId));  
                return jobClient.getJob(jobId);    
            }
            catch (Exception e){
                return;
            }
        });
    }

    /*@POST
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Job> createJob() {
        return jobClient.createJob();
    }*/
    @Produces(MediaType.APPLICATION_JSON)
    public Observable<Job> createJob() {
        return Observable.defer(() -> {
            try {
                //return Observable.just(jobClient.createJob());
                return jobClient.createJob();
            }
            catch (Exception e) {
                return;
            }
        });
    }
}
