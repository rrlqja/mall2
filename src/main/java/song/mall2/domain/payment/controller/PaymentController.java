package song.mall2.domain.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import song.mall2.domain.order.dto.OrderDto;
import song.mall2.domain.order.dto.SaveOrderDto;
import song.mall2.domain.order.dto.SaveOrderProductDto;
import song.mall2.domain.order.service.OrderService;
import song.mall2.domain.payment.dto.Callback;
import song.mall2.domain.payment.dto.PaymentDto;
import song.mall2.domain.payment.dto.Webhook;
import song.mall2.domain.payment.portone.dto.PortonePaymentRequest;
import song.mall2.domain.payment.service.PaymentService;
import song.mall2.security.authentication.principal.UserPrincipal;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final OrderService orderService;

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable("paymentId") String paymentId,
                                                 @AuthenticationPrincipal UserPrincipal userPrincipal) {
        PaymentDto paymentDto = paymentService.getPaymentByPaymentId(userPrincipal.getId(), paymentId);
//        PaymentDto paymentDto = paymentService.getPaymentByPaymentId(1L, paymentId);

        return ResponseEntity.ok(paymentDto);
    }

    @GetMapping("/{ordersId}")
    public ResponseEntity<PaymentDto> getOrdersPayment(@PathVariable("ordersId") Long ordersId,
                                                       @AuthenticationPrincipal UserPrincipal userPrincipal) {
        PaymentDto paymentDto = paymentService.getPaymentByOrdersId(userPrincipal.getId(), ordersId);
//        PaymentDto paymentDto = paymentService.getPaymentByPaymentId(1L, ordersId);

        return ResponseEntity.ok(paymentDto);
    }

    @GetMapping("/{ordersId}/portone")
    public ResponseEntity<PortonePaymentRequest> getPortone(@PathVariable("orderId") Long ordersId,
                                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        PortonePaymentRequest portonePaymentRequest = paymentService.getPortonePaymentRequest(userPrincipal.getId(), ordersId);
//        PortonePaymentRequest portonePaymentRequest = paymentService.getPortonePaymentRequest(1L, ordersId);

        return ResponseEntity.ok(portonePaymentRequest);
    }

    /**
     * test payment
     */
    @GetMapping("/portone")
    public String portone(Model model) {
        OrderDto orderDto = createOrderEx();

        PortonePaymentRequest portonePaymentRequest = paymentService.getPortonePaymentRequest(1L, orderDto.getId());
        model.addAttribute("portonePaymentRequest", portonePaymentRequest);

        return "pay/portone";
    }

    /**
     * test payment
     */
    @GetMapping("/{ordersId}/cancel")
    public ResponseEntity<Object> getCancelPayment(@PathVariable("ordersId") Long ordersId) {
        paymentService.cancelPayment(1L, ordersId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{ordersId}/cancel")
    public ResponseEntity<Object> postCancelPayment(@PathVariable("ordersId") Long ordersId,
                                                    @AuthenticationPrincipal UserPrincipal userPrincipal) {
        paymentService.cancelPayment(userPrincipal.getId(), ordersId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/webhook")
    public ResponseEntity<Object> postWebhook(@RequestBody Webhook webhook) {
        log.info("webhook: {}", webhook.toString());
        paymentService.paymentWebhook(webhook);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/callback")
    public ResponseEntity<PaymentDto> postCallback(@RequestBody Callback callback,
                                                   @AuthenticationPrincipal UserPrincipal userPrincipal) {
        log.info("callback: {}", callback);
        PaymentDto paymentDto = paymentService.getPaymentByPaymentId(userPrincipal.getId(), callback.getPaymentId());
//        PaymentDto paymentDto = paymentService.getPaymentByPaymentId(1L, callback.getPaymentId());

        return ResponseEntity.ok(paymentDto);
    }

    private OrderDto createOrderEx() {
        SaveOrderProductDto saveOrderProductDto = new SaveOrderProductDto();
        saveOrderProductDto.setProductId(1L);
        saveOrderProductDto.setQuantity(10);
        List<SaveOrderProductDto> saveOrderProductDtoList = new ArrayList<>();
        saveOrderProductDtoList.add(saveOrderProductDto);
        SaveOrderDto saveOrderDto = new SaveOrderDto();
        saveOrderDto.setSaveOrderProductDtoList(saveOrderProductDtoList);

        OrderDto orderDto = orderService.saveOrder(1L, saveOrderDto.getSaveOrderProductDtoList());
        return orderDto;
    }
}