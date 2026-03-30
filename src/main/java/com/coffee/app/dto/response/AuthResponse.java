package com.coffee.app.dto.response;

import java.util.Set;

public record AuthResponse(String accessToken, String refreshToken, String tokenType, long expiresIn, UserInfo user) {
   public static AuthResponse of(String accessToken, String refreshToken, long expiresIn, UserInfo user) {
      return new AuthResponse(accessToken, refreshToken, "Bearer", expiresIn, user);
   }

   public static record UserInfo(String uuid, String username, String email, String givenName,
                                 String familyName, Set<String> roles) {
   }
}
