package lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName WatchCallBack
 * @Description TODO
 * @Author paul
 * @Date 2021/3/30 22:08
 * Vertion 1.0
 * -------------------------------------------------------------_ooOoo_
 * ------------------------------------------------------------o8888888o
 * ------------------------------------------------------------88"-.-"88
 * ------------------------------------------------------------(|--_--|)
 * ------------------------------------------------------------O\--=--/O
 * ---------------------------------------------------------____/`---'\____
 * -------------------------------------------------------.'--\\|-----|//--`.
 * ------------------------------------------------------/--\\|||--:--|||//--\
 * -----------------------------------------------------/--_|||||--:--|||||---\
 * -----------------------------------------------------|---|-\\\-----///-|---|
 * -----------------------------------------------------|-\_|--''\---/''--|---|
 * -----------------------------------------------------\--.-\__--`-`--___/-.-/
 * ---------------------------------------------------___`.-.'--/--.--\--`.-.-__
 * ------------------------------------------------.""-'<--`.___\_<|>_/___.'-->'"".
 * -----------------------------------------------|-|-:--`--\`.;`\-_-/`;.`/---`-:-|-|
 * -----------------------------------------------\--\-`-.---\_-__\-/__-_/---.-`-/--/
 * ---------------------------------------======`-.____`-.___\_____/___.-`____.-'======
 * -------------------------------------------------------------`=---='
 * ---------------------------------------^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 * -----------------------------------------------------佛祖保佑--------永无BUG
 */
public class WatchCallBack implements Watcher ,
        AsyncCallback.StringCallback,
        AsyncCallback.Children2Callback,
        AsyncCallback.StatCallback {

    ZooKeeper zk;

    String threadName;

    CountDownLatch cc = new CountDownLatch(1);

    String pathName;

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                //节点被删除,后面的人去判断自己是不是第一个
                zk.getChildren("/",false,this,"sdf");


                break;
            case NodeDataChanged:
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
            case PersistentWatchRemoved:
                break;
        }

    }


    public void tryLock(){
        try {
            System.out.println(threadName +" created ...");
            zk.create("/lock",threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,this,"saf");
            cc.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unLock(){
        try {
            zk.delete(pathName,-1);
            System.out.println(threadName + " over working ...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }


    public void processResult(int rc, String path, Object ctx, String name) {
        if(name!=null){
            System.out.println(threadName + "    create Node :"+name);
            pathName=name;
            System.out.println("pathName--------"+pathName);
            zk.getChildren("/",false,this,"sdf");
        }
    }

    //children2callback
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        //自己创建完了，并且能看到自己前面的节点
//        System.out.println(threadName + " look lock");
//        for (String child : children) {
//            System.out.println(child);
//        }

        Collections.sort(children);
        int index = children.indexOf(pathName.substring(1));
                //是不是第一个
        if(index==0){
            //yes
            System.out.println(threadName + " i am first");
            try {
                zk.setData("/",threadName.getBytes(),-1);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cc.countDown();
        }
        else{
            //no
            //取出前一个节点,第一个this监控前一个节点，第二个this监控状态回调；前面节点发生删除会回调process

            zk.exists("/"+children.get(index-1),this,this,"sdf");
        }





    }

    //stateCallBack
    public void processResult(int rc, String path, Object ctx, Stat stat) {

    }
}

