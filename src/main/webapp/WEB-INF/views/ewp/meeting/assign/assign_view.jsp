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

<body class="mm1">
<jsp:include page="/WEB-INF/partials/ewp/fragment/navigation.jsp"></jsp:include>
<c:set var = "loginType" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.loginType}"/>
<div class="wrapper">
<form id="testF" onsubmit="return false;">
	</form>
	<div class="titDiv">
		<%-- <div class="backBtnDiv"><i class="fal fa-long-arrow-left" onclick="history.back()" title="뒤로"></i></div> --%>
		<div class="backBtnDiv"><i class="fal fa-long-arrow-left" onclick="#" id="backArrow" title="뒤로"></i></div>
		<div class="pageTit">사용신청</div>
	</div>
	<div class="bodyDiv">
        <div class="scheduleWriteDiv">
			<div class="formLDiv">
				<div class="row except statusRow">
					<div class="item">진행상태</div>
					<div class="answer">
						<div class="status"><span></span></div>
						<div class="approvalComment" id="commentInput"></div>
					</div>
				</div>
				<div class="row except">
					<div class="item">
						<span>전자회의 여부</span>
					</div>
					<div class="answer align-items-center">
						<label for="switchElec" class="switchElec">
							<input type="checkbox" id="switchElec">
							<span class="slider round"></span>
						</label>
					</div>
				</div>
				<div class="row except">
					<div class="item"><span>사업소</span></div>
					<div class="answer align-items-center">
						<div class="flex-row">
							<div class="selectDiv border-0">
			                    <label for="officeSelect" class="ellipsis p-0" disabled></label>
			                    <select id="officeSelect">
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
						<span id="writerInput"></span>
						<span id="writerTelInput" class="ml-1 interphone"></span>
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
						<input type="text" data-input="time" id="holdingDateInput" class="input-md w-30 text-align-center" readonly>
						<input id="beginTimeInput" type="text" class="input-md text-align-center w-20 mx-1" readonly>
						<span>~</span>
						<input id="finishTimeInput" type="text" class="input-md text-align-center w-20 mx-1" readonly>
					</div>
				</div>
				<div class="row">
					<div class="item"><span>제 목</span></div>
					<div class="answer align-items-center">
						<%-- <span id="titleInput"></span> --%>
						<input type="text" class="input-lg width100p" id="titleInput" readonly> 
					</div>
				</div>
				<div class="row line"></div>
				<div class="row">
					<div class="item">
						<span>주관자</span>
					</div>
					<div class="answer align-items-center">
						<%-- <span id="skdHostInput"></span> --%>
						<input type="text" class="input-lg width100p" id="skdHostInput" readonly> 
					</div>
				</div>
				<div class="row electContent">
					<div class="item">
						<span>회의 진행자</span>
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
				<%-- <div class="row electContent">
					<div class="item">
						<span>참관자</span>
					</div>
					<div class="answer">
						<div class="inputForm" id="observerDiv">
						</div>
					</div>
				</div> --%>
				<div class="row except">
					<div class="item">
						<span>참석인원</span>
					</div>
					<div class="answer align-items-center tot">
						<%-- <span id="attendeeCntInput"></span> --%>
						<span>총</span>
						<input type="text" id="attendeeCntInput" class="input-lg" readonly disabled>
						<span>명</span>
					</div>
				</div>
				<div class="row line"></div>
				<div class="row">
					<div class="item"><span>기타요구사항</span></div>
					<div class="answer align-items-center">
						<%-- <span id="contentsInput"></span> --%>
						<textarea rows="2" class="width100p" id="contentsInput" readonly></textarea>
					</div>
				</div>
				<div class="row except electContent">
					<div class="item"><span>알림전송</span></div>
					<div class="answer">
						<div class="checkboxDiv">
							<label for="switchMessenger"><input type="checkbox" id="switchMessenger" checked><span><span class="dot"></span>관련자 메신저알림</span></label>
							<%-- <label for="switchMail"><input type="checkbox" id="switchMail" checked><span><span class="dot"></span>관련자 메일알림</span></label> --%>
							<label for="switchSms"><input type="checkbox" id="switchSms" checked><span><span class="dot"></span>관련자 SMS 알림</span></label>
						</div>						
					</div>
				</div>
				<div class="row line electContent"></div>
			</div>

			<div class="formRDiv electContent">
				<div class="row">
					<div class="item">
						<div class="tit">
							<span>첨부파일</span>
							<div id="fileInfoBtn" class="btn-help fileStateBtn margin-left-4 margin-right-auto" title="HELP"></div>
						</div>
						<c:if test='${ loginType eq "SSO" }'>
						<button class="btn btn-sm btn-blue-border" id="fileDownBtn">선택파일 다운로드</button>
						</c:if>
						<!-- <button class="btn btn-sm btn-red" id="fileDelBtn">선택파일 삭제</button> -->
					</div>
					<div class="answer">
						<table class="style1">
                            <colgroup>
                                <col width="44px">
                                <col width="66px">
                                <col>
                            </colgroup>
                            <thead>
                                <tr>
                                    <td class="text-align-center"><input type="checkbox" id="allChk"></td>
                                    <td>구분</td>
                                    <td>파일명</td>
                                </tr>
                            </thead>
                            <tbody id="fileContainer">
								
                            </tbody>
                        </table>
					</div>
					<c:if test='${authorityCollection.hasAuthority("FUNC_REPORT") || reportAuthorityCollection.hasAuthority("FUNC_OPINION") }'>
					<div class="btnRow">
						<button type="button" id="reportBtn" class="btn btn-md btn-blue">회의록</button>
					</div>
					</c:if>
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
						<div class="infoMentDiv">
							<p>기밀회의 대상</p>
							<p>회의내용 외부유출시 국가안전보장에 해를 끼치거나 업무상 차질, 혼선이 초래되는 회의</p>
							<p>회사와 정부(타 기관과) 실시하는 중요정책 등 결정, 공유를 위한 회의</p>
							<p>회의결과 등록시 사내열람등급 3등급 이상인 회의<br>
							예)보안심사위원회, 인사위원회, 계약심의위원회, 입찰제안서 평가위원회, 투자심의 위원회 등</p>
						</div>
						<div class="answer checkDiv">
							<label for="switchHostSecuLvl" class="dupCK01">
								<input type="checkbox" id="switchHostSecuLvl">
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
								<input type="checkbox" id="switchAttendeeSecuLvl">
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
		<button class="btn btn-lg btn-silver" id="backBtn">뒤 로</button>
		<c:if test='${authorityCollection.hasAuthority("FUNC_UPDATE") && loginType eq "SSO" }'>
		<button class="btn btn-lg btn-white" id="updateBtn">수 정</button>
		</c:if>
		<c:if test='${authorityCollection.hasAuthority("FUNC_FINISH") && loginType eq "SSO" }'>
		<button class="btn btn-lg btn-blue" id="finishBtn">회의종료</button>
		</c:if>
		<c:if test='${authorityCollection.hasAuthority("FUNC_DELETE") && loginType eq "SSO" }'>
		<button class="btn btn-lg btn-red" id="deleteBtn">삭 제</button>
		</c:if>
		<c:if test='${authorityCollection.hasAuthority("FUNC_CANCEL") && loginType eq "SSO" }'>
		<button class="btn btn-lg btn-red-border" id="cancelBtn">사용취소</button>
		</c:if>
	</div>
</div>
<div id="fileStateModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="xBtn" data-modal-btn="CLOSE"></div>
            <!-- <div class="modalTitle">파일 구분</div> -->
            <div class="modalBody flex-direction-column align-items-center">
                <div class="commonMent">
                    <p>회의 등록 시 첨부된 파일은 판서가 가능한 파일로 변환작업을 거치게 됩니다.</p>
                </div>	
				<div class="f1">
                    <div class="row">
                        <div class="item">
							<i class="fas fa-cog fa-spin text-center colorBlue"></i>
							<!-- <i class="fas fa-circle-notch fa-spin"></i> -->
						</div>
                        <div class="answer">
                            <span>변환 진행 중인 파일입니다. 변환이 완료된 파일만 회의 진행 중 판서가 가능합니다.</span>
                        </div>
                    </div>
                    <div class="row">
                        <div class="item">
							<i class="fas fa-exclamation-triangle colorRed"></i>
						</div>
                        <div class="answer">
                            <span>변환에 실패한 파일입니다. 첨부한 파일이 올바른 HWP, PDF, MS OFFICE 문서가 맞는지 확인해 주세요.</span>
                        </div>
                    </div>
				</div>
            </div>
        </div>
    </div>
</div>
<script>
const roomType = "${roomType}";
const skdKey = "${skdKey}";
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
<script type="module" src="/resources/front-end-assets/js/ewp/page/meeting/assign/assign_view.js"></script>
</body>
</html>