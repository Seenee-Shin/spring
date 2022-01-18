function sendMessage(){
	const message = $("#inputChatting").val(); 
	//채팅 textarea 값 
	
	if(message.trim().length == 0){
		alert("내용을 입력해주세요")
	}else{
		const obj={}; //js객체 생성
		obj.memberNo = memberNo
		obj.memberId = memberId
		obj.memberName = memberName
		obj.message = message
		obj.chatRoomNo = chatRoomNo
		
		//console.log(obj)
		//만들어진 js객체를 json으로 변환, websocket객체의 handleTextMessage()로 전달
		//JSON.stringify : js객체 -> json으로 변환
		//JSON.parse : json객체 -> js으로 변환
		chattingSock.send(JSON.stringify(obj))
		
		$('#inputChatting').val("") //전달된 메세지 삭제
	}
}

//웹소켓 서버에서 전달된 메세지가 있을경우 수행되는 이벤트 
chattingSock.onmessage= function(e){
	console.log(JSON.parse(e.data))
	
	// 메소드를 통해 전달받은 객체값을 JSON객체로 변환해서 obj 변수에 저장.
   const obj = JSON.parse(e.data);


   const li = $("<li>");


   const p = $("<p class='chat'>");
   const span = $("<span class='chatDate'>");
   span.html(obj.createDate);

/*   const chat = obj.message.replace(/\\n/g, "<br>");
   p.html(chat);*/
	
	if(obj.message != undefined){
	//xss, 개행문자 처리
	let chat = XSS( obj.message)
	chat = chat.repalceAll("\n","<br>")
	p.html(chat)	
	}else{
		//메세지가 없는 경우 (나가기)
		p.html("<b>"+obj.memberName+"님이 나가셨습니다.</b>")
	}
	
	
   if (obj.memberNo == memberNo) { //내가 쓴 채팅
      li.addClass("myChat");
      li.append(span);
      li.append(p);
   } else {
      li.html("<b>" + obj.memberName + "</b><br>");
      li.append(p);
      li.append(span);
   }


   $(".display-chatting").append(li);

   // 채팅 입력 시 스크롤을 가장 아래로 내리기
   $(".display-chatting").scrollTop($(".display-chatting")[0].scrollHeight);
}

$('#send').on("click", sendMessage);

//xss 함수
function XSS(message){
	let str = message 
	str.replace(/&/g,"&amp")
	str.replace(/</g,"&lt")
	str.replace(/>/g,"&gt")
	str.replace(/"/g,"&quot")
	
	return str
}


//나가기 버튼 동작
$("#exit-btn").on("click", function(){
	if(confirm("채팅방을 나가겠습니까?")){
		//방에있는 자신의 정보 삭제 
		
		const obj = {}
		obj.memberNo = memberNo
		obj.chatRoomNo = chatRoomNo
		obj.memberName = memberName
		
		//웹소켓 처리 객체로 전달 
		chattingSock.send(JSON.stringify(obj))
		
		//방나가기 
		
		//location.replace : 해당 주소 화면으로 변경(이전화면의 히스토리가 남지 않음)
		location.replace(contextPath + "/chat/roomList")
		
	}
})