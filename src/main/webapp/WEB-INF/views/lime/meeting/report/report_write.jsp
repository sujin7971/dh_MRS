<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- 
* 회의록 등록/수정 페이지
 --%>
<!DOCTYPE HTML>
<html lang="en">
<head>
	<meta charset="UTF-8"/>
	<meta http-equiv="Content-Type" content="text/html">     
	<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
	<meta http-equiv="Content-Security-Policy" 
	content="default-src 'self'; 
	connect-src 'self' data:;
	img-src 'self' data:;
	script-src 'self' 'unsafe-inline'; 
	style-src 'self' 'unsafe-inline'; 
	style-src-elem 'self' 'unsafe-inline' https://fonts.googleapis.com; 
	font-src 'self' https://fonts.gstatic.com">
	<title>L-MRS</title>
	<meta name="description" content="LIME schedule.meeting MANAGEMENT SYSTEM">
	<meta name="author" content="BPLMS">
	<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">    
	<meta name="_csrf" content="${_csrf.token}"/>
	<meta name="_csrf_header" content="${_csrf.headerName}"/>  
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
	<link rel="stylesheet" href="/resources/core-assets/modules/limePad/limePad.css">
	
	<link rel="stylesheet" href="/resources/library/smarteditor2/css/ko_KR/smart_editor2.css">
	<script src="/resources/library/smarteditor2/js/service/HuskyEZCreator.js"></script>
	
	<%-- PDF 변환 저장 --%>
	<script src="/resources/library/jspdf/jspdf.umd.min.js"></script>
	<script src="/resources/library/html2canvas.js"></script>
	<%-- PDF 변환 저장 --%>
	<style type="text/css">
	#report_container {
	width: 100%;
	position: relative;
    border: 1px solid #b5b5b5;
	}
	#report_container, #report_container h1, #report_container h2, #report_container h3, #report_container h4, #report_container h5, #report_container h6, #report_container input, #report_container textarea, #report_container select, #report_container table, #report_container button {
	    font-family: '돋움',Dotum,Helvetica,sans-serif;
	    font-size: 13px;
	    color: #333;
	}
	body, #report_container, #report_container p, #report_container h1, #report_container h2, #report_container h3, #report_container h4, #report_container h5, #report_container h6, #report_container ul, #report_container ol, #report_container li, #report_container dl, #report_container dt, #report_container dd, #report_container table, #report_container th, #report_container td, #report_container form, #report_container fieldset, #report_container legend, #report_container input, #report_container textarea, #report_container button, #report_container select {
	    margin: 0;
	    padding: 0;
	}
	#report_container .report_content_wrapper {
	    position: relative;
	    z-index: 22;
	    margin: 0;
	    padding: 0;
	    *zoom: 1;
	}
	#report_container ul {
	    display: block;
	    list-style-type: disc;
	    /* margin-block-start: 1em;
	    margin-block-end: 1em; */
	    margin-inline-start: 0px;
	    margin-inline-end: 0px;
	    padding-inline-start: 40px;
	}
	.report_content{
		margin: 15px;
	    word-wrap: break-word;
	    *word-wrap: normal;
	    *word-break: break-all;
	}
	.report_content p, .report_content br{
		margin : 0;
		padding : 0;
	}
	</style>
</head>

<body class="mm1">
<jsp:include page="/WEB-INF/partials/lime/fragment/navigation.jsp"></jsp:include>
<div class="wrapper">
    <div class="titDiv">
		<div class="backBtnDiv"><i class="fal fa-long-arrow-left" onclick="history.back()" title="뒤로"></i></div>
        <div class="pageTit">회의록</div>
        <!-- <div class="mobileSrchBtn" onclick=""><i class="far fa-search"></i></div> -->
        <div class="comment">회의록작성자는 참석자의 검토의견 요청 확인 후 PDF 파일로 최종 업로드하세요.</div>
	</div>

	<div class="overflow-auto margin-bottom-10">
		<div class="bodyDiv">
	        <div class="reportFormDiv" id="reportBox">
	            <div class="rowGroup">
	                <div class="row">
	                    <div class="item"><span>회의일시</span></div>
	                    <div class="answer">
	                        <div class="inputForm" id="scheduleInput"></div>
	                    </div>
	                </div>
	                <div class="row">
	                    <div class="item"><span>회의실</span></div>
	                    <div class="answer">
	                        <div class=" inputForm" id="roomNameInput"></div>
	                    </div>
	                </div>
	            </div>            
	            <div class="row line"></div>
	            <div class="row">
	                <div class="item"><span>제목</span></div>
	                <div class="answer">
	                    <div class="inputForm" id="titleInput"></div>
	                </div>
	            </div>
	            <div class="row">
	                <div class="item"><span>회의내용</span></div>
	                <div class="answer" id="contentsBox">
	                    <textarea style="width: 100%; height: 200px;" rows="20" class="se2_textarea" id="smartEditor"></textarea>
	                </div>
	            </div>
                <div class="row">
                    <div class="item"><span>보안관련 설정</span></div>
                    <div class="answer">
                        <div class=" inputForm">공개대상 : 회의 진행자(동부서)/참석자(동부서)/참관자(동부서)</div>
                    </div>
                </div>
                <div class="row">
                    <div class="item"><span>작 성</span></div>
                    <div class="answer">
                        <div class="inputForm">
                            <span id="modDateTimeInput"></span>
                            <span id="nameplateInput"></span><span class="interphone" id="telInput"></span>
                        </div>
                    </div>
                </div>
				
				<div class="row line"></div>
	
	            <div class="row">
	                <div class="item">
	                    <div class="tit"><span>참석자</span><span class="confirmCount mx-2" id="reportConfirmCountInput">6/8</span></div>
	                </div>
	                <div class="answer">
	                    <div class="attendSign">
	                        <ul id="attendeeBox">
	                            
	                        </ul>
	                    </div>
	                </div>
	            </div>
	    	</div>
		</div>
	
	
		<div class="pageBtnDiv report"> <!--회의록작성자 & 회의 종료전-->
			<%-- <button class="btn btn-lg btn-silver" id="cancelBtn">취 소</button> --%>
			<c:if test='${authorityCollection.hasAuthority("FUNC_UPDATE") }'>
			<button class="btn btn-lg btn-blue-border" id="tempSaveBtn">임시저장</button>
			</c:if>
			<c:if test='${authorityCollection.hasAuthority("FUNC_FINISH") }'>
	        <button class="btn btn-lg btn-blue" id="finishBtn">최종 PDF 저장</button>
	        </c:if>
		</div>
		
	</div>
</div>
<!-- 본인 검토의견 삭제 -->
<div id="commentDelModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <!-- <div class="modalTitle"></div> -->
            <div class="modalBody">
                <div class="commonMent">
                    <p>검토의견을 삭제합니다.</p>
                </div>
            </div>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-silver flex1" data-modal-btn="CANCEL">취 소 </button>
                <button class="btn btn-md btn-blue flex1" data-modal-btn="OK">확 인</button>
            </div>
        </div>
    </div>
</div>
<!-- 회의록 내용 동의 취소 -->
<div id="cancelAgreeModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <!-- <div class="modalTitle"></div> -->
            <div class="modalBody">
                <div class="commonMent">
                    <p>회의록 내용 동의를 취소합니다.</p>
                </div>
            </div>
            <ul class="modalInfo">
                <li>회의록 내용 동의 철회 후 재동의 하시려면 다시 사인을 작성해야 합니다.</li>
            </ul>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-silver flex1" data-modal-btn="CANCEL">취 소 </button>
                <button class="btn btn-md btn-blue flex1" data-modal-btn="OK">확 인</button>
            </div>
        </div>
    </div>
</div>
<!-- 임시저장 -->
<div id="tempSaveModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <!-- <div class="modalTitle"></div> -->
            <div class="modalBody">
                <div class="commonMent">
                    <p>회의록이 임시저장 되었습니다.</p>
                </div>
            </div>
            <ul class="modalInfo">
                <li>회의록 작성이 완료되었다면 '임시저장 & 검토요청' 버튼을 클릭하여 참석자들에게 내용 확인을 요청합니다.</li>
            </ul>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-blue flex1" data-modal-btn="OK">확 인</button>
            </div>
        </div>
    </div>
</div>
<!-- 임시저장 & 검토요청 -->
<div id="tempSaveRqModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <!-- <div class="modalTitle"></div> -->
            <div class="modalBody">
                <div class="commonMent">
                    <p>회의록을 임시저장하고</p>
                    <p>참석자들에게 회의록 검토를 요청합니다.</p>
                </div>
            </div>
            <ul class="modalInfo">
                <li>검토요청은 메신저 및 SMS로 참석자에게 전달됩니다.</li>
                <li>검토요청을 받은 참석자는 본 회의록에 접속하여 검토의견을 메모할 수 있습니다.</li>
                <li>검토의견 메모 시 '회의록 내용에 동의'를 선택한 참석자는 본인 사인을 입력하며 '참석자 검토확인' 란에 사인이미지가 표츌됩니다.</li>
            </ul>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-silver flex1" data-modal-btn="CANCEL">취 소 </button>
                <button class="btn btn-md btn-blue flex1" data-modal-btn="OK">확 인</button>
            </div>
        </div>
    </div>
</div>
<!-- 최종 PDF 저장 -->
<div id="savePdfModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <!-- <div class="modalTitle"></div> -->
            <div class="modalBody">
                <div class="commonMent">
                    <p>회의록 작성을 완료하고 PDF 파일로 등록합니다.</p>
                </div>
            </div>
            <ul class="modalInfo">
                <li>최종 등록된 회의록은 '사용신청목록' 메뉴에서 해당 회의를 클릭하여 볼 수 있습니다.</li>
                <li>또는 파일함의 해당 회의를 검색하여 확인 가능합니다.</li>
                <li>최종 등록된 회의록은 수정할 수 없습니다.</li>
            </ul>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-silver flex1" data-modal-btn="CANCEL">취 소 </button>
                <button class="btn btn-md btn-blue flex1" data-modal-btn="OK">확 인</button>
            </div>
        </div>
    </div>
</div>
<div id="attendSignModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalTitle">참석자 사인</div>
            <div class="modalBody">
            	<div id="signPad" class="border overflow-visible">
            	
            	</div>
            </div>
            <div class="modalBtnDiv">
                <div class="btn btn-md btn-silver" data-modal-btn="CANCEL">취 소</div>
                <div class="btn btn-md btn-blue" data-modal-btn="OK">저 장</div>
            </div>
        </div>
    </div>
</div>
<script>
const scheduleId = "${scheduleId}";
const meetingId = "${meetingId}";
</script>
<script type="module" src="/resources/front-end-assets/js/lime/page/meeting/report/report_write.js"></script>
</body>
</html>