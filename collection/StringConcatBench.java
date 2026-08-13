package collection;

/**
 * StringConcatBench —— Day 2 实验二：String / StringBuilder / StringBuffer 拼接性能对比
 *
 * 对应需求：collection/ISSUE-002-failfast与String实验.md
 * 对应讲义：notes/day02-集合与String/01-学习讲义.md §四
 */
public class StringConcatBench {

    /** 拼接次数（如果 String 方式太慢等不及，降到 30000） */
    private static final int TIMES = 100_000;

    public static void main(String[] args) {
        System.out.println("拼接次数：" + TIMES);
        System.out.println("String        +=      耗时：" + benchString() + " ms");
        System.out.println("StringBuilder append  耗时：" + benchStringBuilder() + " ms");
        System.out.println("StringBuffer  append  耗时：" + benchStringBuffer() + " ms");

        /*
         * 【实测数据】（2026-08-13，10 万次拼接）：
         *   String        ：2030 ms
         *   StringBuilder ：2 ms
         *   StringBuffer  ：2 ms
         *   结论：String += 比 StringBuilder 慢约 1000 倍。
         *
         * 【思考题 1】String 方式为什么慢这么多？
         *   String 不可变 → 每次 += 都要新建一个 String 对象，并把旧内容完整拷贝一遍。
         *   第 i 次拼接要拷贝约 i 个字符，总拷贝量是 1+2+...+n ≈ n²/2（平方级），
         *   还会产生 n 个临时对象给 GC 回收。所以又慢又费内存。
         *
         * 【思考题 2】StringBuffer 理论上慢在哪？为什么我实测和 StringBuilder 一样快？
         *   StringBuffer 的 append 等方法加了 synchronized，理论上多一层锁开销。
         *   但现代 JVM 对无竞争的锁有优化（锁消除/轻量级锁），单线程测试下开销可忽略，
         *   所以实测两者几乎相等。锁开销要在多线程竞争时才体现。
         */
    }

    /**
     * TODO 1：用 String 的 += 拼接 0 ~ TIMES-1，返回耗时毫秒数
     *
     * 计时方法已给出：System.currentTimeMillis()
     */
    private static long benchString() {
        long start = System.currentTimeMillis();
        // TODO：String s = ""; 循环 s += i;
        String s = "";
        for (int i = 0; i < TIMES; i++) {
            s += i;
        }

        return System.currentTimeMillis() - start;
    }

    /**
     * TODO 2：用 StringBuilder.append 拼接，返回耗时毫秒数
     */
    private static long benchStringBuilder() {
        long start = System.currentTimeMillis();
        // TODO
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TIMES; i++) {
            sb.append(i);
        }

        return System.currentTimeMillis() - start;
    }

    /**
     * TODO 3：用 StringBuffer.append 拼接，返回耗时毫秒数
     */
    private static long benchStringBuffer() {
        long start = System.currentTimeMillis();
        // TODO
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < TIMES; i++) {
            sbf.append(i);
        }

        return System.currentTimeMillis() - start;
    }
}
