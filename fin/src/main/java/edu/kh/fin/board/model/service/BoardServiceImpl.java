package edu.kh.fin.board.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.kh.fin.board.model.dao.BoardDAO;
import edu.kh.fin.board.model.vo.Board;
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
	public Board selectBoard(int boardNo) {
		return dao.selectBoard(boardNo);
	}
}
