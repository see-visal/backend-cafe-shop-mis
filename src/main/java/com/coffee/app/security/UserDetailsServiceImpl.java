package com.coffee.app.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.coffee.app.domain.Permission;
import com.coffee.app.domain.Role;
import com.coffee.app.domain.User;
import com.coffee.app.repository.UserRepository;

import lombok.Generated;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
   private final UserRepository userRepository;

   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      User user = (User)this.userRepository.findByUsername(username).or(() -> this.userRepository.findByEmail(username)).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
      List<GrantedAuthority> authorities = new ArrayList<>();
      user.getRoles().forEach((role) -> {
         authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
         role.getPermissions().forEach((permission) -> authorities.add(new SimpleGrantedAuthority(permission.getName())));
      });
      log.info("USER AUTHORITIES: {}", authorities);
      Set<String> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
      Set<String> permissionNames = user.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet());
      return new CustomUserDetails(user.getUuid(), user.getUsername(), user.getEmail(), user.getPassword(), user.getFamilyName(), user.getGivenName(), user.getPhoneNumber(), user.getGender(), user.getDob(), user.getProfileImage(), user.getCoverImage(), user.getAccountNonExpired(), user.getAccountNonLocked(), user.getCredentialsNonExpired(), user.getIsEnabled(), roleNames, permissionNames);
   }

   @Generated
   public UserDetailsServiceImpl(final UserRepository userRepository) {
      this.userRepository = userRepository;
   }
}
