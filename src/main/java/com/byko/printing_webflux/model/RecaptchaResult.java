package com.byko.printing_webflux.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class RecaptchaResult {
    public boolean success;
    public String challenge_ts;
    public String hostname;
    public float score;
    public String action;
}
