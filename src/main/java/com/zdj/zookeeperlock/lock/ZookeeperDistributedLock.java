package com.zdj.zookeeperlock.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.pattern.PathPattern;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author zhangdanjiang
 * @description
 * @date 2018/9/27
 */
@Service("lock")
public class ZookeeperDistributedLock implements Lock {

    private static final String LOCK_PATH = "/LOCK";

    private static final String ZOOKEEPER_IP_PORT = "localhost:2181";

    private ZkClient client = new ZkClient(ZOOKEEPER_IP_PORT, 1000, 1000, new SerializableSerializer());

    private static Logger logger = LoggerFactory.getLogger(ZookeeperDistributedLock.class);

    private CountDownLatch cdl;

    private String beforePath;//当前请求的节点前一个节点

    private String currentPath;//当前请求节点

    //判断有没有LOCK目录，没有则创建
    public ZookeeperDistributedLock() {
        if(!this.client.exists(LOCK_PATH)){
            this.client.createPersistent(LOCK_PATH);
        }
    }


    @Override
    public void lock() {
        if (!tryLock()) {
            waitForLock();
            lock();
        } else {
            logger.info(Thread.currentThread().getName() +" 获取分布式锁！" );
        }
    }

    private void waitForLock() {
        IZkDataListener listener =  new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {

            }
            @Override
            public void handleDataDeleted(String s) throws Exception {
                //捕获到前置节点被删除的事件以后，发令枪countdown,让主线程停止等待
                logger.info(Thread.currentThread().getName()+":捕获到DateDelete事件！---------------");
                if (cdl != null) {
                    cdl.countDown();
                }
            }
        };
        //给排在前面的节点增加数据删除的watcher,本质是启动另外一个线程去监听前置节点
        this.client.subscribeDataChanges(beforePath, listener);
        if (this.client.exists(beforePath)) {
            cdl = new CountDownLatch(1);
            try{
                cdl.await();
            }catch (InterruptedException e){
                logger.error(e.getMessage());
            }
        }
        this.client.unsubscribeDataChanges(beforePath,listener);
    }



    @Override
    public boolean tryLock() {
        //如果currentPath为空，则为第一次尝试加锁，第一次加锁赋值currentPath
        if (currentPath == null || currentPath.length() <= 0) {
            //创建一个临时顺序节点
            currentPath = this.client.createEphemeralSequential(LOCK_PATH + "/" , "lock");
            System.out.println("-------------currentPath="+currentPath);
        }
        //获取所有临时节点并排序，临时节点名称为自增长的字符串，如00000000400
        List<String> childrens = this.client.getChildren(LOCK_PATH );
        Collections.sort(childrens);
//        if(childrens.isEmpty()) {
//            System.out.println("-------------childrens 为空" );
//        }else {
//            StringBuffer sb = new StringBuffer("");
//            for (String children : childrens) {
//                sb.append(children+",");
//            }
//            System.out.println("子节点，"+sb );
//        }

//        System.out.println(currentPath +"============"+LOCK_PATH + '/' + childrens.get(0));
        if (currentPath.equals(LOCK_PATH + '/' + childrens.get(0))) {
            return true;
        }else{//如果当前节点在所有节点中排名不是第一，则获取前面的节点名称，并赋值给beforePath
            int wz = Collections.binarySearch(childrens,currentPath.substring(6));
            beforePath = LOCK_PATH + "/" + childrens.get(wz - 1);
        }
        return false;
    }

    @Override
    public void unlock() {
        client.delete(currentPath);
    }


    @Override
    public void lockInterruptibly() throws InterruptedException {

    }
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }



    @Override
    public Condition newCondition() {
        return null;
    }
}
