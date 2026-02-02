package com.example.test2.service;

import com.example.test2.dto.MemberResponse;
import com.example.test2.dto.MemberSaveRequest;
import com.example.test2.entity.Member;
import com.example.test2.repository.MemberRepository;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final S3Template s3Template;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    //팀원 저장
    @Transactional
    public Long save(MemberSaveRequest request) {
        Member member = Member.builder()
                .name(request.getName())
                .age(request.getAge())
                .mbti(request.getMbti())
                .build();
        return memberRepository.save(member).getId();
    }

    //팀원 조회
    public MemberResponse findById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("팀원을 찾을 수 없습니다."));

        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getAge(),
                member.getMbti()
        );
    }

    //S3 이미지 업로드 및 Key 업데이트
    @Transactional
    public String uploadProfileImage(Long id, MultipartFile file) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("팀원을 찾을 수 없습니다."));

        // 파일명 중복 방지를 위해 유니크한 Key 생성
        String key = "profiles/" + id + "_" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            // IAM Role 권한을 사용하여 S3 업로드
            s3Template.upload(bucketName, key, file.getInputStream(),
                    ObjectMetadata.builder().contentType(file.getContentType()).build());
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 중 오류 발생", e);
        }

        // DB에 S3 Key 저장 (엔티티의 updateProfileImage 메서드 활용)
        member.updateProfileImage(key);
        return "이미지 업로드 성공";
    }

    //7일 만료 Presigned URL 생성
    public String getPresignedUrl(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("팀원을 찾을 수 없습니다."));

        if (member.getProfileImageKey() == null) {
            throw new NoSuchElementException("등록된 프로필 이미지가 없습니다.");
        }

        //유효기간을 정확히 7일로 설정하여 URL 반환
        return s3Template.createSignedGetURL(bucketName, member.getProfileImageKey(), Duration.ofDays(7)).toString();
    }
}