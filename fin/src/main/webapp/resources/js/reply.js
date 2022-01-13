// 전역 변수
//contextPath = "${contextPath}";

// 로그인한 회원의 회원 번호, 비로그인 시 "" (빈문자열)
// loginMemberNo = "${loginMember.memberNo}";

// 현재 게시글 번호
// boardNo = ${board.boardNo};

// 수정 전 댓글 요소를 저장할 변수 (댓글 수정 시 사용)
//beforeReplyRow


function selectReplyList(){
  // 동기식 : 순서대로 처리
  //비동기 : 순서x 동시에 처리 

  //json형태로 조회 및 출력하기 
  $.ajax({
    url : contextPath+"/reply/select",
    data : {"boardNo" : boardNo}, //현재게시글 번호 
    type : "get",
    dataType : "json",  //응답 데이터 형식 : 제이슨 / 자동으로 JS객체로 변환

    success : function(rList){

      console.log(rList)
      //댓글목록을 요소로 만들어 화면에 출력

      //기존 댓글 영역 내용 삭제 
      $("#replyListArea").empty() //요소 비우기

      //rList의 요소를 순차적으로 반복접근 
      $.each(rList,function(i,reply){
                       //const li = $("<li>").addClass("reply-row");
        const li = $('<li class="reply-row">')

        // 자식 댓글인 경우 li요소에 클래스 추가
        if(reply.parentReplyNo != 0) li.addClass("child-reply");

        const div = $('<div>');

        const rWriter = $('<p class="rWriter">').text(reply.memberName)  ;
        const rDate = $('<p class="rDate">').text('작성일 : ' + reply.replyCreateDate);

        // div에 자식으로 rWriter, rDate 추가
        div.append(rWriter, rDate);

        // 댓글 내용
        const rContent = $('<p class="rContent">').html(reply.replyContent);

        li.append(div, rContent);

        //버튼 영역 요소 생성
        const replyBtnArea = $('<div class="replyBtnArea">');


         //로그인이 되어있을 경우
        if(loginMemberNo != ""){
          const childReply = $("<button>").addClass("btn btn-primary btn-sm ml-1").text("대댓글");

          //버튼 클릭 시 대댓글 작성 영역을 출력하는 함수 호출
          //reply.replyNo :대댓글의 부모 댓글의 번호
          // this: 이벤트가 발생한 요소 자체 ->g 해당요소를 기준점으로 하여 parent(), children()등의 순회(탐색 메소드)
          //  append, after, before등의 요소 삽입 메소드 작성예전
          childReply.attr("onclick", "showInsertReply("+ reply.replyNo + ", this)");

          replyBtnArea.append(childReply);
        }


        //수정, 삭제버튼 생성 
        // 댓글 작성자 == 로그인 멤버 -> 수정, 삭제 버튼 영역 생성
        if(reply.memberNo == loginMemberNo){ 
          
          const showUpdate = $('<button>').addClass('btn btn-primary btn-sm ml-1').text('수정'); //버튼생성
          showUpdate.attr("onclick", "showUpdateReply("+reply.replyNo+", this)"); // 함수 생성 후 버튼에 적용

          const deleteReply = $('<button>').addClass('btn btn-primary btn-sm ml-1').text('삭제');      
          deleteReply.attr("onclick", "deleteReply("+reply.replyNo+")");

          replyBtnArea.append(showUpdate, deleteReply);
        }

        li.append(replyBtnArea);

        $("#replyListArea").append(li);


    });
  },
    

    error : function(req,status,error){
      console.log("댓글 목록 조회 실패")
      console.log(req.responseText)
    }

  })
}



// -----------------------------------------------------------------------------------------
// 댓글 등록
function addReply() {
    
  // 게시글 번호(boardNo), 로그인한 회원 번호(loginMemberNo), 댓글 내용

  if(loginMemberNo == ""){ // 로그인이 되어 있지 않은 경우
    alert("로그인 후 이용해 주세요.");

  }else{ // 로그인한 경우



      // 댓글 미작성한 경우
    if( $("#replyContent").val().trim().length == 0  ){
      alert("댓글을 작성한 후 버튼을 클릭해주세요.");
      $("#replyContent").focus();

    }else{ // 댓글을 작성한 경우


          $.ajax({
            url : contextPath + "/reply/insert",
            data : {"memberNo" : loginMemberNo,  
                  "boardNo" : boardNo, 
                  "replyContent" : $("#replyContent").val()},     
            type : "POST",
            success : function(result){
              console.log(result);

              if(result > 0){
                alert("댓글 삽입 성공");
                $("#replyContent").val(""); // 작성한 댓글 내용 삭제

                selectReplyList(); // 댓글 조회 함수를 호출하여 댓글 화면 다시 만들기
              }else{
                alert("댓글 삽입 실패");

              }

            },

            error : function(req, status, error){
              console.log("댓글 삽입 실패");
              console.log(req.responseText);
            }
          });

      }



  }


}