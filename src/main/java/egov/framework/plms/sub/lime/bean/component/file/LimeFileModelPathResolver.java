package egov.framework.plms.sub.lime.bean.component.file;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.component.common.abst.FileModelPathResolver;
import egov.framework.plms.main.bean.mvc.entity.file.FileDetailVO;
import egov.framework.plms.main.bean.mvc.entity.meeting.MeetingInfoVO;
import egov.framework.plms.sub.lime.bean.mvc.service.meeting.LimeMeetingInfoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("lime")
@Primary
public class LimeFileModelPathResolver extends FileModelPathResolver<FileDetailVO> {
	protected final String UPLOAD_ROOT_PATH;
	@Autowired
	private LimeMeetingInfoService mtServ;
	
	public LimeFileModelPathResolver(@Value("${config.file.upload-path}") String UPLOAD_ROOT_PATH) {
		super(UPLOAD_ROOT_PATH);
		this.UPLOAD_ROOT_PATH = UPLOAD_ROOT_PATH;
	}

	@Override
	protected LocalDateTime getMeetingRegDateTime(Integer meetingId) {
		// TODO Auto-generated method stub
		Optional<MeetingInfoVO> opt = mtServ.getMeetingInfoOne(meetingId);
		return opt.map(MeetingInfoVO::getRegDateTime).orElse(null);
	}
}
