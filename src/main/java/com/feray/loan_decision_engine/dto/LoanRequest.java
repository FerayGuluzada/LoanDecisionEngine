package com.feray.loan_decision_engine.dto;
import lombok.Data;


@Data
public class LoanRequest {
    private String personalCode; //String: may contain leading zeros
    private double loanAmount;
    private int loanPeriodMonths;

}
