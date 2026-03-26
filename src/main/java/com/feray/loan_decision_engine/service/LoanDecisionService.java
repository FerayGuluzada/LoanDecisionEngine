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

        double bestAmount = 0;
        int bestPeriod = 0;


        for (int period = MIN_PERIOD; period <= MAX_PERIOD; period++) {


            double maxAmountForPeriod = creditModifier * period;


            if (maxAmountForPeriod < MIN_AMOUNT) {
                continue;
            }


            maxAmountForPeriod = Math.min(maxAmountForPeriod, MAX_AMOUNT);


            if (maxAmountForPeriod > bestAmount) {
                bestAmount = maxAmountForPeriod;
                bestPeriod = period;
            }
        }


        if (bestAmount == 0) {
            return buildResponse(false, 0, 0);
        }

        return buildResponse(true, bestAmount, bestPeriod);
    }

    private LoanResponse buildResponse(boolean approved, double amount, int period) {
        LoanResponse response = new LoanResponse();
        response.setApproved(approved);
        response.setApprovedAmount(amount);
        response.setApprovedPeriodMonths(period);
        return response;
    }
}