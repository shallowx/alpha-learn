package com.alpha.learn.spring6;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@SuppressWarnings("ALL")
@Slf4j
public class KafkaTests {

    private static final String TEST_TOPIC = "test_for_jimmy";

    @Test
    public void testCreateTopic() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        try (AdminClient client = AdminClient.create(props)) {
            NewTopic topic = new NewTopic(TEST_TOPIC, 3, (short) 1);
            client.createTopics(Collections.singletonList(topic));
        }
    }

    @Test
    public void testProducer() throws InterruptedException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "com.alpha.learn.spring6.TestPartitioner");
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, "com.alpha.learn.spring6.TestProducerInterceptor");
        props.put(ProducerConfig.SEND_BUFFER_CONFIG, "65536");
        props.put(ProducerConfig.RECEIVE_BUFFER_CONFIG, "65536");
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "1024");
        props.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "5");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try(KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            List<PartitionInfo> partitions = producer.partitionsFor(TEST_TOPIC);
            log.info("Partitions: {}",  partitions);

            List<Header> topicHeaders = new ArrayList<>();
            topicHeaders.add(new RecordHeader("test_header", "jimmy".getBytes(StandardCharsets.UTF_8)));
            ProducerRecord<String, String> record = new ProducerRecord<>(TEST_TOPIC, null, null, "key_for_", "world", topicHeaders);
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        log.error(exception.getMessage(), exception);
                    } else {
                        long offset = metadata.offset();
                        long timestamp = metadata.timestamp();
                        int partition = metadata.partition();
                        log.info("kafka server response with offset: {}, timestamp: {}, partition: {}", offset, timestamp, partition);
                        countDownLatch.countDown();
                    }
                }
            });
        }
        countDownLatch.await();
    }

    @Test
    public void testConsumer() throws InterruptedException {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test_consumer");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton(TEST_TOPIC), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                consumer.commitSync();
                log.info("Partitions: {}",  partitions);
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                log.info("Partitions: {}",  partitions);
            }
        });

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1));
        Iterator<ConsumerRecord<String, String>> iterator = records.iterator();
        while (iterator.hasNext()) {
            ConsumerRecord<String, String> record = iterator.next();
            String key = record.key();
            String value = record.value();
            consumer.commitSync();
            log.info("key: {}, value: {}", key, value);
        }

        for (int i = 0; i < 4; i++) {
            new ConsumerThread(consumer).start();
        }
    }

    static class ConsumerThread extends Thread {
        private final KafkaConsumer<String, String> consumer;
        public ConsumerThread(KafkaConsumer<String, String> consumer) {
            this.consumer = consumer;
            this.consumer.subscribe(Collections.singleton(TEST_TOPIC));
        }

        @Override
        public void run() {
            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1));
                    for (ConsumerRecord<String, String> record : records) {
                        log.info("key: {}, value: {}", record.key(), record.value());
                    }
                }
            }finally {
                consumer.close();
            }
        }
    }
}
