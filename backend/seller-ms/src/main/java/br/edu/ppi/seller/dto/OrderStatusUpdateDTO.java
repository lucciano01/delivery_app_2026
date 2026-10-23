package br.edu.ppi.seller.dto;

import br.edu.ppi.seller.enums.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateDTO(

        @NotBlank
        String orderCode,

        @NotNull
        OrderStatus orderStatus

) {
}
