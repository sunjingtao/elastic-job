/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.job.lite.internal.sharding;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.fixture.TestSimpleJob;
import com.dangdang.ddframe.job.lite.internal.config.ConfigurationService;
import com.dangdang.ddframe.job.lite.internal.election.LeaderService;
import com.dangdang.ddframe.job.lite.internal.schedule.JobScheduleController;
import com.dangdang.ddframe.job.lite.internal.storage.JobNodeStorage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.util.ReflectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class ExecutionServiceTest {
    
    @Mock
    private JobNodeStorage jobNodeStorage;
    
    @Mock
    private ConfigurationService configService;
    
    @Mock
    private LeaderService leaderService;
    
    @Mock
    private JobScheduleController jobScheduleController;
    
    private final ExecutionService executionService = new ExecutionService(null, "test_job");
    
    @Before
    public void setUp() throws NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        ReflectionUtils.setFieldValue(executionService, "jobNodeStorage", jobNodeStorage);
        ReflectionUtils.setFieldValue(executionService, "configService", configService);
        ReflectionUtils.setFieldValue(executionService, "leaderService", leaderService);
    }
    
    @Test
    public void assertRegisterJobBeginWhenNotMonitorExecution() {
        when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(),
                TestSimpleJob.class.getCanonicalName())).monitorExecution(false).build());
        executionService.registerJobBegin(getShardingContext());
        verify(configService).load(true);
    }
    
    @Test
    public void assertRegisterJobCompletedWhenNotMonitorExecution() {
        when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(),
                TestSimpleJob.class.getCanonicalName())).monitorExecution(false).build());
        executionService.registerJobCompleted(new ShardingContexts("fake_task_id", "test_job", 10, "", Collections.<Integer, String>emptyMap()));
        verify(configService).load(true);
    }
    
    @Test
    public void assertRegisterJobCompleted() {
        when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(),
                TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        executionService.registerJobCompleted(getShardingContext());
        verify(jobNodeStorage).createJobNodeIfNeeded("sharding/0/completed");
        verify(jobNodeStorage).createJobNodeIfNeeded("sharding/1/completed");
        verify(jobNodeStorage).createJobNodeIfNeeded("sharding/2/completed");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/0/running");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/1/running");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/2/running");
    }
    
    @Test
    public void assertCleanPreviousExecutionInfoWhenNotMonitorExecution() {
        when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(),
                TestSimpleJob.class.getCanonicalName())).monitorExecution(false).build());
        executionService.cleanPreviousExecutionInfo();
        verify(leaderService, times(0)).isLeaderUntilBlock();
    }
    
    @Test
    public void assertCleanPreviousExecutionInfoWhenIsNotLeader() {
        when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(),
                TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        when(leaderService.isLeaderUntilBlock()).thenReturn(false);
        when(jobNodeStorage.isJobNodeExisted("leader/sharding/cleaning")).thenReturn(true, false);
        executionService.cleanPreviousExecutionInfo();
        verify(leaderService).isLeaderUntilBlock();
        verify(jobNodeStorage, times(2)).isJobNodeExisted("leader/sharding/cleaning");
    }
    
    @Test
    public void assertCleanPreviousExecutionInfoWhenIsLeaderButNotNeedFixExecutionInfo() {
        when(jobNodeStorage.isJobNodeExisted("sharding")).thenReturn(true);
        when(leaderService.isLeaderUntilBlock()).thenReturn(true);
        when(jobNodeStorage.getJobNodeChildrenKeys("sharding")).thenReturn(Arrays.asList("0", "1", "2"));
        when(jobNodeStorage.isJobNodeExisted("leader/sharding/necessary")).thenReturn(false);
        when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(),
                TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        executionService.cleanPreviousExecutionInfo();
        verify(jobNodeStorage).fillEphemeralJobNode("leader/sharding/cleaning", "");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/0/completed");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/1/completed");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/2/completed");
        verify(jobNodeStorage).removeJobNodeIfExisted("leader/sharding/cleaning");
    }
    
    @Test
    public void assertCleanPreviousExecutionInfoWhenNeedFixExecutionInfoForNewValuesGreater() {
        when(jobNodeStorage.isJobNodeExisted("sharding")).thenReturn(true);
        when(leaderService.isLeaderUntilBlock()).thenReturn(true);
        when(jobNodeStorage.getJobNodeChildrenKeys("sharding")).thenReturn(Arrays.asList("0", "1", "2"));
        when(jobNodeStorage.isJobNodeExisted("leader/sharding/necessary")).thenReturn(true);
        when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(),
                TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        executionService.cleanPreviousExecutionInfo();
        verify(jobNodeStorage).fillEphemeralJobNode("leader/sharding/cleaning", "");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/0/completed");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/1/completed");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/2/completed");
        verify(jobNodeStorage).removeJobNodeIfExisted("leader/sharding/cleaning");
    }
    
    @Test
    public void assertCleanPreviousExecutionInfoWhenNeedFixExecutionInfoForNewValuesLess() {
        when(jobNodeStorage.isJobNodeExisted("sharding")).thenReturn(true);
        when(leaderService.isLeaderUntilBlock()).thenReturn(true);
        when(jobNodeStorage.getJobNodeChildrenKeys("sharding")).thenReturn(Arrays.asList("0", "1", "2"));
        when(jobNodeStorage.isJobNodeExisted("leader/sharding/necessary")).thenReturn(true);
        when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(),
                TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        executionService.cleanPreviousExecutionInfo();
        verify(jobNodeStorage).fillEphemeralJobNode("leader/sharding/cleaning", "");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/0/completed");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/1/completed");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/2/completed");
        verify(jobNodeStorage).removeJobNodeIfExisted("leader/sharding/cleaning");
    }
    
    @Test
    public void assertCleanPreviousExecutionInfoWhenNeedFixExecutionInfoForNewValuesEqual() {
        when(jobNodeStorage.isJobNodeExisted("sharding")).thenReturn(true);
        when(leaderService.isLeaderUntilBlock()).thenReturn(true);
        when(jobNodeStorage.getJobNodeChildrenKeys("sharding")).thenReturn(Arrays.asList("0", "1", "2"));
        when(jobNodeStorage.isJobNodeExisted("leader/execution/necessary")).thenReturn(true);
        when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(),
                TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        executionService.cleanPreviousExecutionInfo();
        verify(jobNodeStorage).fillEphemeralJobNode("leader/sharding/cleaning", "");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/0/completed");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/1/completed");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/2/completed");
        verify(jobNodeStorage).removeJobNodeIfExisted("leader/sharding/cleaning");
    }
    
    @Test
    public void assertClearRunningInfo() {
        executionService.clearRunningInfo(Arrays.asList(0, 1, 2));
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/0/running");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/1/running");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/2/running");
    }
    
    @Test
    public void assertMisfireIfNotRunning() {
        when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 4).build(),
                TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        when(jobNodeStorage.isJobNodeExisted("sharding/0/running")).thenReturn(false);
        when(jobNodeStorage.isJobNodeExisted("sharding/1/running")).thenReturn(false);
        when(jobNodeStorage.isJobNodeExisted("sharding/2/running")).thenReturn(false);
        assertFalse(executionService.misfireIfRunning(Arrays.asList(0, 1, 2)));
        verify(configService).load(true);
        verify(jobNodeStorage).isJobNodeExisted("sharding/0/running");
        verify(jobNodeStorage).isJobNodeExisted("sharding/1/running");
        verify(jobNodeStorage).isJobNodeExisted("sharding/2/running");
    }
    
    @Test
    public void assertMisfireIfRunning() {
        when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 4).build(),
                TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        when(jobNodeStorage.isJobNodeExisted("sharding/0/running")).thenReturn(false);
        when(jobNodeStorage.isJobNodeExisted("sharding/1/running")).thenReturn(true);
        assertTrue(executionService.misfireIfRunning(Arrays.asList(0, 1, 2)));
        verify(jobNodeStorage).isJobNodeExisted("sharding/0/running");
        verify(jobNodeStorage).isJobNodeExisted("sharding/1/running");
        verify(jobNodeStorage).createJobNodeIfNeeded("sharding/0/misfire");
        verify(jobNodeStorage).createJobNodeIfNeeded("sharding/1/misfire");
        verify(jobNodeStorage).createJobNodeIfNeeded("sharding/2/misfire");
    }
    
    @Test
    public void assertGetMisfiredJobItems() {
        when(jobNodeStorage.isJobNodeExisted("sharding/0/misfire")).thenReturn(true);
        when(jobNodeStorage.isJobNodeExisted("sharding/1/misfire")).thenReturn(true);
        when(jobNodeStorage.isJobNodeExisted("sharding/2/misfire")).thenReturn(false);
        assertThat(executionService.getMisfiredJobItems(Arrays.asList(0, 1, 2)), is(Arrays.asList(0, 1)));
        verify(jobNodeStorage).isJobNodeExisted("sharding/0/misfire");
        verify(jobNodeStorage).isJobNodeExisted("sharding/1/misfire");
        verify(jobNodeStorage).isJobNodeExisted("sharding/2/misfire");
    }
    
    @Test
    public void assertClearMisfire() {
        executionService.clearMisfire(Arrays.asList(0, 1, 2));
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/0/misfire");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/1/misfire");
        verify(jobNodeStorage).removeJobNodeIfExisted("sharding/2/misfire");
    }
    
    @Test
    public void assertGetDisabledItems() {
        when(jobNodeStorage.isJobNodeExisted("sharding/0/disabled")).thenReturn(true);
        when(jobNodeStorage.isJobNodeExisted("sharding/1/disabled")).thenReturn(true);
        when(jobNodeStorage.isJobNodeExisted("sharding/2/disabled")).thenReturn(false);
        assertThat(executionService.getDisabledItems(Arrays.asList(0, 1, 2)), is(Arrays.asList(0, 1)));
        verify(jobNodeStorage).isJobNodeExisted("sharding/0/disabled");
        verify(jobNodeStorage).isJobNodeExisted("sharding/1/disabled");
        verify(jobNodeStorage).isJobNodeExisted("sharding/2/disabled");
    }
    
    @Test
    public void assertIsCompleted() {
        when(jobNodeStorage.isJobNodeExisted("sharding/0/completed")).thenReturn(true);
        assertTrue(executionService.isCompleted(0));
    }
    
    @Test
    public void assertNotHaveRunningItemsWhenJNotMonitorExecution() {
        when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 4).build(),
                TestSimpleJob.class.getCanonicalName())).monitorExecution(false).build());
        assertFalse(executionService.hasRunningItems(Arrays.asList(0, 1, 2)));
        verify(configService).load(true);
        verify(jobNodeStorage, times(0)).isJobNodeExisted("sharding/0/running");
        verify(jobNodeStorage, times(0)).isJobNodeExisted("sharding/1/running");
        verify(jobNodeStorage, times(0)).isJobNodeExisted("sharding/2/running");
    }
    
    @Test
    public void assertHasRunningItems() {
        when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 4).build(),
                TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        when(jobNodeStorage.isJobNodeExisted("sharding/0/running")).thenReturn(false);
        when(jobNodeStorage.isJobNodeExisted("sharding/1/running")).thenReturn(true);
        assertTrue(executionService.hasRunningItems(Arrays.asList(0, 1, 2)));
        verify(configService).load(true);
        verify(jobNodeStorage).isJobNodeExisted("sharding/0/running");
        verify(jobNodeStorage).isJobNodeExisted("sharding/1/running");
    }
    
    @Test
    public void assertNotHaveRunningItems() {
        when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 4).build(),
                TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        when(jobNodeStorage.isJobNodeExisted("sharding/0/running")).thenReturn(false);
        when(jobNodeStorage.isJobNodeExisted("sharding/1/running")).thenReturn(false);
        when(jobNodeStorage.isJobNodeExisted("sharding/2/running")).thenReturn(false);
        assertFalse(executionService.hasRunningItems(Arrays.asList(0, 1, 2)));
        verify(configService).load(true);
        verify(jobNodeStorage).isJobNodeExisted("sharding/0/running");
        verify(jobNodeStorage).isJobNodeExisted("sharding/1/running");
        verify(jobNodeStorage).isJobNodeExisted("sharding/2/running");
    }
    
    @Test
    public void assertHasRunningItemsForAll() {
        when(configService.load(true)).thenReturn(
                LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).build());
        when(jobNodeStorage.getJobNodeChildrenKeys("sharding")).thenReturn(Arrays.asList("0", "1", "2"));
        when(jobNodeStorage.isJobNodeExisted("sharding/0/running")).thenReturn(false);
        when(jobNodeStorage.isJobNodeExisted("sharding/1/running")).thenReturn(true);
        assertTrue(executionService.hasRunningItems());
    }
    
    @Test
    public void assertNotHaveRunningItemsForAll() {
        when(configService.load(true)).thenReturn(
                LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).build());
        when(jobNodeStorage.getJobNodeChildrenKeys("sharding")).thenReturn(Arrays.asList("0", "1", "2"));
        when(jobNodeStorage.isJobNodeExisted("sharding/0/running")).thenReturn(false);
        when(jobNodeStorage.isJobNodeExisted("sharding/1/running")).thenReturn(false);
        when(jobNodeStorage.isJobNodeExisted("sharding/2/running")).thenReturn(false);
        assertFalse(executionService.hasRunningItems());
    }
    
    private ShardingContexts getShardingContext() {
        Map<Integer, String> map = new HashMap<>(3, 1);
        map.put(0, "");
        map.put(1, "");
        map.put(2, "");
        return new ShardingContexts("fake_task_id", "test_job", 10, "", map);
    }
}