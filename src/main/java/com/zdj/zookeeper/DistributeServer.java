package com.zdj.zookeeper;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * @author zhangdanjiang
 * @description 服务器动态节点上下线demo
 * @date 2019/1/18
 */
public class DistributeServer {

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        DistributeServer server = new DistributeServer();

        //1 连接zookeeper集群
        server.getConnect();

        //2 注册节点
        server.regist(args[0]);

        //3业务逻辑处理
        server.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void regist(String hostname) throws KeeperException, InterruptedException {
        String path = zkClient.create("/servers/server", hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        System.out.println(hostname + " is online");
    }

    private String connectString = "localhost:2181";
    private int sessionTimeOut = 2000;
    ZooKeeper zkClient ;
    private void getConnect() throws IOException {
        zkClient = new ZooKeeper(connectString, sessionTimeOut, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }
}
