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

    // Repository to fetch user segment information based on personal code
    private final MockUserRepository userRepository;

    private static final double MIN_AMOUNT = 2000;
    private static final double MAX_AMOUNT = 10000;
    private static final int MIN_PERIOD = 12;
    private static final int MAX_PERIOD = 60;

    // Decision strategy:
    // The task allows two interpretations:
    // 1) Return maximum possible loan regardless of input
    // 2) Start from requested values and adjust if needed
    //
    // This implementation follows 1, as the task states to find the maximum sum
    // "regardless of the requested amount", so I compute the global maximum
    // across all periods.
    //
    // Note: A real system could first evaluate the requested amount/period
    // and only fall back to alternatives to better reflect user intent.
    public LoanResponse calculate(LoanRequest request) {

        Segment segment = userRepository.findSegmentByPersonalCode(request.getPersonalCode());

        // 1. Debt -> Reject
        // Thought process: Always reject debt first, before any calculations.

        if (segment == Segment.DEBT) {
            return buildResponse(false, 0, 0);
        }

        int creditModifier = segment.getCreditModifier();

        // 2. Clamp inputs
        // Thought process: Frontend handles this, but to be safe never let an amount be out of allowed scope
        double requestedAmount = Math.max(MIN_AMOUNT, Math.min(request.getLoanAmount(), MAX_AMOUNT));
        int requestedPeriod = Math.max(MIN_PERIOD, Math.min(request.getLoanPeriodMonths(), MAX_PERIOD));

        // 3. Find best possible loan
        // Thought process:
        // - Loop over all allowed periods (12–60 months) to find maximum approvable loan
        // - Instead of calculating the full credit score formula each time ((credit_modifier / loan_amount) * loan_period >= 1),
        //   rearrange it to max_loan_amount <= credit_modifier * loan_period.
        //   This allows to directly find the maximum possible loan for a given period, skipping unnecessary checks.
        // - Cap the result at MAX_AMOUNT and only consider amounts above MIN_AMOUNT.
        // - By doing this, pick the period that gives the largest possible loan without extra calculations.
        double bestAmount = 0;
        int bestPeriod = 0;

        for (int period = MIN_PERIOD; period <= MAX_PERIOD; period++) {

            // Derived from formula: (credit_modifier / loan_amount) * loan_period >= 1
            double maxAmountForPeriod = creditModifier * period;

            maxAmountForPeriod = Math.min(maxAmountForPeriod, MAX_AMOUNT);

            if (maxAmountForPeriod >= MIN_AMOUNT) {
                if (maxAmountForPeriod > bestAmount) {
                    bestAmount = maxAmountForPeriod;
                    bestPeriod = period;
                }
            }
        }

        // 4. If nothing valid → reject
        if (bestAmount < MIN_AMOUNT) {
            return buildResponse(false, 0, 0);
        }

        // 5. If requested amount is acceptable, return BEST
        if (requestedAmount <= bestAmount) {
            return buildResponse(true, bestAmount, bestPeriod);
        }

        // 6. Otherwise return maximum possible
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