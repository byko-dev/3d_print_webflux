package com.byko.printing_webflux.database;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends ReactiveMongoRepository<PhotoData, String> {
}
