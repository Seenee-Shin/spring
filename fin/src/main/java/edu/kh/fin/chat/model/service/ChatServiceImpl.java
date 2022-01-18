package edu.kh.fin.chat.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.fin.chat.model.dao.ChatDAO;
import edu.kh.fin.chat.model.vo.ChatMessage;
import edu.kh.fin.chat.model.vo.ChatRoom;
import edu.kh.fin.chat.model.vo.ChatRoomJoin;
import edu.kh.fin.common.Util;

@Service
public class ChatServiceImpl implements ChatService {

	@Autowired
	private ChatDAO dao;

	//채팅방 리스트
	@Override
	public List<ChatRoom> chatRoomList() {
		return dao.chatRoomList();
	}

	//채팅방 생성
	@Override
	public int openChatRoom(ChatRoom room) {
		int result = dao.openChatRoom(room);
		
		if(result>0) {
			
			return room.getChatRoomNo(); //dao 호출 후 selectKey 사용하여 chatRoomNo 생성
			
		}else {
			
		}
		
		return result;
	}

	
	//채팅방 입장 + 대화내역 
	@Override
	public List<ChatMessage> joinChatRoom(ChatRoomJoin join) {
		
		// 채팅방의 DB 존재여부 확인
		int result = dao.existChatRoom(join.getChatRoomNo());
		
		// 조회결과 
		if (result>0) {
			//결과가 존재하면 채팅방 입장 dao
			try {
				dao.joinChatRoom(join); 
				//재입장시 uniqe 제약조건 예외 발생 -> try-catch 로 예외 무시
				
			} catch (Exception e) {
				//catch문에 코드 미작성으로 예외 무시처리
			}
			
			return dao.selectChatMessage(join.getChatRoomNo());
			
		}else {
			return null;
		}
		
	}

	// 채팅 내용 삽입
	   @Transactional
	   @Override
	   public int insertMessage(ChatMessage cm) {
	      
	      int result = 0;
	      
	      if(cm.getMessage() == null) { // 나가기 버튼
	         
	         result = dao.insertMessage(cm);
	         // 나가기 처리  dao 호출
	         
	         if(result > 0) {
	            result = dao.exitChatRoom(cm);
	         }
	      } else { // 채팅내용 삽입   
	         //XSS 개행문자 처리
	         cm.setMessage(Util.XSS(cm.getMessage()));
	         cm.setMessage(Util.changeNewLine(cm.getMessage()));
	         
	         result = dao.insertMessage(cm);
	      }
	      
	      return result;
	   }
}
