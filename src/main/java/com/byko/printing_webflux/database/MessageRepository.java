package com.byko.printing_webflux.database;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MessageRepository extends ReactiveMongoRepository<MessageData, String> {
    Flux<MessageData> findAllByProjectId(String projectId);
    Flux<MessageData> findAll();
}
