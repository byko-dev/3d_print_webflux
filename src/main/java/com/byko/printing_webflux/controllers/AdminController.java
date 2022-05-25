package com.byko.printing_webflux.controllers;

import com.byko.printing_webflux.database.*;
import com.byko.printing_webflux.enums.Role;
import com.byko.printing_webflux.enums.User;
import com.byko.printing_webflux.model.*;
import com.byko.printing_webflux.security.JWTUtil;
import com.byko.printing_webflux.services.FileService;
import com.byko.printing_webflux.services.Utils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*") //only for development purpose
@AllArgsConstructor
public class AdminController {

    private ProjectRepository projectRepository;
    private AdminRepository adminRepository;
    private JWTUtil jwtUtil;
    private BCryptPasswordEncoder encoder;
    private FileService fileService;
    private MessageRepository messageRepository;
    private PhotoRepository photoRepository;

    @GetMapping("/activity")
    public Mono<ResponseEntity<LastTimeActivityResponse>> getLastTimeAdminActivity(){
        return adminRepository.findFirstByOrderByLastTimeActivityDesc()
                .flatMap(adminData -> Mono.just(new ResponseEntity<>(
                        new LastTimeActivityResponse(adminData.getLastTimeActivity(), adminData.getNameAndLastName(),
                                adminData.getPhotoId()), HttpStatus.OK)));
    }

    @GetMapping("/valid")
    public Mono<Status> checkTokenIsValid(){return Mono.just(new Status("OK"));}

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest authRequest){
        return adminRepository.findByUsername(authRequest.getUsername())
                .filter(userDetails -> encoder.matches(authRequest.getPassword(), userDetails.getPassword()))
                .map(userDetails ->
                        ResponseEntity.ok(new AuthResponse(jwtUtil.generateToken(Role.ROLE_ADMIN, userDetails.getUsername()))))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }

    @PostMapping("/change/password")
    public Mono<ResponseEntity<Status>> adminChangePasswordRequest(@RequestBody @Valid ChangePasswordRequest changePasswordRequest, ServerHttpRequest request){
        return jwtUtil.returnAdminId(request, adminRepository)
                .flatMap(adminId -> adminRepository.findById(adminId)
                        .flatMap(adminData -> {
                            if (encoder.matches(changePasswordRequest.getPassword(), adminData.getPassword())){
                                adminData.setPassword(encoder.encode(changePasswordRequest.newPassword));
                                return adminRepository.save(adminData)
                                        .then(Mono.just(new ResponseEntity<>(new Status("OK"), HttpStatus.OK)));
                            }
                            return Mono.just(new ResponseEntity<>(new Status("Wrong Password!"), HttpStatus.BAD_REQUEST));
                        }));
    }


    @GetMapping("/projects/list")
    public Flux<ProjectData> getProjectsList(){
        return projectRepository.findAll();
    }

    @PostMapping("/change/project/status")
    public Mono<ResponseEntity<Status>> changeProjectStatus(@RequestBody @Valid ChangeProjectStatusRequest request){
        return projectRepository.findById(request.getProjectId())
                .flatMap(projectData -> {
                    projectData.setOrderStatus(request.getNewStatus());
                    return projectRepository.save(projectData)
                            .then(Mono.just(new ResponseEntity<>(new Status("OK"), HttpStatus.OK)));
                });
    }

    @PostMapping(value = "/admin/send/response", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Status>> sendMessageAdmin(@RequestPart("file") Mono<FilePart> filePartMono,
                                                         @RequestPart("content") String content,
                                                         @RequestPart("projectid") String projectId,
                                                         ServerHttpRequest request){
        if(Utils.valid(content, projectId)){
            return fileService.uploadFile(filePartMono)
                    .flatMap(fileId ->
                            jwtUtil.returnAdminId(request, adminRepository)
                                    .flatMap(adminId -> {
                                        messageRepository.save(new MessageData(null,
                                                content, fileId, Utils.getCurrentDate(), User.ADMIN,
                                                request.getRemoteAddress().getAddress().toString(),
                                                projectId, adminId));
                                        return Mono.just(new ResponseEntity<>(new Status("OK"), HttpStatus.OK));
                                    }));
        }
        return Mono.just(new ResponseEntity<>(new Status("BAD REQUEST"), HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/remove/project")
    public Mono<ResponseEntity<Status>> removeProject(@RequestParam("projectid") String projectId){

        return projectRepository.findById(projectId)
                .flatMap(projectData -> {
                    fileService.deleteFile(projectData.getProjectFileId());
                    projectRepository.delete(projectData);

                    return messageRepository.findAllByProjectId(projectData.getId())
                            .flatMap(messageData -> {
                                messageRepository.delete(messageData);
                                return fileService.deleteFile(messageData.getFileId());
                            }).then(Mono.just(new ResponseEntity<>(new Status("OK"), HttpStatus.OK)));
                });
    }


    @PostMapping(value = "/image/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Status>> addPhoto(@RequestPart("file") Mono<FilePart> filePartMono,
                                                 @RequestPart("description") String description,
                                                 @RequestPart("title") String title,
                                                 @RequestPart("alt") String alt){
        if(Utils.valid(description, title, alt)){
            return fileService.uploadFile(filePartMono)
                    .filter(filePart -> !filePart.isEmpty())
                    .flatMap(fileId -> {
                        photoRepository.save(new PhotoData(null, title, alt, description,
                                Utils.getCurrentDate(), fileId));
                        return Mono.just(new ResponseEntity<>(new Status("OK"), HttpStatus.OK));
                    })
                    .switchIfEmpty(Mono.just(new ResponseEntity<>(new Status("BAD REQUEST"), HttpStatus.BAD_REQUEST)));
        }
        return Mono.just(new ResponseEntity<>(new Status("BAD REQUEST"), HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/image/delete")
    public Mono<Void> deleteImage(@RequestParam("imageid") String imageId){
        return fileService.deleteFile(imageId);
    }

    @PostMapping("/image/update")
    public Mono<ResponseEntity<Status>> updateImageData(@RequestBody ImageUpdateRequest imageUpdateRequest){

        return photoRepository.findById(imageUpdateRequest.getId())
                .flatMap(photoData -> {
                   if(imageUpdateRequest.isChangeData()) photoData.setDate(Utils.getCurrentDate());
                   if(Utils.valid(imageUpdateRequest.getTitle())) photoData.setTitle(imageUpdateRequest.getTitle());
                   if(Utils.valid(imageUpdateRequest.getDescription())) photoData.setDescription(imageUpdateRequest.getDescription());
                   if(Utils.valid(imageUpdateRequest.getAlt())) photoData.setAlt(imageUpdateRequest.getAlt());

                   return photoRepository.save(photoData)
                           .flatMap(e -> Mono.just(new ResponseEntity<>(new Status("Ok"), HttpStatus.OK)));

                })
                .switchIfEmpty(Mono.just(new ResponseEntity<>(new Status("BAD REQUEST"), HttpStatus.BAD_REQUEST)));
    }

}
