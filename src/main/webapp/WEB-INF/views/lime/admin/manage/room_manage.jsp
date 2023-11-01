<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- 
* 회의실 등록관리 페이지
 --%>
<!DOCTYPE HTML>
<html lang="en">
<head>
	<meta charset="UTF-8"/>
	<meta http-equiv="Content-Type" content="text/html">     
	<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
	<meta http-equiv="Content-Security-Policy" 
	content="default-src 'self'; 
	script-src 'self' 'unsafe-inline'; 
	style-src 'self' 'unsafe-inline'; 
	style-src-elem 'self' https://fonts.googleapis.com; 
	font-src 'self' https://fonts.gstatic.com">
	<title>B-PLMS 장소관리</title>
	<meta name="description" content="SMART MEETING MANAGEMENT SYSTEM">
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
	<script src="/resources/meetingtime/lime/js/jquery-ui.js"></script>
	<script src="/resources/meetingtime/lime/js/jquery-ui-datepicker.js"></script>
	<script src="/resources/meetingtime/lime/js/jquery.ui.touch-punch.min.js"></script> <!-- 모바일에서 드래그 -->
	<script src="/resources/meetingtime/lime/js/common.js"></script>
    <script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
    
    <link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
    <link rel="stylesheet" href="/resources/front-end-assets/css/main/custom.css">
    <link rel="stylesheet" href="/resources/front-end-assets/css/lime/lime-version-styles.css">
</head>
<body class="mm4">
<jsp:include page="/WEB-INF/partials/lime/fragment/navigation.jsp"></jsp:include>
<c:set var = "userRole" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details}"/>
<div class="wrapper">
    <div class="titDiv">
		<div class="backBtnDiv"><i class="fal fa-long-arrow-left" onclick="history.back()" title="뒤로"></i></div>
        <div class="pageTit">[관리자]</div>
        <div class="mobileSrchBtn" onclick=""><i class="far fa-search"></i></div>
        <!-- <div class="comment"></div> -->
	</div>
	<div class="bodyDiv display-flex flex-direction-column">
        <div class="subTabDiv roomTabOpen">
            <jsp:include page="/WEB-INF/views/lime/admin/admin_nav.jsp"></jsp:include>
        </div>
        <div class="listSrchDiv user_list">
        	<form id="searchForm" class="d-flex">
	        	<div class="row">
	                <div class="answer width200">
	                	<select name="roomType" class="input-md w-100">
		                   	<option value="ALL_ROOM">장소 전체</option>
							<option value="MEETING_ROOM">회의실</option>
							<option value="EDU_ROOM">강의실</option>
							<option value="HALL">강당</option>
						</select>
	                </div>
	            </div>
	            <div class="row mx-2">
	                <div class="answer width200">
		                <select title="선택 구분" name="disableYN" class="input-md w-100">
		                    <option value="ALL">대여가능여부 전체</option>
		                    <option value="N">대여가능</option>
		                    <option value="Y">대여불가</option>
		                </select>
	                </div>
	            </div>
	            <div class="srchBtnDiv">
	                <!--초기화 버튼은 2개. 모바일용, pc용-->
	                <button type="button" class="btn btn-md btn-white mobileReset" name="mobileReset">초기화</button>
	                <button type="button" class="btn btn-md btn-blue srch" name="search">검 색</button>
	                <button type="button" class="btn btn-md btn-white reset" name="reset">초기화</button>
	            </div>
            </form>
        </div>
        <div class="roomListDiv overflow-auto">
            <div class="listHeaderDiv">
                <div class="row">
                    <div class="item no">No</div>
                    <div class="item category">장소구분</div>
                    <div class="item room justify-content-start">명칭</div>
                    <div class="item rent">대여가능<span>여부</span></div>
                    <div class="item reason justify-content-start">대여불가사유</div>
                    <div class="item memo justify-content-start">기타정보</div>
                    <div class="item delete">삭제</div>
                </div>
            </div>
            <div class="listBodyDiv overflow-auto-y" id="listBox">
                <div class="row placeholder-wave">
                    <div class="item no"><span class="placeholder w-100"></span></div>
                    <div class="item category"><span class="placeholder w-100"></span></div>
                    <div class="item room"><span class="placeholder w-100"></span></div>
                    <div class="item rent"><span class="placeholder w-100"></span></div><!-- 대여가능 rentY / 대여불가 rentN-->
                    <div class="item reason"><span class="placeholder w-100"></span></div>
                    <div class="item memo"><span class="placeholder w-100"></span></div>
                    <div class="item delete"><span class="placeholder w-100"></span></div>
                </div>
            </div>
		</div>
	</div>	
	<div class="pageBtnDiv">
		<button class="btn btn-lg btn-blue" id="addRoomBtn" data-btn="addModal">장소 추가</button>
	</div>
</div>

<!-- modal 회의실 등록 -->
<div id="addRoomModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalBody">
                <div class="modalFormDiv">
                <form id="roomForm">
                    <input type="hidden" name="roomId">
                    <div class="row">
                        <div class="item"><span class="mandatoryInput">구분</span></div>
                        <div class="answer">
                            <div class="radioDiv">
                                <label for="cate1">
                                    <input type="radio" id="cate1" name="roomType" value="MEETING_ROOM" checked>
                                    <span><span class="dot"></span>회의실</span>
                                </label>
                                <label for="cate2">
                                    <input type="radio" id="cate2" name="roomType" value="EDU_ROOM">
                                    <span><span class="dot"></span>강의실</span>
                                </label>
                                <label for="cate3">
                                    <input type="radio" id="cate3" name="roomType" value="HALL">
                                    <span><span class="dot"></span>강당</span>
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="row" style="position: relative;">
                        <div class="item"><span class="mandatoryInput">명 칭</span></div>
                        <div class="answer">
                            <input type="text" class="input-lg width100p" placeholder="제1회의실" maxlength="25" name="roomName" oninput="Util.acceptWithinLength(this);Util.displayInputLength(this, '[data-length=roomName]');">
                        </div>
                        <span class="input-limit" data-length="roomName">0/25</span>
                    </div>
                    <div class="row" style="position: relative;">
                        <div class="item"><span>표 기</span></div>
                        <div class="answer">
                            <input type="text" class="input-lg width100p" placeholder="제1회의실" maxlength="25" name="roomLabel" oninput="Util.acceptWithinLength(this);Util.displayInputLength(this, '[data-length=roomLabel]');">
                        </div>
                        <span class="input-limit" data-length="roomLabel">0/25</span>
                    </div>
                    <div class="row" style="position: relative;">
                        <div class="item"><span>층 수</span></div>
                        <div class="answer">
                            <input type="text" class="input-lg width100p" placeholder="" name="roomFloor" maxlength="2" oninput="Util.acceptNumber(this);">
                        </div>
                    </div>
                    <div class="row" style="position: relative;">
                        <div class="item"><span>좌 석</span></div>
                        <div class="answer">
                            <input type="text" class="input-lg width100p" placeholder="" name="roomSize" maxlength="3" oninput="Util.acceptNumber(this);">
                        </div>
                    </div>
                    <div class="row">
                        <div class="item"><span class="mandatoryInput">대여가능여부</span></div>
                        <div class="answer">
                            <div class="radioDiv">
                                <label for="disableN"><input type="radio" id="disableN" name="disableYN" checked value="N"><span><span class="dot"></span>대여가능</span></label>
                                <label for="disableY"><input type="radio" id="disableY" name="disableYN" value="Y"><span><span class="dot"></span>대여불가</span></label>
                            </div>
                        </div>
                    </div>
                    <div class="row" id="disableComment" style="display:none; position:relative;"><!-- 위 대여불가일 경우 보이게 해주세요 -->
                        <div class="item">
                            <span>
<!--                                                                    대여불가사유 -->
                            </span>
                        </div>
                        <div class="answer">
                            <textarea name="disableComment" class="width100p" placeholder="불가사유를 입력해 주세요. 500자 이내" maxlength="500" oninput="Util.acceptWithinLength(this);Util.displayInputLength(this, '[data-length=disableComment]');"></textarea>
                        </div>
                        <span class="input-limit" data-length="disableComment">0/500</span>
                    </div>
                    <div class="row" style="position: relative;">
                        <div class="item"><span>기타정보</span></div>
                        <div class="answer flex-direction-column">
                            <textarea name="roomNote" class="width100p" placeholder="기타정보를 입력하세요. 150자 이내" maxlength="150" oninput="Util.acceptWithinLength(this);Util.displayInputLength(this, '[data-length=roomNote]');"></textarea>
                        </div>
                        <span class="input-limit" data-length="roomNote">0/150</span>
                    </div>
                </form>
                </div>
            </div>
            <div class="modalBtnDiv">
                <div class="btn btn-md btn-silver" data-modal-btn="CANCEL">취 소</div>
                <div class="btn btn-md btn-blue" data-modal-btn="OK">수정</div>
            </div>
        </div>
    </div>
</div>
</body>
<script type="module">
import {Util, Modal} from '/resources/core-assets/essential_index.js';
window.Util = Util;
</script>
<script>
const roomType = ("${roomType}" == "")?undefined:"${roomType}";
const disableYN = ("${disableYN}" == "")?undefined:"${disableYN}";
const pageNo = ("${pageNo}" == "")?undefined:"${pageNo}";
</script>
<script type="module" src="/resources/front-end-assets/js/lime/page/manage/admin/room_manage.js"></script>
</html>