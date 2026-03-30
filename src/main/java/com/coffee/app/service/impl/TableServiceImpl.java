package com.coffee.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffee.app.domain.ShopTable;
import com.coffee.app.dto.request.ShopTableRequest;
import com.coffee.app.dto.response.ShopTableResponse;
import com.coffee.app.exception.ResourceNotFoundException;
import com.coffee.app.repository.ShopTableRepository;
import com.coffee.app.service.TableService;

import lombok.Generated;

@Service
public class TableServiceImpl implements TableService {
   private final ShopTableRepository shopTableRepository;

   @Transactional(
      readOnly = true
   )
   public List<ShopTableResponse> getAll() {
      return this.shopTableRepository.findAllByOrderByTableNumberAsc().stream().map(this::toResponse).toList();
   }

   @Transactional(
      readOnly = true
   )
   public List<ShopTableResponse> getActive() {
      return this.shopTableRepository.findByActiveTrueOrderByTableNumberAsc().stream().map(this::toResponse).toList();
   }

   @Transactional
   public ShopTableResponse create(ShopTableRequest request) {
      this.ensureUniqueTableNumber(request.tableNumber(), (Long)null);
      ShopTable table = ShopTable.builder().tableNumber(request.tableNumber()).label(this.normalizeLabel(request.label(), request.tableNumber())).active(request.active() != null ? request.active() : true).build();
      return this.toResponse(this.shopTableRepository.save(table));
   }

   @Transactional
   public ShopTableResponse update(Long id, ShopTableRequest request) {
      ShopTable table = this.findOrThrow(id);
      this.ensureUniqueTableNumber(request.tableNumber(), id);
      table.setTableNumber(request.tableNumber());
      table.setLabel(this.normalizeLabel(request.label(), request.tableNumber()));
      if (request.active() != null) {
         table.setActive(request.active());
      }

      return this.toResponse(this.shopTableRepository.save(table));
   }

   @Transactional
   public ShopTableResponse setActive(Long id, boolean active) {
      ShopTable table = this.findOrThrow(id);
      table.setActive(active);
      return this.toResponse(this.shopTableRepository.save(table));
   }

   @Transactional
   public void delete(Long id) {
      this.shopTableRepository.delete(this.findOrThrow(id));
   }

   @Transactional(
      readOnly = true
   )
   public boolean isTableActive(Integer tableNumber) {
      return this.shopTableRepository.findByTableNumber(tableNumber).map((table) -> Boolean.TRUE.equals(table.getActive())).orElse(false);
   }

   private void ensureUniqueTableNumber(Integer tableNumber, Long currentId) {
      boolean exists = currentId == null ? this.shopTableRepository.existsByTableNumber(tableNumber) : this.shopTableRepository.existsByTableNumberAndIdNot(tableNumber, currentId);
      if (exists) {
         throw new IllegalArgumentException("Table number already exists: " + String.valueOf(tableNumber));
      }
   }

   private ShopTable findOrThrow(Long id) {
      return this.shopTableRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Table not found: " + String.valueOf(id)));
   }

   private String normalizeLabel(String label, Integer tableNumber) {
      if (label == null || label.isBlank()) {
         return "Table " + String.valueOf(tableNumber);
      } else {
         return label.trim();
      }
   }

   private ShopTableResponse toResponse(ShopTable table) {
      return new ShopTableResponse(table.getId(), table.getTableNumber(), table.getLabel(), table.getActive(), table.getCreatedAt());
   }

   @Generated
   public TableServiceImpl(final ShopTableRepository shopTableRepository) {
      this.shopTableRepository = shopTableRepository;
   }
}