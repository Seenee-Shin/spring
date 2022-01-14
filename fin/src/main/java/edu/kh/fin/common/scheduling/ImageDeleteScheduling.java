package edu.kh.fin.common.scheduling;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import edu.kh.fin.board.model.service.BoardService;

@Component //Bean등록
public class ImageDeleteScheduling {
/*
    * * cron 속성 : UNIX계열 잡 스케쥴러 표현식으로 작성 - cron="초 분 시 일 월 요일 [년도]" - 요일 : 1(SUN) ~ 7(SAT) 
    * ex) 2019년 9월 16일 월요일 10시 30분 20초 cron="20 30 10 16 9 2" // 연도 생략 가능
    * 
    * - 특수문자 * : 모든 수. 
    * - : 두 수 사이의 값. ex) 10-15 -> 10이상 15이하 
    * , : 특정 값 지정. ex) 3,4,7 -> 3,4,7 지정 
    * / : 값의 증가. ex) 0/5 -> 0부터 시작하여 5마다 
    * ? : 특별한 값이 없음. (월, 요일만 해당) 
    * L : 마지막. (월, 요일만 해당)
    * 
    * * 주의사항 - @Scheduled 어노테이션은 매개변수가 없는 메소드에만 적용 가능.
*/
	@Autowired
	private ServletContext servletContaxt;
	//서블릿 컨테이너 -> 요청 응답의 제어 
	
	@Autowired
	private BoardService boardService;
	
	@Scheduled(cron = "0 * * * * *") //0초가 될떼 = 1분 마다 
//	@Scheduled(cron = "0 0 * * * *") //정시 마다 
//	@Scheduled(cron = "0 0 0 * * *") //자정 마다
	public void imageDelete() {
		String serverPath =  servletContaxt.getRealPath("/resources/images/board");
		//이미지가 저장되어 있는 실제 경로 
		
		//지정된 경로에 있는 모든 파일 리스트를 File 배열로 반환 
		//File 객체 : 파일을 참조할 수 있는 객체 
		File[] imgArr = new File(serverPath).listFiles();
		
		//배열을 리스트로 변환 
		List<File> serverImgList = Arrays.asList(imgArr);
		
		//이미지가 저장된 폴더에 있는 파일을 확인
//		for(File img: serverImgList) {
//			System.out.println(img);
//		}
		
		//DB에 파일명 목록 조회
		List<String> dbImgList =boardService.selectImgList();
		
		//serverImgList : 서버에 저장된 파일 목록 
		//dbImgList : Db에 저장된 파일명 목록 
		if(!serverImgList.isEmpty() && !dbImgList.isEmpty()) {
			//서버에 파일이 있을 경우 && 데이터베이스에 파일이 있을때 
			
			//서버에 저장된 파일 목록을 순차 접근
			for(File img : serverImgList) {
				//접근한 파일에 파일명만 추출
				String serverImgName = img.toString().substring(img.toString().lastIndexOf("\\") + 1);
				//img.toString() : 경로 + 파일명
				//toString().substring(index) : 문자열 시작부터 지정된 index이전까지 문자열을 모두 삭제
				
				if(dbImgList.indexOf(serverImgName) == -1) {
					//db파일명 목록에 server파일명이 없으면 해당파일 삭제
					System.out.println(serverImgName + "삭제");
					img.delete();
				}
			}
		}
		
	}
}
