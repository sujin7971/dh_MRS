<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<!DOCTYPE HTML>
<html lang="en">
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
	<meta charset="UTF-8"/>
	<meta http-equiv="Content-Type" content="text/html">     
	<meta http-equiv="Content-Security-Policy" 
	content="default-src 'self'; 
	script-src 'self' 'unsafe-inline'; 
	style-src 'self' 'unsafe-inline'; 
	style-src-elem 'self' https://fonts.googleapis.com; 
	font-src 'self' https://fonts.gstatic.com">
	<title>스마트 회의시스템</title>
	<meta name="description" content="SMART MEETING MANAGEMENT SYSTEM">
	<meta name="author" content="BPLMS">
	<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
	<meta name="_csrf" content="${_csrf.token}" />
	<meta name="_csrf_header" content="${_csrf.headerName}" />     
	<!-- Favicon -->
	<link rel="shortcut icon" href="/resources/meetingtime/ewp/img/meetingtime_favicon.ico">
	<!-- CSS -->
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/fa.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/jquery-ui.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/common.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/meetingtime.css">
    <!-- script -->
	<script src="/resources/meetingtime/ewp/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/meetingtime/ewp/js/jquery-ui-datepicker.js"></script>
	<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
	
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/custom.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/ewp/ewp-version-styles.css">
</head>

<body class="mm6">
<jsp:include page="/WEB-INF/partials/ewp/fragment/navigation.jsp"></jsp:include>
<div class="wrapper">
	<div class="titDiv">
		<div class="backBtnDiv"><i class="fal fa-long-arrow-left" onclick="history.back()" title="뒤로"></i></div>
		<div class="pageTit">사용신청</div>
	</div>
	<div class="bodyDiv">
        <div class="scheduleWriteDiv">
			<div class="formLDiv" style="max-width: unset" id="meetingForm">
				<input type="hidden" name="skdKey" value="${skdKey }">
				<input type="hidden" name="appStatus">
				<input type="hidden" name="appComment">
				<div class="row except statusRow" id="approvalRow">
					<div class="item">진행상태</div>
					<div class="answer">
						<div class="status">
							<span></span>
							<div class="ml-auto">
								<c:if test='${authorityCollection.hasAuthority("FUNC_APPROVAL") }'>
								<button type="button" name="approveBtn" class="btn btn-md btn-blue">승 인</button>
								</c:if>
								<c:if test='${authorityCollection.hasAuthority("FUNC_REJECT") }'>
								<button type="button" name="rejectBtn" class="btn btn-md btn-red ml-2">승인불가</button>
								</c:if>
							</div>
						</div>
						<div class="approvalComment" id="approvalComment"></div>
					</div>
				</div>
				<div class="row except">
					<div class="item">
						<span>전자회의 여부</span>
					</div>
					<div class="answer align-items-center">
						<label class="switchElec">
							<input type="checkbox" name="elecYN" value="Y" disabled>
							<span class="slider round"></span>
						</label>
					</div>
				</div>
				<div class="row except">
					<div class="item"><span>사업소</span></div>
					<div class="answer align-items-center">
						<div class="flex-row">
							<div class="selectDiv border-0">
			                    <label id="officeLabel" for="officeSelect" class="ellipsis p-0"></label>
			                    <select name="officeCode" disabled>
									<c:forEach items="${officeList }" var="office">
										<option value="${office.key }">${office.value }</option>
									</c:forEach>
								</select>
			                </div>
		                </div>
					</div>
				</div>
				<div class="row except">
					<div class="item"><span>장 소</span></div>
					<div class="answer align-items-center">
						<div class="flex-row">
							<div class="selectDiv border-0">
			                    <label id="roomTypeLabel" for="roomTypeSelect" class="ellipsis p-0"></label>
			                    <select name="roomType" disabled>
									<option value="MEETING_ROOM">회의실</option>
									<option value="EDU_ROOM">강의실</option>
									<option value="HALL">강당</option>
								</select>
			                </div>
						</div>
		                <div class="flex-row">
							<input type="text" name="roomName" class="border-0 input-md bg-white" readonly></input>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="item"><span>신청자</span></div>
					<div class="answer align-items-center">
						<input type="text" name="nameplate" class="input-md p-0 bg-white border-0 w-max-content" readonly></input>
						<span class="ml-1 interphone"></span>
						<input type="text" name="officeDeskPhone" class="input-md p-0 bg-white border-0" readonly></input>
					</div>
				</div>
				<div class="row">
					<div class="item">
						<span>일 시</span>
					</div>
					<div class="answer align-items-center">
						<%-- <span id="holdingDateInput"></span>
						<span id="beginTimeInput" class="mx-3" /></span>
						<span>~</span>
						<span id="finishTimeInput" class="mx-3" /></span> --%>
						<input type="text" name="holdingDate" class="input-md w-30 text-align-center" readonly>
						<input type="text" name="beginTime" class="input-md text-align-center w-20 mx-1" readonly>
						<span>~</span>
						<input type="text" name="finishTime" class="input-md text-align-center w-20 mx-1" readonly>
					</div>
				</div>
				<div class="row">
					<div class="item"><span>제 목</span></div>
					<div class="answer align-items-center">
						<%-- <span id="titleInput"></span> --%>
						<input type="text" name="title" class="input-lg width100p form-border" readonly> 
					</div>
				</div>
				<div class="row line"></div>
				<div class="row">
					<div class="item">
						<span>주관자</span>
					</div>
					<div class="answer align-items-center">
						<%-- <span id="skdHostInput"></span> --%>
						<input type="text" name="skdHost" class="input-lg width100p form-border" readonly> 
					</div>
				</div>
				<div class="row electContent">
					<div class="item">
						<span>진행자</span>
					</div>
					<div class="answer">
						<div class="inputForm" id="hostDiv">
						
						</div>
					</div>
				</div>
				<div class="row electContent">
					<div class="item">
						<span>참석자</span>
					</div>
					<div class="answer">
						<div class="inputForm" id="attendeeDiv">
							
						</div>
					</div>
				</div>
				<div class="row except">
					<div class="item">
						<span>참석인원</span>
					</div>
					<div class="answer align-items-center">
						<%-- <span id="attendeeCntInput"></span> --%>
						<input type="text" name="attendeeCnt" class="input-lg" readonly>
					</div>
				</div>
				<div class="row line"></div>
				<div class="row">
					<div class="item"><span>기타요구사항</span></div>
					<div class="answer align-items-center">
						<%-- <span id="contentsInput"></span> --%>
						<textarea rows="2" name="contents" class="width100p" readonly></textarea>
					</div>
				</div>
			</div>
		</div>
	</div>	
	<div class="pageBtnDiv adminUser" id="pageBtnForm"><!-- 관리자의 버튼. 승인/승인불가 -->
		<div class="f2">
			<button type="button" name="backBtn" class="btn btn-lg btn-silver" onclick="history.back()">뒤 로</button>
			<button type="button" name="editBtn" class="btn btn-lg btn-white">수 정</button>
			<button type="button" name="saveBtn" class="btn btn-lg btn-blue">저 장</button>
			<c:if test='${authorityCollection.hasAuthority("FUNC_DELETE") }'>
			<button type="button" name="deleteBtn" class="btn btn-lg btn-red">삭 제</button>
			</c:if>
		</div>		
	</div>
</div>
<script type="module" src="${urls.getForLookupPath('/resources/front-end-assets/js/ewp/page/meeting/assign/dist/assign_approval.bundle.js')}"></script>
</body>
</html>