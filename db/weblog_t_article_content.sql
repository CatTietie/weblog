create table t_article_content
(
    id         bigint unsigned auto_increment comment '文章内容id'
        primary key,
    article_id bigint not null comment '文章id',
    content    text   null comment '教程正文'
)
    comment '文章内容表' charset = utf8mb4;

create index idx_article_id
    on t_article_content (article_id);

INSERT INTO weblog.t_article_content (id, article_id, content) VALUES (17, 17, '## MySQL事务隔离级别：
### 实际情况演示：
模拟多线程（多事务）对同一份数据的脏读问题:
```MySQL
S
SET [SESSION|GLOBAL] TRANSACTION ISOLATION LEVEL [READ UNCOMMITTED|READ COMMITTED|REPEATABLE READ|SERIALIZABLE]

```
实际操作中使用到的一些并发控制语句:
● START TRANSACTION |BEGIN：显式地开启一个事务。
● COMMIT：提交事务，使得对数据库做的所有修改成为永久性。
● ROLLBACK：回滚会结束用户的事务，并撤销正在进行的所有未提交操作

### 脏读（读未提交）：
![](http://8.140.253.113:9000/weblog/1144875e64a444b0b3710792e8c1cbca.png)
### 避免脏读：
![](http://8.140.253.113:9000/weblog/e96f8e734b9a40caab027f4d5c8ed017.png)
虽然避免了读未提交的数据，但是还是在一个事务中读取到不一样的数据。

不可重复读：
也就是上述的读已提交：在一个事务还没结束时。读取到不一样的数据：
![](http://8.140.253.113:9000/weblog/3e542ffcd09647a1830aa0b3197d0d75.png)

可重复读：
![](http://8.140.253.113:9000/weblog/f9ad463ab9844d2f93a29aa7273c27c8.png)
在一个事务还没结束时，重复读取到的是一样的数据
幻读：
![](http://8.140.253.113:9000/weblog/b81813d14a01401da7ba7cbc6e846849.png)
QL 脚本 1 在第一次查询工资为 500 的记录时只有一条，SQL 脚本 2 插入了一条工资为 500 的记录，提交之后；SQL 脚本 1 在同一个事务中再次使用当前读查询发现出现了两条工资为 500 的记录这种就是幻读。
### 事务隔离级别总结：
![](http://8.140.253.113:9000/weblog/34c2a9792dec41fe8a9c47805065f6d4.png)

## 常见的 SQL 日期范围筛选用法：
1. 使用 >= 和 <= 运算符：
`    SELECT * FROM your_table WHERE sale_date >= ''2019-01-01'' AND sale_date <= ''2019-03-31'';`

2.提取年份和月份进行筛选
`    SELECT * FROM your_table WHERE YEAR(sale_date) = 2019 AND MONTH(sale_date) BETWEEN 1 AND 3;`

3.日期函数结合筛选：
例如在 MySQL 中，可以使用 DATE_FORMAT() 函数来提取日期的部分进行筛选。
` SELECT * FROM your_table WHERE DATE_FORMAT(sale_date, ''%Y-%m'') >= ''2019-01'' AND DATE_FORMAT(sale_date, ''%Y-%m'') <= ''2019-03'';`

4.针对日期时间类型（包含具体时间）的筛选：
`    SELECT * FROM your_table WHERE sale_datetime >= ''2019-01-01 00:00:00'' AND sale_datetime <= ''2019-03-31 23:59:59'';`



');
INSERT INTO weblog.t_article_content (id, article_id, content) VALUES (18, 18, '# Java8新特性：
## 一、 Lambda 表达式
解释 Lambda 表达式的概念和用途，比如简化匿名内部类的写法：
```Java
//
new Thread(new Runnable(){
    @Override 
    public void run() { 
        System.out.println("Hello World"); 
    } 
}).start(); 
```
而使用 Lambda 表达式，可以写成：
```Java
//
new Thread(() -> 
           System.out.println("Hello World")
          ).start();
```
## 二、 Stream流
处理集合数据，Filter，Map类型转换和过滤等，如：

```Java
//
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6); 
int sum = numbers.stream().filter(n -> n % 2 == 0).mapToInt(n -> n).sum();

```
## 三、 方法引用
方法引用是一种简洁地引用已有方法的方式
```Java
//
numbers.stream().mapToInt(Integer::valueOf).sum(); 

```
## 四、 接口的默认方法和静态方法
接口现在可以包含默认方法和静态方法,当类不想覆盖这些方法时可以直接使用
```Java
//
interface MyInterface { 
    default void defaultMethod() {
        System.out.println("Default method implementation");
    } 
    static void staticMethod() {
        System.out.println("Static method implementation"); 
    } 
}

```
## 五、 新的日期API
使用 LocalDateTime 来表示本地日期时间,解决线程安全、可读性和易用性等问题
`LocalDateTime now = LocalDateTime.now();`


');
INSERT INTO weblog.t_article_content (id, article_id, content) VALUES (19, 19, '# redis可重入锁+锁重试+看门狗机制原理
## 上述三个机制都为redisson客户端的锁机制
### 可重入：
即同一线程在允许多次获取同一把锁
获取锁逻辑：
利用redis的Hash结构，使用**exist**命令判断当前线程是否存在锁：
1.如果的返回值是否为空，则创建锁，**hash-key**为线程标识，**hash-value**为当前线程获取锁的次数（初始化为1）
2.不为空说明锁已被获取过，再次判断当前锁的线程标识与自己线程是否一致：
1)如果不一致，则说明锁已经被其他线程占用，获取锁失败。
       2）如果一致，说明是重入锁，使用increaby命令将hash-value自增1，并且**刷新key的ttl**
释放锁逻辑：
在释放锁时，首先判断是否为当前线程的锁，然后再取出hash-value，判断其数值大小：
1)如果value的值为大于一，则将value的值自减
2）如果value的值等于一，则将锁删除
### 锁重试：
即在tryLock（）方法里指定获取锁重试时间,如果传入了指定了锁重试时间，则在获取锁失败之后不会立即返回失败，而是在重试时间内再次去获取锁，直到过了重试时间
获取锁逻辑：
1）获取锁成功，在底层的lua脚本会返回**nil**，也就是null，表示获取锁成功，不需要进行重试
2）获取锁失败,底层的lua脚本会返回当前锁的过期时间，在拿到过期时间之后会将之前传入的重试时间刷新为剩余时间，判断剩余时间是否大于过期时间（也就是判断在过期时间内是否还来得及获取锁）：
2.1)剩余时间小于过期时间：说明在重试时间范围内已经来不及获取锁，就已经被自动释放了
2.2）剩余时间大于过期时间：说明在重试时间范围内还来得及获取锁，此时不会立即重新尝试获取，而是会等待订阅信号量（等待拿到锁的线程释放锁时发出信号）,这个等待订阅也会有默认的等待时间，在收到释放锁信号之后，再次刷新剩余时间，然后才重新尝试获取锁，直到获取锁成功或者超过了剩余时间才会停止。
释放锁逻辑：
在释放锁时，会使用**publish**命令来通知重试获取锁的线程，从而停止他们的等待订阅状态。
### 看门狗：
即为了避免业务阻塞，锁自动超时释放的机制：
在**tryLock（）**中，可以传入relaseTime参数，表示锁超时释放的时间，如果不传参，则会执行看门狗机制,一开始，看门狗会默认设置锁30s的有效期，之后通过递归来一直刷新10s的有效期，防止在业务阻塞时锁自动释放，造成线程安全问题。
释放锁时，取消看门狗机制，避免重复刷新有效期');