package com.feray.loan_decision_engine.dto;
import lombok.Data;

@Data
public class LoanResponse {
    private boolean approved;
    private double approvedAmount;
    private int approvedPeriodMonths;
}
