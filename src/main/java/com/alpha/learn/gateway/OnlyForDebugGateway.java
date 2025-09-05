package com.alpha.learn.gateway;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class OnlyForDebugGateway {

    @GetMapping("/mock/{id}")
    public String test(HttpServletRequest request, @PathVariable String id) {
        String header = request.getHeader("Gateway-Request");
        if (header == null || header.isBlank()) {
            header = "::MOCK";
        }
        return "MOCK_VALUE" + header;
    }
}
