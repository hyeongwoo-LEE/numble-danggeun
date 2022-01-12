package com.numble.numbledanggeun.dto.member;

import com.numble.numbledanggeun.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupDTO {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String username;

    @NotBlank
    private String phone;

    @NotBlank
    private String nickname;

    public Member toEntity(){

        return Member.builder()
                .email(email)
                .password(password)
                .username(username)
                .nickname(nickname)
                .phone(phone)
                .build();
    }

}
