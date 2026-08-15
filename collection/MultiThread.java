package collection;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

class CookThread extends Thread{
    public void  run(){
        System.out.println("在新线程做菜");
    }
}


public class MultiThread {

    static int count = 0;
    static final Object lock = new Object();
    static volatile boolean running = true;
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        // 创建一个新线程去"做菜"
////        new Thread(() -> {
////            for (int i = 0; i < 3; i++) {
////                System.out.println("做菜 " + i);
////            }
////        }).start();
//
////        new CookThread().start();
//
//        Callable<String> task = () -> {
//            Thread.sleep(1000);
//            System.out.println("在新线程做菜");
//            return "欧耶";
//        };
//        FutureTask<String> ft = new FutureTask<>(task);
//        new Thread(ft).start();
//        // 主线程继续"上菜"
//        for (int i = 0; i < 3; i++) {
//            System.out.println("上菜 " + i);
//        }
//        String result = ft.get();
//        System.out.println(result);
//        Thread t = new Thread(() -> {
//            System.out.println("run 方法运行在: " + Thread.currentThread().getName());
//        }, "我的新线程");

//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("run 方法运行在: " + Thread.currentThread().getName());
//            }
//        },"我的线程名");
//
//        t.run();     // 直接调 run()
//        t.start();   // 调 start()
           // 共享的账本


//            Runnable add = () -> {
//                for (int i = 0; i < 10000; i++) {
//                    synchronized(lock){
//                        count++;
//                    }
//                        // 两个线程都在对同一个 count +1
//                }
//            };
//
//            Thread t1 = new Thread(add);
//            Thread t2 = new Thread(add);
//            Thread t3 = new Thread(add);
//            t1.start();
//            t2.start();
//            t3.start();
//            t1.join();          // join = 等 t1 干完
//            t2.join();          // 等 t2 干完
//            t3.join();
//
//            System.out.println("期望 20000，实际: " + count);

//            Runnable r = () -> {
//                synchronized (lock) {
//                    System.out.println(Thread.currentThread().getName() + " 拿到锁，睡 2 秒");
//                    try {
//                        Thread.sleep(2000);     // 睡觉，但锁还抱在怀里！
//                    } catch (InterruptedException e) { }
//                    System.out.println(Thread.currentThread().getName() + " 醒了，放锁");
//                }
//            };
//            new Thread(r, "线程A").start();
//            new Thread(r, "线程B").start();
//
        new Thread(() -> {
            System.out.println("子线程开始转圈，等 running 变 false");
            while (running) { }       // 一直读缓存里的 true，可能永远出不来
            System.out.println("子线程结束");
        }).start();

        Thread.sleep(1000);
        running = false;              // main 线程改了值
        System.out.println("main 已把 running 改为 false");

    }
}

