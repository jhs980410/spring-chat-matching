package com.chatmatchingservice.springchatmatching.domain.ticket.service;

import com.chatmatchingservice.springchatmatching.domain.event.dto.SeatResponseDto;
import com.chatmatchingservice.springchatmatching.domain.event.dto.SeatStatus;
import com.chatmatchingservice.springchatmatching.domain.event.dto.SectionResponseDto;
import com.chatmatchingservice.springchatmatching.domain.order.repository.TicketOrderItemRepository;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.Seat;
import com.chatmatchingservice.springchatmatching.domain.ticket.entity.VenueSection;
import com.chatmatchingservice.springchatmatching.domain.ticket.repository.SeatRepository;
import com.chatmatchingservice.springchatmatching.domain.ticket.repository.VenueSectionRepository;
import com.chatmatchingservice.springchatmatching.infra.redis.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatQueryService {

    private final SeatRepository seatRepository;
    private final TicketOrderItemRepository orderItemRepository;
    private final RedisRepository redisRepository;

    public List<SectionResponseDto> getSectionsWithSeats(Long eventId) {

        // 1️⃣ 전체 좌석 조회
        List<Seat> seats = seatRepository.findAllByEventId(eventId);

        // 2️⃣ SOLD 좌석
        Set<Long> soldSeatIds =
                orderItemRepository.findSoldSeatIds(eventId);

        // 3️⃣ Redis LOCKED 좌석
        Set<Long> lockedSeatIds =
                seats.stream()
                        .filter(seat ->
                                redisRepository.isSeatLocked(eventId, seat.getId()))
                        .map(Seat::getId)
                        .collect(Collectors.toSet());

        Map<VenueSection, List<Seat>> grouped =
                seats.stream()
                        .collect(Collectors.groupingBy(Seat::getSection));

        return grouped.entrySet().stream()
                .map(entry -> {
                    VenueSection section = entry.getKey();
                    List<Seat> sectionSeats = entry.getValue();

                    List<SeatResponseDto> seatDtos =
                            sectionSeats.stream()
                                    .map(seat -> {
                                        SeatStatus status;

                                        if (soldSeatIds.contains(seat.getId())) {
                                            status = SeatStatus.SOLD;
                                        } else if (lockedSeatIds.contains(seat.getId())) {
                                            status = SeatStatus.LOCKED;
                                        } else {
                                            status = SeatStatus.AVAILABLE;
                                        }

                                        return new SeatResponseDto(
                                                seat.getId(),
                                                seat.getRowLabel(),
                                                seat.getSeatNumber(),
                                                status
                                        );
                                    })
                                    .toList();

                    long remain =
                            seatDtos.stream()
                                    .filter(s -> s.status() == SeatStatus.AVAILABLE)
                                    .count();

                    return new SectionResponseDto(
                            section.getId(),
                            section.getCode(),
                            section.getName(),
                            section.getGrade(),
                            section.getTicket().getPrice(),
                            sectionSeats.size(),
                            remain,
                            seatDtos
                    );
                })
                .toList();
    }
}
