<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- 
* 관리자 페이지 네비게이션바. <div class="subTabDiv"></div> DOM 내부에 include하여 사용. 현재 활성화된 탭 표시를 위해 필요한 CSS SELECTOR는 아래와 같음.

1. 사용신청 처리: approvalTabOpen > approvalTab
2. 파일함 관리: fileTabOpen > fileTab
3. 공지사항 관리: noticeTabOpen > noticeTab
4. 장소 관리: roomTabOpen > roomTab
5. 배정 담당자 관리: managerTabOpen > managerTab
6. 관리자 지정: adminTabOpen > adminTab

CSS는 /resources/front-end-assets/css/main/custom.css 에 설정.
--%>
<!DOCTYPE html>
<c:set var = "userDetails" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details}"/>

 <c:if test='${userDetails.hasPosition("MNG_ITEM_MR")}'>
   <div class="subTab approvalTab" data-room="MEETING_ROOM" onclick="location.href='/ewp/manager/approval/manage/meeting?roomType=MEETING_ROOM'">회의실<br>사용신청목록 관리</div>
</c:if>
<c:if test='${userDetails.hasPosition("MNG_ITEM_ER")}'>
   <div class="subTab approvalTab" data-room="EDU_ROOM" onclick="location.href='/ewp/manager/approval/manage/meeting?roomType=EDU_ROOM'">강의실<br>사용신청목록 관리</div>
</c:if>
<c:if test='${userDetails.hasPosition("MNG_ITEM_HALL")}'>
   <div class="subTab approvalTab" data-room="HALL" onclick="location.href='/ewp/manager/approval/manage/meeting?roomType=HALL'">강당<br>사용신청목록 관리</div>
</c:if>
