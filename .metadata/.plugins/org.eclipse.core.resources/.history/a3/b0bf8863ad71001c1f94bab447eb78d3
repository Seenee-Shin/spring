package edu.kh.fin.board.model.dao;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.kh.fin.board.model.vo.Board;
import edu.kh.fin.board.model.vo.Pagination;

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
	
}
