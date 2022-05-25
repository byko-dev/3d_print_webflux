package com.byko.printing_webflux.database;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface AdminRepository extends ReactiveMongoRepository<AdminData, String> {
    Mono<AdminData> findByUsername(String username);
    Mono<AdminData> findFirstByOrderByLastTimeActivityDesc();
    Mono<AdminData> findById(String id);
}
