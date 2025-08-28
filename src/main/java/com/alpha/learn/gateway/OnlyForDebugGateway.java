package com.alpha.learn.gateway;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway")
public class OnlyForDebugGateway {

    @RequestMapping("/mock")
    public String test(HttpServletRequest request) {
        String header = request.getHeader("Gateway-Response");
        if (header == null || header.isBlank()) {
            header = "::MOCK";
        }
        return "MOCK_VALUE" + header;
    }
}
