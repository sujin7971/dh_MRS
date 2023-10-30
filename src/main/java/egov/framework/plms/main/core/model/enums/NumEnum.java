package egov.framework.plms.main.core.model.enums;

public interface NumEnum {
    Integer getCode();
    static <E extends Enum<E> & NumEnum> E codeOf(Class<E> enumClass, Integer code) {
        for (E enumValue : enumClass.getEnumConstants()) {
            if (enumValue.getCode().equals(code)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("No enum member with code: " + code);
    }
}
