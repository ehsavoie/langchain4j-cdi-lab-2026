package com.example.demo6.scorer;

import jakarta.enterprise.context.ApplicationScoped;

public interface StyleScorer {

    double scoreStyle(String story, String style);
}
