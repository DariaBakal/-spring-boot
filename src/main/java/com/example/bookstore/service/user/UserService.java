package com.example.bookstore.service.user;

import com.example.bookstore.dto.user.UserRegisterResponseDto;
import com.example.bookstore.dto.user.UserRegistrationRequestDto;
import com.example.bookstore.exception.RegistrationException;

public interface UserService {
    UserRegisterResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;
}
