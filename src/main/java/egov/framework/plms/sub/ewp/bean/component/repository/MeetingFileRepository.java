package egov.framework.plms.sub.ewp.bean.component.repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;

import egov.framework.plms.sub.ewp.bean.component.converter.MeetingFileConvertManager;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileConvertVO;
import lombok.extern.slf4j.Slf4j;


/**
 * 변환 대상 파일 목록 대기줄을 생성하고 관리하는 클래스
 * 
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 * @see {@link MeetingFileConvertManager}
 */
@Slf4j
@Repository
public class MeetingFileRepository {
	/**
	 * 일반 대기열. 우선 대기열이 모두 비워진 후에 처리.
	 * cvtPriority
	 * 0 : 순서대로 추가
	 * 1 : 맨 앞으로 추가
	 */
	private LinkedList<Integer> normalQueue;
	
	/**
	 * 우선 대기열. 가장 먼저 대기열을 비운다.
	 * cvtPriority 값 기준
	 * 2 : 순서대로 추가
	 * 3 : 맨 앞으로 추가
	 */
	private LinkedList<Integer> priorityQueue;
	
	/**
	 * FileConvertVO 객체를 매핑하여 저장할 Map. fileno를 key, FileConvertVO를 value로 하여 저장.
	 */
	private Map<Integer, MeetingFileConvertVO> cvtMap;
	
	@PostConstruct
	private void init() {
		normalQueue = new LinkedList<>();
		priorityQueue = new LinkedList<>();
		cvtMap = new HashMap<>();
	}
	
	public boolean nowQueuing(Integer fileKey) {
		boolean result;
		if(normalQueue.contains(fileKey) || priorityQueue.contains(fileKey)) {
			result = true;
		}else {
			result = false;
		}
		log.debug("nowQueuing fileKey : {}, result : {}", fileKey, result);
		return result;
	}
	
	public boolean addQueue(MeetingFileConvertVO cvt, Integer priority) {
		boolean result = false;
		switch(priority) {
			case 0:
				result = addLast(cvt, false);
				break;
			case 1:
				result = addFirst(cvt, false);
				break;
			case 2:
				result = addLast(cvt, true);
				break;
			case 3:
				result = addFirst(cvt, true);
				break;
		}
		return result;
	}
	
	/**
	 * 대기열에 추가
	 * @param fileKey : 대기열에 추가할 파일번호
	 * @param isPriority : 우선적으로 처리될 우선 대기열에 추가해야할 경우 true, 아니면 false
	 * @return : 이미 대기열에 있는 경우 false, 대기열에 추가 한 경우 true
	 */
	private boolean addLast(MeetingFileConvertVO cvt, boolean isPriority) {
		boolean result;
		Integer fileKey = cvt.getFileKey();
		if(normalQueue.contains(fileKey) || priorityQueue.contains(fileKey)) {
			result = false;
		} else {
			LinkedList<Integer> queue = null;
			if(isPriority) {
				queue = priorityQueue;
			}else {
				queue = normalQueue;
			}
			
			queue.addLast(fileKey);
			cvtMap.put(fileKey, cvt);
			result = true;
		}
		log.debug("addQueue fileKey : {}, isPriority : {}, result : {}", fileKey, isPriority, result);
		return result;
	}
	
	private boolean addFirst(MeetingFileConvertVO cvt, boolean isPriority) {
		boolean result;
		Integer fileKey = cvt.getFileKey();
		if(normalQueue.contains(fileKey) || priorityQueue.contains(fileKey)) {
			result = false;
		} else {
			LinkedList<Integer> queue = null;
			if(isPriority) {
				queue = priorityQueue;
			}else {
				queue = normalQueue;
			}
			
			queue.addFirst(fileKey);
			cvtMap.put(fileKey, cvt);
			result = true;
		}
		log.debug("addQueue fileKey : {}, isPriority : {}, result : {}", fileKey, isPriority, result);
		return result;
	}
	
	/**
	 * 대기열에서 제거
	 * @param fileKey : 대기열에서 제거할 파일번호
	 * @return : 일반 대기열과 우선 대기열 둘 중 한 곳에서 제거에 성공한 경우 true, 아니면 false
	 */
	public MeetingFileConvertVO removeQueue(Integer fileKey) {
		if(normalQueue.remove(fileKey) || priorityQueue.remove(fileKey)) {
			return cvtMap.remove(fileKey);
		}else {
			return null;
		}
	}
	
	/**
	 * 대기열 맨 앞 파일번호를 가져온다
	 * @return : 우선 대기열이 비어있지 않다면 우선 대기열 맨 앞 노드를 반환, 아니면 일반 대기열 맨 앞 노드를 반환. 두 대기열에서 모두 반환할 값이 없는 경우 null 반환.
	 */
	public MeetingFileConvertVO popQueue() {
		Integer fileKey = null;
		try {
			if(!priorityQueue.isEmpty()) {
				fileKey = priorityQueue.pop();
			}else {
				fileKey = normalQueue.pop();
			}
		}catch(NoSuchElementException e) {
			fileKey = null;
		}
		log.debug("popQueue fileKey : {}", fileKey);
		return cvtMap.remove(fileKey);
	}
	
	/**
	 * 대기열 맨 앞으로 순서 이동
	 * @param fileKey : 이동할 파일 번호
	 * @param isPriority : 우선적으로 처리될 우선 대기열에 추가해야할 경우 true, 아니면 false
	 * @return : 어느 대기열에도 포함 되어 있지 않다면 false, 순서 변경에 성곤한 경우 true
	 */
	public boolean swithToFirst(Integer fileKey, boolean isPriority) {
		boolean result;
		if(!normalQueue.remove(fileKey) && !priorityQueue.remove(fileKey)) {
			result = false;
		}else {
		
			LinkedList<Integer> queue = null;
			if(isPriority) {
				queue = priorityQueue;
			}else {
				queue = normalQueue;
			}
			
			queue.addFirst(fileKey);
			result = true;
		}
		log.debug("swithToFirst fileKey : {}, isPriority : {}, result : {}", fileKey, isPriority, result);
		return result;
	}
	
	/**
	 * 대기열 맨 뒤로 순서 이동
	 * @param fileKey : 이동할 파일 번호
	 * @param isPriority : 우선적으로 처리될 우선 대기열에 추가해야할 경우 true, 아니면 false
	 * @return : 어느 대기열에도 포함 되어 있지 않다면 false, 순서 변경에 성곤한 경우 true
	 */
	public boolean swithToLast(Integer fileKey, boolean isPriority) {
		boolean result;
		if(!normalQueue.remove(fileKey) && !priorityQueue.remove(fileKey)) {
			result = false;
		}else {
		
			LinkedList<Integer> queue = null;
			if(isPriority) {
				queue = priorityQueue;
			}else {
				queue = normalQueue;
			}
			
			queue.addLast(fileKey);
			result = true;
		}
		log.debug("swithToLast fileKey : {}, isPriority : {}, result : {}", fileKey, isPriority, result);
		return result;
	}
	
	/**
	 * 우선 대기열로 대기열 변경
	 * @param fileKey : 대기열 변경할 파일 번호
	 * @return : 어느 대기열에도 포함 되어 있지 않다면 false, 순서 변경에 성곤한 경우 true
	 */
	public boolean swithToPriority(Integer fileKey) {
		boolean result;
		
		if(!normalQueue.remove(fileKey) && !priorityQueue.remove(fileKey)) {
			result = false;
		}else {
			priorityQueue.addLast(fileKey);
			result = true;
		}
		log.debug("swithToPriority fileKey : {}, result : {}", fileKey, result);
		return result;
	}
	
	/**
	 * 일반 대기열로 대기열 변경
	 * @param fileKey : 대기열 변경할 파일 번호
	 * @return : 어느 대기열에도 포함 되어 있지 않다면 false, 순서 변경에 성곤한 경우 true
	 */
	public boolean swithToNormal(Integer fileKey) {
		boolean result;
		
		if(!normalQueue.remove(fileKey) && !priorityQueue.remove(fileKey)) {
			result = false;
		}else {
			normalQueue.addLast(fileKey);
			result = true;
		}
		log.debug("swithToNormal fileKey : {}, result : {}", fileKey, result);
		return result;
	}
}
