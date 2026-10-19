package com.newasteralnx.service;

import com.newasteralnx.constant.Role;
import com.newasteralnx.dto.request.CreateUserRequest;
import com.newasteralnx.dto.request.UpdateUserRequest;
import com.newasteralnx.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    Page<UserResponse> getUsers(Role role, Pageable pageable);

    UserResponse getUser(Long id);

    void updateActive(Long id, boolean active);

    void deleteUser(Long id);
}
