package com.example.test2.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSaveRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Min(value = 1, message = "나이는 1세 이상이어야 합니다.")
    private int age;

    @Size(min = 4, max = 4, message = "MBTI는 4자리여야 합니다.")
    private String mbti;
}