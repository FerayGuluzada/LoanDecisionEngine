package com.feray.loan_decision_engine.repository;

import com.feray.loan_decision_engine.model.Segment;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class MockUserRepository {

    private static final Map<String, Segment> USERS = Map.of(
            "49002010965", Segment.DEBT,
            "49002010976", Segment.SEGMENT_1,
            "49002010987", Segment.SEGMENT_2,
            "49002010998", Segment.SEGMENT_3
    );

    public Segment findSegmentByPersonalCode(String personalCode) {
        return USERS.getOrDefault(personalCode, Segment.SEGMENT_1);
    }
}