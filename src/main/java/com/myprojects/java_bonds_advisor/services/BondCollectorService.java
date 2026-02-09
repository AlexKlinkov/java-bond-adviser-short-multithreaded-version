package com.myprojects.java_bonds_advisor.services;

import com.myprojects.java_bonds_advisor.dto.input.BondDetails;
import com.myprojects.java_bonds_advisor.entities.Bond;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BondCollectorService {

    @Autowired
    private MoexService moexService;

    @Autowired
    private TService tService;

    @Getter
    private Map<String, Bond> mapWithPreparedBond = new ConcurrentHashMap<>();
    @Getter
    private boolean isLoading;
    @Getter
    private int bondCount;
    @Getter
    private long theLastTimeDataWasUpdated;


    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStartup() {
        log.debug("Application ready, initializing BondCollectorService...");
        loadData();
    }

    public void loadData() {
        log.info("=== STARTING BOND DATA LOAD ===");
        long startTime = System.currentTimeMillis();

        if (isLoading) { // prevent multiple concurrent loads
            log.info("Data loading already in progress, so be patient!");
            return;
        } else {
            isLoading = true;
        }

        Map<String, BondDetails> bondDetails = moexService.getDetailInformationAboutBonds().stream()
                .collect(Collectors.toConcurrentMap(BondDetails::getSecId, Function.identity(),
                        (existing, replacement) -> existing));

        if (bondDetails.isEmpty()) {
            log.error("No bond details received from MOEX");
            isLoading = false;
            return;
        }

        log.info("Requesting T-Bank data for {} bonds...", bondDetails.size());
        tService.enrichBondDetailsInformation(bondDetails,
                        batchResult -> {
                            mapWithPreparedBond.putAll(batchResult); // Add batch results to main map
                            log.info("Collected batch, total enriched so far: {}", mapWithPreparedBond.size());
                            bondCount = bondDetails.size();
                        })
                .exceptionally(ex -> {
                    log.error("=== ASYNC DATA LOAD FAILED ===", ex);
                    isLoading = false;
                    return null;
                })
                .whenComplete((result, ex) -> {
                    log.info("=== ASYNC DATA LOAD COMPLETE ===");
                    log.info("✓ Successfully loaded {} bonds", bondDetails.size());

                    // Fixes how much time the service handle all data
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("The service accomplished async tasks for: {} min", (duration / 60000.0));

                    theLastTimeDataWasUpdated = System.currentTimeMillis();
                    isLoading = false;
                });

    }

}
