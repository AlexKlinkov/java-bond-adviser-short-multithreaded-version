package com.myprojects.java_bonds_advisor.contollers;

import com.myprojects.java_bonds_advisor.dto.output.BondUserInfo;
import com.myprojects.java_bonds_advisor.services.BondAdvisorService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Controller
@Tag(name = "BondAdvisorController", description = "This controller provides some information about some bonds " +
        "allocated at MOEX and enriched it by some data from T-Bank web pages.")
public class BondAdvisorController {

    @Autowired
    private BondAdvisorService bondAdvisorService;

    @Operation(
            summary = "Get a list of bonds which can be filtered by some provided criteria.",
            description = """
                    Returns the HTML page with a list of bonds.
                    
                    **Filters:**
                    - Filter by coupon rate
                    - Filter by date
                    - Filtering by currency and also included other parameters...
                    
                    **Pagination:**
                    - Pagination is supported
                    - Default page size: 50 entries
                    
                    **Response format:**
                    - HTML page
                    - Uses Thymeleaf template engine
                    - Page auto-refreshes while data is loading
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Just example of prepared rendered view of HTML page with bonds data",
                            content = @Content(
                                    mediaType = "html",
                                    examples = @ExampleObject(
                                            value = "http://127.0.0.1:8081/example-response-for-swagger.html"
                                    )
                            )
                    )
            }
    )
    @Parameters({
            @Parameter(name = "couponRateNoLessThan", description = "The coupon rate is not less than the specified value.", example = "0.05"),
            @Parameter(name = "placingUntilDate", description = "Your desired term for placing money, up to:", example = "31.12.2025"),
            @Parameter(name = "isQualifiedInvestors", description = "Is it for qualified investors?", example = "No"),
            @Parameter(name = "currency", description = "Bond currency SUR-RUB, USD and so on", example = "SUR"),
            @Parameter(name = "couponFrequency", description = "Coupon payment frequency", example = "Every Month"),
            @Parameter(name = "typeOfCouponValue", description = "Coupon value type", example = "Floating"),
            @Parameter(name = "creditRatingOfCompany", description = "Credit rating of the issuing company", example = "High"),
            @Parameter(name = "listLevel", description = "Listing level", example = "1"),
            @Parameter(name = "isSubordination", description = "Is these bonds subordinated", example = "No"),
            @Parameter(name = "isAmortization", description = "Is these bonds amortizating", example = "Yes"),
            @Parameter(name = "page", description = "Page number (starts with 0)", example = "0"),
            @Parameter(name = "size", description = "Number of records displayed on one page", example = "50")
    })
    @GetMapping("/bonds/list")
    public String getBondAdviceByFilters(@RequestParam(required = false) LocalDate placingUntilDate,
                                         @RequestParam(required = false) BigDecimal couponRateNoLessThan,
                                         @RequestParam(required = false) String currency,
                                         @RequestParam(required = false) String couponFrequency,
                                         @RequestParam(required = false) String creditRatingOfCompany,
                                         @RequestParam(required = false) String listLevel,
                                         @RequestParam(required = false) String typeOfCouponValue,
                                         @RequestParam(required = false) String isSubordination,
                                         @RequestParam(required = false) String isQualifiedInvestors,
                                         @RequestParam(required = false) String isAmortization,
                                         @RequestParam(required = false, defaultValue = "0") int page,
                                         @RequestParam(required = false, defaultValue = "50") int size,
                                         Model model) {

        bondAdvisorService.loadOrUpdateDataIfNecessary(); // uploads data in async way

        Page<BondUserInfo> bondPage = bondAdvisorService.getData(placingUntilDate, couponRateNoLessThan, currency,
                couponFrequency, creditRatingOfCompany, listLevel, typeOfCouponValue, isSubordination, isQualifiedInvestors,
                isAmortization, PageRequest.of(page, size)); // gets  pageable data by batch way

        // fills up the template by prepared data
        bondAdvisorService.fillupModelByBaseParams(model, bondPage);
        bondAdvisorService.fillupModelByFilterParams(model, placingUntilDate, couponRateNoLessThan, currency, couponFrequency,
                creditRatingOfCompany, listLevel, typeOfCouponValue, isSubordination, isQualifiedInvestors, isAmortization);

        return "bonds"; // gives the template, which step by step is being filled up by prepared data
    }

    @Hidden // it hides the endpoint from swagger-ui
    @GetMapping("/example-response-for-swagger.html")
    public String example() {
        return "swagger-example-response";
    }

}
