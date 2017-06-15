package kr.co.tworld;

import java.nio.charset.Charset;

import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.client.RestTemplate;

import kr.co.tworld.login.controller.LoginSessionListener;

@Configuration
@EnableRedisHttpSession //레디스 세션
public class TwdLoginConfig extends AbstractCloudConfig  {
	
	@Value("${services.redis.name}")
	private String redisName;

	public RedisConnectionFactory redisConnectionFactory() {
		return connectionFactory().redisConnectionFactory(redisName);
	}
	 
	@Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        return new StringHttpMessageConverter(Charset.forName("EUC-KR"));
    }
	
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
	    RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
	    template.setConnectionFactory(redisConnectionFactory());
//	    template.setConnectionFactory(jedisConnectionFactory());
	    return template;
	}
	
	@Bean
	public RedisTemplate<String, Object> sessionTemplate() {
	    RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
	    template.setConnectionFactory(redisConnectionFactory());
//	    template.setConnectionFactory(jedisConnectionFactory());
	    
	    
	    template.setHashKeySerializer(new StringRedisSerializer());
	    template.setKeySerializer(new StringRedisSerializer());
	    
	    return template;
	}
	
	//redis-session
	@Bean
	public static ConfigureRedisAction configureRedisAction() {
		return ConfigureRedisAction.NO_OP;
	}
	
	//session Listener
	@Bean
	public HttpSessionListener httpSessionListener(){
	    return new LoginSessionListener(); 
	}
	
	@Bean
	@LoadBalanced
	public RestTemplate test(){
		return new RestTemplate();
	}

	
}
