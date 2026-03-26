package com.feray.loan_decision_engine.service;

import com.feray.loan_decision_engine.dto.LoanRequest;
import com.feray.loan_decision_engine.dto.LoanResponse;
import com.feray.loan_decision_engine.model.Segment;
import com.feray.loan_decision_engine.repository.MockUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanDecisionService {

    private final MockUserRepository userRepository;

    private static final double MIN_AMOUNT = 2000;
    private static final double MAX_AMOUNT = 10000;
    private static final int MIN_PERIOD = 12;
    private static final int MAX_PERIOD = 60;

    public LoanResponse calculate(LoanRequest request) {

        Segment segment = userRepository.findSegmentByPersonalCode(request.getPersonalCode());


        if (segment == Segment.DEBT) {
            return buildResponse(false, 0, 0);
        }

        int creditModifier = segment.getCreditModifier();


        double requestedAmount = Math.max(MIN_AMOUNT, Math.min(request.getLoanAmount(), MAX_AMOUNT));
        int requestedPeriod = Math.max(MIN_PERIOD, Math.min(request.getLoanPeriodMonths(), MAX_PERIOD));

        double maxAmountForRequestedPeriod = creditModifier * requestedPeriod;
        maxAmountForRequestedPeriod = Math.min(maxAmountForRequestedPeriod, MAX_AMOUNT);

        if (maxAmountForRequestedPeriod >= MIN_AMOUNT) {


            if (requestedAmount <= maxAmountForRequestedPeriod) {
                return buildResponse(true, maxAmountForRequestedPeriod, requestedPeriod);
            }

            int requiredPeriod = (int) Math.ceil(requestedAmount / (double) creditModifier);

            if (requiredPeriod >= MIN_PERIOD && requiredPeriod <= MAX_PERIOD) {
                return buildResponse(true, requestedAmount, requiredPeriod);
            }


            double approvedAmount = Math.min(requestedAmount, MAX_AMOUNT);
            int approvedPeriod = (int) Math.ceil(approvedAmount / (double) creditModifier);
            return buildResponse(true, approvedAmount, approvedPeriod);
        }


        int requiredPeriod = (int) Math.ceil(requestedAmount / (double) creditModifier);

        if (requiredPeriod >= MIN_PERIOD && requiredPeriod <= MAX_PERIOD) {
            return buildResponse(true, requestedAmount, requiredPeriod);
        }


        double maxAtMaxPeriod = creditModifier * MAX_PERIOD;
        maxAtMaxPeriod = Math.min(maxAtMaxPeriod, MAX_AMOUNT);

        if (maxAtMaxPeriod >= MIN_AMOUNT) {
            return buildResponse(true, maxAtMaxPeriod, MAX_PERIOD);
        }


        return buildResponse(false, 0, 0);
    }

    private LoanResponse buildResponse(boolean approved, double amount, int period) {
        LoanResponse response = new LoanResponse();
        response.setApproved(approved);
        response.setApprovedAmount(amount);
        response.setApprovedPeriodMonths(period);
        return response;
    }
}