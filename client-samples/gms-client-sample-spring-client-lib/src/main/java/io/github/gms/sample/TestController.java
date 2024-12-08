package io.github.gms.sample;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Value("${config.test}")
    private String test;

    @Value("${config.username}")
    private String test1;
    @Value("${config.password}")
    private String test2;

    @Value("${config.other1}")
    private String test3;

    @Value("${config.other2}")
    private String test4;

    @GetMapping
    public String test() {
        return "Test value: " + test + "; Username: " + test1 + "; Password: " + test2 + ";" + test3 + ";" + test4;
    }
}
