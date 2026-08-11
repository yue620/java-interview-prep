# ISSUE-002：fail-fast 复现与修复 + String 拼接性能实验

> 阶段 1 · Day 2（8/12）实践任务
> 对应八股：fail-fast 机制、String 不可变、StringBuilder/StringBuffer

---

## 实验一：把 ConcurrentModificationException 亲手"造"出来再修掉

新建 `collection/FailFastDemo.java`。

### 任务 1：复现异常

写一个方法：创建包含 "a"~"e" 的 ArrayList，用增强 for 循环遍历，遇到 "c" 时调用 `list.remove("c")`。

**验收**：运行后控制台抛出 `ConcurrentModificationException`，**把异常截图或复制到代码注释里**（证明你亲眼见过它）。

### 任务 2：用三种方式修复

在同一个文件里写三个方法，各自完成"遍历中删除 c"且不抛异常：

1. `fixByIterator()`：用 Iterator 的 remove
2. `fixByRemoveIf()`：用 `removeIf`
3. `fixByIndex()`：用普通 for-i 倒序遍历删除（想想为什么要**倒序**？正序删会出什么问题？——这是另一个经典坑，在注释里写下你的理解）

**验收**：三个方法都正常运行，删除后 list 内容正确（打印出来确认）。

### 思考题（写在代码注释里）

- fail-fast 检查发生在迭代器的哪个方法里？
- 为什么说 fail-fast 不是线程安全机制？

---

## 实验二：String 拼接性能对比

新建 `collection/StringConcatBench.java`。

分别用三种方式拼接 100000 次字符串，用 `System.currentTimeMillis()` 计时并打印：

1. `s += i`（String）
2. `StringBuilder.append(i)`
3. `StringBuffer.append(i)`

**验收**：运行并记录三个耗时，在代码注释里写出：
- 三个耗时分别是多少 ms
- String 方式为什么慢（从"不可变"角度解释：每次 += 发生了什么）
- StringBuffer 比 StringBuilder 慢的那部分开销是什么

> 提示：String 方式拼 10 万次可能要几秒甚至十几秒，耐心等；这正是它"慢"的证据。如果太慢可以把次数降到 30000。

---

## 挑战（有余力再做）

把 `MyHashMap.resize()` 改成 JDK 式写法：**不新建 Node，复用旧节点只改 next 指针**（注意：遍历时先保存 `cur.next` 再断链，否则链表会断）。
改完跑一遍 day1 的验收用例，必须仍然全绿。

---

## 提交要求

```bash
git add -A
git commit -m "feat: day2 fail-fast 复现与修复 + String 拼接性能实验"
git push
```
