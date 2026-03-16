package com.mall.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/images")
public class ImagesController {

    @GetMapping("/{imageName}")
    public void getImages(@PathVariable String imageName, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=" + imageName);
        response.setContentType("image/jpeg");
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("images/" + imageName)).readAllBytes());
    }
}