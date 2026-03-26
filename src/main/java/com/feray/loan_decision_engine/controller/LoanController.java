package com.feray.loan_decision_engine.controller;

import com.feray.loan_decision_engine.dto.LoanRequest;
import com.feray.loan_decision_engine.dto.LoanResponse;
import com.feray.loan_decision_engine.service.LoanDecisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/loan")
@RequiredArgsConstructor
public class LoanController {

    private final LoanDecisionService service;

    @PostMapping("/decision")
    public LoanResponse getDecision(@RequestBody LoanRequest request) {
        return service.calculate(request);
    }
}