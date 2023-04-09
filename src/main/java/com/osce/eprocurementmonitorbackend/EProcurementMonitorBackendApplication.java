package com.osce.eprocurementmonitorbackend;

import com.osce.eprocurementmonitorbackend.service.FileStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class EProcurementMonitorBackendApplication implements CommandLineRunner {

    @Resource
    FileStorageService fileStorageService;

    public static void main(String[] args) {
        SpringApplication.run(EProcurementMonitorBackendApplication.class, args);
    }

    @Override
    public void run(String... arg) throws Exception {
//        fileStorageService.deleteAll();
        fileStorageService.init();
    }

}
