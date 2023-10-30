package egov.framework.plms.main.core.xss;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;

/**
 * XSS 방지를 위한 문자열 ESCAPE처리 정책 클래스
 * @author mckim
 * @version 1.0
 * @since 2022. 11. 22
 */
public class HTMLCharacterEscapes extends CharacterEscapes {

    private final int[] asciiEscapes;

    private final CharSequenceTranslator translator;

    public HTMLCharacterEscapes() {

        /**
        * 여기에서 커스터마이징 가능
        * 이전의 Apache Commons Lang3는 LookupTranslator의 파라미터로
        * String[][]을 전달하였으나, 새로운 Apache Commons Text는 파라미터로
        * Map<CharSequence, CharSequence>를 전달해야 한다.
        * */

    	Map<CharSequence, CharSequence> customMap = new HashMap<CharSequence, CharSequence>();
        customMap.put("\'", "&apos;");
        //customMap.put("(", "&#40;");
        //customMap.put(")", "&#41;");
        //customMap.put("#", "&#35;");
        Map<CharSequence, CharSequence> CUSTOM_ESCAPE = Collections.unmodifiableMap(customMap);
        
        // 1. XSS 방지 처리할 특수 문자 지정
        asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
        asciiEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['&'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\"'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['('] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes[')'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['#'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;

        // 2. XSS 방지 처리 특수 문자 인코딩 값 지정
        translator = new AggregateTranslator(
                new LookupTranslator(EntityArrays.BASIC_ESCAPE),  // <, >, &, " 는 여기에 포함됨
                new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE),
                new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE),
                new LookupTranslator(CUSTOM_ESCAPE)
        );

    }

    @Override
    public int[] getEscapeCodesForAscii() {
        return asciiEscapes;
    }

    @Override
    public SerializableString getEscapeSequence(int ch) {
        return new SerializedString(translator.translate(Character.toString((char) ch)));
        // 참고 - 커스터마이징이 필요없다면 아래와 같이 Apache Commons Text에서 제공하는 메서드를 써도 된다.
        // return new SerializedString(StringEscapeUtils.escapeHtml4(Character.toString((char) ch)));
    }
}
