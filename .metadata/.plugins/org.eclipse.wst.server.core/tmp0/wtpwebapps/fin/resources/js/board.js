(function() {
	// 오늘 날짜 출력 
	var today = new Date();
	var month = (today.getMonth() + 1);
	var date = today.getDate();

	var str = today.getFullYear() + "-"
			+ (month < 10 ? "0" + month : month) + "-"
			+ (date < 10 ? "0" + date : date);
	$("#today").html(str);
})();


// 유효성 검사 
function boardValidate() {
	if ($("#boardTitle").val().trim().length == 0) {
		alert("제목을 입력해 주세요.");
		$("#title").focus();
		return false;
	}

	if ($("#boardContent").val().trim().length == 0) {
		alert("내용을 입력해 주세요.");
		$("#content").focus();
		return false;
	}
}

// 이미지 영역을 클릭할 때 파일 첨부 창이 뜨도록 설정하는 함수
$(function() {
	$(".boardImg").on("click", function() {
		var index = $(".boardImg").index(this);
				// 현재 클릭된 요소가 .boardImg 중 몇 번째 인덱스인지 반환



		$("[type=file]").eq(index).click();
		// 타입이 file인 요소 중 몇번째 인덱스 요소를 선택하여 클릭해라
	});

});

//파일 선택 -> inpnut요소 백업 객체
const fileClone = {}


// 각각의 영역에 파일을 첨부 했을 경우(값이 변했을 경우) 미리 보기가 가능하도록 하는 함수
function loadImg(input, num) {

	if (input.files && input.files[0]) {

    fileClone[num] = $(input).clone() //백업객체에 복제본 추가 

		var reader = new FileReader();
	
		reader.readAsDataURL(input.files[0]);
		reader.onload = function(e) {

			$(".boardImg").eq(num).children("img").attr("src", e.target.result);
		}

	} else{
    //console.log("취소")

    //취소 실행된 input태그에 백업해놓은 복제본 추가 
    $(input).before(fileClone[num].clone())
    // 원본 복사본의 복사본을 생성 후 삽입
    $(input).remove() //원본삭제 (새로운 복제본 추가 후 원복삭제)


    //innerHTML, html()
    // 작성된 문자열ㄹ을 html parser를 이용하여 해석 후 반영
    // 읽어들이기 전까지는 문자열 내부에 요소가 잇는지 알수 없다
    // 같은 문장이라도 새로운 요소로 인식
  }
}


// 수정버튼 클릭 시 동작
function updateForm(){
	document.requestForm.action = "updateForm";
	document.requestForm.method = "POST";
	document.requestForm.submit();
}

//이미지 x 버튼 눌렀을때 동작 
$(".deleteImg").on("click",function(e){
  //event 발생 객체 : 이벤트에 관련된 모든 객체

  //이벤트 버븥링(감싸고 있는 요소의 이벤트가 전파됨) 을 방지 
  e.stopPropagation();
  $(this).prev().removeAttr("src") //미리보기 이미지 삭제 

  //클릭된 x버튼의 인덱스와 같은 인덱스에 위치한 input = type="file" 요소의 value 초기화 
  const index = $(this).index(".deleteImg")

  $("input[name=images]").eq(index).val("")
})