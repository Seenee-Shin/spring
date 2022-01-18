package edu.kh.fin.common.aop;


import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import edu.kh.fin.member.model.vo.Member;

//@Aspect : 공통적으로 특정 위치에서 사용할 기능 클래스

@Aspect //공통 관심사가 작성된 클래스임을 명시(해당 클래스 advice,Pointcut이 작성되어있어야함)
@Component //runtime 중  advice 코드가 동적으로 추가 될 수 있도록 스프링이 제어가능한 bean으로 등록 
public class BeforeAspect {
	private Logger logger = LoggerFactory.getLogger(BeforeAspect.class);
	//Join Point : Adivce가 가능한 모든지점  
	//Pointcut : JoinPoint 중 특정지정을 지절 -> Advice 적용부분
		//작성법 
		//1. 직접작성
		// -> execution(접근제한자(생략가능)  리턴타입  패키지명+클래스명.메소드명(매개변수) )
		// * : 모든 값
		// .. : 하위 모든 패키지 || 0개 이상의 매개변수
	
	
		//2. Pointcut이 작성된 메소드 작성
		//3. 타 클래스에 작성된 메소드 작성
	
	
	//@Before("Pointcut") : Pointcut으로 지정된 메소드가 수행되기전 Adivce 수행
	
	
	//모든 컨트롤러 메소드 수행전
//	 @Before("execution(* edu.kh.fin..*Controller.*(..))")  //직접 작성법 
	@Before("PointcutCollection.controllerPointcut()") //직접 작성법 
	public void controllerLog(JoinPoint jp) {
		 
		 //joinPoint : advice가 적용 될 수있는 모든 지점
		 //joinPoint 인터페이스 : 부가기능을 제공하는 인터페이스
		 // -> Before, After, Around 등 모든 관점 수행시 매개변수로 JoinPoint 인터페이스를 구현한 객체를 전달 받음
		 
		
		//logger.debug(Arrays.toString(jp.getArgs()));
		//jp.getArgs() : 수행된 매개변수를 모두 배열로 얻어오기
		
		String className = jp.getTarget().getClass().getSimpleName();
		//jp.getTarget() : 타겟이 된 객체를 얻어옴
		
		String methodName = jp.getSignature().getName();
		//getSignature():수행되려는 메소드 선언부 
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		
		
		Member loginMember = (Member)request.getSession().getAttribute("loginMember");
		//로그인된 회원정보 얻어오기, 없으면 null
		
		
		String str = "";
		
		//ip 주소 얻어오기 
		str += "[ip]"+ getRemoteAddr(request);
		
		//로그인이 되어있는 경우 id 추가 
		if (loginMember != null) {
			str += "(id:" + loginMember.getMemberId() + ")" ;
		}
		
		str += "[Controller]"+className + "." + methodName;
		
		logger.info(str);
	}
	 
	 
	   public static String getRemoteAddr(HttpServletRequest request) {

	        String ip = null;

	        ip = request.getHeader("X-Forwarded-For");

	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	            ip = request.getHeader("Proxy-Client-IP"); 
	        } 

	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	            ip = request.getHeader("WL-Proxy-Client-IP"); 
	        } 

	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	            ip = request.getHeader("HTTP_CLIENT_IP"); 
	        } 

	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	            ip = request.getHeader("HTTP_X_FORWARDED_FOR"); 
	        }

	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	            ip = request.getHeader("X-Real-IP"); 
	        }

	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	            ip = request.getHeader("X-RealIP"); 
	        }

	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	            ip = request.getHeader("REMOTE_ADDR");
	        }

	        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	            ip = request.getRemoteAddr(); 
	        }

	      return ip;

	   }
}
