package com.fintrack.user.service;

import com.fintrack.user.dto.UserDTO;
import com.fintrack.user.exception.UserException;
import com.fintrack.user.model.UserProfile;
import com.fintrack.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserProfileRepository userProfileRepository;

    @Transactional
    public UserDTO.UserResponse createProfile(Long userId, String email,
                                              String firstName, String lastName) {
        if (userProfileRepository.existsByEmail(email)) {
            throw new UserException.UserAlreadyExistsException(email);
        }

        UserProfile profile = UserProfile.builder()
                .id(userId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();

        userProfileRepository.save(profile);
        log.info("User profile created for userId={}", userId);
        return toResponse(profile);
    }

    public UserDTO.UserResponse getProfile(Long userId) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException(userId));
        return toResponse(profile);
    }

    @Transactional
    public UserDTO.UserResponse updateProfile(Long userId, UserDTO.UpdateRequest request) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException(userId));

        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setPhone(request.phone());
        profile.setAddress(request.address());
        profile.setUpdatedAt(LocalDateTime.now());

        userProfileRepository.save(profile);
        log.info("User profile updated for userId={}", userId);
        return toResponse(profile);
    }

    @Transactional
    public void deactivateProfile(Long userId) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException(userId));
        profile.setActive(false);
        profile.setUpdatedAt(LocalDateTime.now());
        userProfileRepository.save(profile);
        log.info("User profile deactivated for userId={}", userId);
    }

    private UserDTO.UserResponse toResponse(UserProfile profile) {
        return new UserDTO.UserResponse(
                profile.getId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getEmail(),
                profile.getPhone(),
                profile.getAddress(),
                profile.isActive()
        );
    }
}