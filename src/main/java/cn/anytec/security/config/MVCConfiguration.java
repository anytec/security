package cn.anytec.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@Configuration

public class MVCConfiguration implements WebMvcConfigurer {

    //请求直接映射页面
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
//        registry.addViewController("/real").setViewName("real");
//        registry.addViewController("/hehe").setViewName("hehe");
    }

    //静态资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //相对路径
//        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
//        registry.addResourceHandler("/img/**").addResourceLocations("classpath:/static/img/");
//        registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/static/fonts/");
        //绝对路径
        /*registry.addResourceHandler("/upload/**").addResourceLocations("file:/data/upload/");
        registry.addResourceHandler("/images/**").addResourceLocations("file:/data/images/");*/
    }

   /* @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/static/html/**","/video_index","/manage_index").excludePathPatterns("/login");
    }*/
}
