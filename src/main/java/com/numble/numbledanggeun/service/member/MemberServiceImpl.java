package com.numble.numbledanggeun.service.member;

import com.numble.numbledanggeun.domain.baordImg.BoardImg;
import com.numble.numbledanggeun.domain.baordImg.BoardImgRepository;
import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.board.BoardRepository;
import com.numble.numbledanggeun.domain.comment.Comment;
import com.numble.numbledanggeun.domain.comment.CommentRepository;
import com.numble.numbledanggeun.domain.heart.HeartRepository;
import com.numble.numbledanggeun.domain.member.Member;
import com.numble.numbledanggeun.domain.member.MemberRepository;
import com.numble.numbledanggeun.dto.member.MemberResDTO;
import com.numble.numbledanggeun.dto.member.MemberUpdateDTO;
import com.numble.numbledanggeun.dto.member.SignupDTO;
import com.numble.numbledanggeun.file.FileStore;
import com.numble.numbledanggeun.file.ResultFileStore;
import com.numble.numbledanggeun.handler.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final BoardImgRepository boardImgRepository;
    private final HeartRepository heartRepository;

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
            throw new CustomException("이미 등록된 닉네임 입니다.");
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
                new CustomException("존재하지 않은 회원입니다."));

        return new MemberResDTO(member);
    }

    /**
     * 회원 프로필 수정
     */
    @Transactional
    @Override
    public void modify(MemberUpdateDTO memberUpdateDTO, Long principalId) throws IOException {

        Member member = memberRepository.findById(principalId).orElseThrow(() ->
                new CustomException("존재하지 않은 회원입니다."));

        member.changeNickname(memberUpdateDTO.getNickname());

        if (!memberUpdateDTO.getImageFile().getOriginalFilename().isEmpty()){
            //서버에 컴퓨터에 저장된 사진 삭제
            fileRemove(member);

            //member - folderPath, filename 변경
            ResultFileStore resultFileStore = fileStore.storeFile(memberUpdateDTO.getImageFile());
            member.changeFolderPath(resultFileStore.getFolderPath());
            member.changeFilename(resultFileStore.getStoreFilename());
        }
    }

    /**
     * 회원 프로필 사진 삭제
     */
    @Transactional
    @Override
    public void removeImage(Long principalId) {

        Member member = memberRepository.findById(principalId).orElseThrow(() ->
                new CustomException("존재하지 않은 회원입니다."));

        //기존 프로필 사진 삭제
        //서버 컴퓨터 이미지 파일 삭제
        fileRemove(member);

        member.changeFilename("");
        member.changeFolderPath("");
    }

    /**
     * 회원 탈퇴
     *
     * 서버 컴퓨터에 저장된 boardImg 삭제 -> boardImg 삭제 -> 작성한 board 삭제(cascade - comment,heart 삭제)
     * -> 작성한 댓글 isExist = false 처리 -> 서버 컴퓨터에 저장된 프로필 사진 삭제 ->  member 삭제 (cascade - heart 삭제)
     */
    @Transactional
    @Override
    public void remove(Long principalId) {

        Member member = memberRepository.findById(principalId).orElseThrow(() ->
                new CustomException("존재하지 않은 회원입니다."));

        List<Board> boardList = boardRepository.findByMember(member);
        //boardImg 삭제
        boardList.forEach(b -> boardImgRemove(b));

        //작성한 board의 댓글 모두 삭제
        boardList.forEach(b -> commentRepository.deleteByBoard(b));

        //작성한 board의 관심 모두 삭제
        boardList.forEach(b -> heartRepository.deleteByBoard(b));

        //작성한 board 삭제(cascade - comment,heart 삭제)
        boardRepository.deleteByMember(member);

        //작성한 댓글 isExist = false 처리
        List<Comment> commentList = commentRepository.getCommentByMember(member);
        commentList.forEach(c -> c.removeComment());

        //서버 컴퓨터에 저장된 프로필 사진 삭제
        fileRemove(member);

        // 삭제 (cascade - heart 삭제)
        memberRepository.deleteById(principalId);
    }

    private void boardImgRemove(Board board) {
        List<BoardImg> boardImgList = boardImgRepository.findBoardImgByBoard(board);

        //서버 컴퓨터에 저장된 boardImg 파일 삭제
        fileRemove(boardImgList);

        //boardImg 삭제
        boardImgRepository.deleteByBoard(board);
    }

    private void fileRemove(Member member) {

        if(!member.getFilename().isBlank()|| !member.getFolderPath().isBlank()){
            String folderPath = member.getFolderPath();
            String storeFileName = member.getFilename();

            File file = new File(fileStore.getFullPath(folderPath, storeFileName));
            file.delete();
        }
    }

    private void fileRemove(List<BoardImg> boardImgList) {

        if(boardImgList != null && boardImgList.size() > 0 ){

            for(BoardImg boardImg: boardImgList){
                String folderPath = boardImg.getFolderPath();
                String storeFileName = boardImg.getFilename();

                File file = new File(fileStore.getFullPath(folderPath, storeFileName));
                file.delete();
            }
        }

    }
}
