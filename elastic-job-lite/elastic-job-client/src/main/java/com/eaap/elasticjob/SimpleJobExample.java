package com.eaap.elasticjob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

public class SimpleJobExample implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println("simple job example has executed !");
    }
}
