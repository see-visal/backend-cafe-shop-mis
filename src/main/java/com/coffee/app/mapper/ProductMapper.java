package com.coffee.app.mapper;

import com.coffee.app.domain.Product;
import com.coffee.app.dto.request.ProductRequest;
import com.coffee.app.dto.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(
   componentModel = "spring"
)
public interface ProductMapper {
   @Mapping(
      target = "category",
      expression = "java(product.getCategory() != null ? product.getCategory().getName() : null)"
   )
   ProductResponse toResponse(Product product);

   @Mappings({@Mapping(
   target = "id",
   ignore = true
), @Mapping(
   target = "category",
   ignore = true
), @Mapping(
   target = "createdAt",
   ignore = true
), @Mapping(
   target = "updatedAt",
   ignore = true
), @Mapping(
   target = "showOnHomepage",
   expression = "java(Boolean.TRUE.equals(request.showOnHomepage()))"
), @Mapping(
   target = "todaySpecial",
   expression = "java(Boolean.TRUE.equals(request.todaySpecial()))"
), @Mapping(
   target = "homePriority",
   source = "homePriority"
), @Mapping(
   target = "active",
   constant = "true"
)})
   Product toEntity(ProductRequest request);

   @Mappings({@Mapping(
   target = "id",
   ignore = true
), @Mapping(
   target = "category",
   ignore = true
), @Mapping(
   target = "createdAt",
   ignore = true
), @Mapping(
   target = "updatedAt",
   ignore = true
), @Mapping(
   target = "showOnHomepage",
   expression = "java(Boolean.TRUE.equals(request.showOnHomepage()))"
), @Mapping(
   target = "todaySpecial",
   expression = "java(Boolean.TRUE.equals(request.todaySpecial()))"
), @Mapping(
   target = "homePriority",
   source = "homePriority"
), @Mapping(
   target = "active",
   ignore = true
)})
   void updateEntity(ProductRequest request, @MappingTarget Product product);
}
