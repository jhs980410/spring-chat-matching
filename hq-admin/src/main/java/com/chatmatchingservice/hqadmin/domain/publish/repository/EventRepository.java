package com.chatmatchingservice.hqadmin.domain.publish.repository;

import com.chatmatchingservice.hqadmin.domain.publish.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

}
