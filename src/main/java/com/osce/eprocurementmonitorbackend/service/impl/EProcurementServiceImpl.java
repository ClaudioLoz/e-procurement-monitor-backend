package com.osce.eprocurementmonitorbackend.service.impl;

import com.osce.eprocurementmonitorbackend.api.controller.v1.EProcurementController;
import com.osce.eprocurementmonitorbackend.api.dto.EProcurementDTO;
import com.osce.eprocurementmonitorbackend.api.dto.EProcurementDetailOutDTO;
import com.osce.eprocurementmonitorbackend.api.dto.EProcurementOutDTO;
import com.osce.eprocurementmonitorbackend.api.dto.FileInfoOutDTO;
import com.osce.eprocurementmonitorbackend.enums.ProcurementStatus;
import com.osce.eprocurementmonitorbackend.model.AuthUser;
import com.osce.eprocurementmonitorbackend.model.EProcurement;
import com.osce.eprocurementmonitorbackend.model.FileInfo;
import com.osce.eprocurementmonitorbackend.repository.AuthUserRepository;
import com.osce.eprocurementmonitorbackend.security.services.UserDetailsImpl;
import com.osce.eprocurementmonitorbackend.repository.EProcurementRepository;
import com.osce.eprocurementmonitorbackend.repository.FileInfoRepository;
import com.osce.eprocurementmonitorbackend.security.services.Utils;
import com.osce.eprocurementmonitorbackend.service.CommentService;
import com.osce.eprocurementmonitorbackend.service.EProcurementService;
import com.osce.eprocurementmonitorbackend.service.FileStorageService;
import com.osce.eprocurementmonitorbackend.service.RatingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
@Transactional
@Service
public class EProcurementServiceImpl implements EProcurementService {


    private final EProcurementRepository eProcurementRepository;

    private final String blockchainURL = "http://192.168.1.36:7545";

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private CommentService commentService;

    public EProcurementServiceImpl(EProcurementRepository eProcurementRepository) {
        this.eProcurementRepository = eProcurementRepository;
    }

    @Override
    public EProcurementDTO createEProcurement(EProcurement eProcurement, MultipartFile[] files) {
        UserDetailsImpl userDetails =
                (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AuthUser authUser = new AuthUser();
        authUser.setId(userDetails.getId());
        eProcurement.setUser(authUser);
        EProcurement savedEProcurement = eProcurementRepository.save(eProcurement);
        HashMap<Long, String> encryptedFiles = new HashMap<>();
        Arrays.asList(files).stream().forEach(file -> {

            try {
                String hash = Utils.getPDFHash(file.getInputStream());
                fileStorageService.save(file, hash);
                FileInfo fileInfo = new FileInfo();
//                fileInfo.setHash(hash);
                fileInfo.setEprocurement(savedEProcurement);
                fileInfoRepository.save(fileInfo);
                String encryptedFile = Utils.encrypt(String.valueOf(savedEProcurement.getId())
                                    , hash + "|" + file.getOriginalFilename());
                encryptedFiles.put(fileInfo.getId(), encryptedFile);
            } catch (IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        EProcurementDTO eProcurementDTO = new EProcurementDTO();
        eProcurementDTO.setEProcurement(savedEProcurement);
        eProcurementDTO.setEncryptedFiles(encryptedFiles);
        return eProcurementDTO;
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
            AuthUser authUser = authUserRepository.findById(eProcurement.getUser().getId()).orElseThrow();
            eProcurementOutDTO.setUsername(authUser.getNin());
            eProcurementOutDTO.setTotalRatingAverage(
                    ratingService.calculateTotalRatingAverageByEProcurementId(eProcurementOutDTO.getId()));
            eProcurementOutDTO.setTotalCommentCount(commentService.calculateTotalCommentCountByEProcurementId(eProcurementOutDTO.getId()));
            return eProcurementOutDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<EProcurementOutDTO> findAllEProcurementsByStatus(String procurementStatus) {
        return eProcurementRepository.findAllByProcurementStatusIs(ProcurementStatus.valueOf(procurementStatus))
                .stream().map(eProcurement -> {
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
                    AuthUser authUser = authUserRepository.findById(eProcurement.getUser().getId()).orElseThrow();
                    eProcurementOutDTO.setUsername(authUser.getNin());
                    return eProcurementOutDTO;
                }).collect(Collectors.toList());
    }

    @Override
    public EProcurementDetailOutDTO findEProcurementById(Long id, String isDetailed) {
        EProcurement foundEProcurement = eProcurementRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
        EProcurementDetailOutDTO eProcurementDetailOutDTO = new EProcurementDetailOutDTO();
        eProcurementDetailOutDTO.setEProcurement(foundEProcurement);
        eProcurementDetailOutDTO.setTotalRatingAverage(
                ratingService.calculateTotalRatingAverageByEProcurementId(eProcurementDetailOutDTO.getEProcurement().getId()));
        if(StringUtils.isNotBlank(isDetailed)) return eProcurementDetailOutDTO; //default blank

        Web3j web3j = null;
        try {
            // Connect to an Ethereum node
            web3j = Web3j.build(new HttpService(blockchainURL));
            // Check if the connection is successful
            Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
            if (web3ClientVersion.hasError()) {
                System.out.println("Error: " + web3ClientVersion.getError().getMessage());
            } else {
                System.out.println("Connection successful! Client version: " + web3ClientVersion.getWeb3ClientVersion());
            }
        } catch (Exception e) {
            System.out.println("Error occurred on Web3j: " + e.getMessage());
        }

        // Contract address and ABI
        String contractAddress = "0xA4376fe6461e0110332edb473FeA183536e6ad90";
        String contractABI = "[{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"_idProcurementDocument\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"_idProcurement\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"_procurementDocument\",\"type\":\"string\"}],\"name\":\"addProcurementDocument\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"id\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"idProcurement\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"string\",\"name\":\"encryptedProcurementDocument\",\"type\":\"string\"},{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"createdAt\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"bool\",\"name\":\"valid\",\"type\":\"bool\"}],\"name\":\"ProcurementDocumentAdded\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"id\",\"type\":\"uint256\"},{\"indexed\":false,\"internalType\":\"bool\",\"name\":\"valid\",\"type\":\"bool\"}],\"name\":\"ProcurementDocumentRemoved\",\"type\":\"event\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"_idProcurementDocument\",\"type\":\"uint256\"}],\"name\":\"removeProcurementDocument\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"name\":\"documents\",\"outputs\":[{\"internalType\":\"uint256\",\"name\":\"id\",\"type\":\"uint256\"},{\"internalType\":\"uint256\",\"name\":\"idProcurement\",\"type\":\"uint256\"},{\"internalType\":\"string\",\"name\":\"encryptedProcurementDocument\",\"type\":\"string\"},{\"internalType\":\"uint256\",\"name\":\"createdAt\",\"type\":\"uint256\"},{\"internalType\":\"bool\",\"name\":\"valid\",\"type\":\"bool\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"name\":\"owner\",\"outputs\":[{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],\"stateMutability\":\"view\",\"type\":\"function\"}]";

        // Function parameters
        String functionName = "documents";
        Web3j auxWeb3j = web3j;
        List<FileInfoOutDTO> fileInfoOutDTOList = fileInfoRepository.findAllByEprocurement_Id(foundEProcurement.getId())
                                                    .stream().map(fileInfo -> {
                                                        FileInfoOutDTO fileInfoOutDTO = new FileInfoOutDTO();

                                                        // Prepare the function call
                                                        Function function = new Function(
                                                                functionName,
                                                                Arrays.asList(new Uint256(fileInfo.getId())),
                                                                Arrays.asList(new TypeReference<Utf8String>() {})
                                                        );

                                                        String encodedFunction = FunctionEncoder.encode(function);

                                                         // Call the smart contract function
                                                        EthCall response = null;
                                                        try {
                                                            response = auxWeb3j.ethCall(
                                                                            Transaction.createEthCallTransaction(
                                                                                    "0x4494845cAB377d218F58A291A19b0D1e824b194E", contractAddress, encodedFunction),
                                                                            DefaultBlockParameterName.LATEST)
                                                                    .send();

                                                            if (response.hasError()) {
                                                                System.out.println("Error: " + response.getError().getMessage());
                                                                return fileInfoOutDTO;
                                                            }

                                                            String result = response.getValue();
                                                            // Remove the "0x" prefix before decoding
                                                            String strippedOutput = result.substring(2);
                                                            // Decode the output into a list of Type objects

                                                            List<TypeReference<?>> typeReferences = new ArrayList<>();
                                                            typeReferences.add(new TypeReference<Uint256>() {});
                                                            typeReferences.add(new TypeReference<Uint256>() {});
                                                            typeReferences.add(new TypeReference<Utf8String>() {});
                                                            typeReferences.add(new TypeReference<Uint256>() {});
                                                            typeReferences.add(new TypeReference<Bool>() {});

                                                            List<Type> decodedOutput = FunctionReturnDecoder.decode(strippedOutput
                                                                                            , (List<TypeReference<Type>>) (List<?>) typeReferences);
                                                            String decryptedFile = Utils.decrypt(String.valueOf(fileInfo.getEprocurement().getId())
                                                                    , decodedOutput.get(2).toString());
                                                            System.out.println(decryptedFile);
                                                            String decryptedHash = decryptedFile.split("\\|")[0];
                                                            Path path = fileStorageService.loadByName(decryptedHash);
                                                            String filename = decryptedFile.split("\\|")[1];
                                                            String url = MvcUriComponentsBuilder
                                                                    .fromMethodName(EProcurementController.class, "findFile", path.getFileName().toString()).build().toString();
                                                            fileInfoOutDTO.setName(filename);
                                                            fileInfoOutDTO.setUrl(url);

                                                        } catch (Exception e) {
                                                            System.out.println("Error occurred: " + e.getMessage());
                                                        }
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
