package com.zdj.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangdanjiang
 * @description
 * @date 2019/1/18
 */
public class DistributeClient {

    public static void main(String[] args) throws Exception {
        //1获取zookeeper 集群连接
        DistributeClient client = new DistributeClient();
        client.getConnect();

        //2注册监听

        client.getChildren();

        //3业务逻辑处理
        client.business();

    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void getChildren() throws KeeperException, InterruptedException {
        List<String> children = zkClient.getChildren("/servers", true);
        //存储服务器节点主机名称集合
        ArrayList<String> hosts = new ArrayList<>();
        for (String child : children) {
            byte[] data = zkClient.getData("/servers/" + child, false, null);
            hosts.add(new String(data));
        }
        // 将所有在线主机名称打印到控制台
        System.out.println(hosts);

    }


    private String connectString = "localhost:2181";
    private int sessionTimeOut = 2000;
    ZooKeeper zkClient ;
    private void getConnect() throws IOException {
        zkClient = new ZooKeeper(connectString, sessionTimeOut, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try {
                    getChildren();
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
