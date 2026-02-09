package com.myprojects.java_bonds_advisor.services;

import com.myprojects.java_bonds_advisor.dto.input.BondDetails;
import com.myprojects.java_bonds_advisor.dto.input.ExtendedBondDetail;
import com.myprojects.java_bonds_advisor.entities.Bond;
import com.myprojects.java_bonds_advisor.mappers.request.BondMapperRequest;
import com.myprojects.java_bonds_advisor.utils.WebClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TService {

    @Autowired
    private BondMapperRequest bondMapperRequest;

    @Autowired
    @Qualifier("myAsyncExecutor")
    private ThreadPoolTaskExecutor executor;

    private static final int BATCH_SIZE = 100;

    /**
     * The main method assembles extra details, including credit rating of companies, which relates to bond
     *
     * @param bondDetails         - a map with BondDetails was got thanks for MoexService
     * @param batchResultConsumer - a result map, which dynamically is being filled up and reflected on the main html page 'bonds'
     */
    public CompletableFuture<Void> enrichBondDetailsInformation(Map<String, BondDetails> bondDetails,
                                                                Consumer<Map<String, Bond>> batchResultConsumer) {

        log.info("Starting TService enrichment on thread: {}", Thread.currentThread().getName());

        List<CompletableFuture<Void>> allBatchFutures = new ArrayList<>();
        List<String> bondDetailsIds = new ArrayList<>(bondDetails.keySet());

        for (int i = 0; i < bondDetailsIds.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, bondDetailsIds.size());
            List<String> batch = bondDetailsIds.subList(i, end);
            final int batchNumber = (i / BATCH_SIZE) + 1;

            // each batch is being handled in async way
            var batchFuture = CompletableFuture.runAsync(() -> {
                log.info("Batch #{} has {} elements", batchNumber, batch.size());
                // an inner of batch data is being handled in parallel way
                processBatchInParallel(batch, bondDetails, batchResultConsumer, batchNumber);
            }, executor);

            allBatchFutures.add(batchFuture);
        }

        return CompletableFuture.allOf(allBatchFutures.toArray(new CompletableFuture[0]));
    }

    private void processBatchInParallel(List<String> batch, Map<String, BondDetails> allDetails,
                                        Consumer<Map<String, Bond>> batchResultConsumer, int batchNumber) {

        // It's necessary in order to avoid of limit request to T-bank web page per one time.
        try (var limitedPool = new ForkJoinPool(2)) {
            limitedPool.submit(() -> {
                Map<String, Bond> batchResult = batch.stream()
                        .parallel()
                        .map(secId -> {
                            try {
                                var extendedBondDetail = fetchBondDetails(secId);
                                if (extendedBondDetail != null && allDetails.containsKey(secId)) {
                                    return Map.entry(secId, bondMapperRequest.mapToEntity(allDetails.get(secId), extendedBondDetail));
                                }
                            } catch (Exception e) {
                                log.debug("Occurred error at here is being handled  bond with secId: #{}", secId, e);
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));

                if (!batchResult.isEmpty()) {
                    batchResultConsumer.accept(batchResult);
                    log.info("Batch {} was successfully treated!", batchNumber);
                }
            }).get();

        } catch (Exception e) {
            log.error("Error batch #{}", batchNumber, e);
        }
    }

    private ExtendedBondDetail fetchBondDetails(String secId) {
        var htmlContent = WebClientUtils.getTBWebPageAsStringByBondTicket(secId);

        if (ObjectUtils.isEmpty(htmlContent))
            return null;

        var document = Jsoup.parse(htmlContent);
        var extendedBondDetail = createExtendedBondDetail(document);
        extendedBondDetail.setSecId(secId);

        return extendedBondDetail;
    }

    /**
     * The auxiliary method, which create entity with extra information about bond (including credit rating of company)
     *
     * @param document - parsed HTML page
     * @return - ready entity
     */
    private ExtendedBondDetail createExtendedBondDetail(Document document) {
        var resultEntity = new ExtendedBondDetail();

        // 1 Take credit rating of the company
        String creditRating = getCreditRatingOfCompany(document);
        resultEntity.setCreditRatingOfCompany(creditRating);

        // 2 Take currency price of the bond
        var currentBondPrice = document.select("div[data-qa-file=TextLineCollapse]").eq(0).text();
        Matcher matcher = Pattern.compile("торгуется по цене (\\d+(\\.\\d+)?)").matcher(currentBondPrice);
        resultEntity.setCurrentBondPrice(matcher.find() ? matcher.group(1) : null);

        // 3. Parsing fields from the BondDetails table (THE LAST PART OF WEB PAGE)
        Elements tableRows = document.select("table[data-qa-file=Table] tr[data-qa-file=TableRow]");

        return getCollectedExtendedBondDetail(tableRows, resultEntity);
    }

    private String getCreditRatingOfCompany(Document document) {

        // 1. Parsing creditRatingOfCompany and offerDate (FIRST PART OF WEB PAGE)
        var onlyCreditRatingOnPage = document.select("div[data-qa-file=SecurityHeader] + " +
                "div.SecurityHeader__panelText_KDJdO").eq(0).text();

        var offerDateOfCreditRatingOfCompany = document.select("div[data-qa-file=SecurityHeader] + " +
                "div.SecurityHeader__panelText_KDJdO").eq(1).text();

        String creditRating;

        // 2. Checking offerDate and yieldToCallOption,
        // coz them impact on index of element, which contains credit rating information
        if (!onlyCreditRatingOnPage.contains("%") && (!offerDateOfCreditRatingOfCompany.contains("%") ||
                !offerDateOfCreditRatingOfCompany.contains("."))) {
            creditRating = document.select("div[data-qa-file=SecurityHeader] div.SecurityHeader__panel" +
                    "Text_KDJdO").eq(0).text();
        } else if (!offerDateOfCreditRatingOfCompany.contains(".")) {
            creditRating = document.select("div[data-qa-file=SecurityHeader] div.SecurityHeader__panel" +
                    "Text_KDJdO").eq(1).text();
        } else {
            creditRating = document.select("div[data-qa-file=SecurityHeader] div.SecurityHeader__panel" +
                    "Text_KDJdO").eq(2).text();
        }

        return creditRating.trim();
    }

    private ExtendedBondDetail getCollectedExtendedBondDetail(Elements tableRows, ExtendedBondDetail resultEntity) {
        for (Element row : tableRows) {

            var title = row.select("div[data-qa-file=BondDetailsTable]").text().toLowerCase(); // get the title/label
            var value = row.select("td[data-qa-file=TableCell]:last-child").text(); // get the value

            var yield = value.contains(",") ? value.replace("%", "")
                    .replace(",", ".").trim() : value.replace("%", "");

            switch (title) {
                case "дата оферты" -> resultEntity.setOfferDate(value);
                case "доходность к оферте" -> resultEntity.setYieldToOfferDate(yield);
                case "дата колл-опциона" -> resultEntity.setBuyBackDate(value);
                case "доходность к колл-опциону" -> resultEntity.setYieldToCallOption(yield);
                case "купон" -> resultEntity.setTypeOfCouponValue(value);
                case "накопленный купонный доход" -> resultEntity.setAccruedCouponIncome(value.contains(",") ?
                        value.split(" ")[0].replace(",", ".").trim() : value.split(" ")[0]);
                case "субординированность" -> resultEntity.setSubordination(value);
                case "амортизация" -> resultEntity.setAmortization(value);
                case "доходность к погашению" -> resultEntity.setYieldToMatDate(yield);
                default -> {
                }
            }
        }
        return resultEntity;
    }
}
