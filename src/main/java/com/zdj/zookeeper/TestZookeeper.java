package com.zdj.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author zhangdanjiang
 * @description
 * @date 2019/1/17
 */
public class TestZookeeper {
    private String connectString = "localhost:2181";
    private int sessionTimeOut = 2000;
    private ZooKeeper zkClient;

    @Before
    public void init() throws IOException {
        zkClient = new ZooKeeper(connectString, sessionTimeOut, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
//                System.out.println("---------------start------------");
//                List<String> children = null;
//                try {
//                    children = zkClient.getChildren("/", true);
//                } catch (KeeperException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                for (String child : children) {
//                    System.out.println(child);
//                }
//                System.out.println("---------------end------------");
            }
        });
    }

    //创建节点
    @Test
    public void createNode() throws KeeperException, InterruptedException {
        String path = zkClient.create("/zdjnode", "zdjdata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(path);
    }

    //获取子节点，并监控节点的变化
    @Test
    public void getDataAndWatch() throws KeeperException, InterruptedException {
        List<String> children = zkClient.getChildren("/", true);
        for (String child : children) {
            System.out.println(child);
        }
        Thread.sleep(Long.MAX_VALUE);

    }

    //判断节点是否存在
    @Test
    public void exist() throws KeeperException, InterruptedException {
        Stat stat = zkClient.exists("/zdj", false);
        System.out.println(stat==null?"not exist":"exists");
    }
}
