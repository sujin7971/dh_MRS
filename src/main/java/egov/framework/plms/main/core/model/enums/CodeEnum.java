package egov.framework.plms.main.core.model.enums;

public interface CodeEnum {
    String getCode();
    static <E extends Enum<E> & CodeEnum> E codeOf(Class<E> enumClass, String code) {
        for (E enumValue : enumClass.getEnumConstants()) {
            if (enumValue.getCode().equals(code)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("No enum member with code: " + code);
    }
}
