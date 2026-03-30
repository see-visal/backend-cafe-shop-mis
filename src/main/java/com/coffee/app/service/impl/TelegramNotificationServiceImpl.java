package com.coffee.app.service.impl;

import com.coffee.app.service.TelegramNotificationService;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TelegramNotificationServiceImpl implements TelegramNotificationService {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(TelegramNotificationServiceImpl.class);
   private static final ZoneId PP_ZONE = ZoneId.of("Asia/Phnom_Penh");
   private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy  ·  HH:mm:ss");
   @Value("${telegram.bot-token:}")
   private String botToken;
   @Value("${telegram.chat-id:}")
   private String chatId;

   public void sendPaymentAlert(String paymentMethod, BigDecimal amount, String orderId) {
      String shortId = (orderId.length() >= 8 ? orderId.substring(0, 8) : orderId).toUpperCase();
      String now = ZonedDateTime.now(PP_ZONE).format(TIME_FMT);
      String var10000;
      switch (paymentMethod) {
         case "QR_CODE" -> var10000 = "\ud83d\udfe2  <b>PAYMENT RECEIVED</b>\n▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔\n\n\ud83e\uddfe  <b>Method</b>\n     KHQR · Bakong / ABA / Wing / ACLEDA\n\n\ud83d\udcb5  <b>Amount Paid</b>\n     <code>$ %.2f  USD</code>\n\n\ud83d\udd16  <b>Order Reference</b>\n     <code>#%s</code>\n\n\ud83d\udd50  <b>Timestamp</b>\n     <code>%s</code>\n\n▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁\n✅  Order queued for barista  ✅".formatted(amount, shortId, now);
         case "CARD" -> var10000 = "\ud83d\udd35  <b>CARD PAYMENT APPROVED</b>\n▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔\n\n\ud83d\udcb3  <b>Method</b>\n     EDC Terminal · Visa / Mastercard / UnionPay\n\n\ud83d\udcb5  <b>Amount Paid</b>\n     <code>$ %.2f  USD</code>\n\n\ud83d\udd16  <b>Order Reference</b>\n     <code>#%s</code>\n\n\ud83d\udd50  <b>Timestamp</b>\n     <code>%s</code>\n\n▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁\n✅  Terminal approved — order queued  ✅".formatted(amount, shortId, now);
         case "CASH" -> var10000 = "\ud83d\udfe1  <b>CASH SALE RECORDED</b>\n▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔\n\n\ud83d\udcb5  <b>Method</b>\n     Cash · Confirmed at counter\n\n\ud83d\udcb0  <b>Amount Collected</b>\n     <code>$ %.2f  USD</code>\n\n\ud83d\udd16  <b>Order Reference</b>\n     <code>#%s</code>\n\n\ud83d\udd50  <b>Timestamp</b>\n     <code>%s</code>\n\n▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁\n\ud83d\udd13  Open drawer · return change  \ud83d\udd13".formatted(amount, shortId, now);
         default -> var10000 = "⚪  <b>NEW PAYMENT</b>\n▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔\n\n\ud83d\udcb3  <b>Method</b>\n     %s\n\n\ud83d\udcb5  <b>Amount Paid</b>\n     <code>$ %.2f  USD</code>\n\n\ud83d\udd16  <b>Order Reference</b>\n     <code>#%s</code>\n\n\ud83d\udd50  <b>Timestamp</b>\n     <code>%s</code>\n\n▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁\n✅  Payment confirmed  ✅".formatted(paymentMethod, amount, shortId, now);
      }

      String html = var10000;
      this.sendMessage(html);
   }

   public void sendCustomerCheckoutSuccess(String customerChatId, String customerName, String orderId, BigDecimal amount, String paymentMethod) {
      String shortIdRaw = orderId != null && orderId.length() >= 8 ? orderId.substring(0, 8) : orderId;
      String shortId = shortIdRaw != null ? shortIdRaw.toUpperCase() : "-";
      String safeName = customerName != null && !customerName.isBlank() ? customerName.trim() : "Customer";
      String now = ZonedDateTime.now(PP_ZONE).format(TIME_FMT);
      String method = paymentMethod != null && !paymentMethod.isBlank() ? paymentMethod : "UNKNOWN";
      String amountText = amount != null ? String.format("%.2f", amount) : "0.00";
      String html = "<b>Checkout Confirmed</b>\n\nHi " + safeName + ", your payment was successful.\n"
         + "Order: <code>#" + shortId + "</code>\n"
         + "Amount: <code>$ " + amountText + " USD</code>\n"
         + "Method: <code>" + method + "</code>\n"
         + "Time: <code>" + now + "</code>\n\n"
         + "Thank you for ordering at SalSee Coffee Shop.";
      this.sendMessageToChatId(customerChatId, html);
   }

   private void sendMessage(String html) {
      this.sendMessageToChatId(this.chatId, html);
   }

   private void sendMessageToChatId(String targetChatId, String html) {
      if (this.botToken != null && !this.botToken.isBlank() && targetChatId != null && !targetChatId.isBlank()) {
         try {
            String url = "https://api.telegram.org/bot" + this.botToken + "/sendMessage";
            Map<String, String> body = new LinkedHashMap<>();
            body.put("chat_id", targetChatId);
            body.put("text", html);
            body.put("parse_mode", "HTML");
            RestClient.create().post().uri(url, new Object[0]).header("Content-Type", new String[]{"application/json"}).body(body).retrieve().toBodilessEntity();
            log.info("Telegram payment alert dispatched successfully");
         } catch (Exception ex) {
            log.error("Telegram notification failed (non-critical): {}", ex.getMessage());
         }

      } else {
         log.warn("Telegram not configured — skipping notification");
      }
   }
}
