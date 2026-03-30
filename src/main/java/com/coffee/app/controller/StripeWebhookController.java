package com.coffee.app.controller;

import com.coffee.app.domain.Order;
import com.coffee.app.repository.OrderRepository;
import com.coffee.app.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/webhooks"})
public class StripeWebhookController {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);
   private final OrderRepository orderRepository;
   private final PaymentService paymentService;
   @Value("${stripe.webhook-secret}")
   private String endpointSecret;

   @PostMapping({"/stripe"})
   public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
      Event event = null;

      try {
         event = Webhook.constructEvent(payload, sigHeader, this.endpointSecret);
      } catch (SignatureVerificationException var10) {
         log.error("Webhook signature verification failed.");
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
      } catch (Exception e) {
         log.error("Webhook fallback error.", e);
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
      }

      EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
      StripeObject stripeObject = null;
      if (dataObjectDeserializer.getObject().isPresent()) {
         stripeObject = (StripeObject)dataObjectDeserializer.getObject().get();
         switch (event.getType()) {
            case "payment_intent.succeeded":
               PaymentIntent paymentIntent = (PaymentIntent)stripeObject;
               this.handlePaymentIntentSucceeded(paymentIntent);
               break;
            case "payment_intent.payment_failed":
               PaymentIntent failedIntent = (PaymentIntent)stripeObject;
               this.handlePaymentIntentFailed(failedIntent);
               break;
            default:
               log.info("Unhandled event type: {}", event.getType());
         }

         return ResponseEntity.ok("");
      } else {
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
      }
   }

   private void handlePaymentIntentSucceeded(PaymentIntent intent) {
      String orderIdStr = (String)intent.getMetadata().get("order_id");
      if (orderIdStr == null || orderIdStr.isBlank()) {
         log.warn("Stripe payment_intent.succeeded missing order_id metadata");
         return;
      }

      try {
         UUID orderId = UUID.fromString(orderIdStr);
         BigDecimal amount = BigDecimal.valueOf(intent.getAmount()).divide(BigDecimal.valueOf(100L));
         this.paymentService.recordSuccessfulPayment(orderId, "CARD", amount, intent.getId());
         log.info("Order {} paid via Stripe webhook.", orderId);
      } catch (Exception e) {
         log.error("Failed to process payment_intent.succeeded for orderId {}", orderIdStr, e);
      }
   }

   private void handlePaymentIntentFailed(PaymentIntent intent) {
      String orderIdStr = (String)intent.getMetadata().get("order_id");
      if (orderIdStr != null) {
         try {
            UUID orderId = UUID.fromString(orderIdStr);
            Optional<Order> orderOpt = this.orderRepository.findById(orderId);
            if (orderOpt.isPresent()) {
               Order order = (Order)orderOpt.get();
               if ("PENDING_PAYMENT".equals(order.getStatus())) {
                  order.setStatus("CANCELLED");
                  this.orderRepository.save(order);
                  log.info("Order {} marked as FAILED via webhook.", orderId);
               }
            }
         } catch (Exception var6) {
            log.error("Failed to process payment_intent.payment_failed for orderId {}", orderIdStr, var6);
         }
      }

   }

   @Generated
   public StripeWebhookController(final OrderRepository orderRepository, final PaymentService paymentService) {
      this.orderRepository = orderRepository;
      this.paymentService = paymentService;
   }
}
