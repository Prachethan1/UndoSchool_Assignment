# Courses Search Service

A Spring Boot application that indexes and searches "courses" in Elasticsearch with filters, pagination, sorting, and (bonus) autocomplete & fuzzy search.

### 📌 Features
* Bulk indexing of 50 sample courses on startup

* Full-text & fuzzy search across course titles and descriptions

* Multi-filter support (category, type, age range, price range, upcoming dates)

* Sorting: by next session date (default), or by price (asc/desc)

* Pagination support

* Autocomplete suggestions using Elasticsearch's completion suggester

* Dockerized Elasticsearch setup via Docker Compose



### 🛠 Tech Stack
* Java 17

* Spring Boot

* Spring Data Elasticsearch 

* Elasticsearch 8.x

* Maven

* Docker & Docker Compose

* Jackson for JSON

### Project Structure 

src/main/java/com/example/courses

├── config/                
├── controller/             
├── document/               
├── repository/             
├── service/                
└── CoursesApplication.java 

src/main/resources/

├── sample-courses.json     
└── application.properties

docker-compose.yml          
README.md                   


### 🛠️ Installation & Setup

**Clone the repository**
```bash
git clone https://github.com/Prachethan1/UndoSchool_Assignment.git
cd UndoSchool_Assignment
```
**Start Elasticsearch via Docker**
```bash
  docker-compose up -d
```
This starts a single-node Elasticsearch accessible at http://localhost:9200.

Verify it with:
```bash
curl http://localhost:9200
```

You should receive a JSON response:
```bash
{
  "name" : "4678aebcbf78",
  "cluster_name" : "docker-cluster",
  "cluster_uuid" : "_TZsu7o9ToOKCnOA5aHytQ",
  "version" : {
    "number" : "8.12.0",
    "build_flavor" : "default",
    "build_type" : "docker",
    "build_hash" : "1665f706fd9354802c02146c1e6b5c0fbcddfbc9",
    "build_date" : "2024-01-11T10:05:27.953830042Z",
    "build_snapshot" : false,
    "lucene_version" : "9.9.1",
    "minimum_wire_compatibility_version" : "7.17.0",
    "minimum_index_compatibility_version" : "7.0.0"
  },
  "tagline" : "You Know, for Search"
}
```

Verify Indexed Data:
```bash
curl "http://localhost:9200/courses/_search?pretty"
```
This returns a list of all indexed courses in Elasticsearch.


**Configure the Application**

By default, the application points to http://localhost:9200.

If needed, update Elasticsearch settings in src/main/resources/application.properties:


```bash
 spring.elasticsearch.uris=http://localhost:9200
```


**Build & Run the Service**

Using Maven:
```bash
  mvn spring-boot:run
```


### 📝 Sample Data

File: src/main/resources/sample-courses.json

Contains 50+ diverse course documents with fields:

* id (unique UUID)

* title (text)

* description (text)

* category (e.g., Math, Science)

* type (ONE_TIME | COURSE | CLUB)

* gradeRange (e.g., "1st–3rd")

* minAge, maxAge (numeric)

* price (decimal)

* nextSessionDate (ISO-8601 datetime)

On app startup, these are bulk indexed into the courses index.

You can re-index anytime using the /api/admin/reindex endpoint.

### API Endpoints: 
**Health Check**

Check if service is up

```bash
GET http://localhost:8080/api/health
```
Response:
```bash
Course Search API is running
```

**Search Courses** 

Search courses with full-text, filters, sorting, and pagination.

```bash
GET http://localhost:8080/api/search
```
Response:
```bash
{
    "total": 50,
    "courses": [
        {
            "id": "2",
            "title": "Painting Basics",
            "description": "Learn how to paint using acrylic and watercolor.",
            "category": "Art",
            "type": "ONE_TIME",
            "gradeRange": "3rd–5th",
            "minAge": 8,
            "maxAge": 10,
            "price": 30.0,
            "nextSessionDate": "2025-07-18T10:00:00.000Z",
            "titleSuggest": "Painting Basics"
        },
        {
            "id": "7",
            "title": "Drama Club",
            "description": "Act, perform, and learn stage presence.",
            "category": "Art",
            "type": "CLUB",
            "gradeRange": "8th–12th",
            "minAge": 13,
            "maxAge": 18,
            "price": 60.0,
            "nextSessionDate": "2025-07-19T16:00:00.000Z",
            "titleSuggest": "Drama Club"
        },
        {
            "id": "1",
            "title": "Introduction to Algebra",
            "description": "A basic course in algebra covering equations and inequalities.",
            "category": "Math",
            "type": "COURSE",
            "gradeRange": "6th–8th",
            "minAge": 11,
            "maxAge": 14,
            "price": 50.0,
            "nextSessionDate": "2025-07-20T15:00:00.000Z",
            "titleSuggest": "Introduction to Algebra"
        },
        {
            "id": "4",
            "title": "Robotics Club",
            "description": "Weekly club for building and programming robots.",
            "category": "Technology",
            "type": "CLUB",
            "gradeRange": "9th–12th",
            "minAge": 14,
            "maxAge": 18,
            "price": 70.0,
            "nextSessionDate": "2025-07-21T17:00:00.000Z",
            "titleSuggest": "Robotics Club"
        },
        {
            "id": "5",
            "title": "Creative Writing Workshop",
            "description": "Explore your imagination and improve your writing.",
            "category": "Language",
            "type": "ONE_TIME",
            "gradeRange": "5th–7th",
            "minAge": 10,
            "maxAge": 12,
            "price": 25.0,
            "nextSessionDate": "2025-07-22T09:00:00.000Z",
            "titleSuggest": "Creative Writing Workshop"
        },
        {
            "id": "6",
            "title": "Geometry Made Easy",
            "description": "Understand shapes, angles, and theorems easily.",
            "category": "Math",
            "type": "COURSE",
            "gradeRange": "6th–8th",
            "minAge": 11,
            "maxAge": 14,
            "price": 45.0,
            "nextSessionDate": "2025-07-23T11:00:00.000Z",
            "titleSuggest": "Geometry Made Easy"
        },
        {
            "id": "8",
            "title": "Physics in Real Life",
            "description": "Practical experiments demonstrating physics concepts.",
            "category": "Science",
            "type": "COURSE",
            "gradeRange": "9th–12th",
            "minAge": 14,
            "maxAge": 18,
            "price": 65.0,
            "nextSessionDate": "2025-07-24T13:00:00.000Z",
            "titleSuggest": "Physics in Real Life"
        },
        {
            "id": "3",
            "title": "Chemistry Experiments",
            "description": "Hands-on experiments to learn basic chemistry concepts.",
            "category": "Science",
            "type": "COURSE",
            "gradeRange": "7th–9th",
            "minAge": 12,
            "maxAge": 15,
            "price": 55.0,
            "nextSessionDate": "2025-07-25T14:00:00.000Z",
            "titleSuggest": "Chemistry Experiments"
        },
        {
            "id": "9",
            "title": "Coding for Beginners",
            "description": "Learn programming basics with fun exercises.",
            "category": "Technology",
            "type": "COURSE",
            "gradeRange": "6th–8th",
            "minAge": 11,
            "maxAge": 14,
            "price": 50.0,
            "nextSessionDate": "2025-07-27T12:00:00.000Z",
            "titleSuggest": "Coding for Beginners"
        },
        {
            "id": "10",
            "title": "Public Speaking 101",
            "description": "Build confidence and improve communication skills.",
            "category": "Language",
            "type": "ONE_TIME",
            "gradeRange": "7th–9th",
            "minAge": 12,
            "maxAge": 15,
            "price": 20.0,
            "nextSessionDate": "2025-07-28T08:00:00.000Z",
            "titleSuggest": "Public Speaking 101"
        }
    ],
    "page": 0,
    "size": 10,
    "totalPages": 5
}
```

Pagination support via page and size parameters:

```bash
http://localhost:8080/api/search?page=0&size=5
```
Response:
```bash
{
    "total": 50,
    "courses": [
        {
            "id": "2",
            "title": "Painting Basics",
            "description": "Learn how to paint using acrylic and watercolor.",
            "category": "Art",
            "type": "ONE_TIME",
            "gradeRange": "3rd–5th",
            "minAge": 8,
            "maxAge": 10,
            "price": 30.0,
            "nextSessionDate": "2025-07-18T10:00:00.000Z",
            "titleSuggest": "Painting Basics"
        },
        {
            "id": "7",
            "title": "Drama Club",
            "description": "Act, perform, and learn stage presence.",
            "category": "Art",
            "type": "CLUB",
            "gradeRange": "8th–12th",
            "minAge": 13,
            "maxAge": 18,
            "price": 60.0,
            "nextSessionDate": "2025-07-19T16:00:00.000Z",
            "titleSuggest": "Drama Club"
        },
        {
            "id": "1",
            "title": "Introduction to Algebra",
            "description": "A basic course in algebra covering equations and inequalities.",
            "category": "Math",
            "type": "COURSE",
            "gradeRange": "6th–8th",
            "minAge": 11,
            "maxAge": 14,
            "price": 50.0,
            "nextSessionDate": "2025-07-20T15:00:00.000Z",
            "titleSuggest": "Introduction to Algebra"
        },
        {
            "id": "4",
            "title": "Robotics Club",
            "description": "Weekly club for building and programming robots.",
            "category": "Technology",
            "type": "CLUB",
            "gradeRange": "9th–12th",
            "minAge": 14,
            "maxAge": 18,
            "price": 70.0,
            "nextSessionDate": "2025-07-21T17:00:00.000Z",
            "titleSuggest": "Robotics Club"
        },
        {
            "id": "5",
            "title": "Creative Writing Workshop",
            "description": "Explore your imagination and improve your writing.",
            "category": "Language",
            "type": "ONE_TIME",
            "gradeRange": "5th–7th",
            "minAge": 10,
            "maxAge": 12,
            "price": 25.0,
            "nextSessionDate": "2025-07-22T09:00:00.000Z",
            "titleSuggest": "Creative Writing Workshop"
        }
    ],
    "page": 0,
    "size": 5,
    "totalPages": 10
}
```

```bash
GET http://localhost:8080/api/search?q=Introduction&maxPrice=100&sort=PRICE_ASC
```
Response:
```bash
{
    "total": 3,
    "courses": [
        {
            "id": "1",
            "title": "Introduction to Algebra",
            "description": "A basic course in algebra covering equations and inequalities.",
            "category": "Math",
            "type": "COURSE",
            "gradeRange": "6th–8th",
            "minAge": 11,
            "maxAge": 14,
            "price": 50.0,
            "nextSessionDate": "2025-07-20T15:00:00.000Z",
            "titleSuggest": "Introduction to Algebra"
        },
        {
            "id": "34",
            "title": "Python Programming",
            "description": "An introduction to Python language basics.",
            "category": "Technology",
            "type": "COURSE",
            "gradeRange": "8th–12th",
            "minAge": 13,
            "maxAge": 18,
            "price": 50.0,
            "nextSessionDate": "2025-08-21T13:00:00.000Z",
            "titleSuggest": "Python Programming"
        },
        {
            "id": "40",
            "title": "Java Programming",
            "description": "Introduction to object-oriented programming.",
            "category": "Technology",
            "type": "COURSE",
            "gradeRange": "9th–12th",
            "minAge": 14,
            "maxAge": 18,
            "price": 55.0,
            "nextSessionDate": "2025-08-27T14:00:00.000Z",
            "titleSuggest": "Java Programming"
        }
    ],
    "page": 0,
    "size": 10,
    "totalPages": 1
}
```


**Autocomplete Suggestions and Fuzzy Search**

Suggest course titles based on partial input

```bash
GET http://localhost:8080/api/search/suggest?q=ja
```
Response:
```bash
[
    "Java Programming"
]
```

```bash
GET http://localhost:8080/api/search/suggest?q=Intrduction
``` 
Response:
```bash
[
    "Introduction to Algebra"
]
```



**ReIndex Courses**
```bash
POST http://localhost:8080/api/reindex
Response:
```
```bash
Data reindexed successfully
```
