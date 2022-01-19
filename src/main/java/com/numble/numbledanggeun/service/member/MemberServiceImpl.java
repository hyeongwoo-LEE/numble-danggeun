package com.numble.numbledanggeun.service.member;

import com.numble.numbledanggeun.domain.baordImg.BoardImg;
import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.member.Member;
import com.numble.numbledanggeun.domain.member.MemberRepository;
import com.numble.numbledanggeun.dto.member.MemberResDTO;
import com.numble.numbledanggeun.dto.member.MemberUpdateDTO;
import com.numble.numbledanggeun.dto.member.SignupDTO;
import com.numble.numbledanggeun.file.FileStore;
import com.numble.numbledanggeun.file.ResultFileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStore fileStore;

    /**
     * 닉네임 중복체크
     */
    @Transactional(readOnly = true)
    @Override
    public Boolean isDuplicateNickname(String nickname) {
        Optional<Member> result = memberRepository.findByNickname(nickname);

        if (result.isPresent()){
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * 회원가입
     */
    @Transactional
    @Override
    public Member join(SignupDTO signupDTO) {

        Optional<Member> result = memberRepository.findByNickname(signupDTO.getNickname());

        if (result.isPresent()){
            throw new IllegalStateException("이미 등록된 ID 입니다.");
        }

        //password 암호화
        String rawPassword = signupDTO.getPassword();
        String encPassword = passwordEncoder.encode(rawPassword);
        signupDTO.setPassword(encPassword);

        Member member = signupDTO.toEntity();

        memberRepository.save(member);

        return member;
    }

    /**
     * 회원 프로필 조회
     */
    @Transactional(readOnly = true)
    @Override
    public MemberResDTO getProfile(Long principalId) {
        Member member = memberRepository.findById(principalId).orElseThrow(() ->
                new IllegalStateException("존재하지 않은 회원입니다."));

        return new MemberResDTO(member);
    }

    /**
     * 회원 프로필 수정
     */
    @Transactional
    @Override
    public void modify(MemberUpdateDTO memberUpdateDTO, Long principalId) throws IOException {

        Member member = memberRepository.findById(principalId).orElseThrow(() ->
                new IllegalStateException("존재하지 않은 회원입니다."));

        member.changeNickname(memberUpdateDTO.getNickname());

        //TODO 기존 이미지 삭제 - 기본 이미지로 설정시 로직 필요
        if (memberUpdateDTO.getImageFile() != null){
            //서버에 컴퓨터에 저장된 사진 삭제
            fileRemove(member);

            //member - folderPath, filename 변경
            ResultFileStore resultFileStore = fileStore.storeFile(memberUpdateDTO.getImageFile());
            member.changeFolderPath(resultFileStore.getFolderPath());
            member.changeFilename(resultFileStore.getStoreFilename());
        }
    }

    private void fileRemove(Member member) {
        String folderPath = member.getFolderPath();
        String storeFileName = member.getFilename();

        File file = new File(fileStore.getFullPath(folderPath, storeFileName));
        file.delete();
    }
}
