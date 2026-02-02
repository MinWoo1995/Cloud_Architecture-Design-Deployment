package com.example.test2.controller;

import com.example.test2.dto.MemberResponse;
import com.example.test2.dto.MemberSaveRequest;
import com.example.test2.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Long> saveMember(@RequestBody @Valid MemberSaveRequest request) {
        log.info("[API - LOG] Member Save Request: {}", request.getName());
        return ResponseEntity.ok(memberService.save(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
        log.info("[API - LOG] Member Find Request ID: {}", id);
        return ResponseEntity.ok(memberService.findById(id));
    }

    @PostMapping("/{id}/profile-image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        log.info("[API - LOG] Profile Image Upload Request ID: {}, FileName: {}", id, file.getOriginalFilename());

        String result = memberService.uploadProfileImage(id, file);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/profile-image")
    public ResponseEntity<String> getProfileImage(@PathVariable Long id) {
        log.info("[API - LOG] Get Presigned URL Request ID: {}", id);

        String presignedUrl = memberService.getPresignedUrl(id);
        return ResponseEntity.ok(presignedUrl);
    }
}