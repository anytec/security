package cn.anytec.security.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public interface RedisService {

    /**
     * set存数据
     * @param key
     * @param value
     * @return
     */
    boolean set(String key, String value);
    boolean set(String key, Object object);

    /**
     * get获取数据
     * @param key
     * @return
     */
    String get(String key);
    <T> T get(String key,Class<T> T);

    /**
     * 设置有效天数
     * @param key
     * @param expire
     * @return
     */
    boolean expire(String key, long expire);

    /**
     * 移除数据
     * @param key
     * @return
     */
    boolean remove(String key);

    void flushdb();

}
