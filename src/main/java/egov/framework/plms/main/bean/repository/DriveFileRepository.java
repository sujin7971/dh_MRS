package egov.framework.plms.main.bean.repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;

import egov.framework.plms.main.bean.mvc.entity.file.FileConvertVO;
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
public class DriveFileRepository {
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
	private Map<Integer, FileConvertVO> cvtMap;
	
	@PostConstruct
	private void init() {
		normalQueue = new LinkedList<>();
		priorityQueue = new LinkedList<>();
		cvtMap = new HashMap<>();
	}
	
	public boolean nowQueuing(Integer fileId) {
		boolean result;
		if(normalQueue.contains(fileId) || priorityQueue.contains(fileId)) {
			result = true;
		}else {
			result = false;
		}
		log.debug("nowQueuing fileId : {}, result : {}", fileId, result);
		return result;
	}
	
	public boolean addQueue(FileConvertVO cvt, Integer priority) {
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
	 * @param fileId : 대기열에 추가할 파일번호
	 * @param isPriority : 우선적으로 처리될 우선 대기열에 추가해야할 경우 true, 아니면 false
	 * @return : 이미 대기열에 있는 경우 false, 대기열에 추가 한 경우 true
	 */
	private boolean addLast(FileConvertVO cvt, boolean isPriority) {
		boolean result;
		Integer fileId = cvt.getFileId();
		if(normalQueue.contains(fileId) || priorityQueue.contains(fileId)) {
			result = false;
		} else {
			LinkedList<Integer> queue = null;
			if(isPriority) {
				queue = priorityQueue;
			}else {
				queue = normalQueue;
			}
			
			queue.addLast(fileId);
			cvtMap.put(fileId, cvt);
			result = true;
		}
		log.debug("addQueue fileId : {}, isPriority : {}, result : {}", fileId, isPriority, result);
		return result;
	}
	
	private boolean addFirst(FileConvertVO cvt, boolean isPriority) {
		boolean result;
		Integer fileId = cvt.getFileId();
		if(normalQueue.contains(fileId) || priorityQueue.contains(fileId)) {
			result = false;
		} else {
			LinkedList<Integer> queue = null;
			if(isPriority) {
				queue = priorityQueue;
			}else {
				queue = normalQueue;
			}
			
			queue.addFirst(fileId);
			cvtMap.put(fileId, cvt);
			result = true;
		}
		log.debug("addQueue fileId : {}, isPriority : {}, result : {}", fileId, isPriority, result);
		return result;
	}
	
	/**
	 * 대기열에서 제거
	 * @param fileId : 대기열에서 제거할 파일번호
	 * @return : 일반 대기열과 우선 대기열 둘 중 한 곳에서 제거에 성공한 경우 true, 아니면 false
	 */
	public FileConvertVO removeQueue(Integer fileId) {
		if(normalQueue.remove(fileId) || priorityQueue.remove(fileId)) {
			return cvtMap.remove(fileId);
		}else {
			return null;
		}
	}
	
	/**
	 * 대기열 맨 앞 파일번호를 가져온다
	 * @return : 우선 대기열이 비어있지 않다면 우선 대기열 맨 앞 노드를 반환, 아니면 일반 대기열 맨 앞 노드를 반환. 두 대기열에서 모두 반환할 값이 없는 경우 null 반환.
	 */
	public FileConvertVO popQueue() {
		Integer fileId = null;
		try {
			if(!priorityQueue.isEmpty()) {
				fileId = priorityQueue.pop();
			}else {
				fileId = normalQueue.pop();
			}
		}catch(NoSuchElementException e) {
			fileId = null;
		}
		log.debug("popQueue fileId : {}", fileId);
		return cvtMap.remove(fileId);
	}
	
	/**
	 * 대기열 맨 앞으로 순서 이동
	 * @param fileId : 이동할 파일 번호
	 * @param isPriority : 우선적으로 처리될 우선 대기열에 추가해야할 경우 true, 아니면 false
	 * @return : 어느 대기열에도 포함 되어 있지 않다면 false, 순서 변경에 성곤한 경우 true
	 */
	public boolean swithToFirst(Integer fileId, boolean isPriority) {
		boolean result;
		if(!normalQueue.remove(fileId) && !priorityQueue.remove(fileId)) {
			result = false;
		}else {
		
			LinkedList<Integer> queue = null;
			if(isPriority) {
				queue = priorityQueue;
			}else {
				queue = normalQueue;
			}
			
			queue.addFirst(fileId);
			result = true;
		}
		log.debug("swithToFirst fileId : {}, isPriority : {}, result : {}", fileId, isPriority, result);
		return result;
	}
	
	/**
	 * 대기열 맨 뒤로 순서 이동
	 * @param fileId : 이동할 파일 번호
	 * @param isPriority : 우선적으로 처리될 우선 대기열에 추가해야할 경우 true, 아니면 false
	 * @return : 어느 대기열에도 포함 되어 있지 않다면 false, 순서 변경에 성곤한 경우 true
	 */
	public boolean swithToLast(Integer fileId, boolean isPriority) {
		boolean result;
		if(!normalQueue.remove(fileId) && !priorityQueue.remove(fileId)) {
			result = false;
		}else {
		
			LinkedList<Integer> queue = null;
			if(isPriority) {
				queue = priorityQueue;
			}else {
				queue = normalQueue;
			}
			
			queue.addLast(fileId);
			result = true;
		}
		log.debug("swithToLast fileId : {}, isPriority : {}, result : {}", fileId, isPriority, result);
		return result;
	}
	
	/**
	 * 우선 대기열로 대기열 변경
	 * @param fileId : 대기열 변경할 파일 번호
	 * @return : 어느 대기열에도 포함 되어 있지 않다면 false, 순서 변경에 성곤한 경우 true
	 */
	public boolean swithToPriority(Integer fileId) {
		boolean result;
		
		if(!normalQueue.remove(fileId) && !priorityQueue.remove(fileId)) {
			result = false;
		}else {
			priorityQueue.addLast(fileId);
			result = true;
		}
		log.debug("swithToPriority fileId : {}, result : {}", fileId, result);
		return result;
	}
	
	/**
	 * 일반 대기열로 대기열 변경
	 * @param fileId : 대기열 변경할 파일 번호
	 * @return : 어느 대기열에도 포함 되어 있지 않다면 false, 순서 변경에 성곤한 경우 true
	 */
	public boolean swithToNormal(Integer fileId) {
		boolean result;
		
		if(!normalQueue.remove(fileId) && !priorityQueue.remove(fileId)) {
			result = false;
		}else {
			normalQueue.addLast(fileId);
			result = true;
		}
		log.debug("swithToNormal fileId : {}, result : {}", fileId, result);
		return result;
	}
}
