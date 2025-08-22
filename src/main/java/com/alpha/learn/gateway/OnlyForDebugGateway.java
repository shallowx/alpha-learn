package com.alpha.learn.gateway;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway")
public class OnlyForDebugGateway {

    @RequestMapping("/mock")
    public String test() {
        return "MOCK_VALUE";
    }
}
