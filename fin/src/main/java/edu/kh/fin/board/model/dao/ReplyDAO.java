package edu.kh.fin.board.model.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.kh.fin.board.model.vo.Reply;

@Repository
public class ReplyDAO {
	
	
	@Autowired //의존성 주입
	private SqlSessionTemplate sqlSession;

	/** 댓글 목록 조회 
	 * @param boardNo
	 * @return List rList
	 */
	public List<Reply> selectList(int boardNo) {
		
		return sqlSession.selectList("replyMapper.selectList", boardNo);
	}

	public int insertReply(Reply reply) {
		// TODO Auto-generated method stub
		return sqlSession.insert("replyMapper.insertReply", reply);
	}

	public int updateReply(Reply reply) {
		// TODO Auto-generated method stub
		return sqlSession.update("replyMapper.updateReply", reply);
	}

	public int deleteReply(int replyNo) {
		// TODO Auto-generated method stub
		return sqlSession.update("replyMapper.deleteReply", replyNo);
	}
}
