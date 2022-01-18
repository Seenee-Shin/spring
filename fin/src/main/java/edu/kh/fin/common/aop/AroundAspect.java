package edu.kh.fin.common.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect // Advice + Pointcut 
@Component
public class AroundAspect {
	private Logger logger = LoggerFactory.getLogger(AroundAspect.class);
	
	//전처리, 후처리를 모두 해결하고자 할 때 사용하는 관점
	// ProceedingHoinPoint 를 매개변수 필수 사용 -> proceed()메소드 사용가능
	@Around("PointcutCollection.serviceImplPointcut()")
	public Object aroundLog(ProceedingJoinPoint pp) throws Throwable {
		// 클래스명
		String className = pp.getTarget().getClass().getSimpleName(); // 대상 객체의 간단한 클래스명(패키지명 제외)
  
		// 메소드명
		String methodName = pp.getSignature().getName(); // 대상 객체 메소드의 정보 중 메소드명을 반환. 
		String str = "";
  
  
		str += "[Service]" +  className + "-" + methodName + "()";

		long startMs = System.currentTimeMillis(); // 서비스 시작 시의 ms 값

		
		Object obj = pp.proceed();
		
		long endMs = System.currentTimeMillis();
		
		str += "[Running Time]" + (endMs - startMs) + "ms";
		
		str += "[Param]"+Arrays.toString(pp.getArgs());
		
		logger.debug(str);
		
		return obj;
	}
}
