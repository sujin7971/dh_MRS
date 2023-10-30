package egov.framework.plms.main.bean.mvc.entity.file.abst;

import egov.framework.plms.main.core.model.enums.file.RelatedEntityType;
import egov.framework.plms.main.core.model.enums.file.RelationType;

public abstract class FileDetailModelVO extends FileInfoModelVO{
	public abstract Integer getRelationId();
	public abstract String getRegisterId();
	public abstract Integer getRelatedEntityId();
	public abstract RelatedEntityType getRelatedEntityType();
	public abstract RelationType getRelationType();
}
