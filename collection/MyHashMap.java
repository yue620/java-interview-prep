package collection;

/**
 * MyHashMap —— Day 1 实践：手写简化版 HashMap
 *
 * 骨架已搭好，标有 TODO 的方法由你实现。
 * 对照需求：collection/ISSUE-001-手写MyHashMap.md
 *
 * 实现完成后运行 main 方法，所有验收用例通过即完成。
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class MyHashMap<K, V> {

    // ==================== 节点定义 ====================

    /**
     * 链表节点：HashMap 中每个桶里挂的就是这样的节点
     */
    static class Node<K, V> {
        final int hash;   // key 经过扰动后的 hash 值（缓存起来，扩容时不用再算）
        final K key;
        V value;
        Node<K, V> next;  // 哈希冲突时挂链表

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    // ==================== 核心字段 ====================

    /** 默认初始容量：16（想一想：为什么必须是 2 的幂？） */
    static final int DEFAULT_CAPACITY = 16;

    /** 负载因子：0.75（想一想：为什么是 0.75？） */
    static final float LOAD_FACTOR = 0.75f;

    /** 底层数组：每个位置是一个"桶"，挂着链表头节点 */
    Node<K, V>[] table;

    /** 当前键值对数量 */
    int size;

    /** 扩容阈值 = 容量 × 负载因子，size 超过它就扩容 */
    int threshold;

    // ==================== 构造方法 ====================

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        this.table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
        this.threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
    }

    // ==================== 需要你来实现的部分 ====================

    /**
     * TODO 1：扰动函数
     *
     * 要求：让 hashCode 的高 16 位也参与运算，减少哈希冲突。
     * 提示：h 和 (h 无符号右移 16 位) 做某种位运算。
     *
     * 思考（口述题）：容量只有 16 时，如果不用扰动函数会出什么问题？
     */
    private int hash(Object key) {
        // 提示：HashMap 允许 null key，null 的 hash 约定为 0
        if(key==null) return 0;
        int h = key.hashCode();
        return h^(h>>>16);
//        throw new UnsupportedOperationException("TODO: 实现扰动函数");


    }

    /**
     * TODO 2：存入键值对
     *
     * 流程提示（对应讲义 §二）：
     *   1. 算 hash → 用 (table.length - 1) & hash 定位桶下标
     *   2. 桶为空 → 直接放新节点
     *   3. 桶非空 → 遍历链表：key 相同则覆盖 value 并返回旧值；
     *      否则在链表【末尾】挂新节点（尾插法，JDK8 的做法）
     *   4. size++ 后检查是否超过 threshold，超过则调用 resize()
     *
     * @return key 已存在时返回被覆盖的旧值，否则返回 null newnode.key.Key(key)
     */
    public V put(K key, V value) {
        int hash = hash(key);
        int idx = (table.length-1)&hash;
        if(table[idx]==null){
            table[idx]=new Node<>(hash,key,value,null);
        }else{
            Node<K,V> newnode = table[idx];
            Node<K,V> prev = null;
            for(;newnode!=null;newnode=newnode.next){
                if(keyEquals(newnode.key,key)){
                    V oldValue =newnode.value;
                    newnode.value=value;
                    return oldValue;
                }
                prev = newnode;
            }
            prev.next= new Node<>(hash,key,value,null);
        }
        size++;
        if(size>threshold){
            resize();
        }
        return null;
//        throw new UnsupportedOperationException("TODO: 实现 put");
    }

    /**
     * TODO 3：根据 key 取值
     *
     * 提示：定位桶 → 遍历链表 → 比较时先比 hash 再比 equals（为什么？）
     *
     * @return 找到返回 value，不存在返回 null
     */
    public V get(K key) {
        int hash = hash(key);
        int idx = hash&(table.length-1);
        Node<K,V> node = table[idx];
        for(Node<K,V> cur=node; cur!=null;cur=cur.next){
            if(cur.hash==hash && keyEquals(cur.key,key)){
                return cur.value;
            }
        }
        return null;
//        throw new UnsupportedOperationException("TODO: 实现 get");
    }

    /**
     * TODO 4：删除指定 key
     *
     * 提示：在链表上删除节点，需要记录前一个节点 prev。
     * 注意处理"删除的是链表头节点"的特殊情况。
     *
     * @return 被删除节点的 value，不存在返回 null
     */
    public V remove(K key) {
        int hash = hash(key);
        int idx = hash&(table.length-1);
        Node<K,V> node = table[idx];
        Node<K,V> prev = null;
        for(Node<K,V> cur=node; cur!=null;cur=cur.next){
            if(cur.hash==hash && keyEquals(cur.key,key)){
                V val = cur.value;
                prev.next=cur.next;
                return val;
            }
            prev = cur;
        }
        return null;
//        throw new UnsupportedOperationException("TODO: 实现 remove");
    }

    /**
     * TODO 5：扩容
     *
     * 要求：新建 2 倍容量的数组，把旧数据重新分配到新数组。
     *
     * 基础版：每个节点用新容量重新计算下标，尾插到新桶。
     * 挑战版（推荐）：高低位拆分 ——
     *   hash & oldCap == 0 → 留在原下标
     *   hash & oldCap != 0 → 移到 原下标 + oldCap
     * 想一想：为什么扩容后节点只可能在这两个位置？（讲义 §四）
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        throw new UnsupportedOperationException("TODO: 实现扩容");
    }

    // ==================== 已帮你实现的部分 ====================

    /** 当前键值对数量 */
    public int size() {
        return size;
    }

    /** 判断两个 key 是否相等（处理了 null 的情况），可直接使用 */
    private static boolean keyEquals(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    // ==================== 验收用例（已写好，全部通过即完成） ====================

    public static void main(String[] args) {
        MyHashMap<String, Integer> map = new MyHashMap<>();

        // 用例 1：存 1000 个键值对，验证扩容不丢数据
        for (int i = 0; i < 1000; i++) {
            map.put("key" + i, i);
        }
        assertEquals(1000, map.size(), "用例1：size 应为 1000");

        // 用例 2：随机抽查 20 个 key 取值正确
        int[] sample = {0, 57, 123, 256, 399, 512, 618, 700, 777, 834,
                        901, 11, 222, 333, 444, 555, 666, 888, 999, 45};
        for (int i : sample) {
            assertEquals(Integer.valueOf(i), map.get("key" + i), "用例2：key" + i + " 取值错误");
        }

        // 用例 3：重复 put 同一个 key，覆盖旧值且 size 不变
        map.put("key0", -1);
        assertEquals(1000, map.size(), "用例3：重复 put 后 size 不应变化");
        assertEquals(Integer.valueOf(-1), map.get("key0"), "用例3：value 应被覆盖为 -1");
        assertEquals(Integer.valueOf(-1), map.put("key0", 0), "用例3：put 应返回旧值 -1");

        // 用例 4：get / remove 不存在的 key 返回 null
        assertEquals(null, map.get("不存在的key"), "用例4：get 不存在应返回 null");
        assertEquals(null, map.remove("不存在的key"), "用例4：remove 不存在应返回 null");

        // 用例 5：remove 后 size 减 1，再 get 返回 null
        assertEquals(Integer.valueOf(500), map.remove("key500"), "用例5：remove 应返回被删的值");
        assertEquals(999, map.size(), "用例5：remove 后 size 应为 999");
        assertEquals(null, map.get("key500"), "用例5：删除后 get 应返回 null");

        // 用例 6：null key 能正常存取（想想 HashMap 为什么允许 null key）
        map.put(null, 42);
        assertEquals(Integer.valueOf(42), map.get(null), "用例6：null key 取值错误");
        assertEquals(Integer.valueOf(42), map.remove(null), "用例6：null key 删除错误");

        System.out.println("✅ 全部验收用例通过！MyHashMap 完成。");
        System.out.println("下一步：打开 java.util.HashMap 源码，对照你的实现找差距。");
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            System.out.println("❌ 失败 [" + message + "] 期望=" + expected + " 实际=" + actual);
            System.exit(1);
        }
        System.out.println("✔ 通过：" + message);
    }
}
