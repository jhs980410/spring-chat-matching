package com.chatmatchingservice.springchatmatching.domain.ticket.dto;

import com.chatmatchingservice.springchatmatching.domain.event.dto.SectionResponseDto;
import com.chatmatchingservice.springchatmatching.domain.ticket.service.SeatQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class SeatController {

    private final SeatQueryService seatQueryService;

    @GetMapping("/{eventId}/seats")
    public List<SectionResponseDto> getSeats(@PathVariable Long eventId) {
        return seatQueryService.getSectionsWithSeats(eventId);
    }
}
