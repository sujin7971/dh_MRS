package egov.framework.plms.main.bean.mvc.service.meeting.abst;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import egov.framework.plms.main.bean.mvc.entity.meeting.abst.MeetingApprovalModelVO;
import egov.framework.plms.main.bean.mvc.mapper.meeting.abst.MeetingApprovalAbstractMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class MeetingApprovalAbstractService<T extends MeetingApprovalModelVO> {
	protected final MeetingApprovalAbstractMapper<T> mapper;
	
	public MeetingApprovalAbstractService(MeetingApprovalAbstractMapper<T> mapper) {
		this.mapper = mapper;
	}
	
	protected boolean insertApprovalOne(T params) {
		try {
			Integer result = mapper.insertApprovalOne(params);
			return (result == 0)?false:true;
		}catch(Exception e) {
			log.error("Failed to insert MeetingApproval one with Params: {}", params);
			log.error("Failed to insert MeetingApproval one messages: {}", e.toString());
			return false;
		}
	}
	
	protected boolean updateApprovalOne(T params) {
		try {
			Integer result = mapper.updateApprovalOne(params);
			return (result == 0)?false:true;
		}catch(Exception e) {
			log.error("Failed to insert MeetingApproval one with Params: {}", params);
			log.error("Failed to insert MeetingApproval one messages: {}", e.toString());
			return false;
		}
	}
	
	public Optional<T> selectApprovalOne(Integer approvalId) {
		try {
			T approval = mapper.selectApprovalOne(approvalId);
			return Optional.ofNullable(approval);
		}catch(Exception e) {
			log.error("Failed to select MeetingApproval one with id: {}", approvalId);
			log.error("Failed to select MeetingApproval one messages: {}", e.toString());
			return Optional.empty();
		}
	}
	
	public List<T> selectApprovalList(T params) {
		try {
			return mapper.selectApprovalList(params);
		}catch(Exception e) {
			log.error("Failed to select MeetingApproval list with Params: {}", params);
			log.error("Failed to select MeetingApproval one messages: {}", e.toString());
			return new ArrayList<T>();
		}
	}
	
	public List<T> selectApprovalListForProcessing() {
		try {
			return mapper.selectApprovalListForProcessing();
		}catch(Exception e) {
			log.error("Failed to select MeetingApproval list for processing");
			log.error("Failed to select MeetingApproval list for processing messages: {}", e.toString());
			return new ArrayList<T>();
		}
	}
}
