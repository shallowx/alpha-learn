package com.alpha.learn.spring6;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;

import java.util.Map;

@Slf4j
public class TestProducerInterceptor implements ProducerInterceptor<String, String> {
    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        Headers headers = record.headers();
        log.info("headers: {}", headers);
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        log.info("acknowledgement: {}", metadata);
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {
        log.info("configure: {}", configs);
    }
}
