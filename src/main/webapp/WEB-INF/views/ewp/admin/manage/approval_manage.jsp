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
	<script src="/resources/meetingtime/ewp/js/jquery-ui-datepicker.js"></script>
	<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
	
	<%-- 페이지네이션 --%>
	<link rel="stylesheet" href="/resources/library/pagination/dist/pagination.css">
	<script src="/resources/library/pagination/dist/pagination.js"></script>
</head>

<body class="mm6">
<jsp:include page="/WEB-INF/partials/ewp/fragment/navigation.jsp"></jsp:include>
<c:set var = "userRole" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details}"/>
<div class="wrapper">
    <div class="titDiv">
		<div class="backBtnDiv"><i class="fal fa-long-arrow-left" onclick="history.back()" title="뒤로"></i></div>
        <div class="pageTit">[배정 담당자]</div>
        <div class="mobileSrchBtn" onclick=""><i class="far fa-search"></i></div>
        <div class="comment">자동승인 기능을 설정해 놓으시면 신청건을 시스템이 자동 승인처리 합니다.</div>
	</div>
	<div class="bodyDiv display-flex flex-direction-column waiting-list">
        <div class="subTabDiv approvalTabOpen" data-room="${roomType }">
        	<jsp:include page="/WEB-INF/views/ewp/admin/approval_nav.jsp"></jsp:include>
        </div>
        <div class="listSrchDiv file_list" id="searchForm">
        	<div class="row">
	            <div class="item"><span>사업소</span></div>
	            <div class="answer">
	                <div class="selectDiv ">
	                	<label id="officeLabel" for="officeSelect" class="ellipsis">본 사</label>
	                    <select name="officeCode" id="officeSelect" title="선택 구분">
	                    	<option value="0">담당 사업소 전체</option>
	                        <c:forEach items="${officeBook}" var="code">
                                <option value="${ code.key }">${ code.value }</option>
                            </c:forEach>
	                    </select>
	                </div>
	            </div>
	        </div>
            <div class="row d-none">
                <div class="item"><span>승인상태</span></div>
                <div class="answer">
                    <div class="selectDiv select-script">
                        <label for="approvalStatusSelect" class="ellipsis" id="approvalStatusLabel">전체</label>
                        <select name="approvalStatus" id="approvalStatusSelect" title="선택 구분" >
                            <option value="0">전체</option>
                            <option value="REQUEST">사용신청</option>
                            <option value="APPROVED">사용승인</option>
                            <option value="CANCELED">사용취소</option>
                            <option value="REJECTED">승인불가</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="row">
	            <div class="item text-nowrap"><span>장소선택</span></div>
	            <div class="answer w-100">
                    <select name="roomKey" class="input-md" title="선택 구분">
                    	<option value="0">전체</option>
                    </select>
	            </div>
	        </div>
            <div class="row">
                <div class="item"><span>기 간</span></div>
                <div class="answer date">
	               	<div data-input="startDate" id="startDateDiv">
	                <input type="text" name="startDate" id="startDateInput" class="width100p input-md cursor-pointer bg-white" readonly></div>
	                <span class="period">~</span>
	                <div data-input="endDate" id="endDateDiv">
	                <input type="text" name="endDate" id="endDateInput" class="width100p input-md cursor-pointer bg-white" readonly></div>
	            </div>
            </div>
            <input type="hidden" name="roomType">
			<div class="rowGroup">
                <div class="item">옵 션</div>
                <div class="answer">
                    <div class="row">
                        <div class="answer checkDiv">
                            <label for="switchElec" class="width100p">
                                <input type="checkbox" id="switchElec" name="elecYN" value="Y">
                                <span>전자회의</span>
                            </label>
                        </div>
                    </div>
                    <div class="row">
                        <div class="answer checkDiv">
                            <label for="switchSecret" class="width100p">
                                <input type="checkbox" id="switchSecret" name="secretYN" value="Y">
                                <span>기밀회의</span>
                            </label>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="item"><span>제 목</span></div>
                <div class="answer">
                    <input type="text" name="title" class="width160 input-md" maxlength="10" placeholder="10자 이내">
                </div>
            </div>
            <div class="row">
                <div class="item"><span>주관자</span></div>
                <div class="answer">
                    <input type="text" name="host" class="input-md" maxlength="10" placeholder="10자 이내">
                </div>
            </div>
            <div class="row">
                <div class="item"><span>참석자</span></div>
                <div class="answer">
                    <input type="text" name="attendeeName" class="width60 input-md" maxlength="5" placeholder="5자 이내">
                </div>
            </div>
            <div class="srchBtnDiv">
                <!--초기화 버튼은 2개. 모바일용, pc용-->
                <button type="button" name="reset" class="btn btn-md btn-white mobileReset">초기화</button>
                <button type="button" name="search" class="btn btn-md btn-blue srch">검 색</button>
                <button type="button" name="mobileReset" class="btn btn-md btn-silver reset">초기화</button>
            </div>
        </div>
        <div class="meetingListMyDiv overflow-auto">
            <div class="listHeaderDiv">
                 <div class="row">
                    <div class="item no">No</div>
                    <div class="item status">결재상태</div>
                    <div class="item type">장소구분</div>
                    <div class="item elecDocs">전자회의</div>
                    <div class="item security">보안</div>
                    <div class="item dateTimeRoom justify-content-start">일시 / 장소</div>
                    <div class="item title">제목</div> 
                    <div class="item host justify-content-start">주관자</div>
                    <div class="item attendUser justify-content-start">참석자</div>                    
                    <div class="item item regDate justify-content-start">등록일</div>
                    <div class="item approvalComment justify-content-start">담당자 메모</div>
                </div>
            </div>
            <div class="listBodyDiv overflow-auto-y" id="listBox">
            	<div class="row placeholder-wave">
                    <div class="item no"><span class="placeholder w-100"></span></div>
                    <div class="item status"><span class="placeholder w-100"></span></div>
                    <div class="item type"><span class="placeholder w-100"></span></div>
                    <div class="item dateTimeRoom">
                        <div>
                            <span class="date placeholder w-100"></span>
                            <span class="time placeholder w-100"></span>
                        </div>
                        <div>
                            <span class="room placeholder w-100"></span>
                        </div>
                    </div>
                    <div class="item title"><span class="placeholder w-100"></span></div>
                    <div class="item host"><span class="placeholder w-100"></span></div>
                    <div class="item regDate"><span class="placeholder w-100"></span></div>
                </div>
            </div>
		</div>
		<nav class="py-2 bg-white d-flex justify-content-center">
			<div id="pagination"></div>
		</nav>
	</div>	
	<div class="pageBtnDiv">
        <!-- <button class="btn btn-lg btn-white" id="">전체선택</button>
        <button class="btn btn-lg btn-blue" id="waitingConfirmBtn" style="display:none">예약 승인</button> -->        
        <!-- <button class="btn btn-lg btn-red-border" id="meetingCancelBtn">승인불가</button> 승인불가는 개별건 보기에서 처리. 목록에서는 처리 불가-->
        <!-- <button class="btn btn-lg btn-red" id="deleteBtn">삭 제</button> -->
        <div class="checkDiv margin-left-auto">
            <label for="autoConfirm" class="autoConfirmBtn">
                <input type="checkbox" id="autoConfirm">
                <span>사용신청 자동승인</span>
            </label>
        </div>
	</div>
</div>


<!-- modal 예약 승인 -->
<div id="waitingConfirmModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <!-- <div class="modalTitle"></div> -->
            <div class="modalBody">
                <div class="commonMent">
                    <p>예약을 승인합니다.</p>
                </div>
            </div>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-silver">취 소</button>
                <button class="btn btn-md btn-blue">승 인</button>
            </div>
        </div>
    </div>
</div>

<!-- modal 강제취소 -->
<div id="meetingCancelModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <!-- <div class="modalTitle"></div> -->
            <div class="modalBody">
                <div class="commonMent">
                    <p>해당 회의의 승인을 불가합니다.</p>
                    <br>
                    <textarea rows="4" placeholder="안내메일에 첨부할 취소사유를 입력하세요."></textarea>
                    <div class="optionDiv">
                        <input type="checkbox" checked="" id="csf"><label for="csf">회의 관련자에게 취소 안내메일 전송</label>
                    </div>
                </div>
            </div>
            <ul class="modalInfo">
                <li>"취소 안내메일 전송"을 선택하시면 신청자와 회의 진행자, 참석자에게 메일이 전송됩니다.</li>
                <li>취소사유입력은 선택사항이며 입력하지 않을시 기본 시스템 멘트로 전송됩니다.</li>
            </ul>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-silver">취 소</button>
                <button class="btn btn-md btn-red">회의 취소</button>
            </div>
        </div>
    </div>
</div>


<!-- modal 회의 삭제 -->
<div id="deleteModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <!-- <div class="modalTitle"></div> -->
            <div class="modalBody">
                <div class="commonMent">
                    <p>회의를 삭제하시면 회의신청자,회의 진행자, 참석자 등 모든 관련자들에게서 해당 회의 조회가 불가능하며 첨부된 모든 파일이 완전히 삭제됩니다.<br>
                    회의를 삭제하시겠습니까?</p>
                </div>
            </div>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-silver">아니오</button>
                <button class="btn btn-md btn-red">삭 제</button>
            </div>
        </div>
    </div>
</div>
<script>
const officeCode = ("${officeCode}" == "")?undefined:"${officeCode}";
const approvalStatus = ("${approvalStatus}" == "")?undefined:"${approvalStatus}";
const roomType = ("${roomType}" == "")?undefined:"${roomType}";
const host = "${host}";

const startDate = "${startDate}";
const endDate = ("${endDate}" == startDate)?moment(startDate).add(7 ,"d").format("YYYY-MM-DD"):"${endDate}";

const pageNo = ("${pageNo}" == "")?undefined:"${pageNo}";
</script>
<script type="module" src="${urls.getForLookupPath('/resources/front-end-assets/js/ewp/page/manage/admin/dist/approval_manage.bundle.js')}"></script>
</body>
</html>