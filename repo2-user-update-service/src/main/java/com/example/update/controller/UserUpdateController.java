package com.example.update.controller;

import com.example.update.dto.UserUpdateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserUpdateController {

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody UserUpdateDTO updateDTO) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "File updated successfully with request body");
        response.put("updatedUserId", updateDTO.getUserId());
        response.put("updatedRole", updateDTO.getRole());
        response.put("updatedDepartment", updateDTO.getDepartment());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/update")
    public ResponseEntity<Map<String, Object>> updateUserByPath(
            @PathVariable("userId") String userId,
            @RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>(payload);
        response.put("userId", userId);
        response.put("status", "UPDATED");
        return ResponseEntity.ok(response);
    }
}
