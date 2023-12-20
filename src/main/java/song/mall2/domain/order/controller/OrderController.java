package song.mall2.domain.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import song.mall2.domain.order.dto.OrderDto;
import song.mall2.domain.order.dto.OrderProductDto;
import song.mall2.domain.order.dto.OrdersIdDto;
import song.mall2.domain.order.dto.SaveOrdersDto;
import song.mall2.domain.order.service.OrderService;
import song.mall2.security.authentication.principal.UserPrincipal;

import java.util.List;

@Slf4j
@Controller
@ResponseBody
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/save")
    public ResponseEntity<OrdersIdDto> postSaveOrder(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                     @RequestBody SaveOrdersDto saveOrdersDto) {
        OrdersIdDto ordersIdDto = orderService.saveOrders(userPrincipal.getId(), saveOrdersDto.getSaveOrderProductDtoList());

        return ResponseEntity.ok(ordersIdDto);
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<OrderDto> orderList = orderService.getOrderList(userPrincipal.getId());

        return ResponseEntity.ok(orderList);
    }

    @GetMapping("/{ordersId}")
    public ResponseEntity<List<OrderProductDto>> getOrderProductList(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                     @PathVariable("ordersId") Long ordersId) {
        List<OrderProductDto> orderProductList = orderService.getOrderProductList(userPrincipal.getId(), ordersId);

        return ResponseEntity.ok(orderProductList);
    }

    @GetMapping("/orderProduct/{orderProductId}")
    public ResponseEntity<OrderProductDto> getOrderProduct(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                           @PathVariable("orderProductId") Long orderProductId) {
        OrderProductDto orderProduct = orderService.getOrderProduct(userPrincipal.getId(), orderProductId);

        return ResponseEntity.ok(orderProduct);
    }
}
