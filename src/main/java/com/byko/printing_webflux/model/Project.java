package com.byko.printing_webflux.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Project {
    private String id;
    private String nameAndLastName;
    private String address;
    private String phoneNumber;
    private String email;
    private String description;
    private String projectFileId;
    private String date;
    private int orderStatus;
    private String ipAddress;
    private String fileName;
}
