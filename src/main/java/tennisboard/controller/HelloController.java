package tennisboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tennisboard.service.HelloService;

import java.util.Map;

@RestController
public class HelloController {

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/api/hello")
    public /*Map<String, String>*/ String sayHello() {
        //return Map.of("message", "Hello World from raw Spring MVC!");
        return helloService.helloFromService();
    }
}