package ru.nsu.dgi.department_assistant.domain.service.impl;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CookieServiceImpl{

    public Optional<String> extractTokenFromCookies(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return Optional.empty();

        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .map(Cookie::getValue);
    }

    public void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        String cookie = String.format("%s=%s; Path=/; HttpOnly; Secure; SameSite=Lax; Max-Age=%d",
                name, value, maxAge);
        response.addHeader("Set-Cookie", cookie);
    }

    public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies)
                    .filter(c -> c.getName().equals(name))
                    .findFirst()
                    .ifPresent(c -> {
                        String cookie = String.format("%s=; Path=/; HttpOnly; Secure; SameSite=Strict; Max-Age=0",
                                name);
                        response.addHeader("Set-Cookie", cookie);
                    });
        }
    }
}