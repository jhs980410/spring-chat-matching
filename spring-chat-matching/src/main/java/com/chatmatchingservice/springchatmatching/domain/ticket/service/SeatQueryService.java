package com.chatmatchingservice.springchatmatching.domain.ticket.service;

import com.chatmatchingservice.springchatmatching.domain.event.dto.SeatResponseDto;
import com.chatmatchingservice.springchatmatching.domain.event.dto.SeatStatus;
import com.chatmatchingservice.springchatmatching.domain.event.dto.SectionResponseDto;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.Seat;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.VenueSection;
import com.chatmatchingservice.springchatmatching.domain.ticket.repository.SeatRepository;
import com.chatmatchingservice.springchatmatching.domain.ticket.repository.VenueSectionRepository;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatQueryService {
    //좌석/구역 조회 전용 (READ)
    private final VenueSectionRepository venueSectionRepository;
    private final SeatRepository seatRepository;
    private final RedisRepository redisRepository;

    public List<SectionResponseDto> getSectionsWithSeats(Long eventId) {

        List<VenueSection> sections =
                venueSectionRepository.findByEventId(eventId);

        return sections.stream()
                .map(section -> {
                    List<Seat> seats =
                            seatRepository.findBySectionId(section.getId());

                    List<SeatResponseDto> seatDtos = seats.stream()
                            .map(seat -> new SeatResponseDto(
                                    seat.getId(),
                                    seat.getRowLabel(),
                                    seat.getSeatNumber(),
                                    SeatStatus.AVAILABLE   // 🔥 조회 시엔 전부 AVAILABLE
                            ))
                            .toList();

                    return new SectionResponseDto(
                            section.getId(),
                            section.getCode(),
                            section.getName(),
                            section.getGrade(),
                            section.getTicket().getPrice(),
                            seats.size(),
                            seats.size(),      // remainSeats = totalSeats
                            seatDtos
                    );
                })
                .toList();
    }

}
