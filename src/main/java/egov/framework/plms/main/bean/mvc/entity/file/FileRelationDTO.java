package egov.framework.plms.main.bean.mvc.entity.file;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

import egov.framework.plms.main.core.model.able.Convertable;
import egov.framework.plms.main.core.model.enums.file.RelatedEntityType;
import egov.framework.plms.main.core.model.enums.file.RelationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileRelationDTO implements Convertable<FileRelationVO>{
	private Integer relationId;
	private String registerId;
	private Integer fileId;
	private FileInfoDTO file;
	private Integer relatedEntityId;
	private RelatedEntityType relatedEntityType;
	private RelationType relationType;
	
	@Builder(builderClassName = "init", builderMethodName = "initDTO")
	public FileRelationDTO(FileRelationVO vo) {
		this.relationId = vo.getRelationId();
		this.registerId = vo.getRegisterId();
		this.fileId = vo.getFileId();
		this.file = Optional.ofNullable(vo.getFile()).map(FileInfoVO::convert).orElse(null);
		this.relatedEntityId = vo.getRelatedEntityId();
		this.relatedEntityType = vo.getRelatedEntityType();
		this.relationType = vo.getRelationType();
	}
	
	@Override
	public FileRelationVO convert() {
		return FileRelationVO.initVO().dto(this).build();
	}
}
