package com.courses.courses;

import com.courses.courses.document.CourseDocument;
import com.courses.courses.dto.SearchRequest;
import com.courses.courses.dto.SearchResponse;
import com.courses.courses.service.CourseSearchService;
import com.courses.courses.service.DataIndexingService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@Testcontainers
class CourseSearchServiceTest {

    @Container
    static ElasticsearchContainer elasticsearchContainer =
            new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.11.0")
                    .withEnv("xpack.security.enabled", "false");

    @DynamicPropertySource
    static void elasticsearchProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", elasticsearchContainer::getHttpHostAddress);
    }

    @Autowired
    private CourseSearchService courseSearchService;

    @Autowired
    private DataIndexingService dataIndexingService;

    @BeforeEach
    void setup() throws InterruptedException {
        dataIndexingService.reindexData();
//        Thread.sleep(2000);
    }

    @Test
    void testFullTextSearch() {
        SearchRequest req = new SearchRequest();
        req.setQ("algebra");

        SearchResponse response = courseSearchService.searchCourses(req);

        assertThat(response.getTotal()).isGreaterThan(0);
        assertThat(response.getCourses()).anyMatch(c -> c.getTitle().toLowerCase().contains("algebra"));
    }

    @Test
    void testPriceRangeFilter() {
        SearchRequest req = new SearchRequest();
        req.setMinPrice(BigDecimal.valueOf(30));
        req.setMaxPrice(BigDecimal.valueOf(60));

        SearchResponse response = courseSearchService.searchCourses(req);

        assertThat(response.getCourses()).allSatisfy(c ->
                assertThat(c.getPrice()).isBetween(BigDecimal.valueOf(30), BigDecimal.valueOf(60))
        );
    }

    @Test
    void testSortingByPriceAsc() {
        SearchRequest req = new SearchRequest();
        req.setSort(SearchRequest.SortOption.PRICE_ASC);

        SearchResponse response = courseSearchService.searchCourses(req);

        var courses = response.getCourses();
        for (int i = 1; i < courses.size(); i++) {
            assertThat(courses.get(i).getPrice())
                    .isGreaterThanOrEqualTo(courses.get(i - 1).getPrice());
        }
    }

    @Test
    void testAgeRangeFilter() {
        SearchRequest req = new SearchRequest();
        req.setMinAge(10);
        req.setMaxAge(15);

        SearchResponse response = courseSearchService.searchCourses(req);

        assertThat(response.getCourses()).allSatisfy(c ->
                assertThat(c.getMinAge()).isLessThanOrEqualTo(15)
                        .isLessThanOrEqualTo(c.getMaxAge())
        );
    }

    @Test
    void testCategoryFilter() {
        SearchRequest req = new SearchRequest();
        req.setCategory("Math");

        SearchResponse response = courseSearchService.searchCourses(req);

        assertThat(response.getCourses()).allSatisfy(c ->
                assertThat(c.getCategory()).isEqualToIgnoringCase("Math")
        );
    }
    @Test
    void testTypeFilter() {
        SearchRequest req = new SearchRequest();
        req.setType(CourseDocument.CourseType.COURSE);

        SearchResponse response = courseSearchService.searchCourses(req);

        assertThat(response.getCourses()).allSatisfy(c ->
                assertThat(c.getType()).isEqualTo(CourseDocument.CourseType.COURSE)
        );
    }

    @Test
    void testDefaultFutureDateFilter() {
        SearchRequest req = new SearchRequest();

        SearchResponse response = courseSearchService.searchCourses(req);

        assertThat(response.getCourses()).allSatisfy(c ->
                assertThat(c.getNextSessionDate()).isAfterOrEqualTo(java.time.Instant.now())
        );
    }

    @Test
    void testStartDateFilter() {
        SearchRequest req = new SearchRequest();
        req.setStartDate(java.time.Instant.parse("2025-08-01T00:00:00Z"));

        SearchResponse response = courseSearchService.searchCourses(req);

        assertThat(response.getCourses()).allSatisfy(c ->
                assertThat(c.getNextSessionDate()).isAfterOrEqualTo(req.getStartDate())
        );
    }
    @Test
    void testGetSuggestions() {
        List<String> suggestions = courseSearchService.getSuggestions("call");

        assertThat(suggestions).isNotEmpty();
        assertThat(suggestions).anyMatch(s -> s.toLowerCase().contains("call"));
    }

}
