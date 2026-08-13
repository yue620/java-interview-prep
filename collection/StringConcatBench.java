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
         * 【记录你的实测数据】：
         *   String        ：______ ms
         *   StringBuilder ：______ ms
         *   StringBuffer  ：______ ms
         *
         * 【思考题 1】String 方式为什么慢这么多？
         *   提示：String 不可变 → 每次 += 发生了什么？第 i 次拼接要拷贝多少个字符？
         *
         * 【思考题 2】StringBuffer 比 StringBuilder 慢的那部分开销是什么？
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
