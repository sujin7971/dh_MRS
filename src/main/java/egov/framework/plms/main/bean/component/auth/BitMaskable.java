package egov.framework.plms.main.bean.component.auth;

import java.util.Collection;

/**
 * 비트 연산 기능을 가진 인터페이스
 * @author mckim
 *
 */
public interface BitMaskable {
	/**
	 * 대상 정수의 비트값에 해당 정수의 비트값을 가지고 있는지 판별
	 * @param target 대상 정수값
	 * @param check 체크할 정수값
	 * @return 가지고 있으면 <code>true<code>, 그렇지 않다면 <code>false<code>
	 */
	boolean hasBit(Integer target, Integer check);
	/**
	 * 두 정수의 비트값 AND 연산 결과를 반환
	 * @param a
	 * @param b
	 * @return a AND b
	 */
	Integer bitWiseAND(Integer a, Integer b);
	/**
	 * 정수값 모음의 비트값 AND 연산 결과를 반환
	 * @param collection 정수값 모음
	 * @return a AND b AND c AND...
	 */
	Integer bitWiseAND(Collection<Integer> collection);
	/**
	 * 두 정수의 비트값 OR 연산 결과를 반환
	 * @param a
	 * @param b
	 * @return a OR b
	 */
	Integer bitWiseOR(Integer a, Integer b);
	/**
	 * 정수값 모음의 비트값 OR 연산 결과를 반환
	 * @param collection 정수값 모음
	 * @return a OR b OR c OR...
	 */
	Integer bitWiseOR(Collection<Integer> collection);
	/**
	 * 대상 정수의 비트값에서 해당 정수의 비트값 제거
	 * @param a
	 * @param b
	 * @return a AND (NOT b)
	 */
	Integer bitWiseMINUS(Integer a, Integer b);
}
