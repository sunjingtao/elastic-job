package com.eaap.elasticjob.controller;

import com.eaap.elasticjob.model.JobBriefInfo;
import com.eaap.elasticjob.model.ShardingInfo;
import com.eaap.elasticjob.service.IJobOperateAPI;
import com.eaap.elasticjob.service.IJobStatisticsAPI;
import com.eaap.elasticjob.service.IShardingOperateAPI;
import com.eaap.elasticjob.service.IShardingStatisticsAPI;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

/**
 * 作业维度操作的RESTful API.
 *
 * @author caohao
 */
@RequestMapping("/jobs")
@RestController
public final class JobOperationController {

    @Autowired
    private IJobStatisticsAPI jobStatisticsAPI;

    @Autowired
    private IJobOperateAPI jobOperateAPI;

    @Autowired
    private IShardingStatisticsAPI shardingStatisticsAPI;

    @Autowired
    private IShardingOperateAPI shardingOperateAPI;
    
    /**
     * 获取作业总数.
     * 
     * @return 作业总数
     */
    @RequestMapping(value = "/count",method = RequestMethod.GET)
    public int getJobsTotalCount() {
        return jobStatisticsAPI.getJobsTotalCount();
    }
    
    /**
     * 获取作业详情.
     * 
     * @return 作业详情集合
     */
    @RequestMapping(value = "getAllJobsBriefInfo",method = RequestMethod.GET)
    public List<JobBriefInfo> getAllJobsBriefInfo() {
         return jobStatisticsAPI.getAllJobsBriefInfo();
    }
    
    /** 
     * 触发作业.
     * 
     * @param jobName 作业名称
     */
    @RequestMapping(value = "/{jobName}/trigger",method = RequestMethod.POST)
    public void triggerJob(@PathVariable("jobName") final String jobName) {
        jobOperateAPI.trigger(Optional.of(jobName), Optional.<String>absent());
    }
    
    /**
     * 禁用作业.
     * 
     * @param jobName 作业名称
     */
    @RequestMapping(value = "/{jobName}/disable",method = RequestMethod.POST)
    public void disableJob(@PathVariable("jobName") final String jobName) {
        jobOperateAPI.disable(Optional.of(jobName), Optional.<String>absent());
    }
    
    /**
     * 启用作业.
     *
     * @param jobName 作业名称
     */
    @RequestMapping(value = "/{jobName}/disable",method = RequestMethod.DELETE)
    public void enableJob(@PathVariable("jobName") final String jobName) {
        jobOperateAPI.enable(Optional.of(jobName), Optional.<String>absent());
    }
    
    /**
     * 终止作业.
     * 
     * @param jobName 作业名称
     */
    @RequestMapping(value = "/{jobName}/shutdown",method = RequestMethod.POST)
    public void shutdownJob(@PathVariable("jobName") final String jobName) {
        jobOperateAPI.shutdown(Optional.of(jobName), Optional.<String>absent());
    }
    
    /**
     * 获取分片信息.
     * 
     * @param jobName 作业名称
     * @return 分片信息集合
     */
    @RequestMapping(value = "/{jobName}/sharding",method = RequestMethod.GET)
    public Collection<ShardingInfo> getShardingInfo(@PathVariable("jobName") final String jobName) {
        return shardingStatisticsAPI.getShardingInfo(jobName);
    }
    
    @RequestMapping(value = "/{jobName}/sharding/{item}/disable",method = RequestMethod.POST)
    public void disableSharding(@PathVariable("jobName") final String jobName, @PathVariable("item") final String item) {
        shardingOperateAPI.disable(jobName, item);
    }
    
    @RequestMapping(value = "/{jobName}/sharding/{item}/disable",method = RequestMethod.DELETE)
    public void enableSharding(@PathVariable("jobName") final String jobName, @PathVariable("item") final String item) {
        shardingOperateAPI.enable(jobName, item);
    }
}
