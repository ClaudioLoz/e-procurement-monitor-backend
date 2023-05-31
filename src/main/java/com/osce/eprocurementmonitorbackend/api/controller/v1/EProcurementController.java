package com.osce.eprocurementmonitorbackend.api.controller.v1;

import com.osce.eprocurementmonitorbackend.api.dto.EProcurementDTO;
import com.osce.eprocurementmonitorbackend.api.dto.EProcurementDetailOutDTO;
import com.osce.eprocurementmonitorbackend.api.dto.EProcurementOutDTO;
import com.osce.eprocurementmonitorbackend.model.EProcurement;
import com.osce.eprocurementmonitorbackend.service.EProcurementService;
import com.osce.eprocurementmonitorbackend.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/eprocurements")
public class EProcurementController {

    private final EProcurementService eProcurementService;

    private final FileStorageService fileStorageService;

    public EProcurementController(EProcurementService eProcurementService, FileStorageService fileStorageService) {
        this.eProcurementService = eProcurementService;
        this.fileStorageService = fileStorageService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EProcurementDTO> createEProcurement(@RequestPart("json") EProcurement eProcurement
            , @RequestPart("files") MultipartFile[] files) {
        return new ResponseEntity<>(eProcurementService.createEProcurement(eProcurement, files), HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<EProcurementOutDTO>> findAllEProcurements() {
        return new ResponseEntity<>(eProcurementService.findAllEProcurements(), HttpStatus.OK);
    }

    @GetMapping("/test")
    public ResponseEntity<List<EProcurementOutDTO>> findAllEProcurementsByStatus(@RequestParam(name = "procurementStatus") String procurementStatus) {
        return new ResponseEntity<>(eProcurementService.findAllEProcurementsByStatus(procurementStatus), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EProcurementDetailOutDTO> findEProcurementById(@PathVariable Long id
                        , @RequestParam(name = "isDetailed", required = false) String isDetailed) {
        return new ResponseEntity<>(eProcurementService.findEProcurementById(id, isDetailed), HttpStatus.OK);
    }

    @GetMapping("/files/{filename:.+}") //TODO: LET DOWNLOAD with originalFileName
    public ResponseEntity<Resource> findFile(@PathVariable String filename) {
        Resource file = fileStorageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

}
