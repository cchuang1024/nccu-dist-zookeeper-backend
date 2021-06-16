package edu.nccu.controller;

import edu.nccu.domain.HostResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceQueryController {

    @GetMapping("/media-host")
    public HostResponse queryHost() {
        return new HostResponse("media-host");
    }
}
