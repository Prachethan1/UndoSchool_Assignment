package com.courses.courses.service;
import com.courses.courses.document.CourseDocument;
import com.courses.courses.repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class DataIndexingService {

    private final CourseRepository courseRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ObjectMapper objectMapper;

    public DataIndexingService(CourseRepository courseRepository, ElasticsearchOperations elasticsearchOperations, ObjectMapper objectMapper) {
        this.courseRepository = courseRepository;
        this.elasticsearchOperations = elasticsearchOperations;
        this.objectMapper = objectMapper;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void indexSampleData() {
        try {

            createIndexIfNotExists();

            Long existingCount = courseRepository.count();
            if (existingCount > 0) {
                return;
            }

            List<CourseDocument> courses = loadSampleCourses();

            courses.forEach(course -> course.setTitleSuggest(course.getTitle()));

            Iterable<CourseDocument> savedCourses = courseRepository.saveAll(courses);
            Long savedCount = courseRepository.count();


        } catch (Exception e) {
            throw new RuntimeException("Data indexing failed", e);
        }
    }

    private void createIndexIfNotExists() {
        IndexOperations indexOps = elasticsearchOperations.indexOps(CourseDocument.class);

        if (!indexOps.exists()) {
            indexOps.create();
            indexOps.putMapping();
        } else {
        }
    }

    private List<CourseDocument> loadSampleCourses() throws IOException {
        ClassPathResource resource = new ClassPathResource("sample-courses.json");
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<List<CourseDocument>>() {});
        }
    }

    public void reindexData() {

        IndexOperations indexOps = elasticsearchOperations.indexOps(CourseDocument.class);
        if (indexOps.exists()) {
            indexOps.delete();
        }

        createIndexIfNotExists();

        try {
            List<CourseDocument> courses = loadSampleCourses();

            courses.forEach(course -> course.setTitleSuggest(course.getTitle()));

            courseRepository.saveAll(courses);

        } catch (IOException e) {
            throw new RuntimeException("Reindexing failed", e);
        }
    }
}