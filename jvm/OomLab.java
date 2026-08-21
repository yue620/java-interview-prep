package jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * OomLab —— Day 6 实验一：OOM 复现 + GC 日志观察 + jmap 分析
 *
 * 运行前配置 VM options（IDEA → Edit Configurations → Modify options → Add VM options）：
 *   -Xmx20m -Xlog:gc*
 *
 * 【GC 日志观察结论】（任务 1，跑完填这里）：
 *   OOM 之前 GC 日志里发生了什么？
 *
 *
 * 【jmap -histo 证据】（任务 2，把 [B 那一行粘贴到这里）：
 *
 *   为什么 byte 数组这么多？对应代码哪一行？
 *
 */
public class OomLab {

    public static void main(String[] args) throws InterruptedException {
        List<byte[]> holder = new ArrayList<>();   // holder 拽住所有数组 → 全部可达 → 无法回收

        while (true) {
            holder.add(new byte[1024 * 1024]);     // 每次 new 1MB，无限塞
            System.out.println("已分配 " + holder.size() + " MB");
            // 任务 2 提示：做 jmap 分析时，先取消下面这行注释拖慢速度，
            // 不然 20MB 几毫秒就满了，来不及 jmap
             Thread.sleep(5000);
        }
    }
}
