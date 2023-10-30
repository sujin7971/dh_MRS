package egov.framework.plms.sub.ewp.bean.mvc.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.admin.AdminRosterVO;
import egov.framework.plms.main.bean.mvc.mapper.admin.DomainRosterMapper;
import egov.framework.plms.main.bean.mvc.mapper.admin.ManagerRosterMapper;
import egov.framework.plms.main.bean.mvc.service.admin.abst.AdminRosterAbstractService;
import egov.framework.plms.main.core.model.enums.meeting.RoomType;
import egov.framework.plms.sub.ewp.bean.mvc.entity.admin.EwpItemManagerRosterVO;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.maria.admin.EwpManagerRosterMapper;
import egov.framework.plms.sub.ewp.bean.mvc.mapper.tibero.admin.EwpItemManagerRosterMapper;
import egov.framework.plms.sub.ewp.core.model.enums.SeqMenu;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Profile("ewp")
@Primary
public class EwpAdminRosterService extends AdminRosterAbstractService {
	@Autowired
	private EwpItemManagerRosterMapper itemMngMapper;
	@Autowired
	private EwpManagerRosterMapper ewpMngMapper;
	
	public EwpAdminRosterService(@Autowired DomainRosterMapper domainMapper, @Autowired ManagerRosterMapper managerMapper) {
		super(domainMapper, managerMapper);
		// TODO Auto-generated constructor stub
	}
	
	public List<AdminRosterVO> getAllRoomManagerList(String userId){
		return getAllRoomManagerList(userId, null);
	}
	
	public List<AdminRosterVO> getAllRoomManagerList(String userId, String officeCode){
		return ewpMngMapper.getAllRoomManagerList(AdminRosterVO.builder().userId(userId).officeCode(officeCode).build());
	}
	
	/**
	 * 대상 사업소에서 해당 사용자의 담당 정보 조회
	 * @param officeCode 사업소 코드
	 * @param userId 사용자 고유키
	 * @return
	 */
	public List<EwpItemManagerRosterVO> getItemManagerList(String officeCode, String userId) {
		return getItemManagerList(officeCode, userId, null);
	}
	
	/**
	 * 해당 장소 유형에 속한 모든 장소의 담당 정보 조회
	 * @param roomType
	 * @return
	 */
	public List<EwpItemManagerRosterVO> getItemManagerList(RoomType roomType) {
		return getItemManagerList(null, null, roomType);
	}
	
	/**
	 * 대상 사용자의 모든 담당 정보 조회
	 * @param officeCode
	 * @return
	 */
	
	public List<EwpItemManagerRosterVO> getItemManagerList(String userId) {
		return getItemManagerList(null, userId, null);
	}
	
	/**
	 * 대상 사업소의 해당 장소 유형의 담당 정보 조회
	 * @param officeCode
	 * @param roomType
	 * @return
	 */
	public List<EwpItemManagerRosterVO> getItemManagerList(String officeCode, RoomType roomType) {
		return getItemManagerList(officeCode, null, roomType);
	}
	
	/**
	 * 대상 사업소 장소유형에 대해 해당 사용자의 담당 정보 조회 
	 * @param officeCode
	 * @param userId
	 * @param roomType
	 * @return
	 */
	public List<EwpItemManagerRosterVO> getItemManagerList(String officeCode, String userId, RoomType roomType) {
		SeqMenu seqMenu;
		roomType = (roomType != null)?roomType:RoomType.ALL_ROOM;
		switch(roomType) {
			case MEETING_ROOM:
				seqMenu = SeqMenu.ITEM_MEETING_ROOM;
				break;
			case EDU_ROOM:
				seqMenu = SeqMenu.ITEM_EDU_ROOM;
				break;
			case HALL:
				seqMenu = SeqMenu.ITEM_HALL;
				break;
			default:
				seqMenu = null;
		}
		
		return itemMngMapper.getItemManagerList(EwpItemManagerRosterVO.builder().officeCode(officeCode).userId(userId).seqMenu(seqMenu).build());
	}
}
