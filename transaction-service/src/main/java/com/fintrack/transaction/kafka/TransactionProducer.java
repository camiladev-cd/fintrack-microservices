package com.fintrack.transaction.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionProducer {

    private static final String TOPIC = "transaction-created";

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public void publishTransactionCreated(TransactionEvent event) {
        kafkaTemplate.send(TOPIC, String.valueOf(event.transactionId()), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Transaction event published — transactionId={}, offset={}",
                                event.transactionId(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish transaction event — transactionId={}: {}",
                                event.transactionId(), ex.getMessage());
                    }
                });
    }
}