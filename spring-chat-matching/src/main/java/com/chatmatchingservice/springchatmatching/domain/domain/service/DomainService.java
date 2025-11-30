package com.chatmatchingservice.springchatmatching.domain.domain.service;

import com.chatmatchingservice.springchatmatching.domain.domain.dto.DomainRequest;
import com.chatmatchingservice.springchatmatching.domain.domain.dto.DomainResponse;
import com.chatmatchingservice.springchatmatching.domain.domain.entity.Domain;
import com.chatmatchingservice.springchatmatching.domain.domain.repository.DomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DomainService {

    private final DomainRepository domainRepository;

    public DomainResponse create(DomainRequest req) {
        Domain d = domainRepository.save(
                Domain.builder()
                        .code(req.code())
                        .name(req.name())
                        .build()
        );
        return new DomainResponse(d.getId(), d.getCode(), d.getName());
    }

    public List<DomainResponse> findAll() {
        return domainRepository.findAll().stream()
                .map(d -> new DomainResponse(d.getId(), d.getCode(), d.getName()))
                .toList();
    }
}
