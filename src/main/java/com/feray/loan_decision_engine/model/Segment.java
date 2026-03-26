package com.feray.loan_decision_engine.model;

import lombok.Getter;

@Getter
public enum Segment {
    DEBT(0),
    SEGMENT_1(100),
    SEGMENT_2(300),
    SEGMENT_3(1000);

    private final int creditModifier;

    Segment(int creditModifier) {
        this.creditModifier = creditModifier;
    }
}
