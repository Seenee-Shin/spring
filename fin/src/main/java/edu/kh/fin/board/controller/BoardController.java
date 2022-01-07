package edu.kh.fin.board.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.fin.board.model.service.BoardService;
import edu.kh.fin.board.model.vo.Board;
import edu.kh.fin.board.model.vo.Pagination;
import edu.kh.fin.common.Util;

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
								Model md , RedirectAttributes ra) {
		Board board = service.selectBoard(boardNo);
		
		String path = null;
		
		//조회 결과에 따른 처리
		if(board != null) {
			md.addAttribute("board",board);
			path = "board/boardView";
			
		}else {
			Util.swalSetMessage("해당 글이 존재하지 않습니다.", null , "info", ra);
			path = "redirect:../list";
		}
		
		return path;
		
		
	}
}