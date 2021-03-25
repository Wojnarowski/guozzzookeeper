import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName APP
 * @Description TODO
 * @Author paul
 * @Date 2021/3/25 23:18
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
public class APP {

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        System.out.println( "Hello World!" );

        final CountDownLatch cd = new CountDownLatch(1);

        final ZooKeeper zk = new ZooKeeper("10.0.0.40:2181,10.0.0.41:2181,10.0.0.42:2181",
                3000, new Watcher() {
            public void process(WatchedEvent event) {
            //Watch 的回调方法！
                Event.KeeperState state = event.getState();
                Event.EventType type = event.getType();
                String path = event.getPath();
                System.out.println("new zk watch: "+ event.toString());
                switch (state) {
                    case Unknown:
                        break;
                    case Disconnected:
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
                        System.out.println("connected");
                        cd.countDown();
                        break;
                    case AuthFailed:
                        break;
                    case ConnectedReadOnly:
                        break;
                    case SaslAuthenticated:
                        break;
                    case Expired:
                        break;
                    case Closed:
                        break;
                }

                switch (type) {
                    case None:
                        break;
                    case NodeCreated:
                        break;
                    case NodeDeleted:
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
        });

        cd.await();
        final ZooKeeper.States state = zk.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("ing......");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("ed........");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }

        String pathName =zk.create("/ooxx","oldData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        final Stat stat = new Stat();
        byte[] node = zk.getData("/ooxx", new Watcher() {
            public void process(WatchedEvent event) {
                System.out.println("getData watch: "+event.toString());
                try {
                    zk.getData("/ooxx",this  ,stat);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },stat);

        System.out.println(new String(node));

        //触发回调
        Stat stat1 = zk.setData("/ooxx", "newdata".getBytes(), 0);
        //还会触发吗？
        Stat stat2 = zk.setData("/ooxx", "newdata01".getBytes(), stat1.getVersion());

        System.out.println("-------async start----------");
//        zk.getData("/ooxx", false, new AsyncCallback.DataCallback(){
//
//            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
//                System.out.println("-------async call back----------");
//                System.out.println(ctx.toString());
//                System.out.println(" fdsa "+new  String(data));
//            }
//        },"abc");
        zk.getData("/ooxx",false, new AsyncCallback.DataCallback() {
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println("-------async call back----------");
                System.out.println("-------async call back----------"+ctx.toString());
                System.out.println("-------async call back----------"+new String(data));
            }
        },"abc");
        System.out.println("-------async over----------");


        Thread.sleep(2222222);
    }
}

