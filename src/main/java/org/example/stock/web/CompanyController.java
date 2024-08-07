package org.example.stock.web;

import lombok.AllArgsConstructor;
import org.example.stock.model.Company;
import org.example.stock.model.constants.CacheKey;
import org.example.stock.persist.entity.CompanyEntity;
import org.example.stock.service.CompanyService;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final CacheManager cacheManager;

    @GetMapping("autocomplete")
    public ResponseEntity<?> autocompleteCompany(@RequestParam String keyword) {

        /* Trie 자료구조를 이용한 검색완성기능 */
//        var result = this.companyService.autocomplete(keyword);
//        return ResponseEntity.ok(result);

        /*LIKE를 이용한 검색완성기능 -> 중간의 일치 키워드로도 찾을 수 있다는 장점*/
        var result = this.companyService.getCompanyNamesByKeyword(keyword);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(final Pageable pageable) {
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companies);
    }

    /**
     * 회사 & 배당금 정보 추가
     * @param request
     * @return
     */
    @PostMapping
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> addCompany(@RequestBody Company request){
        String ticker = request.getTicker().trim();
        if(ObjectUtils.isEmpty(ticker)){
            throw new RuntimeException("ticker is empty");
        }

        Company company = this.companyService.save(ticker);
        this.companyService.addAutocompleteKeyword(company.getName());
        return ResponseEntity.ok(company);
    }

    // 배당금 정보 삭제하기
    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker){
        String companyName = this.companyService.deleteCompany(ticker);

        // cache 데이터도 지워주기
        this.clearFinanceCache(companyName);

        return ResponseEntity.ok(companyName);
    }

    public void clearFinanceCache(String companyName){
        this.cacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);
    }
}
