package com.chatmatchingservice.springchatmatching.domain.domain.repository;

import com.chatmatchingservice.springchatmatching.domain.domain.entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DomainRepository extends JpaRepository<Domain, Long> {}
