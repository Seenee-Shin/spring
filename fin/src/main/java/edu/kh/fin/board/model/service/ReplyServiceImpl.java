package edu.kh.fin.board.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.kh.fin.board.model.dao.ReplyDAO;
import edu.kh.fin.board.model.vo.Reply;
import edu.kh.fin.common.Util;

@Service //비지니스 로직 서비스 + Bean 등록
public class ReplyServiceImpl implements ReplyService {
	
	@Autowired //Bean으로 등록된 개체 중 같은 타입 또는 상속 관게 객체를 자동으로 찾아 의존성 주입 인식
	private ReplyDAO dao;

	
	
	@Override //상속받은 메소드를 자식클래스에서 제정의
	public List<Reply> selectList(int boardNo) {
		// TODO Auto-generated method stub
		return dao.selectList(boardNo);
	}



	@Override
	public int insertReply(Reply reply) {
		//xss, 개행문자 처리
		reply.setReplyContent(Util.XSS(reply.getReplyContent()));
		reply.setReplyContent(Util.changeNewLine(reply.getReplyContent()));
		
		
		return dao.insertReply(reply);
	}



	@Override
	public int updateReply(Reply reply) {
		reply.setReplyContent(Util.XSS(reply.getReplyContent()));
		reply.setReplyContent(Util.changeNewLine(reply.getReplyContent()));
		
		return dao.updateReply(reply);
	}



	@Override
	public int deleteReply(int replyNo) {
		// TODO Auto-generated method stub
		return dao.deleteReply(replyNo);
	}
	
	

}
