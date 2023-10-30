package egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.meeting;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingSecurityAgreementVO;

@Mapper
public interface EwpMeetingSecurityAgreementMapper {
	Integer insertMeetingSecurityAgreementOne(EwpMeetingSecurityAgreementVO params);
	Integer deleteMeetingSecurityAgreementOne(Integer attendId);
	EwpMeetingSecurityAgreementVO selectMeetingSecurityAgreementOne(Integer attendId);
	List<EwpMeetingSecurityAgreementVO> selectMeetingSecurityAgreementAll(Integer meetingId);
}
