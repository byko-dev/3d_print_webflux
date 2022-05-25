package com.byko.printing_webflux.services;

import com.byko.printing_webflux.database.PhotoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsResource;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.InputStream;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@AllArgsConstructor
public class PhotoService {

    private ReactiveGridFsTemplate reactiveGridFsTemplate;
    private PhotoRepository photoRepository;

    public Flux<InputStream> getPhotoInputStream(String fileId){
        return this.reactiveGridFsTemplate.findOne(query(where("_id").is(fileId)))
                .flatMap(reactiveGridFsTemplate::getResource)
                .flatMapMany(ReactiveGridFsResource::getInputStream);
    }
}
