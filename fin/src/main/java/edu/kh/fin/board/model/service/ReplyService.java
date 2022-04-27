package edu.kh.fin.board.model.service;

import java.util.List;

import edu.kh.fin.board.model.vo.Reply;


public interface ReplyService {

	/** 댓글 목록 조회 
	 * @param boardNo
	 * @return rList
	 */
	//List<Reply> selectList(int boardNo);

	/** 댓글 삽입
	 * @param reply
	 * @return
	 */
	int insertReply(Reply reply);

	int updateReply(Reply reply);

	int deleteReply(int replyNo);

}
