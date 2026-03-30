package com.coffee.app.service;

import com.coffee.app.dto.request.AdminCreateUserRequest;
import com.coffee.app.dto.request.AdminUserRequest;
import com.coffee.app.dto.response.AdminUserResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserManagementService {
   Page<AdminUserResponse> getAllUsers(Pageable pageable, List<String> roles);

   AdminUserResponse createUser(AdminCreateUserRequest request);

   AdminUserResponse getUserByUuid(String uuid);

   AdminUserResponse updateUser(String uuid, AdminUserRequest request);

   AdminUserResponse setUserEnabled(String uuid, boolean enabled);

   AdminUserResponse setUserLocked(String uuid, boolean locked);

   void deleteUser(String uuid);
}
