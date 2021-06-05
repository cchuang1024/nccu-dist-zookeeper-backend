package edu.nccu.coordinator;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MapReduceCoordinator {

    @Autowired
    private CuratorFramework zkClient;


    public CompletableFuture<Integer> squareAndSum(List<Integer> numbers) {
        return null;
    }
}
