package com.example.ziwa.dto;

import com.example.ziwa.model.AppUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {

    private Long id;
    private String username;
    private AppUser.UserRole role;
    private String fullName;

    public static UserInfo fromAppUser(AppUser user) {
        return UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .fullName(user.getFullName())
                .build();
    }
}
