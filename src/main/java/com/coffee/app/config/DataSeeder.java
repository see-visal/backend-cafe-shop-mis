package com.coffee.app.config;

import com.coffee.app.domain.Category;
import com.coffee.app.domain.Ingredient;
import com.coffee.app.domain.Product;
import com.coffee.app.repository.CategoryRepository;
import com.coffee.app.repository.IngredientRepository;
import com.coffee.app.repository.ProductRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {
   private final CategoryRepository categoryRepository;
   private final ProductRepository productRepository;
   private final IngredientRepository ingredientRepository;

   public DataSeeder(final CategoryRepository categoryRepository, final ProductRepository productRepository, final IngredientRepository ingredientRepository) {
      this.categoryRepository = categoryRepository;
      this.productRepository = productRepository;
      this.ingredientRepository = ingredientRepository;
   }

   @Override
   @Transactional
   public void run(String... args) throws Exception {
      Category coffeeCat = this.upsertCategory("Coffee", "coffee");
      Category teaCat = this.upsertCategory("Tea", "tea");
      Category pastryCat = this.upsertCategory("Pastries", "cake");

      this.upsertProduct("Espresso", "Rich and strong single shot of espresso.", new BigDecimal("2.50"), coffeeCat, "products/espresso.jpg", true);
      this.upsertProduct("Cappuccino", "Espresso with steamed milk and a thick layer of foam.", new BigDecimal("4.00"), coffeeCat, "products/cappuccino.jpg", true);
      this.upsertProduct("Iced Latte", "Espresso and cold milk poured over ice.", new BigDecimal("4.50"), coffeeCat, "products/iced_latte.jpg", true);
      this.upsertProduct("Matcha Green Tea", "Premium matcha green tea mixed with steamed milk.", new BigDecimal("5.00"), teaCat, "products/matcha_green_tea.jpg", true);
      this.upsertProduct("Croissant", "Flaky, buttery French pastry.", new BigDecimal("3.50"), pastryCat, "products/croissant.jpg", true);

      this.upsertIngredient("Coffee Beans", "kg", new BigDecimal("50.0"), new BigDecimal("10.0"), "Global Roasters");
      this.upsertIngredient("Milk", "L", new BigDecimal("100.0"), new BigDecimal("20.0"), "Local Dairy");
      this.upsertIngredient("Matcha Powder", "kg", new BigDecimal("5.0"), new BigDecimal("1.0"), "Kyoto Teas");

      System.out.println("DEFAULT DATABASE DATA SYNCHRONIZED.");
   }

   private Category upsertCategory(String name, String icon) {
      Category category = this.categoryRepository.findByName(name).orElseGet(Category::new);
      category.setName(name);
      category.setIcon(icon);
      return (Category)this.categoryRepository.save(category);
   }

   private void upsertProduct(String name, String description, BigDecimal price, Category category, String imageUrl, boolean active) {
      Product product = this.productRepository.findFirstByName(name).orElseGet(Product::new);
      product.setName(name);
      product.setDescription(description);
      product.setPrice(price);
      product.setCategory(category);
      product.setImageUrl(imageUrl);
      product.setActive(active);
      this.productRepository.save(product);
   }

   private void upsertIngredient(String name, String unit, BigDecimal stockQty, BigDecimal lowThreshold, String supplier) {
      Ingredient ingredient = this.ingredientRepository.findByName(name).orElseGet(Ingredient::new);
      ingredient.setName(name);
      ingredient.setUnit(unit);
      ingredient.setStockQty(stockQty);
      ingredient.setLowThreshold(lowThreshold);
      ingredient.setSupplier(supplier);
      this.ingredientRepository.save(ingredient);
   }
}
