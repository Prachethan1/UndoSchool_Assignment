package com.courses.courses.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import com.courses.courses.document.CourseDocument;
import com.courses.courses.dto.SearchRequest;
import com.courses.courses.dto.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class CourseSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient elasticsearchClient;

    public CourseSearchService(ElasticsearchOperations elasticsearchOperations, ElasticsearchClient elasticsearchClient) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.elasticsearchClient = elasticsearchClient;
    }

    public SearchResponse searchCourses(SearchRequest request) {
        Query query = buildSearchQuery(request);
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageable)
                .withSort(buildSort(request.getSort()))
                .build();

        SearchHits<CourseDocument> searchHits =
                elasticsearchOperations.search(nativeQuery, CourseDocument.class);

        List<CourseDocument> courses = searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .toList();

        long totalHits = searchHits.getTotalHits();
        int totalPages = (int) Math.ceil((double) totalHits / request.getSize());


        SearchResponse response = new SearchResponse();
        response.setTotal(totalHits);
        response.setCourses(courses);
        response.setPage(request.getPage());
        response.setSize(request.getSize());
        response.setTotalPages(totalPages);

        return response;
    }


    public List<String> getSuggestions(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        Query autocompleteOrFuzzy = Query.of(q -> q
                .bool(b -> b
                        .should(s1 -> s1
                                .matchPhrasePrefix(mpp -> mpp
                                        .field("titleSuggest")
                                        .query(query)
                                )
                        )
                        .should(s2 -> s2
                                .match(m -> m
                                        .field("titleSuggest")
                                        .query(query)
                                        .fuzziness("AUTO")
                                )
                        )
                        .minimumShouldMatch("1")
                )
        );

        var searchRequest =
                co.elastic.clients.elasticsearch.core.SearchRequest.of(s -> s
                        .index("courses")
                        .query(autocompleteOrFuzzy)
                        .source(src -> src.filter(f -> f.includes("title")))
                        .size(10)
                );

        try {
            var response = elasticsearchClient.search(searchRequest, Map.class);

            return response.hits().hits().stream()
                    .map(hit -> (Map<String, Object>) hit.source())
                    .map(source -> (String) source.get("title"))
                    .filter(Objects::nonNull)
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch suggestions", e);
        }
    }


    private Query buildSearchQuery(SearchRequest request) {
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        if (request.getQ() != null && !request.getQ().trim().isEmpty()) {
            Query multiMatchQuery = Query.of(q -> q
                    .multiMatch(mm -> mm
                            .query(request.getQ())
                            .fields("title^2", "description")
                            .fuzziness("AUTO")
                    ));
            boolQueryBuilder.must(multiMatchQuery);
        }

        if (request.getMinAge() != null) {
            Query minAgeQuery = Query.of(q -> q
                    .range(r -> r
                            .field("maxAge")
                            .gte(JsonData.of(request.getMinAge()))
                    )
            );
            boolQueryBuilder.filter(minAgeQuery);
        }

        if (request.getMaxAge() != null) {
            Query maxAgeQuery = Query.of(q -> q
                    .range(r -> r
                            .field("minAge")
                            .lte(JsonData.of(request.getMaxAge()))
                    )
            );
            boolQueryBuilder.filter(maxAgeQuery);
        }

        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            Query categoryQuery = Query.of(q -> q
                    .term(t -> t
                            .field("category.keyword")
                            .value(request.getCategory()))
            );
            boolQueryBuilder.filter(categoryQuery);
        }

        if (request.getType() != null) {
            Query typeQuery = Query.of(q -> q
                    .term(t -> t
                            .field("type.keyword")
                            .value(request.getType().name()))
            );
            boolQueryBuilder.filter(typeQuery);
        }

        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
            Query priceQuery = Query.of(q -> {
                RangeQuery.Builder rangeBuilder = new RangeQuery.Builder().field("price");
                if (request.getMinPrice() != null) {
                    rangeBuilder.gte(JsonData.of(request.getMinPrice()));
                }
                if (request.getMaxPrice() != null) {
                    rangeBuilder.lte(JsonData.of(request.getMaxPrice()));
                }
                return q.range(rangeBuilder.build());
            });
            boolQueryBuilder.filter(priceQuery);
        }

        if (request.getStartDate() != null) {
            Query dateQuery = Query.of(q -> q
                    .range(r -> r
                            .field("nextSessionDate")
                            .gte(JsonData.of(request.getStartDate().toString()))
                    )
            );
            boolQueryBuilder.filter(dateQuery);
        } else {
            Query futureQuery = Query.of(q -> q
                    .range(r -> r
                            .field("nextSessionDate")
                            .gte(JsonData.of(Instant.now().toString()))
                    )
            );
            boolQueryBuilder.filter(futureQuery);
        }

        return Query.of(q -> q.bool(boolQueryBuilder.build()));
    }

    private co.elastic.clients.elasticsearch._types.SortOptions buildSort(SearchRequest.SortOption sortOption) {
        SortOrder order = "asc".equalsIgnoreCase(sortOption.getDirection()) ? SortOrder.Asc : SortOrder.Desc;
        return co.elastic.clients.elasticsearch._types.SortOptions.of(s -> s
                .field(f -> f
                        .field(sortOption.getField())
                        .order(order)));
    }
}
