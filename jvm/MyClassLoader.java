package jvm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * MyClassLoader —— Day 6 实验二：自定义类加载器
 *
 * 对应需求：jvm/ISSUE-005-OOM与类加载实验.md
 *
 * 目标：从指定目录读取 .class 文件，加载成 Class 对象。
 *
 * 【实测记录】（2026-08-21）：
 *   第一次：Hello.class 放在 out/production（classpath 上）
 *     → getClassLoader() 打印 AppClassLoader！双亲委派：父亲（AppClassLoader）
 *       在 classpath 里找得到 Hello，直接加载了，轮不到 MyClassLoader。
 *   第二次：Hello.class 搬到 D:\kimiCode\baguxuexi\custom-classes（不在 classpath）
 *     → 父亲们都不认识这个目录，委派失败 → 才调用我的 findClass
 *     → 打印 jvm.MyClassLoader，抢到加载权 ✅
 *
 * 思考题 1：为什么重写 findClass 而不是 loadClass？
 *   loadClass 里实现了双亲委派流程（先问父亲，父亲不行才调 findClass）。
 *   重写 loadClass 会破坏委派机制（核心类可能被重复加载/伪造）；
 *   findClass 是"父亲们都加载不了才轮到我"的兜底钩子，自定义逻辑放这里最安全。
 *
 * 思考题 2：这体现了双亲委派的什么特点？
 *   "父亲优先"：只要父亲能加载，孩子就没有机会（第一次实测就是证明）。
 *   好处：避免重复加载 + 防止伪造核心类（假冒的 java.lang.String 会被委派到
 *   Bootstrap 加载正牌，假冒货永远不会生效）。
 */
public class MyClassLoader extends ClassLoader {

    /** .class 文件所在的目录 */
    private final String classDir;

    public MyClassLoader(String classDir) {
        this.classDir = classDir;
    }

    /**
     * TODO：实现按名字找类
     *
     * 步骤提示：
     *   1. 拼出文件路径：classDir + name + ".class"
     *      （简单起见，name 就是不带包名的类名，如 "Hello"）
     *   2. 用 Files.readAllBytes(Path.of(...)) 读字节
     *   3. 调用 defineClass(name, bytes, 0, bytes.length) 转成 Class 并返回
     *      —— defineClass 是 ClassLoader 自带的方法，负责把字节流变成 Class 对象
     *   4. 读不到文件就 throw new ClassNotFoundException()
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            // 拼出完整路径：目录 + 类名 + .class
            byte[] bytes = Files.readAllBytes(Path.of(classDir, name + ".class"));
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("找不到类文件: " + name, e);
        }
    }

    // ==================== 验收（已写好，先读完说明再跑） ====================

    /**
     * 准备步骤：
     *   1. 先在 jvm 目录外（比如项目根目录）创建一个 Hello.java：
     *        public class Hello {
     *            public static void say() { System.out.println("hello from MyClassLoader!"); }
     *        }
     *   2. 用 IDEA 或 javac 编译出 Hello.class
     *   3. 把下面 classDir 改成 Hello.class 所在目录的路径
     */
    public static void main(String[] args) throws Exception {
        String classDir = "D:\\kimiCode\\baguxuexi\\custom-classes"; // Hello.class 所在目录（不能在 classpath 上！）

        MyClassLoader loader = new MyClassLoader(classDir);
        Class<?> clazz = loader.loadClass("Hello");

        System.out.println("加载的类: " + clazz.getName());
        System.out.println("它的类加载器: " + clazz.getClassLoader());   // 应打印 jvm.MyClassLoader

        // 反射调用它的静态方法 say()
        clazz.getMethod("say").invoke(null);
    }
}
