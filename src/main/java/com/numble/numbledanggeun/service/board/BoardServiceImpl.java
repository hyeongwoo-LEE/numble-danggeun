package com.numble.numbledanggeun.service.board;

import com.numble.numbledanggeun.domain.baordImg.BoardImg;
import com.numble.numbledanggeun.domain.baordImg.BoardImgRepository;
import com.numble.numbledanggeun.domain.board.Board;
import com.numble.numbledanggeun.domain.board.BoardRepository;
import com.numble.numbledanggeun.domain.board.PostState;
import com.numble.numbledanggeun.dto.board.BoardDTO;
import com.numble.numbledanggeun.dto.board.BoardUpdateDTO;
import com.numble.numbledanggeun.file.FileStore;
import com.numble.numbledanggeun.file.ResultFileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

    private BoardImg toBoardImg(Board board, ResultFileStore resultFileStore) {

        return BoardImg.builder()
                .board(board)
                .folderPath(resultFileStore.getFolderPath())
                .filename(resultFileStore.getStoreFilename())
                .build();
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

    private void fileRemove(List<BoardImg> boardImgList) {

        if(boardImgList != null && boardImgList.size() > 0 ){

            for(BoardImg meetingImg: boardImgList){
                String folderPath = meetingImg.getFolderPath();
                String storeFileName = meetingImg.getFilename();

                File file = new File(fileStore.getFullPath(folderPath, storeFileName));
                file.delete();
            }
        }

    }
}
