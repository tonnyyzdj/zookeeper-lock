package com.zdj.zookeeperlock;

import com.zdj.zookeeperlock.lock.ZookeeperDistributedLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zhangdanjiang
 * @description
 * @date 2018/9/27
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TicketTest {

    private int count = 10;

//    @Resource
//    private Lock lock ;



    @Test
    public void ticketTest() throws Exception{
        TicketRunable tr = new TicketRunable();

        Thread t1 = new Thread(tr, "窗口A");
        Thread t2 = new Thread(tr, "窗口B");
        Thread t3 = new Thread(tr, "窗口C");
        Thread t4 = new Thread(tr, "窗口D");

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        Thread.currentThread().join();
    }


    public class TicketRunable implements Runnable {

        @Override
        public void run() {
            while(count > 0){
                Lock lock = new ZookeeperDistributedLock();
                lock.lock() ;
                try{
                    if(count > 0){
                        System.out.println(Thread.currentThread().getName() + "售出第" + (count--) + "张票");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    lock.unlock();
                }

                try{
                    Thread.sleep(200);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

}

