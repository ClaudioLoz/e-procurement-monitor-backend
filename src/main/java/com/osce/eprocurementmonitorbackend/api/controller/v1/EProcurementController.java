package com.osce.eprocurementmonitorbackend.api.controller.v1;

import com.osce.eprocurementmonitorbackend.api.dto.EProcurementDetailOutDTO;
import com.osce.eprocurementmonitorbackend.api.dto.EProcurementOutDTO;
import com.osce.eprocurementmonitorbackend.api.dto.FileInfoOutDTO;
import com.osce.eprocurementmonitorbackend.model.EProcurement;
import com.osce.eprocurementmonitorbackend.model.FileInfo;
import com.osce.eprocurementmonitorbackend.service.EProcurementService;
import com.osce.eprocurementmonitorbackend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/eprocurements")
public class EProcurementController {

    @Autowired
    private EProcurementService eProcurementService;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EProcurement> createEProcurement(@RequestPart("json") EProcurement eProcurement
            , @RequestPart("files") MultipartFile[] files) {
        return new ResponseEntity<>(eProcurementService.createEProcurement(eProcurement, files), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EProcurementOutDTO>> findAllEProcurements() {
        return new ResponseEntity<>(eProcurementService.findAllEProcurements(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EProcurementDetailOutDTO> findEProcurementById(@PathVariable Long id) {
        return new ResponseEntity<>(eProcurementService.findEProcurementById(id), HttpStatus.OK);
    }


    @GetMapping("/files")
    public ResponseEntity<List<FileInfoOutDTO>> findAllEProcurementFiles() {
        List<FileInfoOutDTO> fileInfoOutDTOList = fileStorageService.loadAll().map(path -> {
            FileInfoOutDTO fileInfoOutDTO = new FileInfoOutDTO();
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(EProcurementController.class, "findFile", path.getFileName().toString()).build().toString();
            fileInfoOutDTO.setName(filename);
            fileInfoOutDTO.setUrl(url);
            return fileInfoOutDTO;
        }).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(fileInfoOutDTOList);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> findFile(@PathVariable String filename) {
        Resource file = fileStorageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

}
