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

<c:if test='${userDetails.hasRole("ROLE_SYSTEM_ADMIN") or userDetails.hasRole("ROLE_MASTER_ADMIN")}'>
   <c:if test='${userDetails.hasRole("ROLE_MASTER_ADMIN")}'>
      <div class="subTab archiveTab d-none" onclick="location.href='/lime/admin/master/manage/meeting/archive'">파일함 관리</div>
   </c:if>
   <div class="subTab noticeTab" onclick="location.href='/lime/admin/system/manage/notice'">공지사항 관리</div>
   <div class="subTab roomTab" onclick="location.href='/lime/admin/system/manage/room'">장소 관리</div>
</c:if>
<c:if test='${userDetails.hasRole("ROLE_DEV")}'>
   <div class="subTab sessionTab" onclick="location.href='/lime/dev/manage/session'">세션 접속 목록</div>
</c:if>
