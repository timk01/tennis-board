package tennisboard;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/api/hello")
    public Map<String, String> sayHello() {
        return Map.of("message", "Hello World from raw Spring MVC!");
    }
}