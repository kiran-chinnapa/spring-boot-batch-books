package com.batch.books.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
public class LaunchController {

    Logger log = LoggerFactory.getLogger(LaunchController.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job fileReaderJob;

    @Autowired
    private Job restApiReaderJob;

    @GetMapping("/launchFileReader")
    public BatchStatus launchFileReader() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        Map<String, JobParameter> parameterMap = new HashMap<>();
        parameterMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(parameterMap);
        JobExecution jobExecution = jobLauncher.run(fileReaderJob, jobParameters);

        log.info("status of the job :" + jobExecution.getStatus());

        log.info("Batch is running......");
        while (jobExecution.isRunning()) {
            log.info(".....");
        }
        return jobExecution.getStatus();
    }

    @GetMapping("/launchRestApiReader")
    public BatchStatus launchRestApiReader() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        Map<String, JobParameter> parameterMap = new HashMap<>();
        parameterMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(parameterMap);
        JobExecution jobExecution = jobLauncher.run(restApiReaderJob, jobParameters);

        log.info("status of the job :" + jobExecution.getStatus());

        log.info("Batch is running......");
        while (jobExecution.isRunning()) {
            log.info(".....");
        }
        return jobExecution.getStatus();
    }
}
