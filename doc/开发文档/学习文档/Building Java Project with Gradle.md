# Building Java Project with Gradle

## Start a new Spring Boot

用 [start.spring.io](https://start.spring.io/) 创建web project.

## Add your code

```jsx
package com.example.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DemoApplication {
  
    
    public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
    }
    
    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
    return String.format("Hello %s!", name);
    }
  
}
```

## Try it

```jsx
gradle bootRun
```