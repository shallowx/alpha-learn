package com.alpha.learn.spring6;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import java.util.List;
import java.util.Map;

@Slf4j
public class TestPartitioner implements Partitioner {
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        int hash = topic.hashCode() + key.hashCode();
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        return hash %  partitions.size();
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {
        log.info("configure: {}", configs);
    }
}