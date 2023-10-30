package egov.framework.plms.main.bean.mvc.entity.file.abst;

import egov.framework.plms.main.core.model.enums.file.RelatedEntityType;
import egov.framework.plms.main.core.model.enums.file.RelationType;

public abstract class FileDetailModelDTO extends FileInfoModelDTO{
	public abstract Integer getRelationId();
	public abstract String getRegisterId();
	public abstract Integer getRelatedEntityId();
	public abstract RelatedEntityType getRelatedEntityType();
	public abstract RelationType getRelationType();
	
	public abstract void setRelationId(Integer value);
	public abstract void setRegisterId(String value);
	public abstract void setRelatedEntityId(Integer value);
	public abstract void setRelatedEntityType(RelatedEntityType value);
	public abstract void setRelationType(RelationType value);
	
}
