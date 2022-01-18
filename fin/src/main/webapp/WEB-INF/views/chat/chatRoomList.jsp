<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<title>채팅방 목록</title>
<link rel="stylesheet" href="${contextPath}/resources/css/chat-style.css">
<body>
	<jsp:include page="../common/header.jsp"></jsp:include>
	<div class="container my-5">
		<h1>채팅방 목록</h1>
			<div class="list-wrapper">
				<table class="table table-hover table-striped my-5" id="list-table">
					<thead>
						<tr>
							<th>방번호</th>
							<th>채팅방 제목(주제)</th>
							<th>개설자</th>
							<th>참여인원수</th>
						</tr>
					</thead>
					
					
					<%-- 채팅 목록 출력 --%>
					<tbody>
					
						<c:choose>
						
							<%-- 조회된 게시글 목록이 없을 때 --%>
							<c:when test="${empty chatRoomList }">
								<tr>
									<td colspan="4">존재하는 채팅방이 없습니다.</td>
								</tr>
							</c:when>
							
							
							<%-- 조회된 채팅방 목록이 있을 때 --%>
							<c:otherwise>
								
								<c:forEach var="chatRoom" items="${chatRoomList}">
									<tr>
										<td>${chatRoom.chatRoomNo}</td> <%-- 채팅방번호 --%>
										<td>
											${chatRoom.title}
											
											<c:if test="${!empty loginMember }">
												<a class="btn btn-primary btn-sm" href="${contextPath}/chat/room/${chatRoom.chatRoomNo}">참여</a>
											</c:if>
										</td> <%-- 제목 --%>
										
										<td>${chatRoom.memberName}</td> <%-- 개설자 --%>
										<td>${chatRoom.cnt}</td> <%-- 참여인원수 --%>
									</tr>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</tbody>
				</table>
				
				
				<%-- 로그인이 되어있는 경우 --%>
				<c:if test="${!empty loginMember }">
					 <a class="btn btn-primary float-right" data-toggle="modal" href="#openChatRoom">채팅방 만들기</a>
				</c:if>
			</div>
		</div>

	<div class="modal fade" id="openChatRoom" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">

			<div class="modal-content">

				<div class="modal-header">
					<h5 class="modal-title" id="myModalLabel">채팅방 만들기</h5>
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
				</div>

				<div class="modal-body">
					<form method="POST" action="${contextPath}/chat/openChatRoom">

						<div class="form-label-group">
							<input type="text" id="chatRoomTitle" name="title" class="form-control" placeholder="채팅방 제목" required> 
							<label for="chatRoomTitle">채팅방 제목</label>
						</div>
						
						

						<button class="btn btn-lg btn-primary btn-block" type="submit">만들기</button>
						
						
					</form>
				</div>
			</div>
		</div>
	</div>
			
			
	<jsp:include page="../common/footer.jsp"></jsp:include>



	<script>
		
	</script>
</body>
</html>
