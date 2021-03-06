package edu.kh.fin.board.controller;

import java.awt.Image;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
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
import edu.kh.fin.board.model.service.ReplyService;
import edu.kh.fin.board.model.vo.Board;
import edu.kh.fin.board.model.vo.Category;
import edu.kh.fin.board.model.vo.Pagination;
import edu.kh.fin.board.model.vo.Reply;
import edu.kh.fin.board.model.vo.Search;
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
	
	@Autowired //만들어진 Bean 의존성 주입
	private ReplyService replyService;
	

	//게시글 목록 조회
	@RequestMapping("list")	  		 // cp(페이징 파라미터) 전달받기 
	public String selectBoardList(@RequestParam(value="cp", required = false, defaultValue = "1") int cp,
								  Model md, Search search) {
		
		Pagination pagination = null;
		List<Board> boardList = null;
		
		//검색 값이 있는 경우
		if(search.getCt() != null || ( search.getSv() != null && !search.getSv().trim().equals("")) ) {
		
			//검색 조건에 맞는 전체 게시글 수 count + 페이징 처리 계산
			pagination = service.getPagination(cp,search);
			
			boardList = service.selectBoardList(pagination, search);	
			
		} else {
			//페이징 처리용 객체 Pagination 생성 
			//1.전체게시글 수 count + 페이징에 필요한 값 계산 
			pagination = service.getPagination(cp);
			
			//지정된 범위의 게시글 목록 조회 
			boardList = service.selectBoardList(pagination);
		}
		
		List<Category> category = service.selectCategory();
		md.addAttribute("category", category);
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
								Model md , RedirectAttributes ra, HttpSession session
								/* @ModelAttribute("loginMember") Member loginMember*/) {
		// @SessionAttributes 
		// 1) Model.addAttribute(K,V) 수행 시 K가 일치하는 값을 Request -> Session scope로 이동
		// 2) Model.addAttribute(K,V) Session으로 이동한 값을 얻어와
		//		-> @ModelAttribute("K")에 전달
		
		int memberNo= 0;
		
		//session에 login member가 있을 경우 
		if(session.getAttribute("loginMember") != null) {
			memberNo = ((Member)session.getAttribute("loginMember")).getMemberNo();
		}
		
		Board board = service.selectBoard(boardNo,memberNo);
		
		String path = null;
		
		//조회 결과에 따른 처리
		if(board != null) { 
			//댓글 목록 조회 Service호출 
			//List<Reply> rList = replyService.selectList(boardNo);
			
			//md.addAttribute("rList", rList);
			
			//게시글이 존재할 때 
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
	
	
	//게시글 수정 화면 전환 
	@RequestMapping(value = "updateForm", method = RequestMethod.POST)
	public String updateForm(int boardNo, Model md){ 
		//카테고리 목록 조회 
		List<Category> category = service.selectCategory();
		
		// 게시글 상세 조회 
		Board board = service.selectBoard(boardNo);
		
		md.addAttribute("category",category);
		md.addAttribute("board",board);
		
		return "board/boardUpdate";
	}
	
	//게시글 수정
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public String updateBoard(@ModelAttribute("loginMember") Member loginMember, Board board,
			@RequestParam(value = "cp", required = false, defaultValue = "1") int cp,
			@RequestParam("deleteImages") String deleteImages, @RequestParam("images") List<MultipartFile> images,
			RedirectAttributes ra, HttpSession session) {
		
		//deleteImages 가 비어있을경우 "" (빈문자열) 로 넘어온다 
		
		//웹 접근경로(web path), 서버 저장경로(serverPath)
				String webPath = "/resources/images/board/";
				
				String serverPath= session.getServletContext().getRealPath(webPath);
		
		//2)게시글 수정 Service 호출
		int result = service.updateBoard(board,images,webPath,serverPath, deleteImages);
		
		
		String path = null;
		if(result > 0) {
			Util.swalSetMessage("게시글 수정 완료", null, "success", ra);
			path = "view/"+board.getBoardNo()+"?cp="+cp;
		}else {
			Util.swalSetMessage("수정 실패", null, "error", ra);
			path = "updateForm";
		}
		
		
		return "redirect:"+path;
	}
	
	//게시글 삭제 
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public String  deleteBoard(int boardNo, @RequestParam(value = "cp", required = false, defaultValue = "1")int cp,
			RedirectAttributes ra) {
		
		//삭제 service 호출
		int result = service.deleteBoard(boardNo) ;
		
		String path = null;
		
		if(result >0) {
			//삭제 성공 시 "list?cp=" + cp 로 리다이렉트 + 성공 메세지
			Util.swalSetMessage("게시글 삭제 성공", null, "success", ra);
			path = "list?cp="+cp;
		}else {
			//실패시 삭제하려던 글 상세조회 페이지로 리다이렉트 + 실패 메세지 
			Util.swalSetMessage("게시글 삭제 실패", null, "error", ra);
			path = "view/"+ boardNo + "list?cp" +cp;
		}
		return "redirect:"+path;
	}
	
	
	//
	
	
	
	
}


