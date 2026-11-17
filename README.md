# Repo 2: User Update Service (`repo2-user-update-service`)

Microservice that exposes a POST endpoint accepting any request body,
locates the target `<param1>User-Detail.json` created by Repo 1, and updates the file with the request body contents.

---

## Endpoints

### 1. POST Endpoint
- **URL**: `/api/users/update`
- **Method**: `POST`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
  ```json
  {
    "userId": "USR101",
    "department": "Platform Engineering",
    "role": "Principal AI Architect",
    "salary": 165000,
    "status": "PROMOTED",
    "customNotes": "Updated by Repo 2 Service"
  }
  ```
- **Behavior**:
  Locates `USR101User-Detail.json` (created by Repo 1), merges all fields from the request body into the file, updates `_lastUpdatedBy`, and saves the file.

### 2. POST with Path Parameter
- **URL**: `/api/users/<user_id>/update`
- **Method**: `POST`
- **Headers**: `Content-Type: application/json`
- **Example**:
  ```bash
  curl -X POST "http://localhost:5002/api/users/USR101/update" \
       -H "Content-Type: application/json" \
       -d '{"role": "VP of Architecture", "bonus": 25000}'
  ```

---

## Running the Service
```bash
mvn clean install
mvn spring-boot:run
```
Port: `8080` (Spring Boot default)
