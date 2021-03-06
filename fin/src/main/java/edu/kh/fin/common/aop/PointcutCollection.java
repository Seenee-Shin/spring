package edu.kh.fin.common.aop;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Pointcut;

public class PointcutCollection {
	
	@Pointcut("execution(* edu.kh.fin..*Controller.*(..))")
	public void controllerPointcut() {}
	
	@Pointcut("execution(* edu.kh.fin..*ServiceImpl.*(..))")
	public void serviceImplPointcut() {}
	
	

}
