package com.coffee.app.controller;

import com.coffee.app.dto.request.PaymentActionRequest;
import com.coffee.app.dto.response.PaymentResponse;
import com.coffee.app.service.PaymentService;
import io.github.tongbora.bakong.dto.BakongRequest;
import io.github.tongbora.bakong.dto.BakongResponse;
import io.github.tongbora.bakong.dto.CheckTransactionRequest;
import io.github.tongbora.bakong.service.BakongService;
import java.util.UUID;
import kh.gov.nbc.bakong_khqr.model.KHQRData;
import kh.gov.nbc.bakong_khqr.model.KHQRResponse;
import lombok.Generated;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/payment"})
public class PaymentController {
   private final BakongService bakongService;
   private final PaymentService paymentService;

   @PostMapping({"/generate-qr"})
   public KHQRResponse<KHQRData> generateQR(@RequestBody BakongRequest request) {
      return this.bakongService.generateQR(request);
   }

   @PostMapping({"/qr-image"})
   public ResponseEntity<byte[]> getQRImage(@RequestBody KHQRData qrData) {
      byte[] image = this.bakongService.getQRImage(qrData);
      return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(image);
   }

   @PostMapping({"/check-transaction"})
   public BakongResponse checkTransaction(@RequestBody CheckTransactionRequest request) {
      return this.bakongService.checkTransactionByMD5(request);
   }

   @PatchMapping({"/void-or-refund/{paymentId}", "/payments/{paymentId}/void-or-refund"})
   @PreAuthorize("hasRole('ADMIN')")
   public ResponseEntity<PaymentResponse> voidOrRefundPayment(@PathVariable UUID paymentId, @RequestBody(required = false) PaymentActionRequest request) {
      return ResponseEntity.ok(this.paymentService.voidOrRefundPayment(paymentId, request != null ? request.reason() : null));
   }

   @Generated
   public PaymentController(final BakongService bakongService, final PaymentService paymentService) {
      this.bakongService = bakongService;
      this.paymentService = paymentService;
   }
}
