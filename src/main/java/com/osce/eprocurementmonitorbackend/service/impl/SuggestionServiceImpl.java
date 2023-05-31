package com.osce.eprocurementmonitorbackend.service.impl;

import com.osce.eprocurementmonitorbackend.api.dto.SuggestionDTO;
import com.osce.eprocurementmonitorbackend.model.AuthUser;
import com.osce.eprocurementmonitorbackend.model.EProcurement;
import com.osce.eprocurementmonitorbackend.model.Suggestion;
import com.osce.eprocurementmonitorbackend.repository.AuthUserRepository;
import com.osce.eprocurementmonitorbackend.repository.EProcurementRepository;
import com.osce.eprocurementmonitorbackend.repository.SuggestionRepository;
import com.osce.eprocurementmonitorbackend.security.services.UserDetailsImpl;
import com.osce.eprocurementmonitorbackend.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SuggestionServiceImpl implements SuggestionService {

    private final SuggestionRepository suggestionRepository;

    @Autowired
    private EProcurementRepository eProcurementRepository;
    @Autowired
    private AuthUserRepository authUserRepository;

    public SuggestionServiceImpl(SuggestionRepository suggestionRepository) {
        this.suggestionRepository = suggestionRepository;
    }


    @Override
    public SuggestionDTO createSuggestion(SuggestionDTO suggestionDTO) {
        Suggestion suggestion = new Suggestion();

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AuthUser authUser = new AuthUser();
        authUser.setId(userDetails.getId());
        suggestion.setUser(authUser);
        EProcurement foundEProcurement = eProcurementRepository.findById(suggestionDTO.getEProcurementId())
                .orElseThrow(() -> new IllegalArgumentException("EProcurement not found with ID: " + suggestionDTO.getEProcurementId()));
        suggestion.setEprocurement(foundEProcurement);

        suggestion.setText(suggestionDTO.getText());
        suggestionRepository.save(suggestion);
        suggestionDTO.setId(suggestion.getId());
        suggestionDTO.setAuthUserName(userDetails.getName());
        suggestionDTO.setCreatedDate(suggestion.getCreatedDate());
        return suggestionDTO;
    }


    @Override
    public List<SuggestionDTO> findAllSuggestionsByEProcurementId(Long eProcurementId) {
        return suggestionRepository.findAllByEprocurement_Id(eProcurementId).stream().map(suggestion -> {
            SuggestionDTO suggestionDTO = new SuggestionDTO();
            suggestionDTO.setEProcurementId(suggestion.getEprocurement().getId());
            suggestionDTO.setText(suggestion.getText());
            suggestionDTO.setId(suggestion.getId());
            suggestionDTO.setCreatedDate(suggestion.getCreatedDate());
            AuthUser authUser = authUserRepository.findById(suggestion.getUser().getId()).orElseThrow();
            suggestionDTO.setAuthUserName(authUser.getName());
            return suggestionDTO;
        }).collect(Collectors.toList());
    }

}
