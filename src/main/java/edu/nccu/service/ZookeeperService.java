package edu.nccu.service;

import java.util.concurrent.Semaphore;

import edu.nccu.component.ZkClients;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static edu.nccu.config.Constant.PATH_HOST;
import static edu.nccu.config.Constant.PATH_ID;

@Service
@Slf4j
public class ZookeeperService {

    private ZkClient zkClient;
    private Semaphore mutex;

    @Autowired
    public ZookeeperService(ZkClients zkClients) {
        this.zkClient = zkClients.getOne(ZookeeperService.class.getName());
        this.mutex = new Semaphore(1);
    }


    public void createHostNode(String mediaHost) {
        try{
            mutex.acquire();

            zkClient.createEphemeral(PATH_HOST, mediaHost);
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }finally {
            mutex.release();
        }
    }

    public void createIdNode(String hostId) {
        try{
            mutex.acquire();

            zkClient.createEphemeral(PATH_ID, hostId);
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }finally {
            mutex.release();
        }
    }

    public void createParentNode(String parentDir) {
        try{
            mutex.acquire();

            zkClient.createPersistent(parentDir, true);
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }finally {
            mutex.release();
        }
    }

    public void destroy() {
        try{
            mutex.acquire();

            this.zkClient.delete(PATH_ID);
            this.zkClient.delete(PATH_HOST);
            this.zkClient.close();
        } catch (InterruptedException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }finally {
            mutex.release();
        }
    }

    public void cleanHost() {
        zkClient.delete(PATH_ID);
        zkClient.delete(PATH_HOST);
    }

    public boolean isHostExists() {
        return zkClient.exists(PATH_HOST);
    }

    public boolean isIdExists() {
        return zkClient.exists(PATH_ID);
    }

    public String readId() {
        return zkClient.readData(PATH_ID).toString();
    }

    public String readHost() {
        return zkClient.readData(PATH_HOST).toString();
    }
}
