package com.byko.printing_webflux.services;

import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class FileService {

    private ReactiveGridFsTemplate gridFsTemplate;

    public FileService(ReactiveGridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    public Flux<Void> downloadFile(String fileId, ServerHttpResponse response){
        return this.gridFsTemplate.findOne(query(where("_id").is(fileId)))
                .flatMap(gridFsTemplate::getResource)
                .flatMapMany(resource -> {
                    response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename="+resource.getFilename());
                    response.getHeaders().setContentType(MediaType.
                            APPLICATION_OCTET_STREAM);
                    return response.writeWith(resource.getDownloadStream());
                });
    }

    public Mono<String> getFileTitle(String fileId){
        return this.gridFsTemplate.findOne(query(where("_id").is(fileId)))
                .flatMap(file -> Mono.just(file.getFilename()));
    }

    public Mono<String> uploadFile(Mono<FilePart> fileParts){
        return fileParts
                .filter(filterPart -> !filterPart.filename().isBlank())
                .flatMap(part -> this.gridFsTemplate.store(part.content(), part.filename(),
                        String.valueOf(part.headers().getContentType()), new BasicDBObject("title", part.filename())))
                .map((id) -> id.toString())
                .switchIfEmpty(Mono.just(""));
    }

    public Mono<Void> deleteFile(String fileId){
        return this.gridFsTemplate.delete(query(where("_id").is(fileId)));
    }
}
