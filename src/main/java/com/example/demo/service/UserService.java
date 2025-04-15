package com.example.demo.service;

import com.example.demo.dto.UserRequestDTO;


import com.example.demo.dto.UserResponseDTO;
import com.example.demo.user.User;

public interface UserService {
	UserResponseDTO registerUser(UserRequestDTO userRequestDTO);

}
