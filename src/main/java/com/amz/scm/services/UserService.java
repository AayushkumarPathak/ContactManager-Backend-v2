package com.amz.scm.services;


import com.amz.scm.payloads.UserDto;
import java.util.List;


public interface UserService {
    UserDto saveUser(UserDto user);

    UserDto getUserById(long id);

    UserDto updateUser(UserDto user,Long userId);

    void deleteUser(long id);

    boolean isUserExist(long userId);

    boolean isUserExistByEmail(String email);

    List<UserDto> getAllUsers();

    UserDto getUserByEmail(String email);

}
