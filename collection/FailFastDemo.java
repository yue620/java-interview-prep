package collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * FailFastDemo —— Day 2 实验一：亲手制造并修复 ConcurrentModificationException
 *
 * 对应需求：collection/ISSUE-002-failfast与String实验.md
 * 对应讲义：notes/day02-集合与String/01-学习讲义.md §五
 */
public class FailFastDemo {

    public static void main(String[] args) {
        // 任务 1：先运行 reproduce()，亲眼看到异常抛出来
        // 把异常信息复制到 reproduce 方法的注释里留证后，再把这行注释掉
//        reproduce();

        // 任务 2：三个修复方法，全部应正常打印删除后的结果
        System.out.println("迭代器修复：" + fixByIterator());      // 期望 [a, b, d, e]
        System.out.println("removeIf 修复：" + fixByRemoveIf());    // 期望 [a, b, d, e]
        System.out.println("for-i 倒序修复：" + fixByIndex());      // 期望 [a, b, d, e]
    }

    /** 造一份测试数据：["a", "b", "c", "d", "e"] */
    private static List<String> sampleList() {
        return new ArrayList<>(List.of("a", "b", "c", "d", "e"));
    }

    // ==================== 任务 1：复现异常 ====================

    /**
     * TODO 1：用增强 for 循环遍历 list，遇到 "c" 时调用 list.remove("c")
     *
     * 运行后应该抛出 ConcurrentModificationException。
     *
     * 【在这里粘贴你实际看到的异常信息，作为"亲眼见过"的证据】：
     * Exception in thread "main" java.util.ConcurrentModificationException
     * 	at java.base/java.util.ArrayList$Itr.checkForComodification(ArrayList.java:1096)
     * 	at java.base/java.util.ArrayList$Itr.next(ArrayList.java:1050)
     * 	at collection.FailFastDemo.reproduce(FailFastDemo.java:44)
     * 	at collection.FailFastDemo.main(FailFastDemo.java:18)
     *
     * 思考题（用注释回答）：这个异常是迭代器的哪个方法在什么时候抛出的？
     * next()方法在expectedModCount与 modCount不一致时抛出异常
     */
    private static void reproduce() {
        List<String> list = sampleList();
        for(String l:list){
            if(l.equals("c")){
                list.remove(l);
            }
        }
        // TODO：增强 for 遍历 + remove

    }

    // ==================== 任务 2：三种方式修复 ====================

    /**
     * TODO 2a：用 Iterator 的 remove() 删除 "c"
     *
     * 提示：迭代器自己的 remove 为什么会合法？（回忆 modCount 与 expectedModCount）
     * 注意：必须先 next() 才能 remove()。
     *
     * @return 删除后的 list
     */
    private static List<String> fixByIterator() {
        List<String> list = sampleList();
        Iterator<String> it = list.iterator();
        // TODO：遍历 + 用 it 自己的方法删除 "c"
        //可以怎么替换成for，解释一下iterator的各种方法
        while(it.hasNext()){
            if(it.next().equals("c")) it.remove();
        }

        return list;
    }

    /**
     * TODO 2b：用 removeIf 一行搞定（Java 8+）
     *
     * @return 删除后的 list
     */
    private static List<String> fixByRemoveIf() {
        List<String> list = sampleList();
        // TODO：一行 lambda
        list.removeIf(s -> s.equals("c"));

        return list;
    }

    /**
     * TODO 2c：用普通 for-i 循环【倒序】遍历，按索引删除 "c"
     *
     * 思考题（用注释回答）：为什么必须【倒序】？正序删除会漏掉元素——
     * 删掉 i 位置的元素后，后面的元素全部前移一位，而循环变量 i 还在 ++……
     * 试着正序写一遍，观察漏删现象，再把结论写在注释里。
     *
     * @return 删除后的 list
     */
    private static List<String> fixByIndex() {
        List<String> list = sampleList();
        // TODO：倒序 for-i + remove(index)
        for (int i = list.size() - 1; i >= 0; i--) {   // 从最后一个开始，走到 0
            if (list.get(i).equals("c")) {
                list.remove(i);
            }
        }

        return list;
    }
}
