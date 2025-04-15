package com.example.demo.service;

import com.example.demo.dto.UserRequestDTO;

import com.example.demo.dto.UserResponseDTO;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserResponseDTO registerUser(UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setFirstname(userRequestDTO.getName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

        User savedUser = userRepository.save(user);

        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(savedUser.getId());
        responseDTO.setName(savedUser.getUsername());
        responseDTO.setEmail(savedUser.getEmail());

        return responseDTO;
    }
}
