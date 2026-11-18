package com.example.update.controller;

import com.example.update.dto.UserUpdateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserUpdateController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Directory where Repo 1 creates the <param1>User-Detail.json file
    private static final String STORAGE_DIR = System.getenv().getOrDefault("SHARED_STORAGE_DIR", "../shared_data/");

    /**
     * POST endpoint accepting request body.
     * Locates <userId>User-Detail.json created by Repo 1 and updates it with the incoming request body.
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody UserUpdateDTO updateDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            String userId = updateDTO.getUserId();
            if (userId == null || userId.trim().isEmpty()) {
                response.put("error", "userId is required in request body");
                return ResponseEntity.badRequest().body(response);
            }

            // Target file created by Repo 1: <userId>User-Detail.json
            String filename = userId + "User-Detail.json";
            File targetFile = new File(STORAGE_DIR, filename);

            // If file doesn't exist in shared_data, check local directory fallback
            if (!targetFile.exists()) {
                targetFile = new File(filename);
            }

            // Read existing user detail file or initialize new map if absent
            Map<String, Object> userData;
            if (targetFile.exists()) {
                userData = objectMapper.readValue(targetFile, Map.class);
            } else {
                userData = new HashMap<>();
                userData.put("userId", userId);
            }

            // Update file contents with the request body fields
            if (updateDTO.getDepartment() != null) {
                userData.put("department", updateDTO.getDepartment());
            }
            if (updateDTO.getRole() != null) {
                userData.put("role", updateDTO.getRole());
            }
            if (updateDTO.getStatus() != null) {
                userData.put("status", updateDTO.getStatus());
            }
            if (updateDTO.getSalary() != null) {
                userData.put("salary", updateDTO.getSalary());
            }
            if (updateDTO.getAdditionalAttributes() != null) {
                userData.putAll(updateDTO.getAdditionalAttributes());
            }
            userData.put("_lastUpdatedBy", "repo2-user-update-service");

            // Write updated data back to the JSON file
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(targetFile, userData);

            response.put("message", "File updated successfully with request body");
            response.put("updatedFile", targetFile.getName());
            response.put("updatedUserId", updateDTO.getUserId());
            response.put("updatedRole", updateDTO.getRole());
            response.put("updatedDepartment", updateDTO.getDepartment());
            response.put("updatedData", userData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Failed to update file: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Optional: POST with path parameter /api/users/{userId}/update
     */
    @PostMapping("/{userId}/update")
    public ResponseEntity<Map<String, Object>> updateUserByPath(
            @PathVariable("userId") String userId,
            @RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();

        try {
            String filename = userId + "User-Detail.json";
            File targetFile = new File(STORAGE_DIR, filename);
            if (!targetFile.exists()) {
                targetFile = new File(filename);
            }

            Map<String, Object> userData = targetFile.exists()
                    ? objectMapper.readValue(targetFile, Map.class)
                    : new HashMap<>();

            userData.putAll(payload);
            userData.put("userId", userId);
            userData.put("_lastUpdatedBy", "repo2-user-update-service");

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(targetFile, userData);

            response.put("message", "File updated successfully via path parameter");
            response.put("updatedFile", targetFile.getName());
            response.put("updatedData", userData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Failed to update file: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
