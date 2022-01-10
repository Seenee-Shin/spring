package edu.kh.fin.board.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.kh.fin.board.model.dao.BoardDAO;
import edu.kh.fin.board.model.vo.Board;
import edu.kh.fin.board.model.vo.Category;
import edu.kh.fin.board.model.vo.Pagination;

@Service
public class BoardServiceImpl implements BoardService{

	@Autowired
	private BoardDAO dao;

	@Override
	public Pagination getPagination(int cp) {
		
		//전체 게시글 수 count
		int listCount = dao.getListCount();
		
		 return new Pagination(listCount, cp);
	}

	@Override
	public List<Board> selectBoardList(Pagination pagination) {
		return dao.selectBoardList(pagination);
	}

	@Override
	public Board selectBoard(int boardNo, int memberNo) {
		Board board = dao.selectBoard(boardNo);
		
		if(board != null && board.getMemberNo() != memberNo) {
			//조회수 증가 
			int result = dao.increaseReadCount(boardNo);
			
			if(result > 0) {
				//DB와 미리 조회된 board의 readCount 동기화
				board.setReadCount(board.getReadCount()+1);
			}
		}
		
		return dao.selectBoard(boardNo);
	}

	@Override
	public List<Category> selectCategory() {
		
		return dao.selectCategory();
	}
}
