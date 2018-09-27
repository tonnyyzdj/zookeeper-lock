package com.zdj.zookeeperlock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zhangdanjiang
 * @description
 * @date 2018/9/26
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WatchTest {
    private static final String ZOOKEEPER_IP_PORT = "localhost:2181";

    private ZkClient client = new ZkClient(ZOOKEEPER_IP_PORT, 1000, 1000, new SerializableSerializer());

    @Test
    public void watchTest() throws InterruptedException {
        //1.创建一个持久节点
        String path = "/watchr";
        client.createPersistent(path);

        //2.实例化一个监听器
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                //捕获到节点被删除的事件
                System.out.println("收到节点被删除事件，被删除的节点为："+ dataPath);
            }

            @Override
            public void handleDataChange(String s,Object o) throws Exception {

            }
        };
        //3,给该节点增加监听器
        this.client.subscribeDataChanges(path, listener);
        Thread.currentThread().join();

    }
}
