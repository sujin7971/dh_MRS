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
	<link rel="shortcut icon" href="/resources/meetingtime/lime/img/meetingtime_favicon.ico">
	<!-- CSS -->
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/fa.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/jquery-ui.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/common.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/meetingtime.css">
    <!-- script -->
	<script src="/resources/meetingtime/lime/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/meetingtime/lime/js/jquery-ui-datepicker.js"></script>
	<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
	
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/custom.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/lime/lime-version-styles.css">
</head>

<body class="mm1">
<jsp:include page="/WEB-INF/partials/lime/fragment/navigation.jsp"></jsp:include>
<c:set var = "loginType" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.loginType}"/>
<div class="wrapper">
<form id="testF" onsubmit="return false;">
	</form>
	<div class="titDiv">
		<%-- <div class="backBtnDiv"><i class="fal fa-long-arrow-left" onclick="history.back()" title="뒤로"></i></div> --%>
		<div class="backBtnDiv"><i class="fal fa-long-arrow-left" id="backArrow" title="뒤로"></i></div>
		<div class="pageTit">사용신청</div>
	</div>
	<div class="bodyDiv">
        <div class="scheduleWriteDiv"><!--------  전자회의 입력사항은 row 에 class="electContent" 를 추가하세요-------->
			<div class="formLDiv">	
				<form id="meetingForm1">
				<input name="scheduleId" type="hidden" value="${scheduleId}"/>
				<input name="meetingId" type="hidden"/>
				<input name="roomId" type="hidden"/>
				<input name="beginDateTime" type="hidden"/>
				<input name="finishDateTime" type="hidden"/>
				<div id="approvalStatusDiv" class="row except statusRow">
					<input name="approvalStatus" type="hidden"/>
					<div class="item">진행상태</div>
					<div class="answer">
						<div class="status"><span></span></div>
						<input type="text" name="approvalComment" class="input-md bg-white border-0" readonly></input>
					</div>
				</div>
				<div class="row except">
					<div class="item">
						<span>전자회의 여부</span>
					</div>
					<div class="answer align-items-center">
						<label for="switchElec" class="switchElec">
							<input type="checkbox" id="switchElec" name="elecYN" value="Y" disabled>
							<span class="slider round"></span>
						</label>
					</div>
				</div>
				<div class="row infoMent">
					<div class="item"></div>
					<div class="answer">전자회의는 자료파일을 업로드하고 참석자들과 공유 및 개별 판서기능을 제공합니다.</div>
				</div>
				<div class="row">
					<div class="item"><span>장 소</span></div>
					<div class="answer align-items-center">
						<div class="flex-row">
		                    <select name="roomType" class="input-md w-100 border-0 p-0 opacity-100 text-black" disabled>
								<option value="MEETING_ROOM">회의실</option>
								<option value="EDU_ROOM">강의실</option>
								<option value="HALL">강당</option>
							</select>
						</div>
		                <div class="flex-row">
							<input type="text" name="roomName" class="input-md bg-white border-0" readonly></input>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="item"><span>신청자</span></div>
					<div class="answer align-items-center">
						<input type="text" name="userName" class="input-md p-0 bg-white border-0" readonly></input>
						<span class="ml-1 interphone"></span>
						<input type="text" name="officeDeskPhone" class="input-md p-0 bg-white border-0" readonly></input>
					</div>
				</div>
				<div class="row infoMent">
					<div class="item"></div>
					<div class="answer">회의실 사용을 위해 사용신청을 작성하는 사용자를 지칭합니다.</div>
				</div>
				<div class="row">
					<div class="item">
						<span>일 시</span>
					</div>
					<div class="answer align-items-center">
						<div data-input="holdingDate" id="holdingDateDiv" class="w-30">
						<input type="text" data-input="time" name="holdingDate" class="input-md w-100 bg-white text-align-center" inputmode='none' readonly>
						</div>
						<input name="beginTime" type="text" class="input-md bg-white text-align-center w-20 ml-1" inputmode='none' readonly/>
						<span class="mx-1">~</span>
						<input name="finishTime" type="text" class="input-md bg-white text-align-center w-20 ml-1" inputmode='none' readonly/>
					</div>
				</div>
				<div class="row">
					<div class="item"><span>제 목</span><span class="input-limit" data-length="title">0/30</span></div>
					<div class="answer input-delete">
						<input type="text" class="input-lg bg-white width100p" name="title" maxlength="30" placeholder="30자 이내"  readonly> 
					</div>
				</div>
				<div class="row line"></div>
				<div class="row">
					<div class="item">
						<span>주관자</span>
						<span class="input-limit" data-length="scheduleHost">0/10</span>
					</div>
					<div class="answer input-delete">
						<input type="text" class="input-lg bg-white width100p" name="scheduleHost" maxlength="10" placeholder="회의 주관자를 입력해 주세요"  readonly> 
					</div>
				</div>
				<div class="row electContent">
					<div class="item">
						<span>진행자</span>
					</div>
					<div class="answer">
						<div class="inputForm" id="facilitatorDiv">
						
						</div>
					</div>
				</div>
				<div class="row electContent infoMent">
					<div class="item"></div>
					<div class="answer">실제로 회의를 진행하는 사용자로서 참석자확인, 사용자초대, 회의록작성, 보조진행 지정 등의 권한을 가집니다.</div>
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
						<input type="text" name="attendeeCnt" class="input-lg bg-white mx-1" maxlength="3" readonly><!-- 전자회의를 제외한 회의실 강의실 강당은 수동입력 -->
						<span>명</span>
					</div>
				</div>
				<div class="row line"></div>
				
				<div class="row">
					<div class="item"><span>기타요구사항</span><span class="input-limit" data-length="contents">0/100</span></div>
					<div class="answer input-delete">
						<textarea rows="2" class="width100p" name="contents" maxlength="100" placeholder="100자 이내로 입력하세요. 선택사항입니다." readonly></textarea>
					</div>
				</div>
				<%-- <div class="row except electContent">
					<div class="item"><span>알림전송</span></div>
					<div class="answer">
						<div class="checkboxDiv">
							<label for="switchMessenger"><input type="checkbox" id="switchMessenger"><span><span class="dot"></span>관련자 메신저알림</span></label>
							<label for="switchMail"><input type="checkbox" id="switchMail"><span><span class="dot"></span>관련자 메일알림</span></label>
							<label for="switchSms"><input type="checkbox" id="switchSms"><span><span class="dot"></span>관련자 SMS 알림</span></label>
						</div>						
					</div>
				</div> 
				<div class="row infoMent electContent">
					<div class="item"></div>
					<div class="answer">사용신청 확정 시 관련자에게 전송되며, 관련자란 신청자가 위에서 입력한 모든 사용자를 말합니다.</div>
				</div> --%>
				<%-- <div class="row line"></div>
				<div class="row except">
					<div class="item"><span>신청자</span></div>
					<div class="answer">
						<div class="inputForm nonStyle"> (<a href="officeDeskPhone:<c:out value="${schedule.writer.contactNumber }"/>"><c:out value="${schedule.writer.contactNumber }"/></a>)</div>
					</div>
				</div> --%>
				<div class="row line electContent"></div>
				</form>
			</div>
			
			<div class="formRDiv electContent">
				<form id="meetingForm2">
				<div class="row">
					<div class="item">
						<div class="tit">
							<span>첨부파일</span>
							<div id="fileInfoBtn" class="btn-help fileStateBtn margin-left-4 margin-right-auto" title="HELP"></div>
						</div>
						<button type="button" class="btn btn-sm btn-blue-border" id="fileDownBtn">선택파일 다운로드</button>
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
					<c:if test='${authorityCollection.hasAuthority("FUNC_REPORT") }'>
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
							<input type="checkbox" id="switchSecret" name="secretYN" value="Y" disabled>
							<span class="slider round"></span>
						</label>
					</div>
				</div>
				<div class="row">
					<section id="secretOnInfo" class="secuMon" style="display:none">
						<div class="infoMentDiv">
							<p>"기밀회의" 로 설정되면 회의 종료 후 모든 자료 (첨부파일, 회의진행 중 생성된 모든 파일, 참석자목록) 가 파기되어 회의 진행자 까지도 회의관련된 자료의 열람이 불가합니다.<br>
								단, 회의 자체의 히스토리(일시,장소,제목,주관자)는 보관됩니다.</p>
						</div>
					</section>					
					<section id="secretOffInfo" class="secuMoff">
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
				</form>
			</div>
		</div>
	</div>	
	<div class="pageBtnDiv">
		<button class="btn btn-lg btn-silver" id="backBtn">뒤 로</button>
		<c:if test='${authorityCollection.hasAuthority("FUNC_UPDATE")}'>
		<button class="btn btn-lg btn-white" id="updateBtn">수 정</button>
		</c:if>
		<c:if test='${authorityCollection.hasAuthority("FUNC_DELETE")}'>
		<button class="btn btn-lg btn-red" id="deleteBtn">삭 제</button>
		</c:if>
		<c:if test='${authorityCollection.hasAuthority("FUNC_CANCEL")}'>
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
const scheduleId = "${scheduleId}";
</script>
<script type="module" src="/resources/front-end-assets/js/lime/page/meeting/assign/assign_view.js"></script>
</body>
</html>