package com.example.test2.service;

import com.example.test2.dto.MemberResponse;
import com.example.test2.dto.MemberSaveRequest;
import com.example.test2.entity.Member;
import com.example.test2.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Long save(MemberSaveRequest request) {
        Member member = Member.builder()
                .name(request.getName())
                .age(request.getAge())
                .mbti(request.getMbti())
                .build();
        return memberRepository.save(member).getId();
    }

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
}