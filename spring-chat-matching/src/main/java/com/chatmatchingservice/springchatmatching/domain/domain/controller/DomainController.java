package com.chatmatchingservice.springchatmatching.domain.domain.controller;

import com.chatmatchingservice.springchatmatching.domain.domain.dto.DomainRequest;
import com.chatmatchingservice.springchatmatching.domain.domain.dto.DomainResponse;
import com.chatmatchingservice.springchatmatching.domain.domain.service.DomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/domains")
@RequiredArgsConstructor
@Slf4j
public class DomainController {

    private final DomainService domainService;

    @PostMapping
    public ResponseEntity<DomainResponse> create(@RequestBody DomainRequest req) {
        return ResponseEntity.ok(domainService.create(req));
    }

    @GetMapping
    public ResponseEntity<List<DomainResponse>> getAll() {
        return ResponseEntity.ok(domainService.findAll());
    }
}
