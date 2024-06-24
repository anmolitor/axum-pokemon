package com.example.benchmarks.pokemon.domain;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Scope("singleton")
public class EVGenerator {
    private static final Random random = new Random();

    private static final int MAX_EV = 252;
    private static final int MAX_TOTAL_EVS = 510;

    public Stats<Integer> generate() {
        // 510 total evs are to be fairly distributed among all 6 stats
        int[] evs = new int[6];
        int totalEVsDistributed = 0;
        while (totalEVsDistributed < MAX_TOTAL_EVS) {
            int statIndex = random.nextInt(evs.length);
            // Distribute EVs to a stat if it is under 252
            if (evs[statIndex] < MAX_EV) {
                int evsToDistribute = random.nextInt(MAX_EV - evs[statIndex]) + 1;
                evs[statIndex] += evsToDistribute;
                totalEVsDistributed += evsToDistribute;
            }
        }
        return new Stats<>(evs[0], evs[1], evs[2], evs[3], evs[4], evs[5]);
    }
}
