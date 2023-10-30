<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- 
* 관리자 지정 페이지
 --%>
<!DOCTYPE HTML>
<html lang="en">
<head>
	<meta charset="UTF-8"/>
	<meta http-equiv="Content-Type" content="text/html">     
	<meta http-equiv="X-UA-Compatible" content="IE=edge, chrome=1">
	<title>스마트 회의시스템</title>
	<meta name="description" content="SMART MEETING MANAGEMENT SYSTEM">
	<meta name="author" content="BPLMS">
	<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
	<meta name="_csrf" content="${_csrf.token}"/>
	<meta name="_csrf_header" content="${_csrf.headerName}"/>       
	<!-- Favicon -->
	<link rel="shortcut icon" href="/resources/meetingtime/img/meetingtime_favicon.ico">
	<!-- CSS -->
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/fa.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/jquery-ui.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/common.css">
	<link rel="stylesheet" href="/resources/meetingtime/ewp/css/meetingtime.css">
    <!-- script -->
	<script src="/resources/meetingtime/ewp/js/jquery-3.6.0.min.js"></script>
	<script src="/resources/meetingtime/ewp/js/jquery-ui.js"></script>
	<script src="/resources/meetingtime/ewp/js/jquery-ui-datepicker.js"></script>
	<script src="/resources/meetingtime/ewp/js/jquery.ui.touch-punch.min.js"></script> <!-- 모바일에서 드래그 -->
	<script src="/resources/meetingtime/ewp/js/common.js"></script>
	<script src="/resources/meetingtime/ewp/js/common.js"></script>
    <script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
	<!-- 
    <script type="text/javascript" src="/meetingtime/js/chart.js"></script>
    <script type="text/javascript" src="/meetingtime/js/chartjs-plugin-datalabels.js"></script>
     -->
    <link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
    <link rel="stylesheet" href="/resources/front-end-assets/css/main/custom.css">
    <link rel="stylesheet" href="/resources/front-end-assets/css/ewp/ewp-version-styles.css">
</head>

<body class="mm4">
<c:set var="branch" value="${sessionScope.branch }"></c:set>
<span>${branch }</span>
<!-- <div id="gnb"></div> --><!-- include -->
<jsp:include page="/WEB-INF/partials/ewp/fragment/navigation.jsp"></jsp:include>
<c:set var = "userRole" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details}"/>
<div class="wrapper">
    <div class="titDiv">
		<div class="backBtnDiv"><i class="fal fa-long-arrow-left" onclick="history.back()" title="뒤로"></i></div>
        <div class="pageTit">[관리자]</div>
        <div class="mobileSrchBtn"><i class="far fa-search"></i></div>
        <div class="comment">목록에는 현재 세션에 접속중인 사용자의 리스트가 나타납니다.</div>
	</div>
	<div class="bodyDiv display-flex flex-direction-column">
        <div class="subTabDiv sessionTabOpen">
            <jsp:include page="/WEB-INF/views/ewp/admin/admin_nav.jsp"></jsp:include>
        </div>
        <div class="listSrchDiv user_list">
            <div class="row">
                <div class="item"><span class="userCnt font-weight-bold"></span></div>
            </div>
            <div class="row d-none">
                <div class="item"><span>사업소</span></div>
                <div class="answer width150">
                    <div class="selectDiv select-script">
                        <label for="selectbox" class="ellipsis">본 사</label>
                        <select id="selectbox" title="선택 구분">
                            <c:forEach items="${officeBook}" var="code">
                                <option value="${ code.key }">${ code.value }</option>
                            </c:forEach>
                            <!-- 
                            <option>본 사</option>
                            <option>울산발전본부</option>
                            <option>....</option>
                             -->
                        </select>
                    </div>
                </div>
            </div>
            <div class="row d-none">
                <div class="item"><span>부 서</span></div>
                <div class="answer width140">
                    <input type="text" id="" class="width100p input-md searchevt">
                </div>
            </div>
            <div class="row d-none">
                <div class="item"><span>이 름</span></div>
                <div class="answer">
                    <input type="text" id="" class="width70 input-md searchevt">
                </div>
            </div>
            <div class="row d-none">
                <div class="item"><span>사 번</span></div>
                <div class="answer">
                    <input type="text" id="" class="width80 input-md searchevt">
                </div>
            </div>
            <!-- <div class="row">
                <div class="answer checkDiv">
                    <label for="wf" class="width100p">
                        <input type="checkbox" id="wf" name="">
                        <span>시스템관리자</span>
                    </label>
                </div>
            </div> -->
            <div class="srchBtnDiv d-none">
                <!--초기화 버튼은 2개. 모바일용, pc용-->
                <button class="btn btn-md btn-white mobileReset">초기화</button>
                <button class="btn btn-md btn-blue srch">불러오기</button>
                <button class="btn btn-md btn-white reset">초기화</button>
            </div>
        </div>
        <div class="userListDiv overflow-auto">
            <div class="listHeaderDiv">
                <div class="row" onclick="">
                    <div class="item no">No</div>
                    <div class="item comp justify-content-start">사업소</div>
                    <div class="item part">부 서</div>
                    <div class="item grade">직 급</div>
                    <div class="item name">이 름</div>
                    <div class="item compNo">사 번</div>
                    <div class="item mail">이메일</div>
                    <div class="item phone">연락처</div>
                    <div class="item phoneIn">내 선</div>
                    <div class="item loginDate">로그인 일시</div>
                </div>
            </div>
            <div class="listBodyDiv overflow-auto">
            </div>
		</div>
	</div>	
	<!-- <div class="pageBtnDiv">
		<button class="btn btn-lg btn-blue" id="addMemberBtn">사용자 추가</button>
	</div> -->
</div>


<!-- modal 사용자 등록 -->
<div id="addMemberModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalBody">
                <div class="modalFormDiv">
                    <div class="row">
                        <div class="item"><span>사업소</span></div>
                        <div class="answer">
                            <input type="text" class="input-lg width100p" disabled>
                        </div>
                    </div>
                    <div class="row">
                        <div class="item"><span>부 서</span></div>
                        <div class="answer">
                            <input type="text" class="input-lg width100p" disabled>
                        </div>
                    </div>
                    <div class="row">
                        <div class="item"><span>직 급</span></div>
                        <div class="answer">
                            <input type="text" class="input-lg width100p" disabled>
                        </div>
                    </div>
                    <div class="row">
                        <div class="item"><span>이 름</span></div>
                        <div class="answer">
                            <input type="text" class="input-lg width100p" disabled>
                        </div>
                    </div>
                    <div class="row">
                        <div class="item"><span>관리자</span></div>
                        <div class="answer checkDiv">
                            <label for="mail" class="height40 width100p">
                                <input type="checkbox" id="mail" name="mail">
                                <span>시스템관리자 지정</span>
                            </label>
                        </div>
                    </div>
                    <div class="row">
                        <div class="item"><span>사 번</span></div>
                        <div class="answer">
                            <input type="number" class="input-lg width100p" disabled>
                        </div>
                    </div>
                    <div class="row">
                        <div class="item"><span>이메일</span></div>
                        <div class="answer">
                            <input type="text" class="input-lg width100p" disabled>
                        </div>
                    </div>
                    <div class="row">
                        <div class="item"><span>연락처</span></div>
                        <div class="answer">
                            <input type="text" class="input-lg width100p" disabled>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modalInfo">
                <span></span>
                관리자에게는 사용신청에 대한 승인관리, 공지사항 관리, 장소 등록관리 등의 권한이 부여됩니다.   
            </div>
            <div class="modalBtnDiv">
                <button type="button" class="btn btn-md btn-silver" data-btn="cancel">취 소</button>
				<!-- <button type="button" class="btn btn-md btn-red" data-btn="delete">삭 제</button> -->
				<button type="button" class="btn btn-md btn-blue" data-btn="post">저 장</button>
            </div>
        </div>
    </div>
</div>
</body>

<script type="text/javascript">
const $userLoginKey = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.user.userId}";
const $saupsoList = '${officeBook}';
$(document).ready(function(){ 
	initAdminList();
});
const initAdminList = () => {
	
	$.ajax({
		url: "/api/ewp/admin/master/session/userList",
		async: false,
		beforeSend: function(){
			$("div.listSrchDiv input").val('');
			$("div.listBodyDiv").html('');
		},
		success: function(res){
			res.users.forEach( (item,idx) => {
				let emp = item.user;
				$("div.listBodyDiv").append(
					"<div class='row' onclick=''>"
				   +    "<div class='item no'>" + (idx+1) + "</div>"
				   +    "<div class='item comp'>" + $("#selectbox option[value="+emp.officeCode+"]").text() + "</div>"
				   +    "<div class='item part'>" + emp.offiNm + "</div>"
				   +    "<div class='item grade'>" + emp.posNm + "</div>"
				   +    "<div class='item name'>"
				   +        "<span class=" + (emp.adminDiv == 'MASTER' ? "'adminS'" : "'admin'") + ">"
				   +        emp.userName
				   +        "</span>"
				   +    "</div>"
				   +    "<div class='item compNo'>" + emp.userId + "</div>"
				   +    "<div class='item mail'>" + emp.email + "</div>"
				   +    "<div class='item phone'>" + emp.personalCellPhone + "</div>"
				   +    "<div class='item phoneIn'>" + emp.officeDeskPhone + "</div>"
				   +    "<div class='item loginDate'>" + moment(item.loginDateTime).format('YYYY-MM-DD HH:mm:ss') + "</div>"
				   +"</div>"
				);
			});
			
		$(".userCnt").html("총 : <span class='fs-5'>" + res.userCnt + "</span>명");
		},
		error: function(err){
			
		},
		complete: function(){
			
		}
	});
};

$('.searchevt').keydown(function(event){
    if (event.keyCode == 13) {
    	adminTargetSearch()
    	$('.searchevt').blur();
    }
})
</script>
</html>