package egov.framework.plms.main.bean.mvc.entity.file;

import java.util.Optional;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.enums.file.RelatedEntityType;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class FileRelationVO implements Convertable<FileRelationDTO>{
	private Integer relationId;
	private String registerId;
	private Integer fileId;
	private FileInfoVO file;
	private Integer relatedEntityId;
	private RelatedEntityType relatedEntityType;
	private RelationType relationType;
	
	@Builder(builderClassName = "init", builderMethodName = "initVO")
	public FileRelationVO(FileRelationDTO dto) {
		this.relationId = dto.getRelationId();
		this.registerId = dto.getRegisterId();
		this.fileId = dto.getFileId();
		this.file = Optional.ofNullable(dto.getFile()).map(FileInfoDTO::convert).orElse(null);
		this.relatedEntityId = dto.getRelatedEntityId();
		this.relatedEntityType = dto.getRelatedEntityType();
		this.relationType = dto.getRelationType();
	}
	
	@Override
	public FileRelationDTO convert() {
		return FileRelationDTO.initDTO().vo(this).build();
	}
	
}
