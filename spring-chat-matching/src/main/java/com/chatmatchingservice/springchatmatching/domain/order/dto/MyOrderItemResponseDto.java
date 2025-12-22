package com.chatmatchingservice.springchatmatching.domain.order.dto;

import lombok.Getter;

@Getter
public class MyOrderItemResponseDto {

    private String ticketName;
    private int quantity;
    private int unitPrice;

    private MyOrderItemResponseDto() {}

    public static MyOrderItemResponseDto from(MyOrderRow row) {
        MyOrderItemResponseDto item = new MyOrderItemResponseDto();
        item.ticketName = row.getTicketName();
        item.quantity = row.getQuantity();
        item.unitPrice = row.getUnitPrice();
        return item;
    }
}