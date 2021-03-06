package edu.kh.fin.member.model.service;

import java.util.Map;

import edu.kh.fin.member.model.vo.Member;

// 인터페이스 : 모든 메소드가 추상 
// 필드 : public static final
// 메소드 : public abstract(+ default 메소드 가능)

//Service에서 interface를 사용하는 이유 
//1.프로젝트의 규칙성을 부여하기 위해서 
//		인터페이스 상속-> 오버라이딩 강제화 -> 상속받은 모든 클래스가 동일한 형태

//2. 유지보수, 수정에 용이
//		수정에 용이/인터페이스를 상속받은 클래스에 수정코드 작성 -> 부모인터페이스 = 자식객체(다형성/ 업캐스팅); 상태에서 대입되는 객채만 교체

//3.클래스간의 결합도를 낮
//4. Spring AOP 
//		AOP는 spring-proxy를 이용해 동작하는데 spring-proxy 객체는 Service인터페이스를 상속 받아 동작 
public interface MemberService {
	
	/** 로그인 
	 * @param member
	 * @return
	 */
	Member login(Member member);

	/** id 중복검사
	 * @param inputId
	 * @return
	 */
	int idDupCheck(String inputId);

	/**이메일 중복 검사
	 * @param inputEmail
	 * @return
	 */
	int emailDupCheck(String inputEmail);

	int signUp(Member member);

	int updateMember(Member member);

	int updatePw(Map<String, String> map);

	int secessionMember(Map<String, String> map);
}
