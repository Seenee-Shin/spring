package edu.kh.fin.member.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.fin.common.Util;
import edu.kh.fin.member.model.service.MemberService;
import edu.kh.fin.member.model.vo.Member;

//@RequestMapping : 요청주소 , 전달방식에 따라 연결되는 클래스 또는 메소드를 지정하는 어노테이션 
//					-> class + method에 동시 작성하여 하나의 주소로 해석 가능  

@Controller //프레젠테이션 레이어, 웹앱에 전달된 요청과 응답을 처리하도록 명시 + Bean등록 
@RequestMapping("/member/*") // /fin/member/로 시작하는 모든 요청을 받아 처리 
@SessionAttributes({"loginMember"}) // loginMember가 request에서 session으로 범위변경
public class MemberController {
	
	//필드작성 
	//@Autowired : Component-scan을 통해 Bean으로 등록된 객체 중 타입이 같거나 상속 관계인 객체를 찾아 자동으로 DI(의존성 주입)
	@Autowired  
	private MemberService service;
	
	//@RequestMapping("login") // /fin/member/login 메소드 처리
	//@RequestMapping(value = "login" , method = RequestMethod.GET)
	//1.()안에 작성된 매개변수가 한개인 결루 매핑할 요청 주소 
	//2.()안에 작성된 매개변수가 두개 이상인 경우 각 값의 key를 작성하여 분류 
	//	-> method 를 작성하지 않으면 방식 관계없이 모두 처리  	
	
	//1. HttpServletRequest를 이용한 파라미터 전달받기 
//	@RequestMapping(value = "login" , method = RequestMethod.POST)
	public String login(HttpServletRequest req) {
		//컨트롤러 메소드에 원하는 객체의 타입을 매개변수로 작성하면 요청/응답 관련 객체를 얻어오거나 새로운 객체를 생성해서 의존성 주입(DI)
		
		System.out.println(req.getParameter("memberId"));
		System.out.println(req.getParameter("memberPw"));
		
		//Spring redirect
		return "redirect:/"; // /fin/로 재요청
	}
	
	
	
	//2. RequestParam 어노테이션 사용 
	// 메소드 매개변수 앞에 작성 
	// 어노테이션 뒤쪽에 작성된 매개변수에 파라미터 저장
	
	//@RequestParam 속성
	// value : 전달 받은 input 태그의 name 속성 값 (매개변수 1개일때 기본값) 
	// Required : 파라미터 필수로 전달(미작성시 기본값 true) -> 미작성시 400에러 발생
	// defaultValue : 전달받은 파라미터 값이 없을때 지정할 기본값 
//	@RequestMapping(value = "login" , method = RequestMethod.POST)
	public String login2(@RequestParam("memberId") String id,
						@RequestParam("memberPw") String pw, @RequestParam(value="test", required =false, defaultValue = "기본값") String t) {
		System.out.println(id);
		System.out.println(pw);
		System.out.println(t);
		
		return "redirect:/";
	}

	
	
	//3. RequsetParam 생략 
	//파라미터와 변수의 이름이 같으면 어노테이션 생략가능
	//@RequestMapping(value = "login" , method = RequestMethod.POST)
	public String login3(String memberId, String memberPw) {
		
		return "redirect:/";
	}
	
	
	
	
	//4. @ModelAttribute 
	// 요청시 전달 받은 파라미터를 객체 형태로 매핑하는 역할을 하는 어노테이션
	// 객체로 매핑하기위한 조건
	// 1) input 태그 name속성 값과 객체의 멤버 변수(필드명)이 같아야함 (커멘트 객체)
	// 2) 객체로 만들어길 클래스에 기본 생성자가 있어야함 
	//	--> 스프링이 객체를 자동으로 생성할 때 사용 
	// 3) setter가 작성되어 있어야함 
	
	//@RequestMapping(value = "login" , method = RequestMethod.POST)
	public String login4(@ModelAttribute Member member) {
		 System.out.println(member.getMemberId());
		 System.out.println(member.getMemberPw());
		return "redirect:/";
	}
	
	
	
	//5. @ModelAttribute생략 
	@RequestMapping(value = "login" , method = RequestMethod.POST)
	public String login5(Member member, Model model, RedirectAttributes ra,
						 @RequestParam(value = "save", required = false) String save,
						 HttpServletRequest req, HttpServletResponse resp) {
		 // Model : 데이터를 K:V형태로 저장, 전달 (기본적으로 request 범위)
		 // Contoller 위쪽에 어노테이션@SessionAttributes 작성으로 session에 저장
		
		 //login : db에서 id,pw가 일치하는 회원 정보를 조회하여 Session에 추가 
		 Member loginMember = service.login(member);
		 
		 if(loginMember != null) {
			 //로그인 정보 세션에 추가

			 // -> page, request, session, application과 사용방법이 유사함
			 model.addAttribute("loginMember", loginMember);
			 
			 //*******쿠키 생성 ***********
			 //1. cookie 객체 생성 
			 Cookie cookie = new Cookie("saveId", loginMember.getMemberId());
			 
			 //2. 쿠키 유지시간 지정 
			 if(save != null) { //아이디저장 체크 (한달 유지)
				 cookie.setMaxAge(60*60*24*30);
			 }else {
				 cookie.setMaxAge(0);
				 //미체크
				 // 기존 쿠키를 새로 생선된 쿠키로 변경 (삭제)
			 }
			 
			 //3. 쿠키가 적용 되는 경로 (범위)
			 //최상위 경로 지정 -> 어디서든 쿠키 사용 가능
			 cookie.setPath(req.getContextPath());
			 // 매개변수로 http를 받아 contextPath불러오기 
			 
			 //4.응답객체에 생성한 쿠키를 추가, 클라이언트로 전달
			 resp.addCookie(cookie);
			 
		 }else {
			 //RedirectAttributes : 리다이렉트 시, 리퀘스트 범위로 값을 전달 가능 
			 //RedirectAttributes 값 세팅 : request scope
			 
			 //addFlashAttribute : 일회성으로 사용후 데이터 소멸 
			 //redirect시 request->session으로 일시적으로 이동시킴 
			 //응답 완료시 : session -> request 복귀
			 
			 ra.addFlashAttribute("message", "아이디 또는 비밀번호를 확인해 주세요");
		 }
		return "redirect:/";
	}
	
	
	//logout 
	// remove session
	@RequestMapping("logout")
	public String logout(SessionStatus status) {
		// session.invalidate() 를 사용하면 req.getSession을 통해 얻어온 세션만 만료 
		// 해당 session은 @SessionAttribute + model.attribute()로 작성된 세선이기 때문에 위 방법 사용 불가 
		
		// SessionStatus를 이용 아래와 같은 방법으로 세션을 제거한다 
		// SessionStatus : 세션 상태 관리 객체 
		// ->@SessionAttribute를 통해 등록된 Session 관리 가능 
		
		status.setComplete();
		//.setComplte:Mark the current handler's session processing as complete, allowing for clean up of session attributes.

		
		return "redirect:/"; 
	}
	
	//signup 페이지로 전환 
	@RequestMapping(value = "signUp", method = RequestMethod.GET)
	public String signUp() {
		//회원가입 화면으로 포워드 (singUp.jsp)
		//포워드 방법 : return에 포워드할 주소 입력 (servlet-context에 prefix,suffix확인 후 작성)
		return "member/signUp";
	}
	
	
	//id 중복검사 
	@RequestMapping(value = "idDupCheck", method = RequestMethod.GET)
	@ResponseBody
	//@ResponseBody:메소드에서 반환 값이 주소가아닌 값 자체를 나타냄(주로 ajax를 이용한 데이터 응답시 사용)
	public int idDupCheck(String inputId) {
		//매개변수에 null 값이 들어오지 않을때 @RequestParam을 생략 가능
		
		//service 호출 후 결과 반환
		int result = service.idDupCheck(inputId);
		
		return result;
		// return 값을 포워드(servlet-context에 prefix,suffix)하기 때문에 404에러 발생 
		// 값 자체 반환을 명시 (@ResponseBody)
	}
	
	//email 중복검사
	@RequestMapping(value = "emailDupCheck", method=RequestMethod.GET)
	@ResponseBody
	public int emailDupCheck(String inputEmail) {
		
		int result = service.emailDupCheck(inputEmail);
		
		return result;
	}

	@RequestMapping(value = "signUp", method= RequestMethod.POST)
	public String signUp(Member member, RedirectAttributes ra) {
					   //->커멘드 객체 
		//overroading :같은 이름의 메소드라도 적제가능 

		int result = service.signUp(member);
		
		//회원가입 결과 메세지 전달용 변수
		String title;
		String text;
		String icon;
		
		if(result >0) {
			//회원가입 성공 
			title ="회원가입 성공";
			text = member.getMemberName()+"님 환영합니다.";
			icon = "success";
			
		}else{
			//회원가입 실패 
			title ="회원 가입 실패";
			text = "관리자에게 문의해주세요";
			icon = "error";			
		}
		
		ra.addFlashAttribute("title",title);
		ra.addFlashAttribute("text",text);
		ra.addFlashAttribute("icon",icon);
		
		//에러처리
		
		return "redirect:/";
	}
	
	//마이페이지 화면 전환 
	@RequestMapping(value = "myPage", method = RequestMethod.GET)
	public String myPage() {
		return "/member/myPage";
	}
	
	//회원정보수정 
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public String updateMember(@ModelAttribute("loginMember") Member loginMember,
								@RequestParam Map<String, String> param, //모든 파라미터가 Map형식으로 저장됨
								Member member, RedirectAttributes ra) { //비어있는 멤버 객체 생성

		//회원정보 수정 시 필요한 값 
		//1. 입력된 파라미터인 (이메일, 전화번호, 주소)
		//2. 수정할 회원의 회원번호(=로그인한 회원의 회원번호 / session에서 얻어오기)
		
		//수정할 파라미터의 name이 같을 경우 값입력시 원본 데이터의 손상이 일어남
		//session만 변경되는 문제가 발생 
		//수정할 파라미터의 name값을 다르게 지정 
		
		// @ModelAttribute 
		//1.파라미터 set
		//2.@sessionAttribute를 이용해 등록된 Session데이터를 얻어오는 역할 
		//  Session에서 key가 일치하는 값을 얻어와 해당 컨트롤러 내에서 사용 가능하게 한다.
		//  @ModelAttribute("session 키값") 작성
	
		//member에 회원번호, 수정된 파라미터 담기 
		member.setMemberNo(loginMember.getMemberNo());
		member.setMemberEmail(param.get("updateEmail"));
		member.setMemberPhone(param.get("updatePhone"));
		member.setMemberAddress(param.get("updateAddress"));
		
		//회원정보 수정 Service 호출 
		int result = service.updateMember(member);
		
		//회원가입 결과 메세지 전달용 변수
		String title = null;
		String text = null; 
		String icon = null;
		
		if(result > 0) {//수정성공
			title = "수정 완료";
			text = "회원 정보를 성공적으로 수정하였습니다.";
			icon = "success";
			// session의 loginMember 수정 
			
			loginMember.setMemberEmail(param.get("updateEmail"));
			loginMember.setMemberPhone(param.get("updatePhone"));
			loginMember.setMemberAddress(param.get("updateAddress"));
			
		}else {//실패 
			title = "수정 실패.";
			text = "정보 수정에 실패하였습니다.";
			icon = "error";
		}
		
		ra.addFlashAttribute("title",title);
		ra.addFlashAttribute("text",text);
		ra.addFlashAttribute("icon",icon);
		
		//에러처리
		
		return "redirect:myPage";
		
	}
	
	//비밀번호 수정 화면 전환 
	@RequestMapping(value = "updatePw", method = RequestMethod.GET)
	public String updatePw() {
		
		return "member/updatePw"; 
	}
	
	@RequestMapping(value = "updatePw", method= RequestMethod.POST)
	public String  updatePw(@ModelAttribute("loginMember") Member loginMember, String currentPw, String newPw1,
							RedirectAttributes ra ) {
		
		//비밀번호 수정 
		//1.Service(회원번호 + 현재 비밀번호 + 새 비밀번호) 호출  
		//2. DB에 저장된 비밀 번호 조회 
		//3. DB 저장된 비밀번호와 입력된 현재 비밀번호 비교 (matches 이용)
		//4. 일치시 새 비밀번호 암호화 , 불일치 시 컨트롤러로 return
		//5. 비밀번호 변경 DAO호출 후 값 controller로 반환
		loginMember.setMemberPw(newPw1);
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("memberNo", loginMember.getMemberNo()+"");
		map.put("currentPw", currentPw);
		map.put("newPw", newPw1);
		
		int result = service.updatePw(map);
		
		if (result > 0) {
			Util.swalSetMessage("비밀번호 변경 성공", "비밀번호가 변경되었습니다.", "success", ra);
			
		}else {
			Util.swalSetMessage("비밀번호 변결 실패", "관리자에게 문의해주세요", "error", ra);
		}
		
		return "redirect:myPage";
	}
	
	//회원탈퇴 화면 전환 
	@RequestMapping(value = "secession", method = RequestMethod.GET)
	public String secession() {
		
		return "member/secession"; 
	}
	
	
	//회원탈퇴 
	@RequestMapping(value = "secession", method = RequestMethod.POST)
	public String secession(@ModelAttribute("loginMember") Member loginMember, String currentPw, RedirectAttributes ra,SessionStatus status) {
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("memberNo", loginMember.getMemberNo()+"");
		map.put("currentPw", currentPw);
		
		int result = service.secessionMember(map);
		
		if (result > 0) {
			Util.swalSetMessage("회원탈퇴 ", "이용해 주셔서 감사합니다.", "success", ra);
			
			//sessiom에서 삭제
			status.setComplete();
			
		}else {
			Util.swalSetMessage("회원탈퇴 실패", "관리자에게 문의해주세요", "error", ra);
		}
		
		return "redirect:/"; 
	}
	/* Spring 예외 처리 방법 
	 * 1.메소드별 try-catch/throws 예외처리 (1순위 : 가장먼저 처리됨) 
	 * 
	 * 2. Controller별 예외처리 : @ExceptionHandler(처리할 예외.class) (2순위)
	 * 		-> DispatcherSevlet에 <annotation-driven /> 필수 작성 (자동생성됨)
	 * 
	 * 3. 전역(프로젝트 내의 모든 클래스)에서 발생하는 예외를 하나의 클래스에서 처리 : @ControllerAdvidce (3순위)
	 * */
	
	@ExceptionHandler(Exception.class)
	public String exceptionHandler(Exception e, Model md) {
		
		//Model : 데이터 전달용 객체(Map형식, request범위)
		
		md.addAttribute("errorMessage", "회원 관련 서비스 이용 중 문제가 발생했습니다.");
		md.addAttribute("e", e);
		
		return "/common/error";
	}
	

	

}