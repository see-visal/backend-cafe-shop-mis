package com.coffee.app.service;

import java.util.List;

import com.coffee.app.dto.request.ShopTableRequest;
import com.coffee.app.dto.response.ShopTableResponse;

public interface TableService {
   List<ShopTableResponse> getAll();

   List<ShopTableResponse> getActive();

   ShopTableResponse create(ShopTableRequest request);

   ShopTableResponse update(Long id, ShopTableRequest request);

   ShopTableResponse setActive(Long id, boolean active);

   void delete(Long id);

   boolean isTableActive(Integer tableNumber);
}