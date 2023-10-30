package egov.framework.plms.sub.ewp.bean.mvc.service.meeting;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.service.meeting.abst.MeetingNoteAbstractService;
import egov.framework.plms.main.core.util.FileNameUtil;
import egov.framework.plms.sub.ewp.bean.mvc.entity.file.MeetingFileInfoVO;
import egov.framework.plms.sub.ewp.core.util.MeetingFilePathUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("ewp")
public class EwpMeetingNoteService extends MeetingNoteAbstractService<MeetingFileInfoVO>{
	private final EwpMeetingInfoService mtServ;
	
	@Override
	protected File getNoteSourceFolder(MeetingFileInfoVO fileVO) {
		String ROOT_PATH = MeetingFilePathUtil.getRootPath(mtServ.getMeetingInfoOne(fileVO.getMeetingKey()).get());
		String FILE_NAME = FileNameUtil.getFileNameFormat(fileVO);
		File SRC_FOLDER = new File(MeetingFilePathUtil.getBookletSourceFolderPath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME)));
		return SRC_FOLDER;
	}
	
	@Override
	protected File getNoteDrawFolder(MeetingFileInfoVO fileVO, String userId) {
		String ROOT_PATH = MeetingFilePathUtil.getRootPath(mtServ.getMeetingInfoOne(fileVO.getMeetingKey()).get());
		String FILE_NAME = FileNameUtil.getFileNameFormat(fileVO);
		File DRAW_FOLDER = new File(MeetingFilePathUtil.getBookletUserPath(ROOT_PATH, FilenameUtils.getBaseName(FILE_NAME), userId));
		return DRAW_FOLDER;
	}

	@Override
	protected File getMemoFolder(Integer meetingKey, String userId) {
		String ROOT_PATH = MeetingFilePathUtil.getRootPath(mtServ.getMeetingInfoOne(meetingKey).get());
		String USER_MEMO_FOLDER_PATH = MeetingFilePathUtil.getMemoStorePath(ROOT_PATH, userId);
		File MEMO_FOLDER = new File(USER_MEMO_FOLDER_PATH);
		if(!MEMO_FOLDER.exists()) {
			MEMO_FOLDER.mkdir();
		}
		return MEMO_FOLDER;
	}

}
