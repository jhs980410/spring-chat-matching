package com.chatmatchingservice.springchatmatching.domain.ticket.dto;

import java.util.List;

public record SeatReserveRequest(
        Long eventId,
        List<Long> seatIds
) {}
