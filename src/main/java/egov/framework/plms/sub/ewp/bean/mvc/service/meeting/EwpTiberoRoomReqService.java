package egov.framework.plms.sub.ewp.bean.mvc.service.meeting;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import egov.framework.plms.main.bean.component.properties.ReserveConfigProperties;
import egov.framework.plms.main.core.exception.ApiDataOperationException;
import egov.framework.plms.main.core.exception.abst.ApiException;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.main.core.model.enums.user.ManagerRole;
import egov.framework.plms.main.core.model.response.ErrorMessage;
import egov.framework.plms.main.core.model.response.ResponseMessage;
import egov.framework.plms.sub.ewp.bean.mvc.entity.assign.EwpRoomReqVO;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.tibero.meeting.AllRoomReqMapper;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.tibero.meeting.EduRoomReqMapper;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.tibero.meeting.HallReqMapper;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.tibero.meeting.MeetingRoomReqMapper;
import egov.framework.plms.sub.ewp.bean.mvc.service.organization.EwpUserInfoService;
import egov.framework.plms.sub.ewp.bean.mvc.service.room.EwpTiberoRoomInfoService;
import egov.framework.plms.sub.ewp.core.model.login.EwpAuthenticationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 동서발전 경영지원서비스에서 사용중인 Tibero DB에서 사용신청 내역 CRUD 처리
 * @author mckim
 * @version 1.0
 * @since 2023. 1. 27
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Profile("ewp")
public class EwpTiberoRoomReqService {
	private final ReserveConfigProperties reserveProperties;
	
	private final EwpTiberoRoomInfoService rmServ;
	private final AllRoomReqMapper reqMapper;
	private final MeetingRoomReqMapper mrqMapper;
	private final EduRoomReqMapper erqMapper;
	private final HallReqMapper hrqMapper;
	private final EwpUserInfoService userServ;
	/**
	 * 사용신청 등록
	 * @param roomType 사용신청을 등록한 장소의 분류
	 * @param reqVO 사용신청
	 * @return
	 * @throws Exception
	 * @see {@link RoomType}
	 */
	public boolean postRoomAssign(RoomType roomType, EwpRoomReqVO reqVO) throws ApiException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		EwpAuthenticationDetails details = (EwpAuthenticationDetails) authentication.getDetails();
		boolean isRequestManager = details.hasPosition(ManagerRole.REQUEST_MANAGER);
		if(roomType == RoomType.MEETING_ROOM) {
			return postMeetingRoomAssign(reqVO, isRequestManager);
		}else if(roomType == RoomType.EDU_ROOM){
			return postEduRoomAssign(reqVO, isRequestManager);
		}else if(roomType == RoomType.HALL){
			return postHallAssign(reqVO, isRequestManager);
		}else {
			return false;
		}
	}
	public boolean postRoomAssignWithoutValidation(RoomType roomType, EwpRoomReqVO reqVO) throws ApiException {
		boolean isRequestManager = true;
		if(roomType == RoomType.MEETING_ROOM) {
			return postMeetingRoomAssign(reqVO, isRequestManager);
		}else if(roomType == RoomType.EDU_ROOM){
			return postEduRoomAssign(reqVO, isRequestManager);
		}else if(roomType == RoomType.HALL){
			return postHallAssign(reqVO, isRequestManager);
		}else {
			return false;
		}
	}
	
	/**
	 * 회의실 사용신청 등록
	 * @param reqVO 사용신청
	 * @param isRequestManager 
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = {Exception.class})
	private boolean postMeetingRoomAssign(EwpRoomReqVO reqVO, boolean isRequestManager) throws ApiException {
		Integer consecutive = mrqMapper.checkMeetingRoomReq5Day(reqVO);
		if(!isRequestManager && consecutive >= reserveProperties.getConsecutiveLimit()) {
			throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.CONFLICT_CONSECUTIVE_DAYS);
		}
		Integer insertRes = mrqMapper.doInsertMeetingRoomReq(reqVO);
		if(insertRes == 1) {// 회의 예약 성공
			return true;
		}else {
			throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.CONFLICT_EXISTING_MEETING);
		}
	}
	
	/**
	 * 강의실 사용신청 등록
	 * @param reqVO 사용신청
	 * @param isRequestManager 
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = {Exception.class})
	private boolean postEduRoomAssign(EwpRoomReqVO reqVO, boolean isRequestManager) throws ApiException {
		Integer consecutive = erqMapper.checkEduRoomReq5Day(reqVO);
		if(!isRequestManager && consecutive >= reserveProperties.getConsecutiveLimit()) {
			throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.CONFLICT_CONSECUTIVE_DAYS);
		}
		Integer insertRes = erqMapper.doInsertEduRoomReq(reqVO);
		if(insertRes == 1) {// 회의 예약 성공
			return true;
		}else {
			throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.CONFLICT_EXISTING_MEETING);
		}
	}
	
	/**
	 * 강당 사용신청 등록
	 * @param reqVO 사용신청
	 * @param isRequestManager 
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = {Exception.class})
	private boolean postHallAssign(EwpRoomReqVO reqVO, boolean isRequestManager) throws ApiException {
		Integer consecutive = hrqMapper.checkHallReq5Day(reqVO);
		if(!isRequestManager && consecutive >= reserveProperties.getConsecutiveLimit()) {
			throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.CONFLICT_CONSECUTIVE_DAYS);
		}
		Integer insertRes = hrqMapper.doInsertHallReq(reqVO);
		if(insertRes == 1) {// 회의 예약 성공
			return true;
		}else {
			throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.CONFLICT_EXISTING_MEETING);
		}
	}
	/**
	 * 사용신청 수정
	 * @param roomType 사용신청을 등록한 장소의 분류
	 * @param reqVO 사용신청
	 * @return
	 * @throws Exception
	 * @see {@link RoomType}
	 */
	public boolean putRoomAssign(RoomType roomType, EwpRoomReqVO reqVO) throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		EwpAuthenticationDetails details = (EwpAuthenticationDetails) authentication.getDetails();
		boolean isRequestManager = details.hasPosition(ManagerRole.REQUEST_MANAGER);
		if(roomType == RoomType.MEETING_ROOM) {
			return putMeetingRoomAssign(reqVO, isRequestManager);
		}else if(roomType == RoomType.EDU_ROOM){
			return putEduRoomAssign(reqVO, isRequestManager);
		}else if(roomType == RoomType.HALL){
			return putHallAssign(reqVO, isRequestManager);
		}else {
			return false;
		}
	}
	
	public boolean putRoomAssignWithoutValidation(RoomType roomType, EwpRoomReqVO reqVO) throws Exception {
		boolean isRequestManager = true;
		if(roomType == RoomType.MEETING_ROOM) {
			return putMeetingRoomAssign(reqVO, isRequestManager);
		}else if(roomType == RoomType.EDU_ROOM){
			return putEduRoomAssign(reqVO, isRequestManager);
		}else if(roomType == RoomType.HALL){
			return putHallAssign(reqVO, isRequestManager);
		}else {
			return false;
		}
	}
	/**
	 * 회의실 사용신청 수정
	 * @param reqVO 사용신청
	 * @param isRequestManager 
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = {Exception.class})
	private boolean putMeetingRoomAssign(EwpRoomReqVO reqVO, boolean isRequestManager) throws Exception {
		Integer consecutive = mrqMapper.checkMeetingRoomReq5Day(reqVO);
		if(!isRequestManager && consecutive > reserveProperties.getConsecutiveLimit()) {
			throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.CONFLICT_CONSECUTIVE_DAYS);
		}
		Integer result = mrqMapper.doUpdateMeetingRoomReq(reqVO);
		if(result == 1) {// 회의 예약 성공
			return true;
		}else {
			throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.CONFLICT_EXISTING_MEETING);
		}
	}
	/**
	 * 강의실 사용신청 수정
	 * @param reqVO 사용신청
	 * @param isRequestManager 
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = {Exception.class})
	private boolean putEduRoomAssign(EwpRoomReqVO reqVO, boolean isRequestManager) throws Exception {
		Integer consecutive = erqMapper.checkEduRoomReq5Day(reqVO);
		if(!isRequestManager && consecutive > reserveProperties.getConsecutiveLimit()) {
			throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.CONFLICT_CONSECUTIVE_DAYS);
		}
		Integer result = erqMapper.doUpdateEduRoomReq(reqVO);
		if(result == 1) {
			return true;
		}else {
			throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.CONFLICT_EXISTING_MEETING);
		}
	}
	/**
	 * 강당 사용신청 수정
	 * @param reqVO 사용신청
	 * @param isRequestManager 
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = {Exception.class})
	private boolean putHallAssign(EwpRoomReqVO reqVO, boolean isRequestManager) throws Exception {
		Integer consecutive = hrqMapper.checkHallReq5Day(reqVO);
		if(!isRequestManager && consecutive > reserveProperties.getConsecutiveLimit()) {
			throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.CONFLICT_CONSECUTIVE_DAYS);
		}
		Integer result = hrqMapper.doUpdateHallReq(reqVO);
		if(result == 1) {
			return true;
		}else {
			throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.CONFLICT_EXISTING_MEETING);
		}
	}
	/**
	 * 사용신청 결재 상태 수정
	 * @param roomType 사용신청을 등록한 장소의 분류
	 * @param reqKey 사용신청 키
	 * @param userKey 수정한 사용자 키
	 * @param appStatus 결재값
	 * @param appComment 결재 코멘트
	 * @return
	 * @throws Exception
	 */
	public ResponseMessage putAssignStatus(RoomType roomType, Integer reqKey, String userKey, Character appStatus, String appComment) throws Exception {
		if(roomType == RoomType.MEETING_ROOM) {
			return putMeetingRoomAssignStatus(reqKey, userKey, appStatus, appComment);
		}else if(roomType == RoomType.EDU_ROOM){
			return putEduRoomAssignStatus(reqKey, userKey, appStatus, appComment);
		}else if(roomType == RoomType.HALL){
			return putHallAssignStatus(reqKey, userKey, appStatus, appComment);
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.ASSIGN.PUT_FAIL.value())
					.data(ResponseMessage.DetailCode.ASSIGN.BAD_ROOM_TYPE.value())
					.build();
		}
	}
	/**
	 * 회의실 사용신청 결재 상태 수정
	 * @param reqKey 사용신청 키
	 * @param userKey 수정한 사용자 키
	 * @param appStatus 결재값
	 * @param appComment 결재 코멘트
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = {Exception.class})
	private ResponseMessage putMeetingRoomAssignStatus(Integer reqKey, String userKey, Character appStatus, String appComment) throws Exception {
		Integer result = mrqMapper.doUpdateMeetingRoomReqStatus(EwpRoomReqVO.builder().seqReq(reqKey).appStatus(appStatus).appReason(appComment).build());
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.SCHEDULE.PUT_SUCCESS.value())
					.build();
		}else {
			throw new Exception(ErrorMessage.MessageCode.SCHEDULE.UNPROCESSABLE_ENTITY.value());
		}
	}
	/**
	 * 강의실 사용신청 결재 상태 수정
	 * @param reqKey 사용신청 키
	 * @param userKey 수정한 사용자 키
	 * @param appStatus 결재값
	 * @param appComment 결재 코멘트
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = {Exception.class})
	private ResponseMessage putEduRoomAssignStatus(Integer reqKey, String userKey, Character appStatus, String appComment) throws Exception {
		Integer result = erqMapper.doUpdateEduRoomReqStatus(EwpRoomReqVO.builder().seqReq(reqKey).appStatus(appStatus).appReason(appComment).build());
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.SCHEDULE.PUT_SUCCESS.value())
					.build();
		}else {
			throw new Exception(ErrorMessage.MessageCode.SCHEDULE.UNPROCESSABLE_ENTITY.value());
		}
	}
	/**
	 * 강당 사용신청 결재 상태 수정
	 * @param reqKey 사용신청 키
	 * @param userKey 수정한 사용자 키
	 * @param appStatus 결재값
	 * @param appComment 결재 코멘트
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = {Exception.class})
	private ResponseMessage putHallAssignStatus(Integer reqKey, String userKey, Character appStatus, String appComment) throws Exception {
		Integer result = hrqMapper.doUpdateHallReqStatus(EwpRoomReqVO.builder().seqReq(reqKey).appStatus(appStatus).appReason(appComment).build());
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.SCHEDULE.PUT_SUCCESS.value())
					.build();
		}else {
			throw new Exception(ErrorMessage.MessageCode.SCHEDULE.UNPROCESSABLE_ENTITY.value());
		}
	}
	/**
	 * 사용신청 삭제(DB에는 남음)
	 * @param roomType 사용신청을 등록한 장소의 분류
	 * @param reqKey 고유키
	 * @param regUser 삭제를 요청(처리)한 사용자 고유키
	 * @return
	 * @throws Exception
	 * @see {@link RoomType}
	 */
	public ResponseMessage deleteRoomReq(RoomType roomType, Integer reqKey, String regUser) {
		if(roomType == RoomType.MEETING_ROOM) {
			return deleteMeetingRoomReq(reqKey, regUser);
		}else if(roomType == RoomType.EDU_ROOM){
			return deleteEduRoomReq(reqKey, regUser);
		}else if(roomType == RoomType.HALL){
			return deleteHallReq(reqKey, regUser);
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.ASSIGN.DELETE_FAIL.value())
					.data(ResponseMessage.DetailCode.ASSIGN.BAD_ROOM_TYPE.value())
					.build();
		}
	}
	/**
	 * 사용신청 삭제(DB에서도 완전 삭제)
	 * @param roomType 사용신청을 등록한 장소의 분류
	 * @param reqKey 고유키
	 * @param regUser 삭제를 요청(처리)한 사용자 고유키
	 * @return
	 * @throws Exception
	 * @see {@link RoomType}
	 */
	public ResponseMessage deleteFailedRoomReq(RoomType roomType, Integer reqKey) {
		if(roomType == RoomType.MEETING_ROOM) {
			return deleteFailedMeetingRoomReq(reqKey);
		}else if(roomType == RoomType.EDU_ROOM){
			return deleteFailedEduRoomReq(reqKey);
		}else if(roomType == RoomType.HALL){
			return deleteFailedHallReq(reqKey);
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.BAD_REQUEST)
					.message(ResponseMessage.MessageCode.ASSIGN.DELETE_FAIL.value())
					.data(ResponseMessage.DetailCode.ASSIGN.BAD_ROOM_TYPE.value())
					.build();
		}
	}
	/**
	 * 회의실 사용신청 삭제(DB에는 남음)
	 * @param reqKey 사용신청 고유키
	 * @param regUser 삭제 요청한 사용자키
	 * @return
	 */
	@Transactional(rollbackFor = {Exception.class})
	private ResponseMessage deleteMeetingRoomReq(Integer reqKey, String regUser) {
		Integer result = mrqMapper.doDeleteMeetingRoomReq(EwpRoomReqVO.builder().seqReq(reqKey).regUser(regUser).build());
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.SCHEDULE.DELETE_SUCCESS.value())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
					.message(ResponseMessage.MessageCode.SCHEDULE.DELETE_SUCCESS.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.NOT_UPDATED)
							.message(ErrorMessage.MessageCode.SCHEDULE.UNPROCESSABLE_ENTITY.value())
							.build())
					.build();
		}
	}
	/**
	 * 회의실 사용신청 삭제(DB에서도 완전 삭제)
	 * @param reqKey 사용신청 고유키
	 * @param regUser 삭제 요청한 사용자키
	 * @return
	 */
	@Transactional(rollbackFor = {Exception.class})
	private ResponseMessage deleteFailedMeetingRoomReq(Integer reqKey) {
		Integer result = mrqMapper.doDeleteFailedMeetingRoomReq(reqKey);
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.SCHEDULE.DELETE_SUCCESS.value())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
					.message(ResponseMessage.MessageCode.SCHEDULE.DELETE_SUCCESS.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.NOT_UPDATED)
							.message(ErrorMessage.MessageCode.SCHEDULE.UNPROCESSABLE_ENTITY.value())
							.build())
					.build();
		}
	}
	/**
	 * 강의실 사용신청 삭제(DB에는 남음)
	 * @param reqKey 사용신청 고유키
	 * @param regUser 삭제 요청한 사용자키
	 * @return
	 */
	@Transactional(rollbackFor = {Exception.class})
	private ResponseMessage deleteEduRoomReq(Integer reqKey, String regUser) {
		Integer result = erqMapper.doDeleteEduRoomReq(EwpRoomReqVO.builder().seqReq(reqKey).regUser(regUser).build());
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.SCHEDULE.DELETE_SUCCESS.value())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
					.message(ResponseMessage.MessageCode.SCHEDULE.DELETE_SUCCESS.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.NOT_UPDATED)
							.message(ErrorMessage.MessageCode.SCHEDULE.UNPROCESSABLE_ENTITY.value())
							.build())
					.build();
		}
	}
	/**
	 * 강의실 사용신청 삭제(DB에서도 완전 삭제)
	 * @param reqKey 사용신청 고유키
	 * @param regUser 삭제 요청한 사용자키
	 * @return
	 */
	@Transactional(rollbackFor = {Exception.class})
	private ResponseMessage deleteFailedEduRoomReq(Integer reqKey) {
		Integer result = erqMapper.doDeleteFailedEduRoomReq(reqKey);
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.SCHEDULE.DELETE_SUCCESS.value())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
					.message(ResponseMessage.MessageCode.SCHEDULE.DELETE_SUCCESS.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.NOT_UPDATED)
							.message(ErrorMessage.MessageCode.SCHEDULE.UNPROCESSABLE_ENTITY.value())
							.build())
					.build();
		}
	}
	/**
	 * 강당 사용신청 삭제(DB에는 남음)
	 * @param reqKey 사용신청 고유키
	 * @param regUser 삭제 요청한 사용자키
	 * @return
	 */
	@Transactional(rollbackFor = {Exception.class})
	private ResponseMessage deleteHallReq(Integer reqKey, String regUser) {
		Integer result = hrqMapper.doDeleteHallReq(EwpRoomReqVO.builder().seqReq(reqKey).regUser(regUser).build());
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.SCHEDULE.DELETE_SUCCESS.value())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
					.message(ResponseMessage.MessageCode.SCHEDULE.DELETE_SUCCESS.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.NOT_UPDATED)
							.message(ErrorMessage.MessageCode.SCHEDULE.UNPROCESSABLE_ENTITY.value())
							.build())
					.build();
		}
	}
	/**
	 * 강당 사용신청 삭제(DB에서도 완전 삭제)
	 * @param reqKey 사용신청 고유키
	 * @param regUser 삭제 요청한 사용자키
	 * @return
	 */
	@Transactional(rollbackFor = {Exception.class})
	private ResponseMessage deleteFailedHallReq(Integer reqKey) {
		Integer result = hrqMapper.doDeleteFailedHallReq(reqKey);
		if(result == 1) {
			return ResponseMessage.builder(ResponseMessage.StatusCode.OK)
					.message(ResponseMessage.MessageCode.SCHEDULE.DELETE_SUCCESS.value())
					.build();
		}else {
			return ResponseMessage.builder(ResponseMessage.StatusCode.UNPROCESSABLE_ENTITY)
					.message(ResponseMessage.MessageCode.SCHEDULE.DELETE_SUCCESS.value())
					.error(ErrorMessage.builder(ErrorMessage.ErrorCode.NOT_UPDATED)
							.message(ErrorMessage.MessageCode.SCHEDULE.UNPROCESSABLE_ENTITY.value())
							.build())
					.build();
		}
	}
	/**
	 * 배정요청 조회
	 * @param roomType 사용신청을 등록한 장소의 분류
	 * @param seqReq: 조회할 배정요청 키
	 * @return
	 */
	public EwpRoomReqVO getRoomReqOne(RoomType roomType, Integer reqKey) {
		if(roomType == RoomType.MEETING_ROOM) {
			return getMeetingRoomReqOne(reqKey);
		}else if(roomType == RoomType.EDU_ROOM){
			return getEduRoomReqOne(reqKey);
		}else if(roomType == RoomType.HALL){
			return getHallReqOne(reqKey);
		}
		return null;
	}
	/**
	 * 배정요청 다음 배정 조회
	 * @param roomType 사용신청을 등록한 장소의 분류
	 * @param seqReq: 조회할 배정요청 키
	 * @return
	 */
	public EwpRoomReqVO getNextRoomReqOne(RoomType roomType, Integer reqKey) {
		EwpRoomReqVO reqVO = null;
		if(roomType == RoomType.MEETING_ROOM) {
			reqVO = mrqMapper.getNextMeetingRoomReqOne(reqKey);
		}else if(roomType == RoomType.EDU_ROOM){
			reqVO = erqMapper.getNextEduRoomReqOne(reqKey);
		}else if(roomType == RoomType.HALL){
			reqVO = hrqMapper.getNextHallReqOne(reqKey);
		}
		return reqVO;
	}
	/**
	 * 배정요청 목록 조회
	 * @param roomType 사용신청을 등록한 장소의 분류
	 * @param reqVO
	 * @return
	 */
	public List<EwpRoomReqVO> getRoomReqList(RoomType roomType, EwpRoomReqVO reqVO) {
		if(roomType == RoomType.ALL_ROOM) {
			return getRoomReqList(reqVO);
		}else if(roomType == RoomType.MEETING_ROOM) {
			return getMeetingRoomReqList(reqVO);
		}else if(roomType == RoomType.EDU_ROOM){
			return getEduRoomReqList(reqVO);
		}else if(roomType == RoomType.HALL){
			return getHallAssignList(reqVO);
		}
		return null;
	}
	/**
	 * 배정요청 목록 개수 조회
	 * @param roomType 사용신청을 등록한 장소의 분류
	 * @param reqVO
	 * @return
	 */
	public Integer getRoomReqListCnt(RoomType roomType, EwpRoomReqVO reqVO) {
		if(roomType == RoomType.ALL_ROOM) {
			return getRoomReqListCnt(reqVO);
		}else if(roomType == RoomType.MEETING_ROOM) {
			return getMeetingRoomReqListCnt(reqVO);
		}else if(roomType == RoomType.EDU_ROOM){
			return getEduRoomReqListCnt(reqVO);
		}else if(roomType == RoomType.HALL){
			return getHallReqListCnt(reqVO);
		}
		return 0;
	}
	/**
	 * 회의실 배정요청 조회
	 * @param seqReq: 조회할 배정요청 키
	 * @return
	 */
	private EwpRoomReqVO getMeetingRoomReqOne(Integer seqReq) {
		EwpRoomReqVO assignVO = mrqMapper.getMeetingRoomReqOne(seqReq);
		return assignVO;
	}
	
	/**
	 * 회의실 배정요청 목록 조회
	 * @param reqVO
	 * @return
	 */
	private List<EwpRoomReqVO> getMeetingRoomReqList(EwpRoomReqVO reqVO){
		List<EwpRoomReqVO> resultList = mrqMapper.getMeetingRoomReqList(reqVO);
		return resultList;
	}
	/**
	 * {@link EwpTiberoRoomReqService#getMeetingRoomReqList}검색 결과를 통해 조회될 칼럼의 개수 요청. 현재는 회의실 삭제 전 예약된 사용신청확인을 위해 사용
	 * @param param {@link EwpTiberoRoomReqService#getMeetingRoomReqList} 파라미터와 동일
	 * @return
	 */
	private Integer getMeetingRoomReqListCnt(EwpRoomReqVO reqVO){
		return mrqMapper.getMeetingRoomReqListCnt(reqVO);
	}
	
	/**
	 * 강의실 배정요청 조회
	 * @param seqReq: 조회할 배정요청 키 
	 * @return
	 */
	private EwpRoomReqVO getEduRoomReqOne(Integer seqReq) {
		EwpRoomReqVO assignVO = erqMapper.getEduRoomReqOne(seqReq);
		return assignVO;
	}
	
	/**
	 * 강의실 배정요청 조회
	 * @param reqVO
	 * @return
	 */
	private List<EwpRoomReqVO> getEduRoomReqList(EwpRoomReqVO reqVO){
		List<EwpRoomReqVO> resultList = erqMapper.getEduRoomReqList(reqVO);
		return resultList;
	}
	/**
	 * {@link EwpTiberoRoomReqService#getEduRoomReqList}검색 결과를 통해 조회될 칼럼의 개수 요청. 현재는 강의실 삭제 전 예약된 사용신청확인을 위해 사용
	 * @param param {@link EwpTiberoRoomReqService#getEduRoomReqList} 파라미터와 동일
	 * @return
	 */
	private Integer getEduRoomReqListCnt(EwpRoomReqVO reqVO){
		return erqMapper.getEduRoomReqListCnt(reqVO);
	}
	/**
	 * 강당 배정요청 조회
	 * @param seqReq: 조회할 배정요청 키 
	 * @return
	 */
	private EwpRoomReqVO getHallReqOne(Integer seqReq) {
		EwpRoomReqVO assignVO = hrqMapper.getHallReqOne(seqReq);
		return assignVO;
	}
	
	/**
	 * 강당 배정요청 조회
	 * @param reqVO
	 * @return
	 */
	private List<EwpRoomReqVO> getHallAssignList(EwpRoomReqVO reqVO){
		List<EwpRoomReqVO> resultList = hrqMapper.getHallReqList(reqVO);
		return resultList;
	}
	/**
	 * {@link EwpTiberoRoomReqService#getEduRoomReqList}검색 결과를 통해 조회될 칼럼의 개수 요청. 현재는 강의실 삭제 전 예약된 사용신청확인을 위해 사용
	 * @param param {@link EwpTiberoRoomReqService#getEduRoomReqList} 파라미터와 동일
	 * @return
	 */
	private Integer getHallReqListCnt(EwpRoomReqVO reqVO){
		return hrqMapper.getHallReqListCnt(reqVO);
	}
	
	/**
	 * 회의관리 시스템과 회의실 배정요청 상태 동기화를 위한 조회
	 * @param reqVO
	 * @return
	 */
	public List<EwpRoomReqVO> getMeetingRoomReqStatusList(){
		return mrqMapper.getMeetingRoomReqStatusList();
	}	
	
	/**
	 * 회의관리 시스템과 강의실 배정요청 상태 동기화를 위한 조회
	 * @param reqVO
	 * @return
	 */
	public List<EwpRoomReqVO> getEduRoomReqStatusList(){
		return erqMapper.getEduRoomReqStatusList();
	}
	
	/**
	 * 회의관리 시스템과 강당 배정요청 상태 동기화를 위한 조회
	 * @param reqVO
	 * @return
	 */
	public List<EwpRoomReqVO> getHallRoomReqStatusList(){
		return hrqMapper.getHallReqStatusList();
	}	
	
	/**
	 * 사용신청에 대한 통합 검색
	 * @param param
	 * @return
	 */
	public List<EwpRoomReqVO> getRoomReqList(EwpRoomReqVO param){
		List<EwpRoomReqVO> resultList = reqMapper.getRoomReqListSearch(param);
		return resultList;
	}
	/**
	 * {@link EwpTiberoRoomReqService#getRoomReqListSearch}검색 결과를 통해 조회될 칼럼의 개수 요청. 페이지네이션을 위한 조회
	 * @param param {@link EwpTiberoRoomReqService#getRoomReqListSearch} 파라미터와 동일
	 * @return
	 */
	public Integer getRoomReqListCnt(EwpRoomReqVO param){
		return reqMapper.getRoomReqListCnt(param);
	}
}
