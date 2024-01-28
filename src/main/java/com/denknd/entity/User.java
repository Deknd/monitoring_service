package com.denknd.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<Address> addresses;
    private List<Role> roles;
}
