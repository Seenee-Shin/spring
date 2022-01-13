<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<title>게시판</title>
<link rel="stylesheet" href="${contextPath}/resources/css/board-style.css">

	<jsp:include page="../common/header.jsp"></jsp:include>
	
	<div class="container my-5">
      <h1 id="boardName">
         게시판
         
         <select id="selectCategory" name="ct">
            <option>전체</option>
            <c:forEach items="${category}"  var="c">
               <option value="${c.categoryCode}">${c.categoryName }</option>
            </c:forEach>
         </select>
      </h1>
		
		<!-- 파라미처 중 sv가있으면 변수 생성 -->
		<c:if test="${!empty param.sv }">
			<c:set var="s" value="&sk=${param.sk}$sv=${param.sv}"/>
		</c:if>
		<!-- 파라미처 중 ct가있으면 변수 생성 -->
		<c:if test="${!empty param.ct }">
			<c:set var="c" value="&ct=${param.ct}"/>
		</c:if>
		
		<div class="list-wrapper">
			<table class="table table-hover table-striped my-5" id="list-table">
				<thead>
					<tr>
						<th>글번호</th>
						<th>카테고리</th>
						<th>제목</th>
						<th>작성자</th>
						<th>조회수</th>
						<th>작성일</th>
					</tr>
				</thead>


				<%-- 게시글 목록 출력 --%>
				<tbody>
				
					<c:choose>
						
						<c:when test="${empty boardList}"> 
							<%-- 조회된 게시글 목록이 없을 때 --%>
							<tr>
								<td colspan="6">게시글이 존재하지 않습니다.</td>
							</tr>
						</c:when>
						
						<c:otherwise>
							<c:forEach items="${boardList}" var="board">
							
								<%-- 조회된 게시글 목록이 있을 때 --%>
								<tr>
									<%-- 글 번호 --%>
									<td>${board.boardNo}</td>
									
									<%-- 카테고리명 --%>
									<td>${board.categoryName}</td>
									
									<%-- 글 제목 --%>
									<td class="boardTitle">
										<a href="${contextPath}/board/view/${board.boardNo}?cp=${pagination.currentPage}${c}${s}">
										<!-- <a href="view"> -->
										
											<c:choose>
												<c:when test="${board.statusName eq '블라인드' }">                                                          
													<strong style="color:red; font-size:11px">
														관리자에 의해 블라인드 처리된 게시글입니다.
													</strong>
												</c:when>
												<c:otherwise> 
													
													<c:if test="${board.imgList[0].imgLevel == 0}">
														<img src="${contextPath}${board.imgList[0].imgPath}${board.imgList[0].imgName}">
														
													</c:if>
												
													${board.boardTitle}
												</c:otherwise>
											</c:choose>
										
										</a>
									</td>
									
									
									<%-- 작성자 --%>
									<td>${board.memberName }</td>
								
									<%-- 조회수 --%>
									<td>${board.readCount}</td>
									
									<%-- 작성일 --%>
									<td>${board.createDate }</td>
								</tr>
							</c:forEach>
						</c:otherwise>
					
					</c:choose>
				</tbody>
			</table>
		</div>


		<%-- 로그인이 되어있는 경우에만 글쓰기 버튼 노출 --%>
		<c:if test="${!empty loginMember }">
			<button type="button" class="btn btn-primary float-right" id="insertBtn" onclick="location.href = '${contextPath}/board/insert';">글쓰기</button>
		</c:if>


		<%---------------------- Pagination ----------------------%>
		
		

		<div class="my-5">
			<ul class="pagination">
				
				
				<c:if test="${pagination.startPage != 1 }">
					<li><a class="page-link" href="list?cp=1${c}${s}">&lt;&lt;</a></li>
					<li><a class="page-link" href="list?cp=${pagination.prevPage}${c}${s}">&lt;</a></li>
				</c:if>
				
				<%-- 페이지네이션 번호 목록 --%>
				<c:forEach begin="${pagination.startPage}" end="${pagination.endPage}" step="1"  var="i">
					<c:choose>
						<c:when test="${i == pagination.currentPage}">
							<li><a class="page-link" style="color:black; font-weight:bold;">${i}</a></li>   
						</c:when>
						
						<c:otherwise>
							<li><a class="page-link" href="list?cp=${i}${c}${s}">${i}</a></li>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				
				<c:if test="${pagination.endPage != pagination.maxPage }">
					<li><a class="page-link" href="list?cp=${pagination.nextPage}${c}${s}">&gt;</a></li>
					<li><a class="page-link" href="list?cp=${pagination.maxPage }${c}${s}">&gt;&gt;</a></li>
				</c:if>
			</ul>
		</div>


		<!-- 검색창 -->
		<div class="my-5">

			<form action="list" method="GET" class="text-center" id="searchForm">

				<select name="sk" class="form-control" style="width: 100px; display: inline-block;">
					<option value="title">글제목</option>
					<option value="content">내용</option>
					<option value="titcont">제목+내용</option>
					<option value="writer">작성자</option>
				</select> <input type="text" name="sv" class="form-control" style="width: 25%; display: inline-block;">
				<button class="form-control btn btn-primary" style="width: 100px; display: inline-block;">검색</button>
			</form>

		</div>
	</div>
	<jsp:include page="../common/footer.jsp"></jsp:include>
	<script>
		//주소 쿼리 스트링에서 검색 관련 값을 얻어와 유지 
		/*
			location.search : 주소 중 쿼리 스트링만 반환
			
			URLSearchParams 객체 : 쿼리스트링에서 원하는 파라미터의 값을 얻어올 수 있는 객체 
			-has() : 일치하는 파라미터가 있으면 true 
			-get("Key") : 일치하는 파라미터의 value반환
			-getAll("Key") : 일치하는 모든 파라미터의 값 얻어오기(ex. 체크박스) 
			-append(Key, value) : 새로운 파라미터 추가
			-delete(Key) : 특정 파라미터 제거
			
		*/
		//쿼리 스트링에서 파라미더를 얻어와 반환하는 함수 
		function getParam(key) {
			return new URLSearchParams(location.search).get(key)
		}
			
		//검색 select 세팅 
		const skOptions = document.querySelectorAll("select[name=sk] > option")
		
		//향상된 for문으로 option하나씩 접근 (배열 = of / 하나 in)
		for (let op of skOptions){
			
			if(option.value == getParam("sk")){ //현재 접근한 option의 value와 쿼리스트링 sk값이 같다면 
				//일치하는 option 태그에 selected 속성 추가 
				op.setAttribute("selected", true)
			}
			
		}
			//검색 input 
			document.querySelector("input[name=sv]").value = getParam("sv")
			
		//카테고리가 select가 change 되었을때
		document.getElementById("selectCategory").addEventListener("change",function(){
			//쿼리스트링을 누적할 변수 
			let qs = ""
			
			//쿼리스트링에 cp 없으면 1, 있으면 작성된 값
			if(getParam("cp") == null) qs += "?cp=1"
			else qs+= "?cp=" + getParam("cp")		
			
			//카테고리 select가 '전체'가 아니면 qs에 쿼리스트링 추가 
			if(this.value != "전체") qs += "&ct" + this.value
					
			location.href = "list" + qs; 
			
			if(getParam("sv") != null){
				qs += "&sk=" + getParam("sk") + "&sv=" + getParam("sv")
			}
			
		})
	</script>

</body>
</html>
