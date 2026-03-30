package com.coffee.app.controller;

import com.coffee.app.domain.Payment;
import com.coffee.app.dto.request.AdminCreateUserRequest;
import com.coffee.app.dto.request.AdminUserRequest;
import com.coffee.app.dto.request.CategoryRequest;
import com.coffee.app.dto.request.IngredientRequest;
import com.coffee.app.dto.request.PaymentActionRequest;
import com.coffee.app.dto.request.ProductRequest;
import com.coffee.app.dto.request.PromoCodeRequest;
import com.coffee.app.dto.request.ShopTableRequest;
import com.coffee.app.dto.request.StatusUpdateRequest;
import com.coffee.app.dto.request.StockAdjustRequest;
import com.coffee.app.dto.response.AdminUserResponse;
import com.coffee.app.dto.response.AuditLogResponse;
import com.coffee.app.dto.response.CategoryResponse;
import com.coffee.app.dto.response.DashboardResponse;
import com.coffee.app.dto.response.IngredientResponse;
import com.coffee.app.dto.response.LoyaltyOverviewResponse;
import com.coffee.app.dto.response.OrderResponse;
import com.coffee.app.dto.response.PaymentResponse;
import com.coffee.app.dto.response.ProductResponse;
import com.coffee.app.dto.response.PromoCodeResponse;
import com.coffee.app.dto.response.RatingResponse;
import com.coffee.app.dto.response.ReceiptResponse;
import com.coffee.app.dto.response.ShopTableResponse;
import com.coffee.app.dto.response.SettlementReportResponse;
import com.coffee.app.repository.PaymentRepository;
import com.coffee.app.repository.UserRepository;
import com.coffee.app.service.AdminService;
import com.coffee.app.service.AuditLogService;
import com.coffee.app.service.CategoryService;
import com.coffee.app.service.FileStorageService;
import com.coffee.app.service.InventoryService;
import com.coffee.app.service.OrderService;
import com.coffee.app.service.PaymentService;
import com.coffee.app.service.ProductService;
import com.coffee.app.service.PromoCodeService;
import com.coffee.app.service.RatingService;
import com.coffee.app.service.TableService;
import com.coffee.app.service.UserManagementService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Generated;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping({"/api/admin"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
   private static final Set<String> ALLOWED_UPLOAD_DIRECTORIES = Set.of("products", "categories", "ingredients", "users", "users/profile", "users/cover", "users/customers");
   private final ProductService productService;
   private final OrderService orderService;
   private final InventoryService inventoryService;
   private final AdminService adminService;
   private final CategoryService categoryService;
   private final PaymentService paymentService;
   private final UserManagementService userManagementService;
   private final AuditLogService auditLogService;
   private final PromoCodeService promoCodeService;
   private final RatingService ratingService;
   private final UserRepository userRepository;
   private final TableService tableService;
   private final PaymentRepository paymentRepository;
   private final FileStorageService fileStorageService;


   @GetMapping({"/dashboard"})
   public ResponseEntity<DashboardResponse> getDashboard() {
      return ResponseEntity.ok(this.adminService.getDashboard());
   }

   @GetMapping({"/categories"})
   public ResponseEntity<List<CategoryResponse>> getCategories() {
      return ResponseEntity.ok(this.categoryService.getAll());
   }

   @PostMapping({"/categories"})
   public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request, @AuthenticationPrincipal Jwt jwt) {
      CategoryResponse category = this.categoryService.create(request);
      this.auditLogService.log(this.actorId(jwt), "CREATE_CATEGORY", "Category", String.valueOf(category.id()), "Created category: " + category.name());
      return ResponseEntity.status(201).body(category);
   }

   @PutMapping({"/categories/{id}"})
   public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Integer id, @RequestBody @Valid CategoryRequest request, @AuthenticationPrincipal Jwt jwt) {
      CategoryResponse category = this.categoryService.update(id, request);
      this.auditLogService.log(this.actorId(jwt), "UPDATE_CATEGORY", "Category", String.valueOf(id), "Updated category: " + category.name());
      return ResponseEntity.ok(category);
   }

   @PostMapping(
      value = {"/categories/{id}/image"},
      consumes = {"multipart/form-data"}
   )
   public ResponseEntity<Map<String, String>> uploadCategoryImage(@PathVariable Integer id, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal Jwt jwt) {
      if (file.isEmpty()) {
         return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
      }

      String contentType = file.getContentType();
      if (contentType != null && contentType.startsWith("image/")) {
         try {
            String imageUrl = this.fileStorageService.uploadFile(file, "categories");
            this.categoryService.updateCategoryImage(id, imageUrl);
            this.auditLogService.log(this.actorId(jwt), "UPLOAD_CATEGORY_IMAGE", "Category", String.valueOf(id), "Updated category image");
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
         } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload image: " + e.getMessage()));
         }
      } else {
         return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
      }
   }

   @DeleteMapping({"/categories/{id}"})
   public ResponseEntity<Void> deleteCategory(@PathVariable Integer id, @AuthenticationPrincipal Jwt jwt) {
      this.categoryService.delete(id);
      this.auditLogService.log(this.actorId(jwt), "DELETE_CATEGORY", "Category", String.valueOf(id), "Deleted category");
      return ResponseEntity.noContent().build();
   }

   @PostMapping(
      value = {"/uploads/image"},
      consumes = {"multipart/form-data"}
   )
   public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam(defaultValue = "users") String directory, @AuthenticationPrincipal Jwt jwt) {
      if (file.isEmpty()) {
         return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
      }

      String normalizedDirectory = directory == null ? "users" : directory.trim().toLowerCase();
      if (!ALLOWED_UPLOAD_DIRECTORIES.contains(normalizedDirectory)) {
         return ResponseEntity.badRequest().body(Map.of("error", "Unsupported upload directory: " + directory));
      }

      String contentType = file.getContentType();
      if (contentType == null || !contentType.startsWith("image/")) {
         return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
      }

      try {
         String imageUrl = this.fileStorageService.uploadFile(file, normalizedDirectory);
         this.auditLogService.log(this.actorId(jwt), "UPLOAD_IMAGE", "Media", imageUrl, "Uploaded image to " + normalizedDirectory);
         return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
      } catch (Exception e) {
         return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload image: " + e.getMessage()));
      }
   }

   @GetMapping({"/products"})
   public ResponseEntity<Page<ProductResponse>> getAllProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
      PageRequest pageable = PageRequest.of(page, size, Sort.by(new String[]{"createdAt"}).descending());
      return ResponseEntity.ok(this.productService.getAllProducts(pageable));
   }

   @PostMapping({"/products"})
   public ResponseEntity<ProductResponse> createProduct(@RequestBody @Valid ProductRequest request, @AuthenticationPrincipal Jwt jwt) {
      ProductResponse product = this.productService.createProduct(request);
      this.auditLogService.log(this.actorId(jwt), "CREATE_PRODUCT", "Product", product.id().toString(), "Created product: " + product.name());
      return ResponseEntity.status(201).body(product);
   }

   @PutMapping({"/products/{id}"})
   public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID id, @RequestBody @Valid ProductRequest request, @AuthenticationPrincipal Jwt jwt) {
      ProductResponse product = this.productService.updateProduct(id, request);
      this.auditLogService.log(this.actorId(jwt), "UPDATE_PRODUCT", "Product", id.toString(), "Updated product: " + product.name());
      return ResponseEntity.ok(product);
   }

   @DeleteMapping({"/products/{id}"})
   public ResponseEntity<Void> softDeleteProduct(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
      this.productService.softDelete(id);
      this.auditLogService.log(this.actorId(jwt), "DELETE_PRODUCT", "Product", id.toString(), "Archived product");
      return ResponseEntity.noContent().build();
   }

   @PostMapping(
      value = {"/products/{id}/image"},
      consumes = {"multipart/form-data"}
   )
   public ResponseEntity<Map<String, String>> uploadProductImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal Jwt jwt) {
      if (file.isEmpty()) {
         return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
      }

      String contentType = file.getContentType();
      if (contentType != null && contentType.startsWith("image/")) {
         try {
            String imageUrl = this.fileStorageService.uploadFile(file, "products");
            this.productService.updateProductImage(id, imageUrl);
            this.auditLogService.log(this.actorId(jwt), "UPLOAD_PRODUCT_IMAGE", "Product", id.toString(), "Updated product image");
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
         } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload image: " + e.getMessage()));
         }
      } else {
         return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
      }
   }

   @GetMapping({"/orders"})
   public ResponseEntity<Page<OrderResponse>> getAllOrders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String status, @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate dateFrom, @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate dateTo) {
      PageRequest pageable = PageRequest.of(page, size, Sort.by(new String[]{"createdAt"}).descending());
      LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
      LocalDateTime to = dateTo != null ? dateTo.atTime(23, 59, 59) : null;
      return ResponseEntity.ok(this.orderService.getAllOrders(pageable, status, from, to));
   }

   @PutMapping({"/orders/{id}/status"})
   public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable UUID id, @RequestBody @Valid StatusUpdateRequest request, @AuthenticationPrincipal Jwt jwt) {
      OrderResponse order = this.orderService.updateStatus(id, request.status(), (UUID)null);
      this.auditLogService.log(this.actorId(jwt), "ADMIN_UPDATE_ORDER_STATUS", "Order", id.toString(), "Set order status to " + request.status());
      return ResponseEntity.ok(order);
   }

   @DeleteMapping({"/orders/{id}"})
   public ResponseEntity<Void> cancelOrder(@PathVariable UUID id) {
      this.orderService.cancelOrder(id);
      return ResponseEntity.noContent().build();
   }

   @PatchMapping({"/orders/{id}/cancel"})
   public ResponseEntity<Void> cancelOrderCompat(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
      this.orderService.cancelOrder(id);
      this.auditLogService.log(this.actorId(jwt), "ADMIN_CANCEL_ORDER", "Order", id.toString(), "Cancelled order");
      return ResponseEntity.noContent().build();
   }

   @GetMapping({"/orders/export"})
   public void exportOrdersCsv(HttpServletResponse response, @RequestParam(required = false) String status, @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate dateFrom, @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate dateTo) throws IOException {
      response.setContentType("text/csv");
      response.setHeader("Content-Disposition", "attachment; filename=\"orders.csv\"");
      PageRequest pageable = PageRequest.of(0, 2147483647, Sort.by(new String[]{"createdAt"}).descending());
      LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
      LocalDateTime to = dateTo != null ? dateTo.atTime(23, 59, 59) : null;
      List<OrderResponse> orders = this.orderService.getAllOrders(pageable, status, from, to).getContent();
      PrintWriter writer = response.getWriter();
      writer.println("Order ID,Status,Order Type,Table,Total Price,Created At,Served At,Items");
      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

      for(OrderResponse o : orders) {
         String items = (String)o.items().stream().map((i) -> {
            int var10000 = i.quantity();
            return var10000 + "x " + i.productName();
         }).reduce((a, b) -> a + " | " + b).orElse("");
         writer.printf("\"%s\",\"%s\",\"%s\",%s,\"%.2f\",\"%s\",\"%s\",\"%s\"%n", o.id(), o.status(), o.orderType(), o.tableNumber() != null ? o.tableNumber() : "", o.totalPrice(), o.createdAt() != null ? o.createdAt().format(fmt) : "", o.servedAt() != null ? o.servedAt().format(fmt) : "", items);
      }

      writer.flush();
   }

   @GetMapping({"/ingredients"})
   public ResponseEntity<List<IngredientResponse>> getIngredients() {
      return ResponseEntity.ok(this.inventoryService.getAllIngredients());
   }

   @GetMapping({"/ingredients/{id}"})
   public ResponseEntity<IngredientResponse> getIngredient(@PathVariable UUID id) {
      return ResponseEntity.ok(this.inventoryService.getIngredientById(id));
   }

   @GetMapping({"/ingredients/alerts"})
   public ResponseEntity<List<IngredientResponse>> getLowStockAlerts() {
      return ResponseEntity.ok(this.inventoryService.getLowStockIngredients());
   }

   @PostMapping({"/ingredients"})
   public ResponseEntity<IngredientResponse> createIngredient(@RequestBody @Valid IngredientRequest request, @AuthenticationPrincipal Jwt jwt) {
      IngredientResponse ingredient = this.inventoryService.createIngredient(request);
      this.auditLogService.log(this.actorId(jwt), "CREATE_INGREDIENT", "Ingredient", ingredient.id().toString(), "Created ingredient: " + ingredient.name());
      return ResponseEntity.status(201).body(ingredient);
   }

   @PutMapping({"/ingredients/{id}"})
   public ResponseEntity<IngredientResponse> updateIngredient(@PathVariable UUID id, @RequestBody @Valid IngredientRequest request, @AuthenticationPrincipal Jwt jwt) {
      IngredientResponse ingredient = this.inventoryService.updateIngredient(id, request);
      this.auditLogService.log(this.actorId(jwt), "UPDATE_INGREDIENT", "Ingredient", id.toString(), "Updated ingredient: " + ingredient.name());
      return ResponseEntity.ok(ingredient);
   }

   @PostMapping(
      value = {"/ingredients/{id}/image"},
      consumes = {"multipart/form-data"}
   )
   public ResponseEntity<Map<String, String>> uploadIngredientImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal Jwt jwt) {
      if (file.isEmpty()) {
         return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
      }

      String contentType = file.getContentType();
      if (contentType != null && contentType.startsWith("image/")) {
         try {
            String imageUrl = this.fileStorageService.uploadFile(file, "ingredients");
            this.inventoryService.updateIngredientImage(id, imageUrl);
            this.auditLogService.log(this.actorId(jwt), "UPLOAD_INGREDIENT_IMAGE", "Ingredient", id.toString(), "Updated ingredient image");
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
         } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to upload image: " + e.getMessage()));
         }
      } else {
         return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
      }
   }

   @PatchMapping({"/ingredients/adjust-stock"})
   public ResponseEntity<IngredientResponse> adjustStock(@RequestBody @Valid StockAdjustRequest request, @AuthenticationPrincipal Jwt jwt) {
      IngredientResponse ingredient = this.inventoryService.adjustStock(request);
      this.auditLogService.log(this.actorId(jwt), "ADJUST_STOCK", "Ingredient", ingredient.id().toString(), "Adjusted stock by " + request.delta());
      return ResponseEntity.ok(ingredient);
   }

   @DeleteMapping({"/ingredients/{id}"})
   public ResponseEntity<Void> deleteIngredient(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
      this.inventoryService.deleteIngredient(id);
      this.auditLogService.log(this.actorId(jwt), "DELETE_INGREDIENT", "Ingredient", id.toString(), "Deleted ingredient");
      return ResponseEntity.noContent().build();
   }

   @GetMapping({"/payments/order/{orderId}"})
   public ResponseEntity<PaymentResponse> getPaymentByOrder(@PathVariable UUID orderId) {
      return ResponseEntity.ok(this.paymentService.getPaymentByOrder(orderId));
   }

   @PatchMapping({"/payments/{paymentId}/confirm"})
   public ResponseEntity<PaymentResponse> confirmPayment(@PathVariable UUID paymentId, @RequestParam(required = false) String transactionRef, @AuthenticationPrincipal Jwt jwt) {
      PaymentResponse payment = this.paymentService.confirmPayment(paymentId, transactionRef);
      this.auditLogService.log(this.actorId(jwt), "CONFIRM_PAYMENT", "Payment", paymentId.toString(), "Confirmed payment");
      return ResponseEntity.ok(payment);
   }

   @PatchMapping({"/payments/{paymentId}/void-or-refund", "/payments/{paymentId}/refund"})
   public ResponseEntity<PaymentResponse> voidOrRefundPayment(@PathVariable UUID paymentId, @RequestBody(required = false) PaymentActionRequest request, @AuthenticationPrincipal Jwt jwt) {
      PaymentResponse payment = this.paymentService.voidOrRefundPayment(paymentId, request != null ? request.reason() : null);
      this.auditLogService.log(this.actorId(jwt), "VOID_OR_REFUND_PAYMENT", "Payment", paymentId.toString(), "Processed void/refund payment action");
      return ResponseEntity.ok(payment);
   }

   @GetMapping({"/orders/{orderId}/receipt"})
   public ResponseEntity<ReceiptResponse> getReceipt(@PathVariable UUID orderId) {
      return ResponseEntity.ok(this.paymentService.getReceipt(orderId));
   }

   @GetMapping({"/users"})
   public ResponseEntity<Page<AdminUserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(required = false) List<String> roles) {
      PageRequest pageable = PageRequest.of(page, size, Sort.by(new String[]{"id"}).descending());
      return ResponseEntity.ok(this.userManagementService.getAllUsers(pageable, roles));
   }

   @PostMapping({"/users"})
   public ResponseEntity<AdminUserResponse> createUser(@RequestBody @Valid AdminCreateUserRequest request, @AuthenticationPrincipal Jwt jwt) {
      AdminUserResponse created = this.userManagementService.createUser(request);
      AuditLogService var10000 = this.auditLogService;
      String var10001 = this.actorId(jwt);
      String var10004 = created.id();
      String var10005 = created.username();
      var10000.log(var10001, "CREATE_USER", "User", var10004, "Created user: " + var10005 + " with role: " + request.role());
      return ResponseEntity.status(201).body(created);
   }

   @GetMapping({"/users/{uuid}"})
   public ResponseEntity<AdminUserResponse> getUser(@PathVariable String uuid) {
      return ResponseEntity.ok(this.userManagementService.getUserByUuid(uuid));
   }

   @PutMapping({"/users/{uuid}"})
   public ResponseEntity<AdminUserResponse> updateUser(@PathVariable String uuid, @RequestBody @Valid AdminUserRequest request, @AuthenticationPrincipal Jwt jwt) {
      AdminUserResponse updated = this.userManagementService.updateUser(uuid, request);
      this.auditLogService.log(this.actorId(jwt), "UPDATE_USER", "User", uuid, "Updated user: " + updated.username());
      return ResponseEntity.ok(updated);
   }

   @PatchMapping({"/users/{uuid}/enable"})
   public ResponseEntity<AdminUserResponse> setUserEnabled(@PathVariable String uuid, @RequestParam boolean enabled, @AuthenticationPrincipal Jwt jwt) {
      AdminUserResponse updated = this.userManagementService.setUserEnabled(uuid, enabled);
      this.auditLogService.log(this.actorId(jwt), enabled ? "ENABLE_USER" : "DISABLE_USER", "User", uuid, "Set enabled=" + enabled + " for user: " + updated.username());
      return ResponseEntity.ok(updated);
   }

   @PatchMapping({"/users/{uuid}/lock"})
   public ResponseEntity<AdminUserResponse> setUserLocked(@PathVariable String uuid, @RequestParam boolean locked, @AuthenticationPrincipal Jwt jwt) {
      AdminUserResponse updated = this.userManagementService.setUserLocked(uuid, locked);
      this.auditLogService.log(this.actorId(jwt), locked ? "LOCK_USER" : "UNLOCK_USER", "User", uuid, "Set locked=" + locked + " for user: " + updated.username());
      return ResponseEntity.ok(updated);
   }

   @DeleteMapping({"/users/{uuid}"})
   public ResponseEntity<Void> deleteUser(@PathVariable String uuid, @AuthenticationPrincipal Jwt jwt) {
      this.userManagementService.deleteUser(uuid);
      this.auditLogService.log(this.actorId(jwt), "DELETE_USER", "User", uuid, "Deleted user: " + uuid);
      return ResponseEntity.noContent().build();
   }

   @GetMapping({"/audit"})
   public ResponseEntity<Page<AuditLogResponse>> getAuditLogs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size, @RequestParam(required = false) String actorId, @RequestParam(required = false) String entity, @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate dateFrom, @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate dateTo) {
      PageRequest pageable = PageRequest.of(page, size);
      LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
      LocalDateTime to = dateTo != null ? dateTo.atTime(23, 59, 59) : null;
      return ResponseEntity.ok(this.auditLogService.query(actorId, entity, from, to, pageable));
   }

   @GetMapping({"/promos"})
   public ResponseEntity<List<PromoCodeResponse>> getPromos() {
      return ResponseEntity.ok(this.promoCodeService.getAll());
   }

   @PostMapping({"/promos"})
   public ResponseEntity<PromoCodeResponse> createPromo(@RequestBody @Valid PromoCodeRequest request, @AuthenticationPrincipal Jwt jwt) {
      PromoCodeResponse promo = this.promoCodeService.create(request);
      this.auditLogService.log(this.actorId(jwt), "CREATE_PROMO", "PromoCode", promo.id().toString(), "Created promo code: " + promo.code());
      return ResponseEntity.status(201).body(promo);
   }

   @PatchMapping({"/promos/{id}/expire"})
   public ResponseEntity<PromoCodeResponse> expirePromo(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
      PromoCodeResponse promo = this.promoCodeService.expire(id);
      this.auditLogService.log(this.actorId(jwt), "EXPIRE_PROMO", "PromoCode", id.toString(), "Expired promo code: " + promo.code());
      return ResponseEntity.ok(promo);
   }

   @DeleteMapping({"/promos/{id}"})
   public ResponseEntity<Void> deletePromo(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
      this.promoCodeService.delete(id);
      this.auditLogService.log(this.actorId(jwt), "DELETE_PROMO", "PromoCode", id.toString(), "Deleted promo code");
      return ResponseEntity.noContent().build();
   }

   @GetMapping({"/tables"})
   public ResponseEntity<List<ShopTableResponse>> getTables() {
      return ResponseEntity.ok(this.tableService.getAll());
   }

   @PostMapping({"/tables"})
   public ResponseEntity<ShopTableResponse> createTable(@RequestBody @Valid ShopTableRequest request, @AuthenticationPrincipal Jwt jwt) {
      ShopTableResponse table = this.tableService.create(request);
      this.auditLogService.log(this.actorId(jwt), "CREATE_TABLE", "Table", String.valueOf(table.id()), "Created shop table");
      return ResponseEntity.status(201).body(table);
   }

   @PutMapping({"/tables/{id}"})
   public ResponseEntity<ShopTableResponse> updateTable(@PathVariable Long id, @RequestBody @Valid ShopTableRequest request, @AuthenticationPrincipal Jwt jwt) {
      ShopTableResponse table = this.tableService.update(id, request);
      this.auditLogService.log(this.actorId(jwt), "UPDATE_TABLE", "Table", String.valueOf(id), "Updated shop table");
      return ResponseEntity.ok(table);
   }

   @PatchMapping({"/tables/{id}/active"})
   public ResponseEntity<ShopTableResponse> setTableActive(@PathVariable Long id, @RequestParam boolean active, @AuthenticationPrincipal Jwt jwt) {
      ShopTableResponse table = this.tableService.setActive(id, active);
      this.auditLogService.log(this.actorId(jwt), active ? "ENABLE_TABLE" : "DISABLE_TABLE", "Table", String.valueOf(id), "Set table active=" + active);
      return ResponseEntity.ok(table);
   }

   @DeleteMapping({"/tables/{id}"})
   public ResponseEntity<Void> deleteTable(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
      this.tableService.delete(id);
      this.auditLogService.log(this.actorId(jwt), "DELETE_TABLE", "Table", String.valueOf(id), "Deleted shop table");
      return ResponseEntity.noContent().build();
   }

   @PatchMapping({"/products/{id}/availability"})
   public ResponseEntity<ProductResponse> setProductAvailability(@PathVariable UUID id, @RequestParam boolean available, @AuthenticationPrincipal Jwt jwt) {
      ProductResponse product = this.productService.setAvailability(id, available);
      this.auditLogService.log(this.actorId(jwt), available ? "MARK_PRODUCT_AVAILABLE" : "MARK_PRODUCT_UNAVAILABLE", "Product", id.toString(), "Set product availability to " + available);
      return ResponseEntity.ok(product);
   }

   @GetMapping({"/products/{productId}/ratings"})
   public ResponseEntity<List<RatingResponse>> getProductRatings(@PathVariable UUID productId) {
      return ResponseEntity.ok(this.ratingService.getRatingsByProduct(productId));
   }

   @GetMapping({"/products/{productId}/ratings/average"})
   public ResponseEntity<Map<String, Object>> getProductAverageRating(@PathVariable UUID productId) {
      Double avg = this.ratingService.getAverageRating(productId);
      return ResponseEntity.ok(Map.of("productId", productId, "averageStars", avg != null ? avg : 0.0));
   }

   @GetMapping({"/loyalty"})
   public ResponseEntity<LoyaltyOverviewResponse> getLoyaltyOverview() {
      Long totalPoints = this.userRepository.sumLoyaltyPoints();
      Long usersWithPoints = this.userRepository.countUsersWithPoints();
      LoyaltyOverviewResponse response = new LoyaltyOverviewResponse(totalPoints != null ? totalPoints : 0L, 0L, this.userRepository.count(), usersWithPoints != null ? usersWithPoints : 0L);
      return ResponseEntity.ok(response);
   }

   @GetMapping({"/reports/settlement"})
   public ResponseEntity<SettlementReportResponse> getSettlement(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date) {
      return ResponseEntity.ok(this.buildSettlementReport(date));
   }

   @GetMapping({"/reports/settlement/export"})
   public void exportSettlementCsv(HttpServletResponse response, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate date) throws IOException {
      SettlementReportResponse report = this.buildSettlementReport(date);
      response.setContentType("text/csv");
      response.setHeader("Content-Disposition", "attachment; filename=\"settlement-" + date + ".csv\"");
      PrintWriter writer = response.getWriter();
      writer.println("Date,Total Orders,Total Revenue,Cash Revenue,Cash Orders,KHQR Revenue,KHQR Orders,Card Revenue,Card Orders,Total Refunds,Net Revenue");
      writer.printf("%s,%d,%.2f,%.2f,%d,%.2f,%d,%.2f,%d,%.2f,%.2f%n", report.date(), report.totalOrders(), report.totalRevenue(), report.cashRevenue(), report.cashOrders(), report.khqrRevenue(), report.khqrOrders(), report.cardRevenue(), report.cardOrders(), report.totalRefunds(), report.netRevenue());
      writer.flush();
   }

   private SettlementReportResponse buildSettlementReport(LocalDate date) {
      LocalDateTime start = date.atStartOfDay();
      LocalDateTime end = date.atTime(23, 59, 59);
      PageRequest pageable = PageRequest.of(0, 10000, Sort.by(new String[]{"createdAt"}));
      Page<OrderResponse> page = this.orderService.getAllOrders(pageable, (String)null, start, end);
      List<OrderResponse> orders = page.getContent();
      long totalOrders = orders.stream().filter((o) -> !"CANCELLED".equals(o.status()) && !"PENDING_PAYMENT".equals(o.status())).count();
      BigDecimal totalRevenue = (BigDecimal)orders.stream().filter((o) -> !"CANCELLED".equals(o.status()) && !"PENDING_PAYMENT".equals(o.status())).map(OrderResponse::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
      List<UUID> orderIds = orders.stream().map(OrderResponse::id).toList();
      List<Payment> payments = orderIds.isEmpty() ? List.of() : this.paymentRepository.findByOrderIdIn(orderIds);
      BigDecimal cashRevenue = BigDecimal.ZERO;
      long cashOrders = 0L;
      BigDecimal khqrRevenue = BigDecimal.ZERO;
      long khqrOrders = 0L;
      BigDecimal cardRevenue = BigDecimal.ZERO;
      long cardOrders = 0L;
      BigDecimal totalRefunds = BigDecimal.ZERO;

      for(Payment payment : payments) {
         String method = payment.getPaymentMethod() != null ? payment.getPaymentMethod().toUpperCase() : "";
         String status = payment.getStatus() != null ? payment.getStatus().toUpperCase() : "";
         BigDecimal amount = payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO;
         if ("REFUNDED".equals(status) || "VOID".equals(status)) {
            totalRefunds = totalRefunds.add(amount);
         }

         if (!"PENDING".equals(status)) {
            if ("CASH".equals(method)) {
               cashRevenue = cashRevenue.add(amount);
               ++cashOrders;
            } else if ("QR_CODE".equals(method) || "KHQR".equals(method)) {
               khqrRevenue = khqrRevenue.add(amount);
               ++khqrOrders;
            } else if ("CARD".equals(method)) {
               cardRevenue = cardRevenue.add(amount);
               ++cardOrders;
            }
         }
      }

      BigDecimal netRevenue = totalRevenue.subtract(totalRefunds);
      return new SettlementReportResponse(date, totalOrders, totalRevenue, cashRevenue, cashOrders, khqrRevenue, khqrOrders, cardRevenue, cardOrders, totalRefunds, netRevenue);
   }

   private String actorId(Jwt jwt) {
      return jwt != null && jwt.getClaimAsString("uuid") != null ? jwt.getClaimAsString("uuid") : jwt != null ? jwt.getSubject() : "ADMIN";
   }

   @Generated
   public AdminController(final ProductService productService, final OrderService orderService, final InventoryService inventoryService, final AdminService adminService, final CategoryService categoryService, final PaymentService paymentService, final UserManagementService userManagementService, final AuditLogService auditLogService, final PromoCodeService promoCodeService, final RatingService ratingService, final UserRepository userRepository, final TableService tableService, final PaymentRepository paymentRepository, final FileStorageService fileStorageService) {
      this.productService = productService;
      this.orderService = orderService;
      this.inventoryService = inventoryService;
      this.adminService = adminService;
      this.categoryService = categoryService;
      this.paymentService = paymentService;
      this.userManagementService = userManagementService;
      this.auditLogService = auditLogService;
      this.promoCodeService = promoCodeService;
      this.ratingService = ratingService;
      this.userRepository = userRepository;
      this.tableService = tableService;
      this.paymentRepository = paymentRepository;
      this.fileStorageService = fileStorageService;
   }
}
