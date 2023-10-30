package egov.framework.plms.main.bean.mvc.entity.meeting.abst;

import java.util.List;

import egov.framework.plms.main.bean.mvc.entity.file.abst.FileDetailModelVO;
import egov.framework.plms.main.core.model.able.Convertable;

public abstract class MeetingArchiveModelVO<T extends FileDetailModelVO> extends MeetingArchiveEntity implements Convertable<MeetingArchiveModelDTO>{
	public abstract List<T> getFiles();
}
