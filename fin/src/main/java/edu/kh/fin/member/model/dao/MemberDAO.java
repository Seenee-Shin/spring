package edu.kh.fin.member.model.dao;

import java.beans.Encoder;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.kh.fin.member.model.vo.Member;

//@Repository : Persistence layer, 영속성을 가지는 속성(파일, db)를 제어하는 클래스 + Bean으로 등록
@Repository
public class MemberDAO {
	
	@Autowired //bean으로 등록된 sqlSessionTemplate 객체 의존성 주임(DI)
	private SqlSessionTemplate sqlSession;
	
	
	/** 로그인
	 * @param memberId
	 * @return
	 */
	public Member login(String memberId) {
		
		Member loginMember = sqlSession.selectOne("memberMapper.login",memberId);
		//.selectOne("mapperName.tagId", parameter) : 특정 mapper의 id가 일치하는 1행 반환 select문 시행
		// + select문에 parameter사용
		//.selectList : 반환하는 값이 여러개일때 사용
		
		return loginMember;
	}


	/** id 중복 체크
	 * @param inputId
	 * @return int
	 */
	public int idDupCheck(String inputId) {
		
		return sqlSession.selectOne("memberMapper.idDupCheck", inputId);
	}


	/** 이메일 중복 체크
	 * @param inputEmail
	 * @return int
	 */
	public int emailDupCheck(String inputEmail) {
		
		return sqlSession.selectOne("memberMapper.emailDupCheck", inputEmail);
	}


	/** 회원가입 
	 * @param member
	 * @return int result
	 */
	public int signUp(Member member) {
		
		return sqlSession.insert("memberMapper.signUp", member);
	}


	/** 회원정보 수정
	 * @param member
	 * @return
	 */
	public int updateMember(Member member) {
		// TODO Auto-generated method stub
		return sqlSession.update("memberMapper.updateMember", member);
	}


	/** 암호화된 비밀번호 조회 
	 * @param memberNo
	 * @return
	 */
	public String selectPw(String memberNo) {
		// TODO Auto-generated method stub
		return sqlSession.selectOne("memberMapper.selectSavePw", memberNo);
	}
	
	
	/** 비빌번호 변경
	 * @param map
	 * @return
	 */
	public int updatePw(Map<String, String> map) {
		// TODO Auto-generated method stub
		return sqlSession.update("memberMapper.updatePw", map);
	}


	public int secessionMember(Map<String, String> map) {
		return sqlSession.update("memberMapper.secessionMember", map);
		
	}
	
}
