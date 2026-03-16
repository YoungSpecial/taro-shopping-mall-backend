package com.mall.config;

import com.mall.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FileStorageInitializer implements CommandLineRunner {

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public void run(String... args) {
        fileStorageService.init();
    }
}