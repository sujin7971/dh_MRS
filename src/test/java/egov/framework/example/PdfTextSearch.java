package egov.framework.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
//import org.apache.log4j.NullAppender;

public class PdfTextSearch {

/*	
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>2.0.26</version>
</dependency> */
	
/*
 * public void main(String[] args) { System.out.println("----- START -----");
 * try { File file = new File("D://BBB.pdf"); PDDocument document =
 * PDDocument.load(file);
 * 
 * 
 * System.out.println("\n PDF Search Result  \n-------------\n");
 * List<Map<String, Object>> textPositionMapList = printSubwords(document,
 * "서론"); //검색어 for(Map<String, Object> textPositionMap : textPositionMapList) {
 * System.out.
 * printf(">>>>>>>>>>>>>  Page %s at %s, %s with width %s and last letter '%s' at %s, %s\n"
 * , textPositionMap.get("pageNum"), textPositionMap.get("hitX"),
 * textPositionMap.get("hitY"), textPositionMap.get("hitWidth"),
 * textPositionMap.get("lastLetter"), textPositionMap.get("lastX"),
 * textPositionMap.get("lastY")); }
 * 
 * 
 * } catch (IOException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); } System.out.println("----- END -----"); }
 * 
 * static List<TextPositionSequence> findSubwords(PDDocument document, int page,
 * String searchTerm) throws IOException { final List<TextPositionSequence> hits
 * = new ArrayList<TextPositionSequence>(); PDFTextStripper stripper = new
 * PDFTextStripper() {
 * 
 * @Override protected void writeString(String text, List<TextPosition>
 * textPositions) throws IOException { TextPositionSequence word = new
 * TextPositionSequence(textPositions); String string = word.toString();
 * 
 * int fromIndex = 0; int index; while ((index = string.indexOf(searchTerm,
 * fromIndex)) > -1) { hits.add(word.subSequence(index, index +
 * searchTerm.length())); fromIndex = index + 1; } super.writeString(text,
 * textPositions); } };
 * 
 * stripper.setSortByPosition(true); stripper.setStartPage(page);
 * stripper.setEndPage(page); stripper.getText(document); return hits; }
 * 
 * static List<Map<String, Object>> printSubwords(PDDocument document, String
 * searchTerm) throws IOException { List<Map<String, Object>>
 * textPositionMapList = new ArrayList<Map<String, Object>>();
 * 
 * 
 * System.out.printf("* Looking for '%s'\n", searchTerm); for (int page = 1;
 * page <= document.getNumberOfPages(); page++) { List<TextPositionSequence>
 * hits = findSubwords(document, page, searchTerm); for (TextPositionSequence
 * hit : hits) { Map<String, Object> textPositionMap = new HashMap<String,
 * Object>(); textPositionMap.put("pageNum", page); TextPosition lastPosition =
 * hit.textPositionAt(hit.length() - 1); textPositionMap.put("hitX",
 * hit.getX()); textPositionMap.put("hitY", hit.getY());
 * textPositionMap.put("hitWidth", hit.getWidth());
 * textPositionMap.put("lastLetter", lastPosition.getUnicode());
 * textPositionMap.put("lastX", lastPosition.getXDirAdj());
 * textPositionMap.put("lastY", lastPosition.getYDirAdj());
 * 
 * textPositionMapList.add(textPositionMap);
 * 
 * } }
 * 
 * return textPositionMapList; }
 */
}
