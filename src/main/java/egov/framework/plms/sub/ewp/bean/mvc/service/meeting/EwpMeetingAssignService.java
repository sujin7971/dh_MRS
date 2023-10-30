package egov.framework.plms.sub.ewp.bean.mvc.service.meeting;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingAssignAbstractService;
import egov.framework.plms.main.core.exception.ApiDataOperationException;
import egov.framework.plms.main.core.model.enums.error.ErrorCode;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingArchiveVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingAssignVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingScheduleVO;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.meeting.EwpMeetingAssignMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("ewp")
@Primary
public class EwpMeetingAssignService extends MeetingAssignAbstractService<EwpMeetingAssignVO> {
	private EwpMeetingAssignMapper mapper;
	@Autowired
	private EwpMeetingScheduleService scheServ;
	@Autowired
	private EwpMeetingInfoService mtServ;
	public EwpMeetingAssignService(@Autowired EwpMeetingAssignMapper mapper) {
		super(mapper);
		this.mapper = mapper;
	}

	@Transactional(rollbackFor = Exception.class, transactionManager = "mariaTransactionManager")
	@Override
	public EwpMeetingAssignVO postMeetingAssign(EwpMeetingAssignVO params) throws ApiDataOperationException {
		EwpMeetingInfoVO meeting = params.getMeetingInfo();
		if(!mtServ.postMeetingInfo(meeting)) {
			throw new ApiDataOperationException(ErrorCode.MEETING_INFO.POST_FAILED);
		}
		Integer meetingKey = meeting.getMeetingKey();
		EwpMeetingScheduleVO schedule = params.toBuilder().meetingKey(meetingKey).build().getMeetingSchedule();
		if(!scheServ.postMeetingSchedule(schedule)) {
			throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.CONFLICT_EXISTING_MEETING);
		}
		Integer skdKey = schedule.getSkdKey();
		return params.toBuilder().skdKey(skdKey).meetingKey(meetingKey).build();
	}
	@Transactional(rollbackFor = Exception.class, transactionManager = "mariaTransactionManager")
	@Override
	public boolean putMeetingAssign(EwpMeetingAssignVO params) throws ApiDataOperationException {
		EwpMeetingScheduleVO schedule = params.getMeetingSchedule();
		EwpMeetingInfoVO meeting = params.getMeetingInfo();
		if(!mtServ.putMeetingInfo(meeting)) {
			throw new ApiDataOperationException(ErrorCode.MEETING_INFO.UPDATE_FAILED);
		}
		if(!scheServ.putMeetingSchedule(schedule)) {
			throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.CONFLICT_EXISTING_MEETING);
		}
		return true;
	}
	@Transactional(rollbackFor = Exception.class, transactionManager = "mariaTransactionManager")
	@Override
	public boolean deleteMeetingAssign(Integer scheduleId) throws ApiDataOperationException {
		Optional<EwpMeetingAssignVO> assignOpt = getMeetingAssignOneByScheduleId(scheduleId);
		if(assignOpt.isPresent()) {
			EwpMeetingAssignVO assign = assignOpt.get();
			EwpMeetingScheduleVO schedule = assign.getMeetingSchedule();
			EwpMeetingInfoVO meeting = assign.getMeetingInfo();
			if(!mtServ.putMeetingInfo(meeting)) {
				throw new ApiDataOperationException(ErrorCode.MEETING_INFO.DELETE_FAILED);
			}
			if(!scheServ.putMeetingSchedule(schedule)) {
				throw new ApiDataOperationException(ErrorCode.MEETING_SCHEDULE.DELETE_FAILED);
			}
			return true;
		}else {
			throw new NoSuchElementException();
		}
	}
	
	@Override
	public Optional<EwpMeetingAssignVO> getMeetingAssignOneByScheduleId(Integer scheduleId) {
		EwpMeetingAssignVO params = EwpMeetingAssignVO.builder().skdKey(scheduleId).build();
		return super.getMeetingAssignOne(params);
	}

	@Override
	public Optional<EwpMeetingAssignVO> getMeetingAssignOneByMeetingId(Integer meetingId) {
		EwpMeetingAssignVO params = EwpMeetingAssignVO.builder().meetingKey(meetingId).build();
		return super.getMeetingAssignOne(params);
	}
	
	public Optional<EwpMeetingAssignVO> getMeetingAssignOneByRequest(RoomType roomType, Integer reqKey) {
		EwpMeetingAssignVO params = EwpMeetingAssignVO.builder().roomType(roomType).reqKey(reqKey).build();
		return super.getMeetingAssignOne(params);
	}
	
	/**
	 * 파일함에 표시할 회의 목록 및 회의 파일 조회<br>
	 * 
	 * 개인 파일함 검색인 경우 {@link EwpMeetingArchiveVO}의 <code>userKey<code>에 사용자 고유키 설정<br>
	 * 부서 파일함 검색인 경우 {@link EwpMeetingArchiveVO}의 <code>deptId<code>에 사용자 소속 부서 고유키 설정<br>
	 * @param param
	 * @return
	 */
	public List<EwpMeetingArchiveVO> getMeetingArchiveList(EwpMeetingArchiveVO param){
		return mapper.getMeetingArchiveList(param);
	}
	
	public List<EwpMeetingAssignVO> getMeetingAssignListForApproval(EwpMeetingAssignVO params){
		try {
			return mapper.getMeetingAssignListForApproval(params);
		}catch(Exception e) {
			log.error("Failed to get MeetingAssign list with params: {}", params.toString());
			log.error("Failed to get MeetingAssign list messages: {}", e.toString());
			e.printStackTrace();
			return new ArrayList<EwpMeetingAssignVO>();
		}
	}
}
