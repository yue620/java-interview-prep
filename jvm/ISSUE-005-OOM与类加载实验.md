# ISSUE-005：OOM 复现 + GC 日志观察 + 自定义类加载器

> 阶段 3 · Day 6（8/19）实践任务
> 对应八股：JVM 内存区域、GC、类加载

---

## 实验一：OOM 复现与 GC 日志（`jvm/OomLab.java`，骨架已给）

### 任务 1：制造 OOM 并抓 GC 日志

骨架里已经写好"无限 new 1MB 数组塞进 List"的代码。你要做的是**配置 VM 参数**再运行：

IDEA：运行配置（右上角运行按钮旁 → Edit Configurations）→ Modify options → Add VM options，填入：

```
-Xmx20m -Xlog:gc*
```

- `-Xmx20m`：最大堆 20MB（让 OOM 来得快一点）
- `-Xlog:gc*`：打印 GC 日志（JDK9+ 写法）

**验收**：
1. 控制台先刷出一堆 GC 日志（`[gc]` 开头，能看到 GC 前后堆大小变化），最后抛 `OutOfMemoryError: Java heap space`
2. 在类注释里回答：从 GC 日志看，OOM 之前发生了什么？（提示：GC 越来越频繁，但每次回收掉的内存越来越少）

### 任务 2：用 jmap 看"是谁吃了内存"

OOM 前（程序还在跑的时候，把 while 里的 sleep 打开拖时间），另开终端：

```bash
jps -l                    # 找进程号
jmap -histo <pid> | more  # 看堆里各类对象的数量和大小
```

**验收**：在输出里找到 `[B`（byte 数组）那一行——它应该排在前面，数量和大小都巨大。把这行复制到类注释里留证，并回答：为什么 byte 数组这么多？（对应你代码里的哪一行？）

## 实验二：自定义类加载器（`jvm/MyClassLoader.java`，骨架已给）

### 任务：写一个能从指定目录加载 .class 文件的类加载器

1. 继承 `ClassLoader`，重写 `findClass(String name)`
2. 在 findClass 里：读取 .class 文件字节 → 调 `defineClass()` 转成 Class 对象
3. main 里用它加载一个类，打印 `clazz.getClassLoader()`——应该显示你的 MyClassLoader

**验收**：打印出类加载器是你自己的 MyClassLoader，而不是 AppClassLoader。

### 思考题（注释里回答）

1. 为什么重写 `findClass` 而不是 `loadClass`？（提示：loadClass 里实现了什么机制？重写它会破坏什么？）
2. 这体现了双亲委派的什么特点？

## 提交要求

```bash
git add -A
git commit -m "feat: day6 OOM 复现与 GC 日志分析 + 自定义类加载器"
git push
```
