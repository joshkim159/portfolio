package com.example.demo3.service;

import com.example.demo3.dto.*;
import com.example.demo3.model.UserEntity;
import com.example.demo3.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    // lsw : ChatService 필드를 불러옴
    @Autowired
    private ChatService chatService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public ResponseDTO<?> signUp(SignUpDTO dto) {
        String name = dto.getName();
        String email = dto.getEmail();
        String password = dto.getPassword();
        String confirmPassword = dto.getConfirmPassword();

        // email 중복 확인
        if (userRepository.existsByEmail(email)) {
            return ResponseDTO.setFailed("중복된 Email 입니다.");
        }

        // password 확인
        if (!password.equals(confirmPassword)) {
            return ResponseDTO.setFailed("비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        // UserEntity 생성
        UserEntity userEntity = new UserEntity(dto);
        userEntity.setPassword(encodedPassword);

        // UserRepository를 이용하여 DB에 Entity 저장 (데이터 적재)
        userRepository.save(userEntity);

        return ResponseDTO.setSuccess("회원 생성에 성공했습니다.");
    }

    public ResponseDTO<LoginResponseDTO> login(LoginDTO dto) {
        String email = dto.getEmail();
        String password = dto.getPassword();

        UserEntity userEntity = userRepository.findByEmail(email).orElse(null);
        if (userEntity == null || !bCryptPasswordEncoder.matches(password, userEntity.getPassword())) {
            return ResponseDTO.setFailed("입력하신 로그인 정보가 존재하지 않습니다.");
        }

        userEntity.setPassword("");

        String token = "";
        int exprTime = 3600000;  // 1시간

        LoginResponseDTO loginResponseDto = new LoginResponseDTO(token, exprTime, userEntity);

        return ResponseDTO.setSuccessData("로그인에 성공하였습니다.", loginResponseDto);
    }

    public ResponseDTO<?> updateProfile(UpdateProfileDTO dto) {
        try {
            UserEntity user = userRepository.findById(dto.getId()).orElse(null);
            if (user == null) {
                return ResponseDTO.setFailed("사용자를 찾을 수 없습니다.");
            }

            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPassword(dto.getPassword());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setAddress(dto.getAddress());
            user.setDetailAddress(dto.getDetailAddress());

            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                user.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
            }

            userRepository.save(user);
            return ResponseDTO.setSuccess("회원 정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseDTO.setFailed("회원 정보 수정 중 오류가 발생했습니다.");
        }
    }

    public ResponseDTO<?> deleteAccount(DeleteUserByEmailDTO dto) {
        try {
            UserEntity user = userRepository.findByUserId(dto.getUserId()).orElse(null);
            if (user == null || !bCryptPasswordEncoder.matches(dto.getPassword(), user.getPassword())) {
                return ResponseDTO.setFailed("비밀번호가 일치하지 않거나 사용자가 존재하지 않습니다.");
            }

            // lsw : 사용자 탈퇴 시 채팅 데이터 삭제
            chatService.deleteChatDataByUserId(user.getUserId());

            userRepository.delete(user);
            return ResponseDTO.setSuccess("회원 탈퇴가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseDTO.setFailed("회원 탈퇴 중 오류가 발생했습니다.");
        }
    }

    public boolean deleteUserByEmail(String email) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {

            // lsw : 사용자 탈퇴 시 채팅 데이터 삭제
            chatService.deleteChatDataByUserId(optionalUser.get().getUserId());

            userRepository.delete(optionalUser.get());
            return true;
        }

        return false;
    }

}
