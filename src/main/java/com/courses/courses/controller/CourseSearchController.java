package com.courses.courses.controller;

import com.courses.courses.document.CourseDocument;
import com.courses.courses.dto.SearchRequest;
import com.courses.courses.dto.SearchResponse;
import com.courses.courses.service.CourseSearchService;
import com.courses.courses.service.DataIndexingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CourseSearchController {

    private final CourseSearchService courseSearchService;
    private final DataIndexingService dataIndexingService;

    public CourseSearchController(CourseSearchService courseSearchService, DataIndexingService dataIndexingService) {
        this.courseSearchService = courseSearchService;
        this.dataIndexingService = dataIndexingService;
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> searchCourses(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) CourseDocument.CourseType type,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(defaultValue = "UPCOMING") SearchRequest.SortOption sort,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        SearchRequest request = new SearchRequest();
        request.setQ(q);
        request.setMinAge(minAge);
        request.setMaxAge(maxAge);
        request.setCategory(category);
        request.setType(type);
        request.setMinPrice(minPrice);
        request.setMaxPrice(maxPrice);
        request.setStartDate(startDate);
        request.setSort(sort);
        request.setPage(page);
        request.setSize(size);

        SearchResponse response = courseSearchService.searchCourses(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/suggest")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam String q) {

        List<String> suggestions = courseSearchService.getSuggestions(q);

        return ResponseEntity.ok(suggestions);
    }

    @PostMapping("/reindex")
    public ResponseEntity<String> reindexData() {
        try {
            dataIndexingService.reindexData();
            return ResponseEntity.ok("Data reindexed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Reindex failed: " + e.getMessage());
        }
    }

        @GetMapping("/health")
        public ResponseEntity<String> health () {
            return ResponseEntity.ok("Course Search API is running");
        }
    }