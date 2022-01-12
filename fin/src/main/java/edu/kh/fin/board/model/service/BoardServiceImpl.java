package edu.kh.fin.board.model.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.fin.board.model.dao.BoardDAO;
import edu.kh.fin.board.model.exception.InsertBoardFailException;
import edu.kh.fin.board.model.exception.UpdateBoardFailException;
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

	/**
	 * 수정화면 전환용 
	 */
	@Override
	public Board selectBoard(int boardNo) {
		
		Board board = dao.selectBoard(boardNo);
		
		//개행 문자 역변경
		board.setBoardContent(Util.changeNewLine2(board.getBoardContent()));
		
		return board;
	}
	
	// 게시글 수정
	   @Transactional
	   @Override
	   public int updateBoard(Board board, List<MultipartFile> images, String webPath, String serverPath, String deleteImages) {
	      
	      // 1) 게시글 제목/ 내용  XSS 처리
	      board.setBoardTitle(Util.XSS(board.getBoardTitle()));
	      board.setBoardContent(Util.XSS(board.getBoardContent()));
	      // 1-1) 개행문자 처리
	      board.setBoardContent(Util.changeNewLine(board.getBoardContent()));
	      
	      // 2) 게시글 부분 수정 진행 - dao 호출
	      int result = dao.updateBoard(board);
	      
	      // 3) 기존에 있었지만 삭제된 이미지 DELETE 처리 진행 
	      if(result > 0) {
	         // 마이바티스는 SQL 수행 시 파라미터를 1개만 받을 수 있다.
	         // 전달할 파라미터가 다수인 경우 Map과 같은 컬렉션 객체를 이용하면 된다.
	         
	         if(!deleteImages.equals("")) { // 삭제할 이미지가 있을 경우 
	            Map<String, Object> map = new HashMap<String, Object>();
	            map.put("boardNo", board.getBoardNo()); // boardNo는 int -> autoBoxing ->Integer -> Object
	            map.put("deleteImages", deleteImages); // String -> Object
	   
	            result = dao.deleteImages(map);
	      
	         }
	      }
	      
	      // 4) images에 담겨있는 파일 정보 중 업로드 된 파일 정보를 imgList에 옮겨 담기
	      if(result > 0){
	         
	         List<BoardImage> imgList = new ArrayList<BoardImage>();
	         
	         for(int i =0; i<images.size(); i++) {
	            // i == images인덱스 == imgLevel
	            
	            // 업로드 된 파일이 있는 경우
	            if( !images.get(i).getOriginalFilename().equals("") ) { // 빈칸이 아니라면
	               
	               BoardImage img = new BoardImage(); //보드이미지 객체에 바뀐내용들을 담는다
	               
	               img.setImgPath(webPath); // 웹 접근 경로
	               img.setImgName(Util.fileRename(images.get(i).getOriginalFilename())); // 변경된 파일명
	               img.setImgOriginal( images.get(i).getOriginalFilename()); // 원본 파일명
	               img.setImgLevel(i); // 이미지 레벨
	               img.setBoardNo( board.getBoardNo()); // 게시글 번호
	               
	               imgList.add(img); // 옮겨진 보드이미지 내용을 imgList에 넣는다
	               
	            }
	         
	         } // for end
	         
	         //  5) imgList가 비어있지 않은 경우
	         // imgList에 비어있는 내용을 update 또는 insert
	         
	         // 향상된 for문으로 반복 접근할 List가 비어있다면 for문은 수행되지 않는다.
	         for(BoardImage img : imgList) {
	            
	            // 서로 다른 행을 일괄적으로 update하는 방법이 없기때문에
	            // 한 행씩 수정
	            result = dao.updateBoardImage(img); // 한 행씩 접근해서 업데이트한다
	            // 결과 1 -> 기존에 저장된 이미지가 수정되었다.
	            // 결과 0 -> 기존에 저장되지 않은 이미지가 추가 되었다 ->insert 진행
	            
	            // 새로운 이미지가 추가되었다 -> INSERT
	            if(result == 0) {
	               result = dao.insertBoardImage(img);
	               
	               if(result == 0) {
	                  // 사용자 정의 예외
	                  throw new UpdateBoardFailException("이미지 삽입 중 문제가 발생하였습니다.");
	               }
	            }
	         
	         }// for end
	         
	         // 6) 전달 받은 images 중 업로드된 파일이 있는 부분을 실제 파일로 저장 ( 전달 받은 images 는 imgList에 저장되어있다)
	         if(!imgList.isEmpty()) { // 파일이 있다면  꺼내서저장한다
	            
	            try { 
	               for(int i =0; i<imgList.size(); i++) {
	                  
	                  // i번째 인덱스가 아니라 imgList.get(i).getImgLevel()의 번호를 얻어온다
	                  images.get(imgList.get(i).getImgLevel())
	                  .transferTo(new File(serverPath + "/" + imgList.get(i).getImgName() ));
	                  
	               }
	               
	            } catch (Exception e) {
	               e.printStackTrace();
	               
	               // Runtime 예외가 발생할까봐  예외처리한것
	               // 사용자 정의 예외(RuntimeException)
	               throw new UpdateBoardFailException();
	            }
	            
	            
	         }
	         
	         
	      } // if end
	      
	      return result;
	   }

	@Override
	public int deleteBoard(int boardNo) {
		
		return dao.deleteBaord(boardNo);
	}	
	

}

