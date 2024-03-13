package org.apache.skywalking.apm.plugin.dayu.jmeter.utils;

import java.util.Objects;

/**
 * 基于twitter的开源雪花算法
 * 注释和程序修改： kyyan
 * <p>
 * 高位部分
 * custom_id(by kyyan): 自定义的ID(字符串,比如某个业务id)
 * <p>
 * <p>
 * 低位long部分：
 * <p>
 * 1.Snowflake 会生成一个 long 类型的 id 值，Snowflake 对于 long 的各个位都有固定的规范，SnowFlake 所生成的 ID 的结构如下：
 * unused                                                  datacenter_id         sequence_id
 * * │                                                           │                    │
 * * │                                                           │                    │
 * * │ │                                                     │   │                    │
 * * v │<──────────────────    41 bits   ───────────────────>│   v                    v
 * ┌───┼─────────────────────────────────────────────────────┼───────┬───────┬────────────────┐
 * │ 0 │ 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0 │ 00000 │ 00000 │ 0000 0000 0000 │
 * └───┴─────────────────────────────────────────────────────┴───────┴───────┴────────────────┘
 * * ^                                        ^                           ^
 * * │                                        │                           │
 * * │                                        │                           │
 * * │                                        │                           │
 * *                                    time in milliseconds          worker_id
 * <p>
 * <p>
 * 1）最高位标识（1 位）
 * 由于 long 基本类型在 Java 中是带符号的，最高位是符号位，正数是 0，负数是 1，所以 id 一般是正数，最高位是 0 。
 * 2）time in milliseconds:毫秒级时间截（41 位）
 * 注意，41 位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截) 得到的值，这里的的开始时间截，一般是我们的 id 生成器开始使用的时间，由我们程序来指定的。
 * 数据机器位（10 位）
 * 3）datacenter_id+worker_id: 这 10 位的机器位实际上是由 5 位的 data-center-id 和 5 位的 worker-id 。
 * 在 Twitter 的设计中，最多支持 32 个数据中心，每个中心最多由 32 台电脑各自计算 ID 。即，总共允许存在 1024 台电脑各自计算 ID 。
 * 每台电脑都由 data-center-id 和 worker-id 标识，逻辑上类似于联合主键的意思。
 * 4)sequence_id:顺序号（12 位）
 * 毫秒内的计数，12 位的计数顺序号支持每个节点每毫秒（同一机器，同一时间截）产生 4096(2的12次方) 个 ID 序号。
 */
public class SnowFlakeIdGenerator {
    // ==============================Fields===========================================
    // 所占位数、位移、掩码/极大值

    // sequence_id
    private static final long sequenceBits = 12L;
    private static final long sequenceShift = 0L;
    private static final long sequenceMask = ~(-1L << sequenceBits);
    // worker_id
    private static final long workerIdBits = 5L;
    private static final long workerIdShift = sequenceBits;
    private static final long workerIdMask = ~(-1L << workerIdBits);
    //datacenter_id
    private static final long dataCenterIdBits = 5L;
    private static final long dataCenterIdShift = sequenceBits + workerIdBits;
    private static final long dataCenterIdMask = ~(-1L << dataCenterIdBits);
    // time in milliseconds
    private static final long timestampBits = 41L;
    private static final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
    private static final long timestampMask = ~(-1L << timestampBits);

    /**
     * 开始时间截 (2022-07-02)
     * 用于计算41位位移时间
     */
    private static final long twepoch = 1656739812000L;
    /*
     * Instant instant = Instant.parse("2022-07-02T13:30:12Z");
     * System.out.println(instant.getEpochSecond());
     * System.out.println(instant.toEpochMilli());
     */

    private static SnowFlakeIdGenerator instance;


    private long sequence = 0L;
    private long workerId;
    private long dataCenterId;
    private Integer customId;

    /**
     * 上次生成 ID 的时间截
     */
    private long lastTimestamp = -1L;

    //==============================Constructors=====================================

    public SnowFlakeIdGenerator() {
        this(0, 0, 0);
    }

    /**
     * 构造函数
     *
     * @param workerId     工作ID (0~31)
     * @param dataCenterId 数据中心 ID (0~31)
     */
    public SnowFlakeIdGenerator(long workerId, long dataCenterId, Integer customId) {
        if (workerId > workerIdMask || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0", workerIdMask));
        }

        if (dataCenterId > dataCenterIdMask || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenterId can't be greater than %d or less than 0", dataCenterIdMask));
        }

        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        this.customId = customId;
    }

    public static void createInstance(long workerId, long dataCenterId, Integer customId) {
        synchronized (SnowFlakeIdGenerator.class){
            if (Objects.isNull(instance)) {
                synchronized (SnowFlakeIdGenerator.class){
                    instance = new SnowFlakeIdGenerator(workerId, dataCenterId, customId);
                }
            }
        }
    }

    public static String generateId() {
        return instance.nextId();
    }

    // ============================== Methods ==========================================

    /**
     * 获得下一个 ID (该方法是线程安全的，synchronized)
     */
    public synchronized String nextId() {

        long timestamp = timeGen();

        // 如果当前时间小于上一次 ID 生成的时间戳，说明系统时钟回退过，这个时候应当抛出异常。
        // 出现这种原因是因为系统的时间被回拨，或出现闰秒现象。
        // 你也可以不抛出异常，而是调用 tilNextMillis 进行等待
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出，即，同一毫秒的序列数已经达到最大
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        // 时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        // 将当前生成的时间戳记录为『上次时间戳』。『下次』生成时间戳时要用到。
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成 64 位的 ID 作为低位
        long low = ((timestamp - twepoch) << timestampLeftShift) // 时间戳部分
                | (dataCenterId << dataCenterIdShift) // 数据中心部分
                | (workerId << workerIdShift) // 机器标识部分
                | sequence; // 序列号部分;

        StringBuilder id = new StringBuilder();// 由于方法已经synchronized，这里就不考虑线程不安全问题了
        id.append(customId).append(low);

        return id.toString();
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param timestamp     当前时间错
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long timestamp, long lastTimestamp) {
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }


    //==============================Test=============================================

    /**
     * 测试
     */
    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        SnowFlakeIdGenerator instance = new SnowFlakeIdGenerator(20, 1, 1101);
        long startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            String id = instance.nextId();
            System.out.println(id);
        }
        System.out.println((System.nanoTime() - startTime) / 1000000 + "ms");
    }
}
