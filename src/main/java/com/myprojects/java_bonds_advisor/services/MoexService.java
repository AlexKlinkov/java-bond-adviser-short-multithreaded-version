package com.myprojects.java_bonds_advisor.services;

import com.google.gson.*;
import com.myprojects.java_bonds_advisor.dto.input.BondDetails;
import com.myprojects.java_bonds_advisor.dto.input.BondHistoryInformation;
import com.myprojects.java_bonds_advisor.utils.MoexDtoCollectorUtils;
import com.myprojects.java_bonds_advisor.utils.WebClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class MoexService {

    private final int AMOUNT_OF_ELEMENTS_ON_ONE_PAGE = 100;

    /**
     * The method provides bond's detail information from MOEX
     *
     * @return - a set of BondDetails
     */
    public Set<BondDetails> getDetailInformationAboutBonds() {
        try {
            // 1. Get bonds history information
            log.info("Starting to fetch bond information from MOEX...");
            @SuppressWarnings("unchecked")
            Set<BondHistoryInformation> bondHistory = (Set<BondHistoryInformation>) getObjectIfObjIsNotEmptyOrNull(
                    getBondsHistoryInformation(), "No bonds found in MOEX history");

            // 2. Get bonds detail information based on history one
            log.info("Getting details for {} bonds", bondHistory.size());
            Set<BondDetails> bondDetails = getBondDetailsSimpleParallel(bondHistory.stream()
                    .map(BondHistoryInformation::getSecId).collect(Collectors.toSet()));

            log.info("Successfully retrieved details for {} bonds", bondDetails.size());
            return bondDetails; // enriched information about bonds

        } catch (Exception e) {
            log.error("Error fetching bond details from MOEX", e);
            return Collections.emptySet();
        }
    }

    // (MULTITHREADED METHOD)
    private Set<BondHistoryInformation> getBondsHistoryInformation() {
        try {
            // 1. Get the first page to know total count elements
            log.info("Fetching bonds history from MOEX...");
            var firstPageJson = (String) getObjectIfObjIsNotEmptyOrNull(
                    WebClientUtils.getMOEXBondHistoryAsString(AMOUNT_OF_ELEMENTS_ON_ONE_PAGE, 0),
                    "Empty response from MOEX history API");

            // 2. Parse total elements
            var root = (JsonObject) getObjectIfObjIsNotEmptyOrNull(
                    JsonParser.parseString(firstPageJson).getAsJsonObject(), "No root in MOEX response");
            var historyCursor = (JsonObject) getObjectIfObjIsNotEmptyOrNull(
                    root.getAsJsonObject("history.cursor"), "No history.cursor in MOEX response");
            var cursorData = (JsonArray) getObjectIfObjIsNotEmptyOrNull(
                    historyCursor.getAsJsonArray("data"), "No cursor data in MOEX response");

            // 3. Gets a number of total elements and common count of pages, where these elements are located
            int totalElements = cursorData.get(0).getAsJsonArray().get(1).getAsInt();
            int totalPages = (int) Math.ceil(totalElements / (double) AMOUNT_OF_ELEMENTS_ON_ONE_PAGE);
            log.info("Total elements: {}, Processing {} pages", totalElements, totalPages);

            Set<BondHistoryInformation> allBonds = ConcurrentHashMap.newKeySet();
            // 4. Process pages in parallel way
            IntStream.range(0, totalPages)
                    .parallel()  // Uses common ForkJoinPool
                    .mapToObj(this::fetchPage)
                    .forEach(allBonds::addAll);

            log.info("Total bonds collected from history: {}", allBonds.size());
            return allBonds;

        } catch (Exception e) {
            log.error("Error getting bonds history", e);
            return Collections.emptySet();
        }
    }

    private Set<BondHistoryInformation> fetchPage(int page) {
        try {
            // 1. Gets a page with bond information
            var pageJson = (String) getObjectIfObjIsNotEmptyOrNull(
                    WebClientUtils.getMOEXBondHistoryAsString(AMOUNT_OF_ELEMENTS_ON_ONE_PAGE,
                            page * AMOUNT_OF_ELEMENTS_ON_ONE_PAGE), String.format("Empty response for page: %s", page));

            // 2. Gets separate parts of the page
            var history = (JsonObject) getObjectIfObjIsNotEmptyOrNull(
                    JsonParser.parseString(pageJson).getAsJsonObject().getAsJsonObject("history"),
                    String.format("No history object in page: %s", page));
            var columns = (JsonArray) getObjectIfObjIsNotEmptyOrNull(history.getAsJsonArray("columns"),
                    String.format("Invalid columns data structure in page: %s", page));
            var data = (JsonArray) getObjectIfObjIsNotEmptyOrNull(history.getAsJsonArray("data"),
                    String.format("Invalid data structure in page: %s", page));

            // 3. Fills up a BondHistoryInformation set
            Set<BondHistoryInformation> pageBonds = new HashSet<>();
            for (JsonElement element : data) {
                try {
                    BondHistoryInformation bond = MoexDtoCollectorUtils.createBondByJsonInnerElems(
                            element.getAsJsonArray(), MoexDtoCollectorUtils.getMapWithIndexOfInnerJsonElem(columns));
                    pageBonds.add(bond);
                } catch (Exception e) {
                    log.debug("Error parsing bond element on page {}: {}", page, e.getMessage());
                }
            }

            log.debug("Fetched page {} with {} bonds", page + 1, pageBonds.size());
            return pageBonds;

        } catch (Exception e) {
            log.error("Error fetching page {}: {}", page, e.getMessage());
            return Collections.emptySet();
        }
    }


    /**
     * The method in parallel way gets extra info about bonds based on their secIds
     *
     * @param secIds - set with unique id of bonds
     * @return a set with prepared entities 'BondDetails'
     */
    private Set<BondDetails> getBondDetailsSimpleParallel(Set<String> secIds) {
        log.info("Processing {} bonds", secIds.size());

        return secIds.parallelStream()
                .map(secId -> {
                    try {
                        String response = WebClientUtils.getMOEXBondDetailsFromWebPage(secId);
                        return !ObjectUtils.isEmpty(response) ? parseBondDetails(response, secId) : null;
                    } catch (Exception e) {
                        log.debug("Failed bond {}: {}", secId, e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private BondDetails parseBondDetails(String jsonResponse, String secId) {
        try {
            // 1. Parse total elements
            var root = (JsonObject) getObjectIfObjIsNotEmptyOrNull(JsonParser.parseString(jsonResponse).getAsJsonObject(),
                    String.format("No root object for bond: %s", secId));
            var description = (JsonObject) getObjectIfObjIsNotEmptyOrNull(
                    root.getAsJsonObject("description"), String.format("No description object for bond: %s", secId));
            var dataArray = (JsonArray) getObjectIfObjIsNotEmptyOrNull(description.getAsJsonArray("data"),
                    String.format("No data array for bond: %s", secId));


            // 2. Parse fields from a dataArray
            Map<String, String> fieldMap = new HashMap<>();
            for (int i = 0; i < dataArray.size(); i++) {
                try {
                    var fieldArray = dataArray.get(i).getAsJsonArray();
                    fieldMap.put(fieldArray.get(0).getAsString(), fieldArray.get(2).getAsString());
                } catch (Exception e) {
                    log.error("Error parsing bond details for bond: {}", secId, e);
                }
            }

            // 3. Creates BondDetails using utility and return
            return MoexDtoCollectorUtils.createMOEXBondDetails(fieldMap, secId);

        } catch (JsonSyntaxException e) {
            log.debug("Invalid JSON response for bond {}: {}", secId, e.getMessage());
            return null;
        } catch (Exception e) {
            log.debug("Error parsing bond details for {}: {}", secId, e.getMessage());
            return null;
        }
    }

    private Object getObjectIfObjIsNotEmptyOrNull(Object inputObject, String logMessageError) {
        if (!ObjectUtils.isEmpty(inputObject))
            return inputObject;

        log.error(logMessageError);
        throw new RuntimeException();
    }

}
