package com.byko.printing_webflux.controllers;

import com.byko.printing_webflux.enums.User;
import com.byko.printing_webflux.database.*;
import com.byko.printing_webflux.model.Message;
import com.byko.printing_webflux.model.Project;
import com.byko.printing_webflux.services.FileService;
import com.byko.printing_webflux.services.PhotoService;
import com.byko.printing_webflux.services.Utils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;


@RestController
@CrossOrigin(origins = "*") //only for development purpose
@AllArgsConstructor
public class ClientController {

    private ProjectRepository projectRepository;
    private FileService fileService;
    private MessageRepository messageRepository;
    private PhotoRepository photoRepository;
    private PhotoService photoService;

    //Reactive Spring haven't MultipartHttpServletRequest equivalent
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value = "/create/project")
    public Mono<ResponseEntity<String>> createProject(@RequestPart("file") Mono<FilePart> filePartMono,
                                         @RequestPart("captcha") String captcha,
                                         @RequestPart("nameAndLastName") String nameAndLastName,
                                         @RequestPart("email") String email,
                                         @RequestPart("address") String address,
                                         @RequestPart("phoneNumber") String phoneNumber,
                                         @RequestPart("description") String description,
                                         ServerHttpRequest request) {

        //TODO: captcha validator
        if(Utils.valid(captcha, nameAndLastName, address, description)
            && Utils.numberRegEx(phoneNumber) && Utils.emailRegEx(email)){
            return fileService.uploadFile(filePartMono).flatMap(fileId ->
                    projectRepository.save(new ProjectData(null, nameAndLastName, address,
                    phoneNumber, email, description, fileId,
                    Utils.getCurrentDate(), 1, request.getRemoteAddress().getAddress().toString()))
                            .flatMap(project -> Mono.just(new ResponseEntity<>(project.getId(), HttpStatus.OK))));
        }
        return Mono.just(new ResponseEntity<>("BAD REQUEST", HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/download")
    public Flux<Void> downloadFile(@RequestParam("projectid") String id, ServerHttpResponse response){
        return fileService.downloadFile(id, response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value = "/send/response")
    public Mono<ResponseEntity<String>> sendMessage(@RequestPart("file") Mono<FilePart> filePartMono,
                               @RequestPart("captcha") String captcha,
                               @RequestPart("content") String content,
                               @RequestPart("projectId") String projectId,
                               ServerHttpRequest request){
        //TODO: captcha validator
        if(Utils.valid(captcha, content, projectId)){

            return fileService.uploadFile(filePartMono)
                    .flatMap(fileId -> messageRepository.save(new MessageData(null, content, fileId, Utils.getCurrentDate(),
                            User.CLIENT, request.getRemoteAddress().getAddress().toString(), projectId, null))
                            .flatMap((e) -> Mono.just(new ResponseEntity<>("OK", HttpStatus.OK))));
        }
        return Mono.just(new ResponseEntity<>("BAD REQUEST", HttpStatus.BAD_REQUEST));
    }


    @GetMapping("/project/conversation")
    public Flux<Message> getProjectMessages(@RequestParam("projectid") String projectId){
        return messageRepository.findAllByProjectId(projectId)
                .flatMap(messageData ->
                        fileService.getFileTitle(messageData.getFileId())
                                .map(title -> new Message(messageData.getMessageContent(),
                                        messageData.getFileId(), title, messageData.getDate(), messageData.getUser())));
    }

    @GetMapping("/project/data")
    public Mono<Project> getProjectData(@RequestParam("projectid") String projectId){

        return projectRepository.findById(projectId)
                .flatMap(projectData -> fileService.getFileTitle(projectData.getProjectFileId())
                        .flatMap(title -> Mono.just(new Project(projectData.getId(), projectData.getNameAndLastName(),
                                projectData.getAddress(), projectData.getPhoneNumber(), projectData.getEmail(),
                                projectData.getDescription(), projectData.getProjectFileId(), projectData.getDate(),
                                projectData.getOrderStatus(), projectData.getIpAddress(), title))));
    }

    @GetMapping("/images")
    public Flux<PhotoData> getAllImagesData(){
        return photoRepository.findAll();
    }

    @GetMapping("/image")
    public Flux<InputStream> getImageFile(@RequestParam("imageid") String imageId){
        return photoService.getPhotoInputStream(imageId);
    }
}
