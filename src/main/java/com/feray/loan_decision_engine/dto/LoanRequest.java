package com.feray.loan_decision_engine.dto;
import lombok.Data;


@Data
public class LoanRequest {
    private String personalCode;
    private double loanAmount;
    private int loanPeriodMonths;

}
