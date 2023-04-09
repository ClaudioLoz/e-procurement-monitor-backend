package com.osce.eprocurementmonitorbackend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileStorageService {
     void init();
     void save(MultipartFile file);
     Resource load(String filename);
     Path loadByName(String filename);
     void deleteAll();
     Stream<Path> loadAll();
}
