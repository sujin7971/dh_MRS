package egov.framework.plms.main.bean.mvc.service.file;

import org.springframework.stereotype.Service;

import egov.framework.plms.main.bean.mvc.entity.file.FileRelationVO;
import egov.framework.plms.main.bean.mvc.mapper.file.FileRelationMapper;
import egov.framework.plms.main.core.model.enums.file.RelatedEntityType;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mckim
 * @version 1.0
 * @since 2023. 2. 15
 */
/**
 * @author mckim
 * @version 1.0
 * @since 2023. 2. 15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileRelationService {
	private final FileRelationMapper mapper;
	
	public boolean insertFileRelationOne(Integer fileId, RelatedEntityType entityType, Integer relatedEntityId, RelationType relationType) {
		FileRelationVO params = FileRelationVO.builder().fileId(fileId).relatedEntityType(entityType).relatedEntityId(relatedEntityId).relationType(relationType).build();
		try{
			Integer result = mapper.insertFileRelationOne(params);
			return (result == 0)?false:true;
		}catch(Exception e) {
			log.error("Failed to insert one FileRelation with params: {}", params);
			log.error("Failed to insert one FileRelation messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean deleteFileRelationOne(Integer relationId) {
		try{
			Integer result = mapper.deleteFileRelationOne(relationId);
			return (result == 0)?false:true;
		}catch(Exception e) {
			log.error("Failed to delete one FileRelation with id: {}", relationId);
			log.error("Failed to delete one FileRelation messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean deleteFileRelationList(Integer relationId) {
		return deleteFileRelationList(FileRelationVO.builder().relationId(relationId).build());
	}
	
	public boolean deleteFileRelationList(Integer fileId, RelatedEntityType entityType, Integer relatedEntityId) {
		return deleteFileRelationList(FileRelationVO.builder().fileId(fileId).relatedEntityType(entityType).relatedEntityId(relatedEntityId).build());
	}
	
	public boolean deleteFileRelationList(Integer fileId, RelatedEntityType entityType, Integer relatedEntityId, RelationType relationType) {
		return deleteFileRelationList(FileRelationVO.builder().fileId(fileId).relatedEntityType(entityType).relatedEntityId(relatedEntityId).relationType(relationType).build());
	}
	
	public boolean deleteFileRelationList(FileRelationVO params) {
		try{
			Integer result = mapper.deleteFileRelationList(params);
			return (result == 0)?false:true;
		}catch(Exception e) {
			log.error("Failed to delete list FileRelation with params: {}", params);
			log.error("Failed to delete list FileRelation messages: {}", e.toString());
			e.printStackTrace();
			return false;
		}
	}
}
