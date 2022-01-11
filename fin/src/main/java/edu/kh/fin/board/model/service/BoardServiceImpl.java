package edu.kh.fin.board.model.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.fin.board.model.dao.BoardDAO;
import edu.kh.fin.board.model.exception.InsertBoardFailException;
import edu.kh.fin.board.model.vo.Board;
import edu.kh.fin.board.model.vo.BoardImage;
import edu.kh.fin.board.model.vo.Category;
import edu.kh.fin.board.model.vo.Pagination;
import edu.kh.fin.common.Util;

@Service
public class BoardServiceImpl implements BoardService{

	@Autowired
	private BoardDAO dao;

	@Override
	public Pagination getPagination(int cp) {
		
		//전체 게시글 수 count
		int listCount = dao.getListCount();
		
		 return new Pagination(listCount, cp);
	}

	@Override
	public List<Board> selectBoardList(Pagination pagination) {
		return dao.selectBoardList(pagination);
	}

	@Override
	public Board selectBoard(int boardNo, int memberNo) {
		Board board = dao.selectBoard(boardNo);
		
		if(board != null && board.getMemberNo() != memberNo) { //게시글이 있음 + 보고있는 게시글의 작성자가 로그인한 멤버가 아닐경우
			//조회수 증가 
			int result = dao.increaseReadCount(boardNo);
			
			if(result > 0) {
				//DB와 미리 조회된 board의 readCount 동기화
				board.setReadCount(board.getReadCount()+1);
			}
		}
		
		return dao.selectBoard(boardNo);
	}

	@Override
	public List<Category> selectCategory() {
		
		return dao.selectCategory();
	}

	
	//게시글 , 이미지 insert
	@Transactional //예외발생 시 rollback
	@Override 
	public int insertBoard(Board board, List<MultipartFile> images, String webPath, String serverPath) {
		// title, content xss 방지 
		board.setBoardTitle(Util.XSS(board.getBoardTitle()));
		board.setBoardContent(Util.XSS(board.getBoardContent()));
		
		// content 개행문자 변경 
		board.setBoardContent(Util.changeNewLine(board.getBoardContent()));
		
		
		
		//DB insert DAO 수행후 반환(boardNo)
		// -> insert 후 PK값을 얻어오는 useGeneratedKeys,selectKey 사용 
		
		int boardNo = dao.insertBoard(board);
		
		System.out.println("insert boardNo : "+ boardNo);
		
		if(boardNo > 0) {
			//실제 업로드도니 이미지를 분별하여 list<Boardimages> imgList에 담기
			List<BoardImage> imgList = new ArrayList<BoardImage>();
			
			for(int i = 0 ; i<images.size(); i++) {
				//i == images index == level
				
				//각 인덱스 요소에 파일이 업로드 되었는지 검사
				if(!images.get(i).getOriginalFilename().equals("")) {
					//업로드가 된 경우 MultipartFile에서 DB저장에 필요한 데이터를 추출 -> add BoardImage -> add imgList
					
					BoardImage img = new BoardImage();
					img.setImgPath(webPath); //web access
					img.setImgOriginal(images.get(i).getOriginalFilename()); //OriginalFileName
					//image rename
					img.setImgName(Util.fileRename(images.get(i).getOriginalFilename()));
					img.setImgLevel(i);
					img.setBoardNo(boardNo); //return from dao 
					
					imgList.add(img); //add to List
				}// end if
			}//end for
			
			//upload images if there are infomation about imgList
			if(!imgList.isEmpty()) {
				int result = dao.insertImgList(imgList);
				
				//삽입 성공한 행의 개수와 imgList 개수가 같을 경우 파일을 서버에 저장 
				//1순위로 확인할 것 : servers -> fin server -> Overview -> serve module 확인 
				
				
				// images : MultipartFile List : 실제 파일 + 정보 
				// imgList : BoardImage List, DB에 저장할 파일 정보
				if(result == imgList.size()) {
					for(int i = 0; i <imgList.size(); i++) {
						
						//업로드된 파일이 있는 images의 인덱스 요소를 얻어와 지정된 경로와 이름으로 파일로 변환하여 저장
						try {
							images.get(imgList.get(i).getImgLevel())
							.transferTo(new File(serverPath+"/"+ imgList.get(i).getImgName()));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							
							throw new InsertBoardFailException("파일 변환중 문제 발생");
							
							//파일 변환이 실패할 경우 사용자 정의 예외 발생
						}
					}
				}else {
					//업로드된 이미지 수와 삽입된 행의 수가 다를경우 
					//사용자 정의 예외 발생
				  throw new InsertBoardFailException();
				  
				}
			}
		}
		return boardNo;
	}
}
