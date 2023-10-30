package egov.framework.plms.sub.ewp.bean.component.file;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.component.common.abst.FileModelPathResolver;
import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.sub.ewp.bean.mvc.entity.meeting.EwpMeetingInfoVO;
import egov.framework.plms.sub.ewp.bean.mvc.service.meeting.EwpMeetingInfoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("ewp")
@Primary
public class EwpFileModelPathResolver extends FileModelPathResolver<FileDetailVO> {
	protected final String UPLOAD_ROOT_PATH;
	@Autowired
	private EwpMeetingInfoService mtServ;
	
	public EwpFileModelPathResolver(@Value("${config.file.upload-path}") String UPLOAD_ROOT_PATH) {
		super(UPLOAD_ROOT_PATH);
		this.UPLOAD_ROOT_PATH = UPLOAD_ROOT_PATH;
	}

	@Override
	protected LocalDateTime getMeetingRegDateTime(Integer meetingId) {
		// TODO Auto-generated method stub
		Optional<EwpMeetingInfoVO> opt = mtServ.getMeetingInfoOne(meetingId);
		return opt.map(EwpMeetingInfoVO::getRegDateTime).orElse(null);
	}
	
	protected String getMeetingFileBasePath(FileDetailVO fileModel) {
		String ROOT_PATH = UPLOAD_ROOT_PATH;
		if (!ROOT_PATH.endsWith(File.separator)) {
			ROOT_PATH += File.separator;
		}
		Integer meetingId = fileModel.getRelatedEntityId();
		LocalDateTime regDateTime = getMeetingRegDateTime(meetingId);
		Integer year = regDateTime.getYear();
		Integer month = regDateTime.getMonth().getValue();
		return ROOT_PATH + "meeting" + File.separator + year + File.separator + month + File.separator + meetingId;
	}
}
