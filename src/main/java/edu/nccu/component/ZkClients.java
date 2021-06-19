package edu.nccu.component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class ZkClients {

    @Value("${zookeeper.connectString}")
    private String connectString;

    private Map<String, ZkClient> registry;

    public ZkClients(){
        this.registry = Collections.synchronizedMap(new HashMap<>());
    }

    public synchronized ZkClient getOne(String className){
        if(!this.registry.containsKey(className)){
            ZkConnection zkConn = new ZkConnection(connectString);
            this.registry.put(className, new ZkClient(zkConn));
        }

        return this.registry.get(className);
    }

}
