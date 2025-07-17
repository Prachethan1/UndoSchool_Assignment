package com.courses.courses.dto;

import com.courses.courses.document.CourseDocument;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

public class SearchRequest {

    private String q; // search keyword
    private Integer minAge;
    private Integer maxAge;
    private String category;
    private CourseDocument.CourseType type;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Instant startDate;
    @Builder.Default
    private SortOption sort = SortOption.UPCOMING;
    @Builder.Default
    private Integer page = 0;
    @Builder.Default
    private Integer size = 10;

    public enum SortOption {
        UPCOMING("nextSessionDate", "asc"),
        PRICE_ASC("price", "asc"),
        PRICE_DESC("price", "desc");

        private final String field;
        private final String direction;

        SortOption(String field, String direction) {
            this.field = field;
            this.direction = direction;
        }

        public String getField() {
            return field;
        }

        public String getDirection() {
            return direction;
        }
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public CourseDocument.CourseType getType() {
        return type;
    }

    public void setType(CourseDocument.CourseType type) {
        this.type = type;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public SortOption getSort() {
        return sort;
    }

    public void setSort(SortOption sort) {
        this.sort = sort;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}