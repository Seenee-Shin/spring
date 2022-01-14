package edu.kh.fin.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import edu.kh.fin.board.model.service.ReplyService;
import edu.kh.fin.board.model.vo.Reply;

@RestController //요청시 값만 반환 되는 컨트롤러
@RequestMapping("/reply/*")
public class ReplyController {
	
	@Autowired
	private ReplyService service;
	
	//@ResponseBody : 주소값이 아닌 값 자체를 return할때 사용 (비동기 통신(ajax)에서 주로 사용)
	//RestFull : 특정한 값만 얻어옴 

	//댓글 목록 조회
	@RequestMapping(value = "select", method = RequestMethod.GET)
	public String selectList(int boardNo) { //ajax 코드에 작성된 데이터 속성의 Key값
		
		List<Reply> rList = service.selectList(boardNo);
		
		//조회된 rList를 json 형태로 변경 
		
		return new Gson().toJson(rList);
		
	}
	
	//댓글 입력 
	@RequestMapping(value = "insert", method = RequestMethod.POST)
	public int insertReply(Reply reply) {
		
		return service.insertReply(reply);
	}
	
	//댓글 수정
	@RequestMapping( value = "update", method = RequestMethod.POST) 
	public int updateReply(Reply reply /*커멘드 객체*/) {
		//replyNo, replyContent
		return service.updateReply(reply);
	}
	
	
	//댓글 삭제
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public int deleteReply(int replyNo) {
		
		return service.deleteReply(replyNo);
	}
}
