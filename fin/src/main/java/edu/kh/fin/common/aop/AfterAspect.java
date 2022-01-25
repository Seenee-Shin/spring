package edu.kh.fin.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AfterAspect {
	private Logger logger = LoggerFactory.getLogger(AfterAspect.class);
	
	@After("PointcutCollection.controllerPointcut()")
	public void afterLog() {
		
		logger.info("--------------------------------------------"+ "\n");
		
	}
	
	@AfterReturning(pointcut = "PointcutCollection.serviceImplPointcut()",returning = "returnObj")
	public void afterReturningLog(JoinPoint jp, Object returnObj) {
		//어노테이션에 작성된 returning = "" 와 매개변수의 변수명이 같으면 타겟의 메소드 반환 값을 얻어와 사용할 수 잇음
		
		logger.debug("[Return]" + returnObj.toString());
	}
	
	//예외
	@AfterThrowing(pointcut = "PointcutCollection.controllerPointcut()",throwing = "e")
	public void afterthrowing(JoinPoint jp, Exception e) {
		String str = "[exception]";
		if (e instanceof NullPointerException) {
			str += "NUllPoint\n";
		}else if (e instanceof IllegalAccessException) {
			str += "부적절한 값\n";
		}else {
			str += "예외발생\n";
		}
		
		logger.error(e.toString());
		
	}
		
	
}
