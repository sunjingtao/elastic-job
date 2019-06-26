package com.eaap.elasticjob.controller.config;

import com.eaap.elasticjob.model.JobSettings;
import com.eaap.elasticjob.service.IJobSettingsAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作业配置的RESTful API.
 *
 * @author caohao
 */
@RequestMapping("/jobs/config")
@RestController
public final class LiteJobConfigController {

    @Autowired
    private IJobSettingsAPI jobSettingsAPI;
    
    /**
     * 获取作业配置.
     * 
     * @param jobName 作业名称
     * @return 作业配置
     */
    @RequestMapping(value = "/{jobName}",method = RequestMethod.GET)
    public JobSettings getJobSettings(@PathVariable("jobName") final String jobName) {
        return jobSettingsAPI.getJobSettings(jobName);
    }
    
    /**
     * 修改作业配置.
     * 
     * @param jobSettings 作业配置
     */
    @RequestMapping(method = RequestMethod.PUT)
    public void updateJobSettings(final JobSettings jobSettings) {
        jobSettingsAPI.updateJobSettings(jobSettings);
    }
    
    /**
     * 删除作业配置.
     * 
     * @param jobName 作业名称
     */
    @RequestMapping(value = "/{jobName}",method = RequestMethod.DELETE)
    public void removeJob(@PathVariable("jobName") final String jobName) {
        jobSettingsAPI.removeJobSettings(jobName);
    }
}
