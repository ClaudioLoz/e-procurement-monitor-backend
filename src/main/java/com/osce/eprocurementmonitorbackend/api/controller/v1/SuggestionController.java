package com.osce.eprocurementmonitorbackend.api.controller.v1;

import com.osce.eprocurementmonitorbackend.api.dto.SuggestionDTO;
import com.osce.eprocurementmonitorbackend.service.SuggestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/suggestions")
public class SuggestionController {

    private final SuggestionService suggestionService;

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<SuggestionDTO> createSuggestion(@Valid @RequestBody SuggestionDTO suggestionDTO) {
        return new ResponseEntity<>(suggestionService.createSuggestion(suggestionDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SuggestionDTO>> findAllSuggestionsByEProcurementId(@RequestParam Long eProcurementId) {
        return new ResponseEntity<>(suggestionService.findAllSuggestionsByEProcurementId(eProcurementId), HttpStatus.OK);
    }

}
