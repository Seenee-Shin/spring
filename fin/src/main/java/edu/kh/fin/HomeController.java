package edu.kh.fin;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.kh.fin.member.model.vo.Member;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	// 최상위주소 "/" 요청시 제어 컨트롤러 
//	@RequestMapping(value = "/", method = RequestMethod.GET)
//	public String home(Locale locale, Model model) {
//		logger.info("Welcome home! The client locale is {}.", locale);
//		
//		Date date = new Date();
//		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
//		
//		String formattedDate = dateFormat.format(date);
//		
//		model.addAttribute("serverTime", formattedDate );
//		
//		return "home";
//	}
	
	@RequestMapping(value="/" , method = RequestMethod.GET)
	public String mainforward() {
		//로그 남기기 
//		logger.debug("메인페이지 접속");
//		logger.info("메인페이지 접속");
//		logger.warn("메인페이지 접속");
//		logger.error("메인페이지 접속");
		return "common/main";
	}
	
	@RequestMapping(value="/loginTest",method = RequestMethod.POST)
	public String loginTest(@ModelAttribute Member member) {
		//매개변수에 어노테이션으로 파라미터를 가져옴, 만약 파라미터와 변수의 이름이 같으면 어노테이션 생략가능
		//@ModelAttribute vo의 필드변수명과 파라미터의 name이 같으면 자동으록 변환

		System.out.println(member);
		
		return null;
	}
	
}
