package cn.anytec.security;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@SpringBootApplication(scanBasePackages = {"cn.anytec.security"})
@MapperScan(basePackages = { "cn.anytec.security.dao" }, sqlSessionFactoryRef = "sqlSessionFactory")
public class SecurityApplication {
	@Autowired
	private RestTemplateBuilder builder;

	// 使用RestTemplateBuilder来实例化RestTemplate对象，spring默认已经注入了RestTemplateBuilder实例
	@Bean
	public RestTemplate restTemplate() {
		return builder.build();
	}

	@Bean
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);

		// 使用Jackson2JsonRedisSerialize 替换默认序列化
//		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

		StringRedisSerializer stringRedisSerializer= new StringRedisSerializer();

//		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

//		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

		// 设置value的序列化规则和 key的序列化规则
		redisTemplate.setKeySerializer(stringRedisSerializer);
//		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
		redisTemplate.setValueSerializer(stringRedisSerializer);
		redisTemplate.setHashKeySerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
		redisTemplate.setHashValueSerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}


	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

}
