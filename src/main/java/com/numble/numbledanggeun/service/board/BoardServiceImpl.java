package com.numble.numbledanggeun.service.board;

import com.numble.numbledanggeun.domain.baordImg.BoardImg;
import com.numble.numbledanggeun.domain.baordImg.BoardImgRepository;
import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.board.BoardRepository;
import com.numble.numbledanggeun.domain.board.PostState;
import com.numble.numbledanggeun.domain.heart.Heart;
import com.numble.numbledanggeun.domain.member.Member;
import com.numble.numbledanggeun.dto.board.*;
import com.numble.numbledanggeun.dto.boardImg.BoardImgDTO;
import com.numble.numbledanggeun.dto.page.SearchDTO;
import com.numble.numbledanggeun.file.FileStore;
import com.numble.numbledanggeun.file.ResultFileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class BoardServiceImpl implements BoardService{

    private final BoardRepository boardRepository;
    private final BoardImgRepository boardImgRepository;
    private final FileStore fileStore;

    /**
     * 중고거래 글쓰기
     */
    @Transactional
    @Override
    public Board register(BoardDTO boardDTO, Long principalId) throws IOException {

        //board 엔티티 저장
        Board board = boardDTO.toEntity(principalId);
        boardRepository.save(board);

        //boardImg 엔티티 저장
        saveImg(board, boardDTO.getImageFiles());

        return board;
    }


    /**
     * 글 수정
     */
    @Transactional
    @Override
    public void modify(BoardUpdateDTO boardUpdateDTO) throws IOException {

        Board board = boardRepository.findById(boardUpdateDTO.getBoardId()).orElseThrow(() ->
                new IllegalStateException("존재하지 않는 글입니다."));

        //변경감지
        board.changeCategory(boardUpdateDTO.getCategory());
        board.changeTitle(boardUpdateDTO.getTitle());
        board.changeContent(boardUpdateDTO.getContent());
        board.changePrice(boardUpdateDTO.getPrice());

        //TODO 수정 필요 - 사진 변경 없을 시 imageFiles - null 값을 넘어옴
        /* 여기서부터 img 변경 */
        //기존 해당 사진 모두 삭제
        List<BoardImg> boardImgList = boardImgRepository.findBoardImgByBoard(board);

        //서버에 컴퓨터에 저장된 사진 삭제
        fileRemove(boardImgList);

        //boardImg 삭제
        boardImgRepository.deleteByBoard(board);

        //새로운 사진 저장
        saveImg(board, boardUpdateDTO.getImageFiles());
    }

    /**
     * 글 상태 수정
     */
    @Transactional
    @Override
    public void modifyPostState(Long boardId, PostState postState) {

        Board board = boardRepository.findById(boardId).orElseThrow(() ->
                new IllegalStateException("존재하지 않는 글입니다."));

        board.changePostState(postState);
    }

    /**
     * 글 삭제
     */
    @Transactional
    @Override
    public void remove(Long boardId) {

        Board board = boardRepository.findById(boardId).orElseThrow(() ->
                new IllegalStateException("존재하지 않는 글입니다."));

        //서버 컴퓨터에서 글 사진파일 삭제 -> boardImg 삭제 -> board 삭제 (cascade - comment, heart)
        List<BoardImg> boardImgList = boardImgRepository.findBoardImgByBoard(board);
        fileRemove(boardImgList);

        boardImgRepository.deleteByBoard(board);

        boardRepository.deleteById(boardId);
    }

    /**
     * 판매글 리스트 조회
     */
    @Transactional(readOnly = true)
    @Override
    public List<BoardResDTO> getAllBoardList(SearchDTO searchDTO, Long principalId) {

        List<Object[]> result = boardRepository.getAllBoardList(searchDTO);

        List<BoardResDTO> boardResDTOList = result.stream().map(arr -> new BoardResDTO(
                (Board) arr[0], //중고글 엔티티
                (int) arr[1], //댓글 수
                (int) arr[2], //관심 수
                principalId)) //사용자 유저 id
                .collect(Collectors.toList());

        return boardResDTOList;
    }

    /**
     * 회원별 판매상품 리스트 조회
     */
    @Transactional(readOnly = true)
    @Override
    public List<BoardResDTO> getBoardListByMemberId(SearchDTO searchDTO, Long principalId) {

        List<Object[]> result = boardRepository.getBoardListByMemberId(searchDTO);

        List<BoardResDTO> boardResDTOList = result.stream().map(arr -> new BoardResDTO(
                (Board) arr[0], //중고글 엔티티
                (int) arr[1], //댓글 수
                (int) arr[2], //관심 수
                principalId)) //사용자 유저 id
                .collect(Collectors.toList());

        return boardResDTOList;
    }

    /**
     * 회원의 관심 목록 리스트
     */
    @Transactional(readOnly = true)
    @Override
    public List<BoardResDTO> getBoardListOfHeart(Long principalId) {
        List<Object[]> result = boardRepository.getBoardListOfHeart(principalId);

        List<BoardResDTO> boardResDTOList = result.stream().map(arr -> new BoardResDTO(
                (Board) arr[0], //중고글 엔티티
                (int) arr[1], //댓글 수
                (int) arr[2], //관심 수
                principalId)) //사용자 유저 id
                .collect(Collectors.toList());

        return boardResDTOList;
    }

    /**
     * 판매글 작성자의 다른 판매글 미리보기 리스트 - 현재 상세보기 글 제외
     */
    @Transactional(readOnly = true)
    @Override
    public List<BoardPreviewDTO> getPreviewBoardListInDetailView(Long boardId) {

        List<Board> result = boardRepository.getPreviewBoardListInDetailView(boardId);

        return result.stream().map(entity -> new BoardPreviewDTO(entity)).collect(Collectors.toList());
    }


    /**
     * 판매글 상세페이지 조회
     */
    @Transactional(readOnly = true)
    @Override
    public BoardDetailDTO getBoard(Long boardId, Long principalId) {
        Board board = boardRepository.getBoardWithAll(boardId);

        return new BoardDetailDTO(board, principalId);
    }


    private void saveImg(Board board, List<MultipartFile> imageFiles) throws IOException {
        if(imageFiles != null && imageFiles.size() > 0){
            List<ResultFileStore> resultFileStores = fileStore.storeFiles(imageFiles);

            //사진 저장
            for(ResultFileStore resultFileStore : resultFileStores){
                BoardImg boardImg = toBoardImg(board, resultFileStore);
                boardImgRepository.save(boardImg);
            }
        }
    }

    private BoardImg toBoardImg(Board board, ResultFileStore resultFileStore) {
        BoardImg boardImg = BoardImg.builder()
                .folderPath(resultFileStore.getFolderPath())
                .filename(resultFileStore.getStoreFilename())
                .build();
        boardImg.setBoard(board);

        return boardImg;
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
