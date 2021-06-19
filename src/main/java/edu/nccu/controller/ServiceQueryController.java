package edu.nccu.controller;

import edu.nccu.domain.HostResponse;
import edu.nccu.service.ZookeeperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceQueryController {

    private ZookeeperService zkService;

    @Autowired
    public ServiceQueryController(ZookeeperService zkService) {
        this.zkService = zkService;
    }

    @GetMapping("/media-host")
    public HostResponse queryHost() {
        String host = zkService.readHost();
        return new HostResponse(host);
    }
}
