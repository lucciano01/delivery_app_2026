package br.edu.ppi.seller.controller;

import br.edu.ppi.seller.constants.OrderConstants;
import br.edu.ppi.seller.dto.OrderStatusUpdateDTO;
import br.edu.ppi.seller.dto.ResponseDTO;
import br.edu.ppi.seller.enums.OrderStatus;
import br.edu.ppi.seller.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static br.edu.ppi.seller.constants.OrderConstants.ORDER_MESSAGE_200;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Tag(name = "API de pedidos", description = "Api que contém os recursos para o vendedor gerenciar os pedidos")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Atualizar status do pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status do pedido alterado com sucesso"),
            @ApiResponse(responseCode = "204", description = "O pedido informado não localizado")
    })
    @PostMapping("/status")
    public ResponseEntity<ResponseDTO> updateStatus(@RequestBody @Valid OrderStatusUpdateDTO orderStatusUpdateDTO){
        orderService.updateOrderStatus(orderStatusUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDTO(ORDER_MESSAGE_200, HttpStatus.OK.value()));
    }
}
