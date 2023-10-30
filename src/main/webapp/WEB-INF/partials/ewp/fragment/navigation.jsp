<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- 
* 네비게이션바 
-로그인한 사용자 정보를 가진 loginEmp 변수 설정
-관리자 페이지 링크는 사용자 empAuth값이 8이상인 경우에만 표시
-화면 로드시 현재시간 표시를 위한 타이머 시작을 위해 startClock함수 호출. 본 jsp페이지에서 startClock함수를 호출하여 타이머 재시작 가능
-현재시간 표시를 위해 1초마다 navClock이 호출되고 만약 본 jsp페이지에 clockCallback함수가 정의되어있다면 navClock이 호출
--%>
<!DOCTYPE html>
<c:set var = "userRole" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details}"/>
<c:set var = "loginEmp" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.user}"/>
<c:set var = "loginType" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.loginType}"/>
<div id="gnbScreen">
</div>

<div class="gnbWrapPC">
    <div class="gnbInfo">
    	<c:url value="/home" var="logoUrl"/>
        <div class="ci" onclick="location.href='${logoUrl }'">
            <img src="/resources/meetingtime/ewp/img/ci_m_ewp.png" alt="">
            <!-- <div class="ciSvg ">
                <?xml version="1.0" encoding="UTF-8"?>
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 411.26 220">
                    <g id="a"/>
                            <g id="b">
                            <g id="c">
                            <g>
                                <path class="d" d="M325.13,64.62c36.9,2.19,34.45-53.98,0-55-36.9-2.19-34.45,53.98,0,55Z"/>
                                <path class="d" d="M86.13,64.62c34.45-1.03,36.9-57.19,0-55-34.45,1.03-36.9,57.19,0,55Z"/>
                                <path class="d" d="M206.13,55c35.68,.52,35.68-55.53,0-55-35.68-.52-35.68,55.53,0,55Z"/>
                                <path class="d" d="M373.63,113.77c5.51-37.81-44.95-39.62-70-44.76-27.36-3.77-21.09,29.8-50.08,17.97-1.25-33.02-49.12-22.4-69.92-23.97-13.68,0-24.88,10.57-25.91,23.98-48.09,7.67-5.96-35.98-94.09-9.48-16.58,3.18-27.87,20-26,36.27-50.61,22.36-49.98,57.01,.77,79.07,5.77-28.35,51.77-7.36,69.23-6.35,26.76,5.08,19.64,38.89,49.91,32.77-.45-34.9,96.23-35.11,95.82,.03,45.27-.26,8.95-36.05,94.27-41.27,12.18-2.36,22.4,4.06,25.22,14.82,50.71-22.07,51.43-56.71,.77-79.07ZM85.13,173.62c-34.45-1.03-36.9-57.19,0-55,34.45,1.03,36.9,57.19,0,55Zm120.82,13.74c-35.68,.52-35.68-55.53,0-55,35.68-.52,35.68,55.53,0,55Zm120.18-13.74c-36.9,2.19-34.45-53.98,0-55,36.9-2.19,34.45,53.98,0,55Z"/>
                            </g>
                        </g>
                    </g>
                </svg>
            </div> -->
            <span>스마트 회의시스템</span>
        </div>        
        <div class="lct">
            <div class="pgName">스마트 회의시스템</div>
            <div class="com"><c:out value="${loginEmp.deptName }"/></div>
            <div data-input="userName" data-btn="userName" class="userName"><c:out value="${loginEmp.userName }"/></div>
            <div class="pt-2 d-none">
            	<c:if test='${userRole.hasRole("ROLE_MASTER_ADMIN")}'>
            	<i class="fas fa-crown" title="최고 관리자"></i>
            	</c:if>
            	<c:if test='${userRole.hasRole("ROLE_SYSTEM_ADMIN")}'>
            	<i class="fas fa-key" title="시스템 관리자"></i>
            	</c:if>
            	<c:if test='${userRole.hasPosition("MNG_REQUEST_ROOM")}'>
            	<i class="far fa-calendar-plus" title="기간 신청 담당자"></i>
            	</c:if>
            	<c:if test='${userRole.hasPosition("MNG_APPROVAL")}'>
            	<i class="far fa-calendar-check" title="배정 담당자"></i>
            	</c:if>
            </div>
        </div>
    </div>

    <div class="shy-menu">
        <span data-input="userName" data-btn="userName" class="userNameOnlyAndroid"><c:out value="${loginEmp.userName }"/></span>
        <a class="shy-menu-hamburger">
            <span class="layer top"></span>
            <span class="layer mid"></span>
            <span class="layer btm"></span>
        </a>
        <div class="shy-menu-panel">
            <ul>
            	<c:if test='${userRole.hasRole("ROLE_GENERAL")}'>
	                <li class="mainMenu mn1" title="오늘의 스케줄" onclick="location.href='/ewp/dashboard'">
	                    <i class="fas fa-home-lg-alt"></i>
	                    <span>오늘의 스케줄</span>
	                </li>
	                <c:if test='${ loginType eq "SSO" }'>
	                <li class="mainMenu mn12" title="배정현황" onclick="location.href='/ewp/meeting/assign/status/list'">
	                    <i class="fas fa-th-large"><i class="fas fa-clock"></i></i>
	                    <span>배정현황</span>
	                </li>
	                </c:if>
	                <li class="mainMenu mn15" title="사용신청목록" onclick="location.href='/ewp/meeting/assign/manage/user'">
	                    <i class="fas fa-list"></i>
	                    <span>회의목록</span>
	                </li>
	                <li class="mainMenu mn2" title="파일함" onclick="location.href='/ewp/meeting/archive/manage/user'">
	                    <i class="fas fa-folder-open"></i>
	                    <span>파일함</span>
	                </li>
	                <c:if test='${ loginType eq "SSO" }'>
	                <li class="mainMenu mn3" title="통계" onclick="location.href='/ewp/meeting/stat'">
	                    <i class="fas fa-chart-pie"></i>
	                    <span>통계</span>
	                </li>
	                </c:if>
                </c:if>
                <c:if test='${ loginType eq "SSO" }'>
	                <c:if test='${userRole.hasRole("ROLE_SYSTEM_ADMIN")}'>
	                	<li class="mainMenu mn5" title="권한관리" onclick="location.href='/ewp/admin/system/manage/authority'">
		            		<i class="fas fa-users-cog"></i>
		            		<span>권한관리</span>
		            	</li>
		                <li class="mainMenu mn4" title="관리자" onclick="location.href='/ewp/admin/system/manage/room'">
		               		<i class="fas fa-lock"></i>
		                    <span>관리자</span>
		            	</li>
	                </c:if>
	                <c:if test='${userRole.hasPosition("MNG_APPROVAL")}'>
		                <li class="mainMenu mn6" title="사용신청 관리" onclick="location.href='/ewp/manager/approval/manage/meeting'">
		                    <i class="fas fa-tasks"></i>
		                    <span>사용신청 관리</span>
		                </li>
		            </c:if>
	            </c:if>
	            <c:if test='${userRole.hasRole("ROLE_DEV")}'>
                	<li class="mainMenu mn7" title="인증 정보 변경" onclick="location.href='/ewp/dev/switch-authentication'">
	                    <i class="fas fa-people-arrows"></i>
	                    <span>인증 정보 변경</span>
	                </li>
	                <li class="mainMenu mn5" title="테스트" onclick="location.href='/ewp/home/test'">
	                    <i class="fas fa-vial"></i>
	                    <span>테스트</span>
	                </li>
	            </c:if>
                <c:if test='${ loginType eq "PLTE" }'>
                <li class="mainMenu mn6 userLogout" title="로그아웃" id="logoutBtn">
                    <i class="fas fa-power-off"></i>
                    <span>시스템 로그아웃</span>
                </li>
                </c:if>
            </ul>
            <div class="menuOpen display-none">
                <i class="fas fa-chevron-down"></i>
            </div>
        </div>
    </div>
</div>

<div class="btn-screen full" title="전체화면"></div>
<div class="btn-screen win" title="화면축소" style="display:none"></div>

<%--
<ul class="noticeAlarm" style="display:none">
    <li>
        <div class="cate">시스템 알림</div>
        <div class="cont" onclick="location.href='http://www.naver.com'">파일변환 및 업로드 완료</div>
        <div class="closeBtn" title="알림끄기" onclick="location.href='http://www.daum.net'"><i class="fas fa-times"></i></div>				
    </li>
    <li>
        <div class="cate">시스템 오류</div>
        <div class="cont" onclick="location.href='http://www.naver.com'">파일변환 도중 오류가 발생하여 파일을 업로드 하지 못했습니다. 파일을 다시 확인해 주세요.</div>
        <div class="closeBtn" title="알림끄기" onclick="location.href='http://www.daum.net'"><i class="fas fa-times"></i></div>
    </li>
</ul>
--%>
<%-- modal 참석자 정보 --%>
<div id="userInfoModal" class="modal" data-modal="profile">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="xBtn" data-modal-action="CLOSE"></div>
            <!-- <div class="modalTitle"></div> -->
            <div class="modalBody flex-direction-column align-items-center">
                <div id="userPic" class="userPic"></div>
                <div class="width100p">
                    <p id="deptName" class="uDivision"><c:out value="${loginEmp.deptHierarchyName }"/></p>
                    <p data-input="userName" id="userName" class="userName uNameGrade"><c:out value="${loginEmp.userName }"/></p>
                </div>
                <div class="f2">
                	<p><span>사번</span><span class="uComNum"><c:out value="${loginEmp.userKey }"/></span></p>
                    <p><span>모바일</span><span class="uPhonNum d-inline"><c:out value="${loginEmp.personalCellPhone }"/></span></p>
                    <p><span>이메일</span><span class="uEmail"><c:out value="${loginEmp.email }"/></span></p>
                </div>
            </div>
            <%-- <div class="modalBtnDiv">
                <button class="btn btn-md btn-silver">확 인</button>
            </div> --%>
        </div>
    </div>
</div>
<script>
const loginType = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.loginType}";
const userRole = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.getAuthorities()}";
const loginKey = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.user.userKey}";
</script>   
<script type="module" src="${urls.getForLookupPath('/resources/front-end-assets/js/ewp/partials/fragment/dist/navigation.bundle.js')}"></script>
