package com.minetec.backend.controller.warehouse;

import com.minetec.backend.dto.filter.workshop.StockFilterForm;
import com.minetec.backend.service.StockService;
import com.minetec.backend.util.Response;
import com.minetec.backend.util.RestResponseFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author Sinan
 */

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stock")
@Secured(value = {"ROLE_ADMIN"})
public class StockController {

    private final StockService stockService;
    private final RestResponseFactory respFactory;

    @PostMapping("/find-all-stock")
    public ResponseEntity findAllStock(@RequestBody @Valid final StockFilterForm form) {
        Pageable pageable = PageRequest.of(form.getPage(), form.getSize(),
            Sort.by(Sort.Order.desc("createdAt")));
        return Response.ok(respFactory.success("18b4ed06d054", "find-all-stock-by-filter",
            stockService.findByStockHistories(form, pageable)));
    }

    /**
     * when xls methods call, fe should be different browser page
     *
     * @param response
     * @param filterForm
     * @throws Exception
     */
    @PostMapping(value = "/xls")
    public void xls(final HttpServletResponse response,
                    @RequestBody @Valid final StockFilterForm filterForm) throws Exception {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + RandomUtils.nextLong());
        stockService.xlsGenerator(response.getOutputStream(), filterForm);
    }

}
