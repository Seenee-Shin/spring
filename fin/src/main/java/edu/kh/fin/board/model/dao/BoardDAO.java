package edu.kh.fin.board.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.kh.fin.board.model.vo.Board;
import edu.kh.fin.board.model.vo.BoardImage;
import edu.kh.fin.board.model.vo.Category;
import edu.kh.fin.board.model.vo.Pagination;
import edu.kh.fin.board.model.vo.Search;

@Repository //DB와 연결하는 객체임을 명시 + Bean으로 등록
public class BoardDAO {
	
	//마이바티스에서 SQL을 수행하는 객체 
	@Autowired
	private SqlSessionTemplate sqlSession; //root-context.xml에 bean으로 등록되어있기 때문에 Autowired가능

	/** 전체 게시글 수 
	 * @return
	 */
	public int getListCount() {
		//전체 게시글 수 count 
		
		
		return sqlSession.selectOne("boardMapper.getListCount");
	}

	/** 지정된 범위의 게시글 목록 조회
	 * @param pagination
	 * @return
	 */
	public List<Board> selectBoardList(Pagination pagination) {
		
		// low bounds : Offset과 limit값을 넘겨 받아 페이징 처리를 쉽게 할 수 있게하는 객체 
		// offset : 몇 행을 건너 뛸 것인지 지정
		// limit : 건너 뛴 위치부터 몇 행을 조회 할지 지정 
		
		int offset = (pagination.getCurrentPage()-1) * pagination.getLimit();
		int limit = pagination.getLimit();
		RowBounds rowBounds = new RowBounds(offset, limit);

		return sqlSession.selectList("boardMapper.selectBoardList", null, rowBounds);
		//selectList 사용시 자동으로 List형태로 반환됨
		//rowBounds 사용시 파라미터가 없는 경우 null로 작성 
	}

	public Board selectBoard(int boardNo) {
		
		return sqlSession.selectOne("boardMapper.selectBoard", boardNo);
	}

	public int increaseReadCount(int boardNo) {
		return sqlSession.update("boardMapper.increaseReadCount",boardNo);
	}

	public List<Category> selectCategory() {
		// TODO Auto-generated method stub
		return sqlSession.selectList("boardMapper.selectCategory");
	}

	/** board insert DAO 
	 * @param board
	 * @return
	 */
	public int insertBoard(Board board) {
		int result = sqlSession.insert("boardMapper.insertBoard", board);
		//parameter로 쓰임 board에 생성된 boardNo를 넣어 반환
		
		if(result > 0) {
			return board.getBoardNo();
		} else {
			return 0;
		}
	}

	public int insertImgList(List<BoardImage> imgList) {
		return sqlSession.insert("boardMapper.insertImgList", imgList);
	}

	/** 게시글 수정 
	 * @param board
	 * @return result
	 */
	public int updateBoard(Board board) {
		// TODO Auto-generated method stub
		return sqlSession.update("boardMapper.updateBoard", board);
	}

	public int deleteImages(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return sqlSession.delete("boardMapper.deleteImages", map);
	}

	public int updateBoardImage(BoardImage img) {
		// TODO Auto-generated method stub
		return sqlSession.update("boardMapper.updateBoardImage", img);
	}

	public int insertBoardImage(BoardImage img) {
		// TODO Auto-generated method stub
		return sqlSession.insert("boardMapper.insertBoardImage", img);
	}

	public int deleteBaord(int boardNo) {
		// TODO Auto-generated method stub
		return sqlSession.insert("boardMapper.deleteBoard",boardNo);
	}

	/**  전체 검색글 개수
	 * @param search
	 * @return
	 */
	public int searchListCount(Search search) {
		// TODO Auto-generated method stub
		return sqlSession.selectOne("boardMapper.searchListCount", search);
	}

	public List<Board> selectBoardList(Pagination pagination, Search search) {
		
		int offset = (pagination.getCurrentPage() -1) * pagination.getLimit();
		
		RowBounds rowb = new RowBounds(offset, pagination.getLimit());
		
		return sqlSession.selectList("boardMapper.selectSearchBoardList", search, rowb);
	}

	/** 이미지 파일명 목록 조회 
	 * @return
	 */
	public List<String> selectImgList() {
		return sqlSession.selectList("boardMapper.selectImgList");
	}
	
}
