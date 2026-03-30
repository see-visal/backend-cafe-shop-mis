package com.coffee.app.controller;

import com.coffee.app.dto.response.CategoryResponse;
import com.coffee.app.dto.response.HomeShowcaseResponse;
import com.coffee.app.dto.response.ProductResponse;
import com.coffee.app.dto.request.PromoValidateRequest;
import com.coffee.app.dto.response.PromoValidateResponse;
import com.coffee.app.dto.response.ShopTableResponse;
import com.coffee.app.service.CategoryService;
import com.coffee.app.service.FileStorageService;
import com.coffee.app.service.PasswordResetService;
import com.coffee.app.service.ProductService;
import com.coffee.app.service.PromoCodeService;
import com.coffee.app.service.TableService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Generated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.Duration;

@RestController
@RequestMapping({"/api/public"})
public class PublicController {
   private final ProductService productService;
   private final CategoryService categoryService;
   private final TableService tableService;
   private final PromoCodeService promoCodeService;
   private final PasswordResetService passwordResetService;
   private final FileStorageService fileStorageService;

   @Operation(
      summary = "Get product categories"
   )
   @GetMapping({"/categories"})
   public ResponseEntity<List<CategoryResponse>> getCategories() {
      return ResponseEntity.ok(this.categoryService.getAll());
   }

   @Operation(
      summary = "Get products",
      description = "Browse active products with pagination and optional filtering"
   )
   @GetMapping({"/products"})
   public ResponseEntity<Page<ProductResponse>> getProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) Integer categoryId, @RequestParam(required = false) String search) {
      PageRequest pageable = PageRequest.of(page, size, Sort.by(new String[]{"createdAt"}).descending());
      return ResponseEntity.ok(this.productService.searchActiveProducts(search, categoryId, pageable));
   }

   @Operation(
      summary = "Get homepage showcase products",
      description = "Returns admin-managed today's specials and featured home products"
   )
   @GetMapping({"/home-showcase"})
   public ResponseEntity<HomeShowcaseResponse> getHomeShowcase() {
      return ResponseEntity.ok(this.productService.getHomeShowcase());
   }

   @Operation(
      summary = "Get product by ID",
      description = "Retrieve detailed information about a specific product"
   )
   @GetMapping({"/products/{id}"})
   public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID id) {
      return ResponseEntity.ok(this.productService.getProductById(id));
   }

   @Operation(
      summary = "Get a storage-backed image",
      description = "Streams a MinIO-backed image through the backend so clients can render files even when MinIO is exposed through the console UI."
   )
   @GetMapping({"/storage/image"})
   public ResponseEntity<byte[]> getStorageImage(@RequestParam String path) {
      FileStorageService.StoredFile storedFile = this.fileStorageService.downloadFile(path);
      MediaType mediaType = MediaType.parseMediaType(storedFile.contentType() != null ? storedFile.contentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE);
      return ResponseEntity.ok()
         .cacheControl(CacheControl.maxAge(Duration.ofHours(1)).cachePublic())
         .contentType(mediaType)
         .body(storedFile.content());
   }

   @Operation(
      summary = "Get active dine-in tables"
   )
   @GetMapping({"/tables"})
   public ResponseEntity<List<ShopTableResponse>> getActiveTables() {
      return ResponseEntity.ok(this.tableService.getActive());
   }

   @Operation(
      summary = "Check table availability"
   )
   @GetMapping({"/tables/{tableNumber}/availability"})
   public ResponseEntity<Map<String, Object>> checkTableAvailability(@PathVariable Integer tableNumber) {
      boolean active = this.tableService.isTableActive(tableNumber);
      return ResponseEntity.ok(Map.of("tableNumber", tableNumber, "active", active));
   }

   @Operation(summary = "Validate promo code")
   @PostMapping({"/promos/validate"})
   public ResponseEntity<PromoValidateResponse> validatePromo(@RequestBody @Valid PromoValidateRequest request) {
      return ResponseEntity.ok(this.promoCodeService.validate(request.code(), request.orderTotal() != null ? request.orderTotal() : java.math.BigDecimal.ZERO));
   }

   // ── Forgot / Reset Password ──────────────────────────────────────────────

   record ForgotPasswordRequest(@NotBlank @Email String email) {}
   record ResetPasswordRequest(@NotBlank @Size(min = 6, max = 6) String otp,
                               @NotBlank @Size(min = 8, max = 128) String newPassword) {}

   @Operation(summary = "Request a password-reset OTP (sent via Telegram)")
   @PostMapping("/auth/forgot-password")
   public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest req) {
      this.passwordResetService.requestReset(req.email());
      return ResponseEntity.ok(Map.of("message", "OTP sent. Check your Telegram (or ask staff) for the 6-digit code."));
   }

   @Operation(summary = "Verify OTP and set a new password")
   @PostMapping("/auth/reset-password")
   public ResponseEntity<Map<String, String>> resetPassword(@RequestBody @Valid ResetPasswordRequest req) {
      this.passwordResetService.resetPassword(req.otp(), req.newPassword());
      return ResponseEntity.ok(Map.of("message", "Password updated successfully. You can now log in."));
   }

   @Generated
   public PublicController(final ProductService productService, final CategoryService categoryService, final TableService tableService, final PromoCodeService promoCodeService, final PasswordResetService passwordResetService, final FileStorageService fileStorageService) {
      this.productService = productService;
      this.categoryService = categoryService;
      this.tableService = tableService;
      this.promoCodeService = promoCodeService;
      this.passwordResetService = passwordResetService;
      this.fileStorageService = fileStorageService;
   }
}
