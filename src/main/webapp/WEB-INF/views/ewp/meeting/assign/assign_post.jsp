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
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/custom.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/ewp/ewp-version-styles.css">
    <!-- script -->
	<script src="/resources/meetingtime/ewp/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/meetingtime/ewp/js/jquery-ui.js"></script>
	<script src="/resources/meetingtime/ewp/js/jquery-ui-datepicker.js"></script>
	<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
	
	<%-- timepicker --%>
	<link rel="stylesheet" href="/resources/library/timepicker/jquery.timepicker.min.css">
	<script src="/resources/library/timepicker/jquery.timepicker.min.js"></script>
</head>

<body class="mm1">
<c:set var = "userRole" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details}"/>
<c:set var="writer" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.user}"></c:set>
<jsp:include page="/WEB-INF/partials/ewp/fragment/navigation.jsp"></jsp:include>
<div class="wrapper">
	<input id="skdKeyInput" type="hidden" value="${skdKey}"/>
	<input id="meetingKeyInput" type="hidden"/>
	<input id="roomKeyInput" type="hidden"/>
	<input id="writerKeyInput" type="hidden" value="${writer.userKey}">
	<input id="beginDateTimeInput" type="hidden"/>
	<input id="finishDateTimeInput" type="hidden"/>
	
	<div class="titDiv">
		<div class="backBtnDiv"><i class="fal fa-long-arrow-left" onclick="history.back()" title="뒤로"></i></div>
        <div class="pageTit"><span>사용신청</span></div>
        <div class="comment"><!--즉시회의에서는 안내멘트 예외항목 add class "display-none" -->
			<span class="mandatoryInput margin-right-8"></span>
			표시는 필수 입력사항입니다.
		</div>
		<button id="loadInfoBtn" class="btn btn-blue btn-sm margin-left-40">이전 신청내용으로 작성하기</button>
		<div class="comment ml-auto">배정 담당자 : <span id="managerName"></span> <span class="interphone" id="managerTel"></span></div>
	</div>
	<div class="bodyDiv">
        <div class="scheduleWriteDiv"><!--------  전자회의 입력사항은 row 에 class="electContent" 를 추가하세요-------->
			<div class="formLDiv">	
				<c:if test='${mode eq "put" }'>
				<div class="row except statusRow">
					<div class="item">진행상태</div>
					<div class="answer">
						<div class="status"><span></span></div>
						<div class="approvalComment" id="commentInput"></div>
					</div>
				</div>
				</c:if>
				<div class="row except">
					<div class="item">
						<span class="hostInput">전자회의 여부</span>
					</div>
					<div class="answer align-items-center">
						<label for="switchElec" class="switchElec">
							<input type="checkbox" id="switchElec">
							<span class="slider round"></span>
						</label>
					</div>
				</div>
				<div class="row infoMent">
					<div class="item"></div>
					<div class="answer">전자회의는 자료파일을 업로드하고 참석자들과 공유 및 개별 판서기능을 제공합니다.</div>
				</div>
				<div class="row">
					<div class="item"><span>사업소</span></div>
					<div class="answer align-items-center">
						<div class="flex-row">
							<div class="selectDiv border-0">
			                    <label for="officeSelect" class="ellipsis p-0" disabled></label>
			                    <select id="officeSelect">
									<c:forEach items="${officeBook }" var="office">
										<option value="${office.key }">${office.value }</option>
									</c:forEach>
								</select>
			                </div>
		                </div>
					</div>
				</div>
				<div class="row">
					<div class="item"><span>장 소</span></div>
					<div class="answer align-items-center">
						<div class="flex-row">
							<div class="selectDiv border-0">
			                    <label for="roomTypeSelect" class="ellipsis p-0" disabled></label>
			                    <select id="roomTypeSelect">
									<option value="MEETING_ROOM">회의실</option>
									<option value="EDU_ROOM">강의실</option>
									<option value="HALL">강당</option>
								</select>
			                </div>
						</div>
		                <div class="flex-row">
							<span id="roomInput"></span>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="item"><span>신청자</span></div>
					<div class="answer align-items-center">
						<span id="writerInput"><c:out value="${writer.nameplate }"/></span>
						<span id="writerTelInput" class="ml-1 interphone"><c:out value="${writer.officeDeskPhone}"/></span>
					</div>
				</div>
				<div class="row infoMent">
					<div class="item"></div>
					<div class="answer">회의실 사용을 위해 사용신청을 작성하는 사용자를 지칭합니다.</div>
				</div>
				<div class="row">
					<div class="item">
						<span class="mandatoryInput">일 시</span>
					</div>
					<div class="answer align-items-center">
						<div data-input="holdingDate" id="holdingDateDiv" class="w-30">
						<input type="text" data-input="time" id="holdingDateInput" class="input-md w-100 text-align-center" inputmode='none'>
						</div>
						<input id="beginTimeInput" type="text" class="input-md text-align-center w-20 ml-1" inputmode='none'/>
						<span class="mx-1">~</span>
						<c:if test='${userRole.hasPosition("MNG_REQUEST_ROOM") and mode eq "post"}'>
							<div data-input="endDate" id="endDateDiv" class="w-30">
							<input type="text" data-input="time" id="endDateInput" class="input-md w-100 text-align-center" inputmode='none'>
							</div>
						</c:if>
						<input id="finishTimeInput" type="text" class="input-md text-align-center w-20 ml-1" inputmode='none'/>
					</div>
				</div>
				<div class="row">
					<div class="item"><span class="mandatoryInput">제 목</span><span class="input-limit" id="titleLength">0/30</span></div>
					<div class="answer input-delete">
						<input type="text" class="input-lg width100p" id="titleInput" maxlength="30" placeholder="30자 이내"> 
						<span id="titleInputClear"></span>
					</div>
				</div>
				<div class="row line"></div>
				<div class="row">
					<div class="item">
						<span class="mandatoryInput">주관자</span>
						<span class="input-limit" id="skdHostLength">0/10</span>
					</div>
					<div class="answer input-delete">
						<input type="text" class="input-lg width100p" id="skdHostInput" maxlength="10" placeholder="회의 주관자를 입력해 주세요"> 
						<span id="skdHostInputClear"></span>
					</div>
				</div>
				<div class="row electContent">
					<div class="item">
						<span class="mandatoryInput">진행자</span>
						<button type="button" class="btn btn-sm btn-blue-border" data-btn="hostBtn">사용자 선택</button>
					</div>
					<div class="answer">
						<div class="inputForm" id="hostDiv">
						
						</div>
						<button type="button" class="btn btn-lg btn-blue-border btn-add-lg" data-btn="hostBtn" title="회의 진행자 선택"></button>
					</div>
				</div>
				<div class="row electContent infoMent">
					<div class="item"></div>
					<div class="answer">실제로 회의를 진행하는 사용자로서 참석자확인, 사용자초대, 회의록작성, 보조진행 지정 등의 권한을 가집니다.</div>
				</div>
				<div class="row electContent">
					<div class="item">
						<span>참석자</span>
						<button type="button" class="btn btn-sm btn-blue-border" data-btn="attendeeBtn">사용자 선택</button>
					</div>
					<div class="answer">
						<div class="inputForm" id="attendeeDiv">
							
						</div>
						<button type="button" class="btn btn-lg btn-blue-border btn-add-lg" data-btn="attendeeBtn"></button>
					</div>
				</div>
				<%-- <div class="row electContent">
					<div class="item">
						<span>참관자</span>
						<button type="button" class="btn btn-sm btn-blue-border" data-btn="observerBtn">사용자 선택</button>
					</div>
					<div class="answer">
						<div class="inputForm" id="observerDiv">
							
						</div>
						<button type="button" class="btn btn-lg btn-blue-border btn-add-lg" data-btn="observerBtn"></button>
					</div>
				</div> --%>
				<div class="row except">
					<div class="item">
						<span>참석인원</span>
					</div>
					<div class="answer tot">
						<span>총</span>
						<input type="text" id="attendeeCntInput" class="input-lg mx-1" maxlength="3"><!-- 전자회의를 제외한 회의실 강의실 강당은 수동입력 -->
						<span>명</span>
					</div>
				</div>
				<div class="row line"></div>
				
				<div class="row">
					<div class="item"><span>기타요구사항</span><span class="input-limit" id="contentsLength">0/100</span></div>
					<div class="answer input-delete">
						<textarea rows="2" class="width100p" id="contentsInput" maxlength="100" placeholder="100자 이내로 입력하세요. 선택사항입니다."></textarea>
						<span id="contentsInputClear""></span>
					</div>
				</div>
				<div class="row except electContent">
					<div class="item"><span>알림전송</span></div>
					<div class="answer">
						<div class="checkboxDiv">
							<label for="switchMessenger"><input type="checkbox" id="switchMessenger"><span><span class="dot"></span>관련자 메신저알림</span></label>
							<%-- <label for="switchMail"><input type="checkbox" id="switchMail"><span><span class="dot"></span>관련자 메일알림</span></label> --%>
							<label for="switchSms"><input type="checkbox" id="switchSms"><span><span class="dot"></span>관련자 SMS 알림</span></label>
						</div>						
					</div>
				</div> 
				<div class="row infoMent electContent">
					<div class="item"></div>
					<div class="answer">사용신청 확정 시 관련자에게 전송되며, 관련자란 신청자가 위에서 입력한 모든 사용자를 말합니다.</div>
				</div>
				<%-- <div class="row line"></div>
				<div class="row except">
					<div class="item"><span>신청자</span></div>
					<div class="answer">
						<div class="inputForm nonStyle"> (<a href="officeDeskPhone:<c:out value="${schedule.writer.contactNumber }"/>"><c:out value="${schedule.writer.contactNumber }"/></a>)</div>
					</div>
				</div> --%>
				<div class="row line electContent"></div>
			</div>
			
			<div class="formRDiv electContent">
				<div class="row">
					<div class="item">
						<div class="d-flex flex-row mr-auto">
							<h4>첨부파일</h4>
							<div class="mx-1 fw-normal">
							<span id="fileSize">0</span><span> / 300MB</span>
							</div>
							<div class="mx-1 fw-normal">
							<span id="fileCount">0</span><span> / 10개</span>
							</div>
						</div>
						<button type="button" class="btn btn-sm btn-blue-border" id="uploadBtn">파일첨부</button>
						<button type="button" class="btn btn-sm btn-red" id="fileDeleteAll">전체삭제</button>
					</div>
					<div class="answer">
						<div class="inputForm">
							<ul id="fileContainer">
							</ul>							
						</div>
					</div>
					<div class="infoMentDiv m-0">
						<p>첨부파일 지원 : 한글(HWP, HWPX), PDF, MS OFFICE 문서(PPT, PPTX, DOC, DOCX, XLS, XLSX), 이미지(JPG, JPEG, GIF, PNG, BMP) </p>
					</div>
					<div class="infoMentDiv m-0">
						<p>파일 선택 후 파일이 표시될 때까지 잠시 딜레이가 발생할 수 있습니다. 이는 브라우저 및 운영 체제에 따라 다소 차이가 있을 수 있습니다. </p>
					</div>
				</div>
				<div class="row except flex-direction-row">
					<div class="item">
						<div class="tit"><span>보안관련 설정</span></div>
					</div>
					<div class="answer align-items-center">
						<label for="switchSecret" class="switch">
							<input type="checkbox" id="switchSecret">
							<span class="slider round"></span>
						</label>
					</div>
				</div>
				<div class="row">
					<section class="secuMon" style="display:none">
						<div class="infoMentDiv">
							<p>"기밀회의" 로 설정되면 회의 종료 후 모든 자료 (첨부파일, 회의진행 중 생성된 모든 파일, 참석자목록) 가 파기되어 회의 진행자 까지도 회의관련된 자료의 열람이 불가합니다.<br>
								단, 회의 자체의 히스토리(일시,장소,제목,주관자)는 보관됩니다.</p>
						</div><div class="infoMentDiv">
							<p>"기밀회의" 로 설정되면 회의 종료 후 모든 자료 (첨부파일, 회의진행 중 생성된 모든 파일, 참석자목록) 가 파기되어 회의 진행자 까지도 회의관련된 자료의 열람이 불가합니다.<br>
								단, 회의 자체의 히스토리(일시,장소,제목,주관자)는 보관됩니다.</p>
						</div>
						<div class="infoMentDiv">
							<p>기밀회의 대상</p>
							<p>회의내용 외부유출시 국가안전보장에 해를 끼치거나 업무상 차질, 혼선이 초래되는 회의</p>
							<p>회사와 정부(타 기관과) 실시하는 중요정책 등 결정, 공유를 위한 회의</p>
							<p>회의결과 등록시 사내열람등급 3등급 이상인 회의<br>
							예)보안심사위원회, 인사위원회, 계약심의위원회, 입찰제안서 평가위원회, 투자심의 위원회 등</p>
						</div>
					</section>					
					<section class="secuMoff">
						<div class="infoMentDiv">
							<p>
							회의종료 후, 해당 회의의 정보 및 자료의 공개범위를 지정할 수 있습니다.<br>
							아래에서 공개대상 범위를 선택하세요.<br>
							<%-- "동 부서원 포함"은 공개대상자의 같은 부서원 모두에게 공개함을 의미합니다. --%>
							</p>
						</div>
						<div class="answer checkDiv">
							<label for="switchHostSecuLvl" class="dupCK01">
								<input type="checkbox" id="switchHostSecuLvl" checked disabled>
								<span>
									공개대상 : 진행자
									<label for="switchHostDeptSecuLvl" class="hostSecu d-none">
										<input type="checkbox" id="switchHostDeptSecuLvl">
										<span>동 부서원 포함</span>
									</label>
								</span>
							</label>
						</div>
						<div class="answer checkDiv margin-top-8">
							<label for="switchAttendeeSecuLvl" class="dupCK02">
								<input type="checkbox" id="switchAttendeeSecuLvl" checked>
								<span>
									공개대상 : 참석자
									<label for="switchAttendeeDeptSecuLvl" class="d-none">
										<input type="checkbox" id="switchAttendeeDeptSecuLvl">
										<span>동 부서원 포함</span>
									</label>
								</span>
							</label>
						</div>
						<%-- <div class="answer checkDiv margin-top-8">
							<label for="switchObserverSecuLvl" class="dupCK03" >
								<input type="checkbox" id="switchObserverSecuLvl">
								<span>
									공개대상 : 참관자
									<label for="switchObserverDeptSecuLvl">
										<input type="checkbox" id="switchObserverDeptSecuLvl">
										<span>동 부서원 포함</span>
									</label>
								</span>
							</label>
						</div> --%>
					</section>
				</div>
			</div>
		</div>
	</div>	
	<div class="pageBtnDiv">
		<button class="btn btn-lg btn-silver" id="cancelBtn">취 소</button>
		<button class="btn btn-lg btn-blue" id="saveBtn">저 장</button>
	</div>
</div>
<!-- modal 회의등록완료 -->
<div id="saveModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <!-- <div class="modalTitle"></div> -->
            <div class="modalBody flex-direction-column">
				<div class="commonMent">
                    <p>회의가 등록되었습니다.</p>
                </div>				
            </div>
            <div class="modalBtnDiv">
                <div class="btn btn-md btn-blue" onclick="location.href='schedule.htm'">확 인</div>
            </div>
        </div>
    </div>
</div>
<div id="datePickerModal" class="modal">
	<div class="modalWrap">
        <div class="modal_content">
            <div class="modalBody flex-direction-column align-items-center">
               <div class="calendarDiv" id="datepicker"></div>
            </div>
            <div class="modalBtnDiv">
                <button type="button" class="btn btn-md btn-silver" data-modal-btn="CANCEL">취 소</button>
                <button type="button" class="btn btn-md btn-blue" data-modal-btn="OK">확 인</button>
            </div>
        </div>
    </div>
</div>
<div id="endDatePickerModal" class="modal">
	<div class="modalWrap">
        <div class="modal_content">
            <div class="modalBody flex-direction-column align-items-center">
               <div class="calendarDiv" id="endDatepicker"></div>
            </div>
            <div class="modalBtnDiv">
                <button type="button" class="btn btn-md btn-silver" data-modal-btn="CANCEL">취 소</button>
                <button type="button" class="btn btn-md btn-blue" data-modal-btn="OK">확 인</button>
            </div>
        </div>
    </div>
</div>
<div id="callRegModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalTitle">이전 신청 목록</div>
            <div class="modalBody flex-direction-column">
				<table class="style1">
					<thead>
						<tr>
							<td>결재</td>
							<td>회의일시</td>
							<td>회의제목</td>
							<td>주관자</td>
							<td>진행자</td>
							<td>참석자</td>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
            </div>
            <div class="modalBtnDiv">
                <div class="btn btn-md btn-silver" data-modal-btn="CLOSE">닫 기</div>
            </div>
        </div>
    </div>
</div>
<%-- 예약된 회의정보 --%>
<div id="infoReservedMeeting" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalTitle">신청내용 불러오기</div>
            <div class="modalBody flex-direction-column align-items-center">
            	<div class="commonMent">
                    <b>현재 작성하던 내용에 덮어씁니다. 아래 신청정보를 적용하시겠습니까?</b>
                </div>
                <div class="f2">
                    <div class="row">
                        <div class="item">일 정</div>
                        <div class="answer">
                            <span id="scheduleInput"></span>
                        </div>
                    </div>
                    <div class="row">
                        <div class="item">제 목</div>
                        <div class="answer">
                            <span id="titleInput"></span>
                        </div>
                    </div>
                    <div class="row">
                        <div class="item">주관자</div>
                        <div class="answer">
                            <span id="hostInput"></span>
                        </div>
                    </div>
                    <div class="row" data-row="facilitator">
                        <div class="item">진행자</div>
                        <div class="answer">
                            <span id="facilitatorNameplateInput"></span>
                        </div>
                    </div>
                    <div class="row" data-row="attendeeList">
                        <div class="item">참석자</div>
                        <div class="answer">
                            <div id="attendeeListInput"></div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-silver flex1" data-modal-btn="CANCEL">취 소 </button>
                <button class="btn btn-md btn-blue flex1" data-modal-btn="OK">확 인</button>
            </div>
        </div>
    </div>
</div>
<script>
const skdKey = "${skdKey}";
const OPEN_TIME = '${OPEN_TIME}';
const CLOSE_TIME = '${CLOSE_TIME}';
const INTERVAL_MINUTE = '${INTERVAL_MINUTE}';
const authorities = '${authorityCollection}';
const roomType = "${roomType}";
const roomKey = "${roomKey}";
const holdingDate = "${holdingDate}";
const startTime = "${startTime}";
const endTime = "${endTime}";
const isPost = (skdKey)?false:true;
//보안 토큰
const token = $("meta[name='_csrf']").attr("content");
const header = $("meta[name='_csrf_header']").attr("content");
$.ajaxSetup({
	beforeSend: function(xhr, settings) {
		if (!/^(GET|HEAD|OPTIONS|TRACE)$/i.test(settings.type) && !this.crossDomain) {
			xhr.setRequestHeader(header, token);
        }
	}
});
</script>
<script type="module" src="${urls.getForLookupPath('/resources/front-end-assets/js/ewp/page/meeting/assign/dist/assign_post.bundle.js')}"></script>
</body>
</html>