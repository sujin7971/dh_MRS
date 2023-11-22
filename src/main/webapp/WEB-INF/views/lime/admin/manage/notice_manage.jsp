<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%-- 테스트 페이지 --%>
<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta http-equiv="Content-Type" content="text/html">
<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
<%-- 촬영된 사진을 바로 보여주기 위해 img src에 blob: 추가 --%>
<meta http-equiv="Content-Security-Policy"
	content="default-src 'self'; 
	img-src 'self' 'unsafe-inline' data: blob: data: ;
	script-src 'self' 'unsafe-inline'; 
	style-src 'self' 'unsafe-inline'; 
	style-src-elem 'self' 'unsafe-inline' https://fonts.googleapis.com; 
	font-src 'self' https://fonts.gstatic.com">
<title>L-MRS</title>
<meta name="description" content="SMART MEETING MANAGEMENT SYSTEM">
<meta name="author" content="BPLMS">
<meta name="viewport"
	content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
<meta name="_csrf" content="${_csrf.token}" />
	<meta name="_csrf_header" content="${_csrf.headerName}" />   
<%-- Favicon --%>
<link rel="shortcut icon"
	href="/resources/meetingtime/lime/img/meetingtime_favicon.ico">
<%-- CSS --%>
<link rel="stylesheet" href="/resources/meetingtime/lime/css/fa.css">
<link rel="stylesheet"
	href="/resources/meetingtime/lime/css/jquery-ui.css">
<link rel="stylesheet" href="/resources/meetingtime/lime/css/common.css">
<link rel="stylesheet"
	href="/resources/meetingtime/lime/css/meetingtime.css">
<%-- script --%>
<script src="/resources/meetingtime/lime/js/jquery-3.6.0.min.js"></script>
<script src="/resources/meetingtime/lime/js/jquery-ui.js"></script>
<script src="/resources/meetingtime/lime/js/jquery-ui-datepicker.js"></script>
<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
<%-- 공통 스크립트 패키지 --%>
<script src="/resources/meetingtime/lime/js/jquery-3.6.0.min.js"></script>
<script src="/resources/meetingtime/lime/js/jquery-ui.js"></script>
<script src="/resources/meetingtime/lime/js/jquery-ui-datepicker.js"></script>
<script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>

<link rel="stylesheet"
	href="/resources/front-end-assets/css/main/global-styles.css">
<link rel="stylesheet"
	href="/resources/front-end-assets/css/main/custom.css">
<link rel="stylesheet"
	href="/resources/front-end-assets/css/lime/lime-version-styles.css">

<%-- 페이지네이션 --%>
	<link rel="stylesheet" href="/resources/library/pagination/dist/pagination.css">
	<script src="/resources/library/pagination/dist/pagination.js"></script>
	
	<style>
		div#fixedNoticeListBox.empty:empty:after {
			content: '상단고정 검색 결과가 없습니다';
		}
	</style>
</head>
<c:set var = "userRole" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details}"/>
<body class="mm4">
	<jsp:include page="/WEB-INF/partials/lime/fragment/navigation.jsp"></jsp:include>
<div class="wrapper">
    <div class="titDiv">
		<div class="backBtnDiv"><i class="fal fa-long-arrow-left" onclick="history.back()" title="뒤로"></i></div>
        <div class="pageTit">[관리자]</div>
        <div class="mobileSrchBtn" onclick=""><i class="far fa-search"></i></div>
        <!-- <div class="comment"></div> -->
	</div>
	<div class="bodyDiv display-flex flex-direction-column">
        <div class="subTabDiv noticeTabOpen">
            <jsp:include page="/WEB-INF/views/lime/admin/admin_nav.jsp"></jsp:include>
        </div>
        <div class="listSrchDiv">
            <div class="row" style="display:none;">
                <div class="item"><span>사업소</span></div>
                <div class="answer">
                    <div class="selectDiv select-script">
                        <label id="officeLabel" for="officeSelect" class="ellipsis">본 사</label>
	                    <select id="officeSelect" title="선택 구분">
	                    	<option value="0">전체</option>
	                        <c:forEach items="${officeBook}" var="code">
                                <option value="${ code.key }">${ code.value }</option>
                            </c:forEach>
	                    </select>
                    </div>
                </div>
            </div>
            <%-- <div class="row">
                <div class="item"><span>등록기간</span></div>
                <div class="answer date">
	               <div data-input="startDate" id="startDateDiv">
	                <input type="text" id="startDateInput" class="width100p input-md" readonly></div>
	                <span class="period">~</span>
	                <div data-input="endDate" id="endDateDiv">
	                <input type="text" id="endDateInput" class="width100p input-md" readonly></div>
	            </div>
            </div> --%>
            <div class="row">
                <div class="item"><span>제 목</span></div>
                <div class="answer">
                    <input type="text" id="titleInput" class="width160 input-md" maxlength="20" placeholder="20자 이내">
                </div>
            </div>
            <div class="srchBtnDiv">
                <!--초기화 버튼은 2개. 모바일용, pc용-->
                <button class="btn btn-md btn-white mobileReset" id="resetMobileBtn">초기화</button>
                <button class="btn btn-md btn-blue srch" id="searchBtn">검 색</button>
                <button class="btn btn-md btn-silver reset" id="resetBtn">초기화</button>
            </div>
        </div>
        <div class="noticeDiv overflow-auto">
            <div class="listHeaderDiv">
                <div class="row">
                    <div class="item col-1">No</div>
<!--                     <div class="item col-1">사업소</div> -->
                    <div class="item col-9">제 목</div>
                    <div class="item col-2">등록일</div>
                </div>
            </div>
            <div class="listBodyDiv overflow-auto-y" id="listBox">
            	<div class="empty text-center border-bottom border-secondary bg-blue-light " id="fixedNoticeListBox"></div>
            	<div class="empty text-center" id="nonfixedNoticeListBox"></div>
                <!--상단고정 글은 row 에 add class "topFix" -->
            </div>
		</div>
		<nav class="py-2 bg-white d-flex justify-content-center">
			<div id="pagination"></div>
		</nav>
	</div>	
	<div class="pageBtnDiv">
		<button class="btn btn-lg btn-blue" id="showWriteModalBtn">작 성</button>
	</div>
</div>
<div id="noticeViewModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
			
            <div class="modalTitle" id="office">공지사항</div>
            <div class="modalBody">
                <div class="modalFormDiv">
					<h2 class="mb-3" id="title"></h2>
					<div class="fs-6 fw-normal text-preline lh-base overflow-scroll border p-1" id="contents" style="min-height:20vh; max-height:40vh; min-width:20vw">
					</div>
					<div id="fileViewBox" class="flex-column my-2">
						<div class="bg-light border-bottom p-2">
							<i class="fas fa-paperclip"></i> 첨부파일 <span id="attachedCount" class="fw-bold">2개</span>
						</div>
						<ul class="list-group list-group-flush">
						</ul>
					</div>
					<div id="writerViewBox" class="fw-normal text-right mt-3 fs-6">
					</div>
                </div>
            </div>
            <div class="modalBtnDiv">
                <div class="btn btn-md btn-silver btn-cancle" data-modal-btn="CLOSE">닫 기</div>
                <div class="btn btn-md btn-blue" data-modal-btn="UPDATE">수 정</div>
            </div>
        </div>
    </div>
</div>
<div id="noticeWriteModal" class="modal addNoticeModal">
    <div class="modalWrap">
        <div class="modal_content overflow-visible">
            <div class="modalBody">
                <div class="modalFormDiv">
                    <!-- <div class="row">
                        <div class="item"><span class="mandatoryInput">사업소</span></div>
                        <div class="answer">
                            <select id="officeSelect" title="선택 구분" name="officeCode" class="form-control-sm">
                            	<c:if test='${userRole.hasRole("ROLE_MASTER_ADMIN")}'>
                            		<option value="0000">전사</option>
                            	</c:if>
		                        <c:forEach items="${officeBook}" var="code">
	                                <option value="${ code.key }">${ code.value }</option>
	                            </c:forEach>
		                    </select>
                        </div>
                    </div> -->
                    <div class="row">
                        <div class="item"><span>상단고정</span></div>
                        <div class="answer">
                            <div class="checkDiv">
                                <label for="fixYN"><input type="checkbox" name="fixYN" id="fixYN"><span>상단고정 적용</span></label>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="item"><span class="mandatoryInput">제 목</span></div>
                        <div class="answer">
                            <input type="text" class="input-lg width100p" placeholder="40자 이내로 입력하세요." name="title" maxlength="40">
                        </div>
                    </div>
                    <div class="row">
                        <div class="item py-1 align-items-start"><span class="mandatoryInput">내 용</span></div>
                        <div class="answer">
                            <textarea rows="20" name="contents"></textarea>
                        </div>
                    </div>
                    <div class="row d-none">
						<div class="item py-2 align-items-start">
                            <span>첨부파일</span>
						</div>
						<div class="answer d-flex flex-column">
							<div class="d-flex flex-row align-items-center" id="fileUploadInfoBox">
								<div>
									<span id="fileSize">0</span><span> / 300MB</span>
									<span id="fileCount">0</span><span> / 10개</span>
									<i class="ml-2 fas fa-question-circle tipIcon">
										<div class="tipDiv" style="left: 0%; width: 400px">
											<div class="tipTit">파일 첨부</div>
											<article>
												<ul class="rowTit">
													<li>첨부 제한: 최대 10개, 전체 용량 300MB 이내.</li>
													<li>첨부파일 지원 : 한글(HWP, HWPX), PDF, MS OFFICE 문서(PPT, PPTX, DOC, DOCX, XLS, XLSX), 이미지(JPG, JPEG, GIF, PNG, BMP)</li>
												</ul>
											</article>
										</div>
									</i>
								</div>
								<div class="ml-auto">
									<button type="button" class="btn btn-sm btn-blue-border" id="uploadBtn">파일첨부</button>
									<button type="button" class="btn btn-sm btn-red" id="fileDeleteAll">전체삭제</button>
								</div>
							</div>
							<div class="border rounded border-third p-2 my-2">
								<ul id="fileContainer">
								</ul>							
							</div>
						</div>
					</div>
                </div>
            </div>
            <div class="modalBtnDiv">
                <div class="btn btn-md btn-silver btn-cancle" data-modal-btn="CLOSE">닫 기</div>
                <div class="btn btn-md btn-blue" data-modal-btn="SAVE" data-modal-close="manual">저 장</div>
                <div class="btn btn-md btn-red" data-modal-btn="DELETE" data-modal-close="manual">삭 제</div>
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
</body>
<script>
const officeCode = ("${officeCode}" == "")?undefined:"${officeCode}";
const pageNo = ("${pageNo}" == "")?undefined:"${pageNo}";
const startDate = "${startDate}";
const endDate = "${endDate}";
</script>
<script type="module" src="/resources/front-end-assets/js/lime/page/manage/admin/notice_manage.js"></script>
</html>