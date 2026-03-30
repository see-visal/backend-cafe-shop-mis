package com.coffee.app.config;

import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
   public void addInterceptors(InterceptorRegistry registry) {
      WebContentInterceptor interceptor = new WebContentInterceptor();
      interceptor.addCacheMapping(CacheControl.noStore(), "/api/public/products", "/api/public/products/*", "/api/public/categories", "/api/public/home-showcase");
      interceptor.addCacheMapping(CacheControl.noStore(), "/api/customer/**", "/api/admin/**", "/api/public/auth/**");
      registry.addInterceptor(interceptor);
   }

   public void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler("/uploads/**").addResourceLocations("file:uploads/").setCacheControl(CacheControl.maxAge(7L, TimeUnit.DAYS).cachePublic());
   }
}
