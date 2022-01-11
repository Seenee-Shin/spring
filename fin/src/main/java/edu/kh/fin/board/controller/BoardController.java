package edu.kh.fin.board.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.fin.board.model.service.BoardService;
import edu.kh.fin.board.model.vo.Board;
import edu.kh.fin.board.model.vo.Category;
import edu.kh.fin.board.model.vo.Pagination;
import edu.kh.fin.common.Util;
import edu.kh.fin.member.model.vo.Member;

@Controller //컨트롤러임을 ㅇ명시 + bean생성으로 spring에서 관리가능 
@RequestMapping("/board/*") //
@SessionAttributes("loginMember") 
//model 속성 추가시 key값이 일치하는 값을 session영역으로 이동 
//Session에서 key가 일치하는 값을 얻어와 해당 컨트롤러 내에서 사용가능하게 함
//  ->@ModelAtrribute("loginMember")를 얻어와 사용가능하게 함
public class BoardController {
	
	@Autowired
	private BoardService service;
	

	
	@RequestMapping("list")	  		 // cp(페이징 파라미터) 전달받기 
	public String selectBoardList(@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
								  Model md) {
		
		//페이징 처리용 객체 Pagination 생성 
		//1.전체게시글 수 count + 페이징에 필요한 값 계산 
		Pagination pagination = service.getPagination(cp);
		
		//System.out.println(pagination);
		
		//지정된 범위의 게시글 목록 조회 
		List<Board> boardList = service.selectBoardList(pagination);
		
		md.addAttribute("pagination", pagination);
		md.addAttribute("boardList", boardList);
		
		return "board/boardList";
	}

	
	/*@PathVariable : URL경로상에 잇는 값을 파라미터로 사용할 수 있게 하는 어노테이션
	 * 
	 * Pathvariable : 자원을 식별하는 용도의 값 (파라미터)
	 * 
	 * QueryString : 필터링 (검색, 정렬, 현재페이지)
	 * 
	 * */
	
	//게시글 상세조회 
	@RequestMapping("view/{boardNo}")
	public String selectBoard(@PathVariable("boardNo") int boardNo,
								@RequestParam(value = "cp", required= false, defaultValue = "1") int cp,
								Model md , RedirectAttributes ra, @ModelAttribute("loginMember") Member loginMember, HttpSession session) {
		int memberNo= 0;
		
		if(session.getAttribute("loginMember") != null) {
			memberNo = ((Member)session.getAttribute("loginMember")).getMemberNo();
		}
		
		Board board = service.selectBoard(boardNo, loginMember.getMemberNo());
		
		String path = null;
		
		//조회 결과에 따른 처리
		if(board != null) { //게시글이 존재할 때 
			md.addAttribute("board",board);
			path = "board/boardView";
			
		}else {
			Util.swalSetMessage("해당 글이 존재하지 않습니다.", null , "info", ra);
			path = "redirect:../list";
		}
		
		return path;
		
		
	}
	
	@RequestMapping(value = "insert", method = RequestMethod.GET)
	public String boardInsert(Model model) {
		
		List<Category> category = service.selectCategory();
		model.addAttribute("category", category);
		
		return "board/boardInsert";
	}
	
	//게시글 작성 
	@RequestMapping(value = "insert", method = RequestMethod.POST)
	public String boardInsert(Board board /*커멘드 객체*/, 
			@RequestParam(value="images", required = false) List<MultipartFile> images, /*upload file*/
			@ModelAttribute("loginMember")Member loginMember, /*session login info*/
			HttpSession session /*file 저장 경로 */, RedirectAttributes ra) {
		
		/*
		 * multipartFile 이 제공하는 메소드
		 * - getOriginalFilename() : 원본 파일명
		 * - getSize() : 파일 크기
		 * - getInputStream() : 파일에 대한 입력 스트림
		 * - transferTo() : **메모리에 저장된 파일과 연결된 MultipartFile객체를 메소드 호출 시 연결된 메모리 파일을 디스크로 저장
		 * 
		 * */
		//로그인 회원 번호를 board에 세팅
		board.setMemberNo(loginMember.getMemberNo());
		
		//웹 접근경로(web path), 서버 저장경로(serverPath)
		String webPath = "/resources/images/board/";
		
		String serverPath= session.getServletContext().getRealPath(webPath);
		
		//게시글 작성 후 상세 조회(DB에 입력된 게시글)할 boardNo 
		int boardNo = service.insertBoard(board, images, webPath, serverPath);
		
		String path = null; 
		if(boardNo > 0) { // insert 성공 
			Util.swalSetMessage("게시글 등록 성공", null, "success", ra);
			path = "view/"+boardNo;
		}else {
			Util.swalSetMessage("게시글 등록 실패", null, "error", ra);
			path = "insert";
			
		}
		
		return "redirect:"+path;
	}
	
	
	//게시글 수정 화변 전환 
	@RequestMapping(value = "updateForm", method = RequestMethod.POST)
	public String updateForm(int boardNo, @ModelAttribute("loginMember")Member loginMember, Model md){ 
		//카테고리 목록 조회 
		List<Category> category = service.selectCategory();
		
		// 게시글 상세 조회 
		Board board = service.selectBoard(boardNo, loginMember.getMemberNo());
		
		md.addAttribute("category",category);
		md.addAttribute("board",board);
		
		return "board/boardUpdate";
	}
}


