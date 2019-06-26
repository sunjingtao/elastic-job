package com.eaap.elasticjob.controller;

import com.eaap.elasticjob.model.JobBriefInfo;
import com.eaap.elasticjob.model.ServerBriefInfo;
import com.eaap.elasticjob.service.IJobOperateAPI;
import com.eaap.elasticjob.service.IJobStatisticsAPI;
import com.eaap.elasticjob.service.IServerStatisticsAPI;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * 服务器维度操作的RESTful API.
 *
 * @author caohao
 */
@RequestMapping("/servers")
@RestController
public class ServerOperationController {

    @Autowired
    private IJobOperateAPI jobOperateAPI;
    @Autowired
    private IJobStatisticsAPI jobStatisticsAPI;
    @Autowired
    private IServerStatisticsAPI serverStatisticsAPI;
    /**
     * 获取服务器总数.
     * 
     * @return 服务器总数
     */
    @RequestMapping(value = "/count",method = RequestMethod.GET)
    public int getServersTotalCount() {
        return serverStatisticsAPI.getServersTotalCount();
    }
    
    /**
     * 获取服务器详情.
     * 
     * @return 服务器详情集合
     */
    @RequestMapping(value = "getAllServersBriefInfo",method = RequestMethod.GET)
    public Collection<ServerBriefInfo> getAllServersBriefInfo() {
        return serverStatisticsAPI.getAllServersBriefInfo();
    }
    
    /**
     * 禁用作业.
     *
     * @param serverIp 服务器IP地址
     */
    @RequestMapping(value = "/{serverIp}/disable",method = RequestMethod.POST)
    public void disableServer(@PathVariable("serverIp") final String serverIp) {
        jobOperateAPI.disable(Optional.<String>absent(), Optional.of(serverIp));
    }
    
    /**
     * 启用作业.
     *
     * @param serverIp 服务器IP地址
     */
    @RequestMapping("/{serverIp}/disable")
    public void enableServer(@PathVariable("serverIp") final String serverIp) {
        jobOperateAPI.enable(Optional.<String>absent(), Optional.of(serverIp));
    }
    
    /**
     * 终止作业.
     *
     * @param serverIp 服务器IP地址
     */
    @RequestMapping(value = "/{serverIp}/shutdown",method = RequestMethod.POST)
    public void shutdownServer(@PathVariable("serverIp") final String serverIp) {
        jobOperateAPI.shutdown(Optional.<String>absent(), Optional.of(serverIp));
    }
    
    /**
     * 清理作业.
     *
     * @param serverIp 服务器IP地址
     */
    @RequestMapping(value = "/{serverIp}",method = RequestMethod.DELETE)
    public void removeServer(@PathVariable("serverIp") final String serverIp) {
        jobOperateAPI.remove(Optional.<String>absent(), Optional.of(serverIp));
    }
    
    /**
     * 获取该服务器上注册的作业的简明信息.
     *
     * @param serverIp 服务器IP地址
     * @return 作业简明信息对象集合
     */
    @RequestMapping(value = "/{serverIp}/jobs",method = RequestMethod.GET)
    public Collection<JobBriefInfo> getJobs(@PathVariable("serverIp") final String serverIp) {
        return jobStatisticsAPI.getJobsBriefInfo(serverIp);
    }
    
    /**
     * 禁用作业.
     * 
     * @param serverIp 服务器IP地址
     * @param jobName 作业名称
     */
    @RequestMapping(value = "/{serverIp}/jobs/{jobName}/disable",method = RequestMethod.POST)
    public void disableServerJob(@PathVariable("serverIp") final String serverIp, @PathVariable("jobName") final String jobName) {
        jobOperateAPI.disable(Optional.of(jobName), Optional.of(serverIp));
    }
    
    /**
     * 启用作业.
     *
     * @param serverIp 服务器IP地址
     * @param jobName 作业名称
     */
    @RequestMapping(value = "/{serverIp}/jobs/{jobName}/disable",method = RequestMethod.DELETE)
    public void enableServerJob(@PathVariable("serverIp") final String serverIp, @PathVariable("jobName") final String jobName) {
        jobOperateAPI.enable(Optional.of(jobName), Optional.of(serverIp));
    }
    
    /**
     * 终止作业.
     *
     * @param serverIp 服务器IP地址
     * @param jobName 作业名称
     */
    @RequestMapping(value = "/{serverIp}/jobs/{jobName}/shutdown",method = RequestMethod.POST)
    public void shutdownServerJob(@PathVariable("serverIp") final String serverIp, @PathVariable("jobName") final String jobName) {
        jobOperateAPI.shutdown(Optional.of(jobName), Optional.of(serverIp));
    }
    
    /**
     * 清理作业.
     *
     * @param serverIp 服务器IP地址
     * @param jobName 作业名称
     */
    @RequestMapping(value = "/{serverIp}/jobs/{jobName}",method = RequestMethod.DELETE)
    public void removeServerJob(@PathVariable("serverIp") final String serverIp, @PathVariable("jobName") final String jobName) {
        jobOperateAPI.remove(Optional.of(jobName), Optional.of(serverIp));
    }
}
