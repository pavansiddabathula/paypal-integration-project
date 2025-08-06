package com.hulkhiretech.payments;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@RestController
public class Addition {
	

    @GetMapping("/cal/add")
    public String add(@RequestParam double a, @RequestParam double b) {
        double resultString = a + b;
        log.info("Adding {} and {} - Result: {}", a, b, resultString);
        return "Sum of two numbers " + a + " and " + b + " is: " + resultString;
    }
}
