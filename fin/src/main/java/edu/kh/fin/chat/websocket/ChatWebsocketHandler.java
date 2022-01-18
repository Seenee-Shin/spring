package edu.kh.fin.chat.websocket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import edu.kh.fin.chat.model.service.ChatService;
import edu.kh.fin.chat.model.vo.ChatMessage;

public class ChatWebsocketHandler extends TextWebSocketHandler {



	   /* WebSocket
    - 브라우저와 웹서버간의 전이중통신을 지원하는 프로토콜이다
    - HTML5버전부터 지원하는 기능이다.
    - 자바 톰캣7버전부터 지원했으나 8버전부터 본격적으로 지원한다.
    - spring4부터 웹소켓을 지원한다. 
    (전이중 통신(Full Duplex): 두 대의 단말기가 데이터를 송수신하기 위해 동시에 각각 독립된 회선을 사용하는 통신 방식. 대표적으로 전화망, 고속 데이터 통신)
    
    
    
    WebSocketHandler 인터페이스 : 웹소켓을 위한 메소드를 지원하는 인터페이스
       -> WebSocketHandler 인터페이스를 상속받은 클래스를 이용해 웹소켓 기능을 구현
       TextWebSocketHandler: WebSocketHandler의 자식 클래스
    
    
    WebSocketHandler 주요 메소드
            
        void handlerMessage(WebSocketSession session, WebSocketMessage message)
        - 클라이언트로부터 메세지가 도착하면 실행
        
        void afterConnectionEstablished(WebSocketSession session)
        - 클라이언트와 연결이 완료되고, 통신할 준비가 되면 실행

        void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
        - 클라이언트와 연결이 종료되면 실행

        void handleTransportError(WebSocketSession session, Throwable exception)
        - 메세지 전송중 에러가 발생하면 실행 
        
          ----------------------------------------------------------------------------
    
    TextWebSocketHandler :  WebSocketHandler 인터페이스를 상속받아 구현한 텍스트 메세지 전용 웹소켓 핸들러 클래스
    
        handleTextMessage(WebSocketSession session, TextMessage message)
        - 클라이언트로부터 텍스트 메세지를 받았을때 실행
         

   */
	
	@Autowired
	private ChatService service;
	
	private Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<WebSocketSession>());
	
	//synchronizedSet : 동기화된 set(hashset은 비동기)
	//->멀티 스레드환경에서 하나의 컬렉션 요소에 여러 스레그가 접근하면 충돌발생 
	// 동기화시켜 충돌을 방지 
	
	//클라이언트와 연결이 완료되고 통신할 준비가 되면 실행


	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		
		//WebSocketSession : 서버간 전이중 통신 담당 객체 
		//-> 웹소켓 요청을 보낸 클라이언트와 통신 할 수 있는 객체
		
		//웹소켓 서버롸 통신하는 클라이언트이 정보를 한곳에 모아둠 
		//웹소켓 통신을 요청한 클라이언트 모음
		sessions.add(session);
		
		System.out.println(session.getId()+"연결됨");
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		System.out.println("전달받은 내용"+ message.getPayload());
		//.getPayload : 전송된 데이터
		
		//jackson-databind의 ObjectMapper객체
		//-JSON문자열의 모든 키 값이 특정 클래스의 필드와 모두 일치하면 새클래스를 이용해 새 객체를 만들고 JSON 문자열을 일거 같을 필드에 값을 대입
		ObjectMapper objectMapper = new ObjectMapper();
		ChatMessage cm = objectMapper.readValue(message.getPayload(), ChatMessage.class);
		
		System.out.println("chaged cm" + cm);
		//웹소켓 요청을 보낸 모든 클라이언트가 담겨져 있음
		
		//채팅 내용을 db에 저장 
		int result = service.insertMessage(cm);
		
		
		
		if (result>0) {
			
			for(WebSocketSession wss : sessions) {
				
				// WebsokectSession : 웹소켓 서버 - 클라이언트 간의 통신을 가능하게 하는 객체
				// + HttpSession을 가로채어 가지고있는객체
				
				//Session에 저장된 모든 클라이언트 세션 정보에서 chatRoomNo 속성을 얻어오는 구문
				int chatRoomNo = (Integer)wss.getAttributes().get("chatRoomNo");
				
				//메세지에 있는 방 번호와 채팅방에 있으면서 (웹소켓 연결중) 같은 방번호를 가지고 있는 클라이언트인 경우
				if (chatRoomNo == cm.getChatRoomNo() ) {
					
				}
				wss.sendMessage(new TextMessage(message.getPayload()));
			}
		}
		
	}

	
	//클라이언트와 연결이 종료 되었을때
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		//session에서 웹소켓 연결을 종료한 클라이언트 세션을 삭제
		sessions.remove(session);
	}
	
	
}
