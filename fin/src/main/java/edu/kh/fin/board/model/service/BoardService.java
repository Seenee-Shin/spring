package edu.kh.fin.board.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.fin.board.model.vo.Board;
import edu.kh.fin.board.model.vo.Category;
import edu.kh.fin.board.model.vo.Pagination;
import edu.kh.fin.board.model.vo.Search;

//인터페이스를 사용하는 이유 
//1.규칙성
//2.유지보수
//3.결합도 
//4.AOP사용 -spring에서 인터페이스를 사용하는 이유

@Service //Bean등록, 서비스 명시
public interface BoardService  {

	Pagination getPagination(int cp);

	List<Board> selectBoardList(Pagination pagination);

	Board selectBoard(int boardNo, int memberNo);

	List<Category> selectCategory();

	/** 게시글 삽입
	 * @param board
	 * @param images
	 * @param webPath
	 * @param serverPath
	 * @return
	 */
	int insertBoard(Board board, List<MultipartFile> images, String webPath, String serverPath);

	int updateBoard(Board board, List<MultipartFile> images, String webPath, String serverPath, String deleteImages);

	Board selectBoard(int boardNo);

	/** 게시글 삭제
	 * @param boardNo
	 * @return result int
	 */
	int deleteBoard(int boardNo);

	/*검색 조건에 맞는 전체 게시글 수 count + 페이징 처리 계산
	 * @param cp
	 * @param search
	 * @return
	 */
	Pagination getPagination(int cp, Search search);

	List<Board> selectBoardList(Pagination pagination, Search search);

	/** 이미지 파일명 목록 조회 
	 * @return
	 */
	List<String> selectImgList();
	
}
