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


//수정폼 전환
function showUpdateReply(replyNo,el){

  //이미 열려있는 댓글 수정 화면이 있으면 닫고 새로열기
  if($(".replyUpdateContent").length>0){
    // 수정화면 클래스가 1개이상일때 (클래스 배열의 길이로 판별)

    if(confirm("확인 클릭시 댓글 수정이 취소됩니다.")){
      $(".replyUpdateContent").eq(0).parent().html(beforeReplyRow);
    }
  }


    //댓글 수정 폼 출력전 원본을 저장
    beforeReplyRow = $(el).parent(). parent().html()
    
    //기존에 작성되어 있던 댓글 내용 저장 
    let beforeContent = $(el).parent().prev().html() 
    //버튼의 부모요소의 바로 전요소 

       // 이전 댓글 내용의 크로스사이트 스크립트 처리 해제, 개행문자 변경
    // -> 자바스크립트에는 replaceAll() 메소드가 없으므로 정규 표현식을 이용하여 변경
    beforeContent = beforeContent.replace(/&amp;/g, "&");
    beforeContent = beforeContent.replace(/&lt;/g, "<");
    beforeContent = beforeContent.replace(/&gt;/g, ">");
    beforeContent = beforeContent.replace(/&quot;/g, "\"");

    //testarea -> 서버:개행문자 \r\n 
    //화면 -> js : 개행문자 \n
    beforeContent = beforeContent.replace(/<br>/g, "\n");

    //기존댓글 영역의 내용, 버튼을 삭제하고 
    //새로운 textarea와 btn 추가
    $(el).parent().prev().remove() //이전 내용 삭제
    const textarea = $("<textarea class = 'replyUpdateContent' rows='3'>").val(beforeContent)

    $(el).parent().before(textarea)


    //수정, 취소버튼 생성
     // 수정 버튼
    const updateReply = $("<button>").addClass("btn btn-primary btn-sm ml-1 mb-4").text("댓글 수정").attr("onclick", "updateReply(" + replyNo + ", this)");

     // 취소 버튼
    const cancelBtn = $("<button>").addClass("btn btn-primary btn-sm ml-1 mb-4").text("취소").attr("onclick", "updateCancel(this)");

    //기존 버튼영역 내부를 초기화 후 수정, 삭제버튼을 추가 
    //변수를 만들어 기준점 확립
    const replyBtnArea = $(el).parent()

    $(replyBtnArea).empty() 
    //내부 초기화 
    $(replyBtnArea).append(updateReply, cancelBtn)

}


// 댓글 수정 폼 취소 
function updateCancel(el ){
  $(el).parent().parent().html(beforeReplyRow)
}


//댓글 수정 
function updateReply(replyNo,el){
  const replyContent = $(el).parent().prev().val()

  $.ajax({
    url: contextPath+"/reply/update",
    data :{"replyNo": replyNo, "replyContent":replyContent},
    type  : "post",
    success : function(result){
      if(result >0){
        swal({"title" : "댓글이 수정 되었습니다.", "icon":"success"})
        
        selectReplyList() //댓글목록 다시 출력
        
      }else{
        swal({"title" : "댓글수정 실패", "icon":"error"})
        
      }
      
    },
    error : function(req, status, error){
      console.log("댓글 수정 실패");
      console.log(req.responseText);
    }
  })
}



//댓글 삭제 
function deleteReply(replyNo){

  if(confirm("댓글을 삭제하시겠습니까?")){
  
  $.ajax({
    url: contextPath+"/reply/delete",
    data : {"replyNo": replyNo},
    type: "post",
    
    success :function(result){
      
      if(result > 0){
        swal({"title": "댓글 삭제 완료", "icon":"success"})
        selectReplyList() //댓글목록 다시 출력

      }else{
        swal({"title" : "댓글 삭제 실패", "icon":"error"})

      }
    },
    error : function(req, status, error){
      console.log("댓글 수정 실패");
      console.log(req.responseText);
    }
  })
}
}


//대댓글 등록 작성 화면 

//이전 대댓글 작성 textarea 기억하기
let beforeChildReplyContent


function showInsertReply(parentReplyNo,el){
  //parentReplyNo : 대댓글 버튼이 글릭되 부모 댓글의 번호 
  // el : 대댓글 버튼 

  //다른 대댓글 textarea가 존재하는 경우 + 이전 대댓글에 작성중인 내용이 있을 경우
  if($(".replyInsertContent").length > 0  && $(beforeChildReplyContent).val().trim().length > 0 ){
    if(confirm(" 확인 클릭시 작성한 내용이 사라집니다.")){

      //다른 textarea 다음 존재하는 btn영역을 삭제
      $(beforeChildReplyContent).next().remove()

      //다른 textarea 삭제
      $(beforeChildReplyContent).remove()
      
    } else{
      return //현재함수를 끝냄 -> 영역추가 x
    }
  }


  //댓글 작성을 위한 textarea 생성 
  const textarea =$("<textarea>").addClass("replyInsertContent").attr("rows","3")

  //버튼 영역
  const replyBtnArea = $("<div>").addClass("replyBtnArea")

  //버튼 
  const insertChildReply = $("<button>").addClass("btn btn-primary btn-sm ml-1 mb-4").text("대댓글 등록")
  
  // + 대댓글 등록
  insertChildReply.attr("onclick","insertChildReply("+parentReplyNo+" , this)")
  
  //취소버튼 
  const insertCancle = $("<button>").addClass("btn btn-primary btn-sm ml-1 mb-4").text("취소")

  insertCancle.attr("onclick","insertCancle(this)")

  //버튼 영역에 등록,취소 버튼 추가 
  $(replyBtnArea).append(insertChildReply, insertCancle)

  //텍스트 작성란 추가
  $(el).parent().after(textarea)
  // 작성란을 버튼영역에 추가
  $(textarea).after(replyBtnArea)

  //새로 생성된 textarea의 위치를 변수에 저장 
  beforeChildReplyContent = textarea;

}


//insertCancle
function insertCancle(el){
  $(el).parent().prev().remove() //textarea 삭제
  $(el).parent().remove()  //replyBtnArea 삭제
}



//
function insertChildReply(parentReplyNo, el){
  const replyContent = $(el).parent().prev() //대댓글 내용
  
  if($(replyContent).val().trim().length == 0){

    alert("내용을 입력해주세요")
    $(replyContent).focus()

  }else{

    $.ajax({
      url : contextPath+"/reply/insert",
      data: {"memberNo" : loginMemberNo,
            "boardNo" : boardNo,
            "parentReplyNo" : parentReplyNo,
            "replyContent" : $(replyContent).val()},
      type : "post",
      success : function(result){
        swal({"title": "댓글 작성 완료", "icon":"success"})
        selectReplyList() //댓글목록 다시 출력


      },

      error : function(req, status, error){
        console.log("댓글 작성 실패");
        console.log(req.responseText);
      }


    })

  }
}