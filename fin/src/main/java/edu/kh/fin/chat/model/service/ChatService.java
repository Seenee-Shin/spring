package edu.kh.fin.chat.model.service;

import java.util.List;

import edu.kh.fin.chat.model.vo.ChatMessage;
import edu.kh.fin.chat.model.vo.ChatRoom;
import edu.kh.fin.chat.model.vo.ChatRoomJoin;

public interface ChatService {

	/** 채팅방 목록 조회
	 * @return
	 */
	List<ChatRoom> chatRoomList();

	/** 채팅방 생성
	 * @param room
	 * @return
	 */
	int openChatRoom(ChatRoom room);

	/** 채팅방 입장 + 내역 호출
	 * @param join
	 * @return
	 */
	List<ChatMessage> joinChatRoom(ChatRoomJoin join);

	/** 채팅 내용 db저장 
	 * @param cm
	 * @return
	 */
	int insertMessage(ChatMessage cm);

}
