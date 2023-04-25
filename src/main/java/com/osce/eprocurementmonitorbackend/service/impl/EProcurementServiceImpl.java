package com.osce.eprocurementmonitorbackend.service.impl;

import com.osce.eprocurementmonitorbackend.api.controller.v1.EProcurementController;
import com.osce.eprocurementmonitorbackend.api.dto.EProcurementDetailOutDTO;
import com.osce.eprocurementmonitorbackend.api.dto.EProcurementOutDTO;
import com.osce.eprocurementmonitorbackend.api.dto.FileInfoOutDTO;
import com.osce.eprocurementmonitorbackend.model.AuthUser;
import com.osce.eprocurementmonitorbackend.model.EProcurement;
import com.osce.eprocurementmonitorbackend.model.FileInfo;
import com.osce.eprocurementmonitorbackend.security.services.UserDetailsImpl;
import com.osce.eprocurementmonitorbackend.repository.EProcurementRepository;
import com.osce.eprocurementmonitorbackend.repository.FileInfoRepository;
import com.osce.eprocurementmonitorbackend.service.EProcurementService;
import com.osce.eprocurementmonitorbackend.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EProcurementServiceImpl implements EProcurementService {

    @Autowired
    private EProcurementRepository eProcurementRepository;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    private FileStorageService fileStorageService;
    @Override
    public EProcurement createEProcurement(EProcurement eProcurement, MultipartFile[] files) {
        UserDetailsImpl userDetails =
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AuthUser authUser = new AuthUser();
        authUser.setId(userDetails.getId());
        eProcurement.setUser(authUser);
        EProcurement savedEProcurement = eProcurementRepository.save(eProcurement);
        Arrays.asList(files).stream().forEach(file -> {
            fileStorageService.save(file);
            FileInfo fileInfo = new FileInfo();
            fileInfo.setName(file.getOriginalFilename());
            fileInfo.setEprocurement(savedEProcurement);
            fileInfoRepository.save(fileInfo);
            //TODO: save  file's hash in blockchain
        });
        return savedEProcurement;
    }

    @Override
    public List<EProcurementOutDTO> findAllEProcurements() {
        return eProcurementRepository.findAll().stream().map(eProcurement -> {
            EProcurementOutDTO eProcurementOutDTO = new EProcurementOutDTO();
            eProcurementOutDTO.setId(eProcurement.getId());
            eProcurementOutDTO.setContractingEntityName(eProcurement.getContractingEntityName());
            eProcurementOutDTO.setContractingEntityRuc(eProcurement.getContractingEntityRuc());
            eProcurementOutDTO.setContractorName(eProcurement.getContractorName());
            eProcurementOutDTO.setContractorRuc(eProcurement.getContractorRuc());
            eProcurementOutDTO.setProcurementObject(eProcurement.getProcurementObject());
            eProcurementOutDTO.setAmount(eProcurement.getAmount());
            eProcurementOutDTO.setContractStartDate(eProcurement.getContractStartDate());
            eProcurementOutDTO.setContractEndDate(eProcurement.getContractEndDate());
            eProcurementOutDTO.setDepartment(eProcurement.getDepartment());
            return eProcurementOutDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public EProcurementDetailOutDTO findEProcurementById(Long id) {
        EProcurement foundEProcurement = eProcurementRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        EProcurementDetailOutDTO eProcurementDetailOutDTO = new EProcurementDetailOutDTO();
        eProcurementDetailOutDTO.setEProcurement(foundEProcurement);
        List<FileInfoOutDTO> fileInfoOutDTOList = fileInfoRepository.findAllByEprocurement_Id(foundEProcurement.getId())
                                                    .stream().map(fileInfo -> {
                                                        Path path = fileStorageService.loadByName(fileInfo.getName());
                                                        FileInfoOutDTO fileInfoOutDTO = new FileInfoOutDTO();
                                                        String filename = path.getFileName().toString();
                                                        String url = MvcUriComponentsBuilder
                                                                .fromMethodName(EProcurementController.class, "findFile", path.getFileName().toString()).build().toString();
                                                        fileInfoOutDTO.setName(filename);
                                                        fileInfoOutDTO.setUrl(url);
                                                        return fileInfoOutDTO;
                                                    }).collect(Collectors.toList());
        eProcurementDetailOutDTO.setFileInfoOutDTOList(fileInfoOutDTOList);
        return eProcurementDetailOutDTO;
    }

    public List<FileInfoOutDTO> findAllFiles(){
        return fileStorageService.loadAll().map(path -> {
                FileInfoOutDTO fileInfoOutDTO = new FileInfoOutDTO();
                String filename = path.getFileName().toString();
                String url = MvcUriComponentsBuilder
                        .fromMethodName(EProcurementController.class, "findFile", path.getFileName().toString()).build().toString();
                fileInfoOutDTO.setName(filename);
                fileInfoOutDTO.setUrl(url);
                return fileInfoOutDTO;
            }).collect(Collectors.toList());
    }

}
