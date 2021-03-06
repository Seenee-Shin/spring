package edu.kh.fin.member.model.service;

import java.beans.Encoder;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.fin.member.model.dao.MemberDAO;
import edu.kh.fin.member.model.vo.Member;

@Service //Service 비지니스로직임을 명시 + Bean 등록
public class MemberServiceImpl implements MemberService{

	@Autowired
	private MemberDAO dao;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	//Bean으로 등록된 BCryptPasswordEncoder 객체를 의존성 주입(DI)
	
	@Override
	public Member login(Member member) {
		System.out.println("서비스 Bean 등록 및 DI 성공");
		//BCrypt 암호화 원리 
		//평문에 추가적인 문자열을 임의로 붙여(salt)서 암호화를 진행 
		// 이를 비교할 수 있는 별도 메소드를 같이 제공 
		String encPw = encoder.encode(member.getMemberPw()); //평문을 암호화 
		
		System.out.println("암호화된 비밀번호 : " + encPw);
		

		//암호화된 비번 & 평문 비교하기
		//String temp = "$2a$10$mimg2fXVjFmBWYOz36eVMOrPEAQKaJQP/w6RL04zqzb2sVcktcfIu";
		//System.out.println(encoder.matches(member.getMemberPw(), temp));
				
		
		// 로그인 DAO호출 
		Member loginMember = dao.login(member.getMemberId());
		
		System.out.println(loginMember);
		
		//DB에 일치하는 아이디를 가진 회원이 있고 입력받은 비밀번호와 암호화된 비밀전호가 같을때 => 로그인 성공
		if(loginMember != null && encoder.matches(member.getMemberPw(), loginMember.getMemberPw())) {
			
			loginMember.setMemberPw(null);
			//DB에서 조회한 비밀번호 제거 (세션에는 비밀번호를 저장하지 않는다.)
		}else {
			
			loginMember =  null;
		}
		
		return loginMember;
	}

	/**
	 *아이디 중복 검사
	 */
	@Override
	public int idDupCheck(String inputId) {

		return dao.idDupCheck(inputId);
	}

	
	/**
	 *이메일 중복 검사
	 */
	@Override
	public int emailDupCheck(String inputEmail) {
		
		return dao.emailDupCheck(inputEmail);
	}

	/**
	 * 회원가입
	 */
	@Transactional(rollbackFor = Exception.class)  //rollbackFor:어떤 예외가 발생했을때 롤백을 실행할지 지정
	@Override
	public int signUp(Member member) {
		//비밀번호 암호화
		String encPw = encoder.encode(member.getMemberPw());
		member.setMemberPw(encPw);
		
		return dao.signUp(member);
		
		//트랜잭션 처리 @Transactional
		/* 스프링에서 트랜잭션을 처리하는 방법
		 * 
		 * 1. 코드 기반 처리방법 : 기존 commit, rollback
		 * 2. 선언적 트랜잭션 처리방법 
		 * 	  1) <tx:advice> XML방식
		 * 	  2) @Transational 어노테이션 방식
		 * 		-> transactionManager가 Bean으로 등록되어 있어야 함 (root-context.xml)
		 * 		   @transactional 어노테이션을 인식하기 위한 <tx:annotaion-driven>태그가 존재하여야함
		 * 
		 * @Transactional 어노테이션은 rollback을 위한 어노테이션 
		 * 커넥션 반환 시 아무런 트랜잭션 처리가 되어있지 않으면 자동 commit
		 * 
		 * @Transactional은 RuntimeException일 발생했을 때 rollback을 수행함
		 *  (exception:코드로 처리가능한 에러)
		 * */
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public int updateMember(Member member) {
		
		return dao.updateMember(member);
	}

	@Override
	public int updatePw(Map<String, String> map) {
		//암호화된 비밀번호 
		String savePw = dao.selectPw(map.get("memberNo"));
		
		//DB에 저장된 비밀번호와 입력된 현재 비밀번호 비교 
		int result =0;
		
		if(encoder.matches(map.get("currentPw"), savePw)) {
			//새로운 비밀번호 암호화 
			String encPw = encoder.encode(map.get("newPw"));
			
			//map의 newPW를 암호화ㅏ된 비밀번호로 덮어쓰기
			map.put("newPw",encPw);
			
			result = dao.updatePw(map);
		}
		
		return result;
	}

	@Override
	public int secessionMember(Map<String, String> map) {
		
		String checkPw = dao.selectPw(map.get("memberNo"));
		
		int result = 0;
		if(encoder.matches(map.get("currentPw"), checkPw)) {
			
			result = dao.secessionMember(map);
		}
		
		return result;
	}
	
	
	

}
