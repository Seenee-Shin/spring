package edu.kh.fin.chat.model.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.kh.fin.chat.model.vo.ChatMessage;
import edu.kh.fin.chat.model.vo.ChatRoom;
import edu.kh.fin.chat.model.vo.ChatRoomJoin;

@Repository
public class ChatDAO {
	
	
	@Autowired
	private SqlSessionTemplate sqlSession;

	/** 채팅방 목록 조회
	 * @return
	 */
	public List<ChatRoom> chatRoomList() {
		return sqlSession.selectList("chatMapper.chatRoomList");
	}

	/** 채팅방 열기
	 * @param room 
	 * @return result
	 */
	public int openChatRoom(ChatRoom room) {
		
		
		return sqlSession.insert("chatMapper.openChatRoom", room);
	}

	/** 채팅방 존재여부
	 * @param chatRoomNo
	 * @return
	 */
	public int existChatRoom(int chatRoomNo) {
		// TODO Auto-generated method stub
		return sqlSession.selectOne("chatMapper.existChatRoom", chatRoomNo);
	}

	/** 채팅방 입장
	 * @param join
	 */
	public void joinChatRoom(ChatRoomJoin join) {
		sqlSession.insert("chatMapper.joinChatRoom", join);
	}

	public List<ChatMessage> selectChatMessage(int chatRoomNo) {
		return sqlSession.selectList("chatMapper.selectChatMessage", chatRoomNo);
	}

	public int insertMessage(ChatMessage cm) {
		// TODO Auto-generated method stub
		return sqlSession.insert("chatMapper.insertMessage",cm);
	}
	
	
	public int exitChatRoom(ChatMessage cm) {
		// TODO Auto-generated method stub
		return sqlSession.insert("chatMapper.exitChatRoom",cm);
	}
	
	
} 
