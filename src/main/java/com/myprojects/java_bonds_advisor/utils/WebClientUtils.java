package com.myprojects.java_bonds_advisor.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Slf4j
@UtilityClass
public class WebClientUtils {

    private static final int TIME_OUT_REQUEST_SECONDS = 30;
    private static final WebClient MOEX_WEB_CLIENT = WebClient.builder().baseUrl("https://iss.moex.com").build();

    // variables for MOEX queries
    private static final String MOEX_URL_WITH_COMMON_HISTORY_INFO_ABOUT_BOUNDS =
            "/iss/history/engines/stock/markets/bonds/securities.json";
    private static final String MOEX_URL_PATTERN_FOR_GET_DETAIL_INFO_ABOUT_EACH_BOND =
            "/iss/securities/{secId}.json";

    private static final WebClient TB_WEB_CLIENT = WebClient.builder().baseUrl("https://www.tbank.ru")
            // by default size is 256Kb. it's not enough to take a page and parse it. Set up 2MB
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1048576 * 2))
            .build();

    // variables for T-Bank queries
    private static final String TB_URL_WITH_PARTICULAR_BOND_ID = "/invest/bonds/{secId}/";


    /**
     * The auxiliary method, which sends request for getting MOEX history info, which stored on a web page
     *
     * @param limitObjectsOnPage - max quantity of objects (JsonArray) on a page
     * @param startFromPage      - number of objects which has to be skipped
     * @return - a page with info as a string from free MOEX API in a json format
     */
    public static String getMOEXBondHistoryAsString(int limitObjectsOnPage, int startFromPage) {
        return MOEX_WEB_CLIENT.get()
                .uri(String.format("%s?limit=%d&start=%d",
                        MOEX_URL_WITH_COMMON_HISTORY_INFO_ABOUT_BOUNDS, limitObjectsOnPage, startFromPage))
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(TIME_OUT_REQUEST_SECONDS))
                .doOnError(e -> log.error("Failed to fetch history bond info from MOEX at the page: {}", startFromPage, e))
                .block();
    }

    /**
     * The method for getting MOEX detail information about particular bond from web page
     *
     * @param secId - unique value of bond's id
     * @return - detail information about bond as string in a json format
     */
    public static String getMOEXBondDetailsFromWebPage(String secId) {
        return MOEX_WEB_CLIENT.get()
                .uri(MOEX_URL_PATTERN_FOR_GET_DETAIL_INFO_ABOUT_EACH_BOND, secId)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(TIME_OUT_REQUEST_SECONDS))
                .doOnError(e -> log.error("Failed to fetch detail bond info from MOEX by secId: {}", secId, e))
                .block();
    }

    /**
     * The method makes a web request to T-Bank a web page which contains extra information about bond
     *
     * @param secId - unique value of bond's id
     * @return - a page of web resource as a string in html format
     */
    public static String getTBWebPageAsStringByBondTicket(String secId) {
        // 1. getting HTML content
        return TB_WEB_CLIENT.get()
                .uri(TB_URL_WITH_PARTICULAR_BOND_ID, secId)
                .retrieve()
                .bodyToMono(String.class)
                .delayElement(Duration.ofSeconds(2))
                .timeout(Duration.ofSeconds(TIME_OUT_REQUEST_SECONDS))
                .doOnError(e -> log.error("Failed to fetch bond info from T-Bank by secId: {}", secId))
                .block();
    }
}
