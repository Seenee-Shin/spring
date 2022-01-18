<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<title>채팅방</title>
<link rel="stylesheet" href="${contextPath}/resources/css/chat-style.css">
<body>
	<jsp:include page="../common/header.jsp"></jsp:include>
	
	<div class="chatting-area">
		<div id="exit-area">
			<button class="btn btn-outline-danger" id="exit-btn">나가기</button>
		</div>
		<ul class="display-chatting">
	
			<c:forEach items="${list}" var="msg">
			<%-- message가 null인경우 & 아닌경우를 구분 --%>
			
			<c:choose>
				<c:when test="${msg.message}">
					<c:set var="message" value="${msg.message}"/>
				</c:when>
					
				<c:otherwise>
					<c:set var="message" value="${msg.memberName}님이 나가셨습니다."/>
				</c:otherwise>
			</c:choose>
			
				<c:if test="${msg.memberNo == loginMember.memberNo}">
					<li class="myChat">
						<span class="chatDate">${msg.createDate}</span>
						<p class="chat">${message}</p>
					</li>
				</c:if>
				
				<c:if test="${msg.memberNo != loginMember.memberNo}">
					<li>
						<b>${msg.memberName}</b>	<br>
						<p class="chat">${message}</p>
						<span class="chatDate">${cmsg.createDate}</span>
					</li>
				</c:if>
			
			</c:forEach>
	
			
			
			
		</ul>	
			
	
	
		<div class="input-area">
			<textarea id="inputChatting" rows="3"></textarea>
			<button id="send">보내기</button>
		</div>
	</div>
	
	<jsp:include page="../common/footer.jsp"></jsp:include>
	
	<!------------------------- sockjs를 이용한 WebSocket 구현을 위해 라이브러리 추가 -------------------------->
	<!-- sockjs : 웹소켓과 유사한 객체를        제공하는 javascript 라이브러리 -->
	<!-- https://github.com/sockjs/sockjs-client -->
	<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
	<script>
		// 로그인이 되어 있을 경우에만
		// /chat 이라는 요청 주소로 통신할 수 있는  WebSocket 객체 생성
		let chattingSock = new SockJS("<c:url value='/chat' />"); //contextPath + /chat 전역변수 
		
		const memberNo = "${loginMember.memberNo}";
		const memberId = "${loginMember.memberId}";
		const memberName = "${loginMember.memberName}";
		const chatRoomNo = "${chatRoomNo}";
		
		const contextPath = "${contextPath}";
	</script>

	<script src="${contextPath}/resources/js/chat.js"></script>
	
</body>
</html>
