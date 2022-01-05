<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%-- JSTL c태그 사용을 위한 taglib 추가 --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Footer -->
<footer class="py-5 bg-dark footer">
	<div class="container">
		<p class="m-0 text-center text-white">Copyright &copy; KH Information Educational Institute A-Class</p>
	</div>
</footer>

<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js" integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" crossorigin="anonymous"></script>

<!-- Bootstrap4 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-Piv4xVNRyMGpqkS2by6br4gNJ7DXjqk09RmUpJ8jgGtD7zP9yug3goQfGII0yAns" crossorigin="anonymous"></script>
<script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>

<!-- 공용 JS -->
<script src="${contextPath}/resources/js/final.js"></script>

<%-- request에 message 속성이 존재하는 경우 alert창으로 해당 내용을 출력 
	redirectAttributes로 request를 session으로 올려 사용 --%>
<c:if test="${ !empty requestScope.message }"> <!-- redirectAttributes에서 request로 바뀌었기때문에 session이 아닌 request -->
   <script>
      $(function(){ // ready() 함수로 페이지 로딩 완료 후 alert 출력
         alert("${message}");
      })
         // EL 작성 시 scope를 지정하지 않으면
         // page -> request -> session -> application 순서로 검색하여
         // 일치하는 속성이 있으면 출력
   </script>
</c:if>

<!-- requestscope에 title존재시 -->
<c:if test="${ !empty requestScope.title }"> <!-- redirectAttributes에서 request로 바뀌었기때문에 session이 아닌 request -->
   <script>
      $(function(){ // ready() 함수로 페이지 로딩 완료 후 alert 출력
         swal({
        	 title : "${title}",
        	 text: "${text}",
        	 icon: "${icon}"
        	
         });
      })
         // EL 작성 시 scope를 지정하지 않으면
         // page -> request -> session -> application 순서로 검색하여
         // 일치하는 속성이 있으면 출력
   </script>
</c:if>


