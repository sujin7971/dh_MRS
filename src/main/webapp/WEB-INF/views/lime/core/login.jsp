<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--
	 MAIN 로그인 페이지
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
	<title>L-MRS</title>
	<meta name="description" content="SMART MEETING MANAGEMENT SYSTEM">
	<meta name="author" content="BPLMS">
	<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">   
	<%-- Favicon --%>
	<link rel="shortcut icon" href="/resources/meetingtime/lime/img/meetingtime_favicon.ico">
	<%-- CSS --%>
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/fa.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/common.css">
	<link rel="stylesheet" href="/resources/meetingtime/lime/css/meetingtime.css">
	<link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
    <%-- script --%>
	<script src="/resources/meetingtime/lime/js/jquery-3.6.0.min.js"></script>
</head>
<c:url value="/login" var="loginProcessingUrl"/>
<body class="index">
    <div class="loginContainer">
        <div class="sys_tit">
            <div class="ciSvg">
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
            </div>
            <div class="ciTit">L-MRS</div>
            <div class="ciSubTit">LIME MEETINGROOM RESERVATION SYSTEM</div>
        </div>
        <!-- <ul class="loginTabDiv">
            <li id="empNav" class="active">내부 임직원</li>
            <li id="guestNav">외부 참석자</li>
        </ul> -->
        <article>
            <div id="empTab" class="d-flex">
                <div class="loginBox">
                	<form id="memberLoginForm" action="${loginProcessingUrl}" method="POST">
	                	<%-- input name 변경하지 말 것 --%>
	                    <div class="inputDiv inputId">
	                        <label>ID</label>
	                        <input type="text" name="username" class="input-lg" placeholder="사번" onkeypress="enterkey()" maxlength="10" oninput="Util.acceptNonblank(this);">
	                    </div>
	                    <div class="inputDiv inputPw">
	                        <label>비밀번호</label>
	                        <input type="password" name="password" class="input-lg" onkeypress="enterkey()" maxlength="16" oninput="Util.acceptNonblank(this);" AUTOCOMPLETE="off">
	                    </div>
	                    <input name="loginType" type="hidden" value="FORMAL">
	                    <input name="${_csrf.parameterName}" type="hidden" value="${_csrf.token}">
	                    <a class="btn btn-lg btn-blue btnLogin transition" onclick="memberLogin()">로그인</a>
                    </form>
                    <div class="loginFuction">
                        <a href="#" id="lostPassword">비밀번호분실</a>
                    </div>
                </div>
            </div>
            <div id="guestTab" class="d-none">
                <div class="loginBox">
                	<form id="guestPostForm">
	                    <div class="inputDiv inputId">
	                        <label>회사명</label>
	                        <input type="text" class="input-lg" name="compName" onkeypress="enterkey()" maxlength="20" oninput="Util.acceptNonblank(this);">
	                    </div>
	                    <div class="inputDiv inputId">
	                        <label>이 름</label>
	                        <input type="text" class="input-lg" name="guestName" onkeypress="enterkey()" maxlength="5" oninput="Util.acceptNonblank(this);"> 
	                    </div>
	                    <div class="inputDiv inputId">
	                        <label>직 급</label>
	                        <input type="text" class="input-lg" name="guestTitle" onkeypress="enterkey()" maxlength="5" oninput="Util.acceptNonblank(this);">
	                    </div>
	                    <a class="btn btn-lg btn-blue btnLogin transition" onclick="guestLogin()">입 장</a>
	                    <input name="${_csrf.parameterName}" type="hidden" value="${_csrf.token}">
                    </form>
                    
                    <form id="guestLoginForm" action="${loginProcessingUrl}" method="POST">
	                	<input type="hidden" name="username">
	                    <input type="hidden" name="loginType" value="GUEST">
	                    <input name="${_csrf.parameterName}" type="hidden" value="${_csrf.token}">
                    </form>
                </div>
            </div>
        </article>
        <div class="copyright">Copyright © BELLOCK. All Rights Reserved.</div>
    </div>

<%-- modal  비번분실 --%>
<div id="lostPasswordModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalTitle">비밀번호 분실</div>
            <div class="modalBody">
                <div class="modalFormDiv">
                    <div class="row">
                        <div class="radioComment">
<!--                            	아래 관리자 이메일로 문의바랍니다. -->
                           	회원 정보에 등록된 이메일을 입력해주십시오.
                        </div>
                    </div>
                    <div class="row">
                        <div class="answer">
<!--                             <input type="text" class="input-lg width100p" readonly> -->
                            <input type="text" class="forgetPwMail input-lg width100p" placeholder="이메일">
                        </div>
                    </div>
                    
                </div>
            </div>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-silver" data-modal-btn="CANCEL">취소</button>
                <button class="btn btn-md btn-blue" data-modal-btn="OK">확 인</button>
            </div>
        </div>
    </div>
</div>
</body>
<script type="module">
import {Util, Modal} from '/resources/core-assets/essential_index.js';
window.Util = Util;
window.Modal = Modal;
</script>
<script>
const loginMessage = "${loginMessage}";
const expiredMessage = "${expiredMessage}";
window.onload = async () => {
	console.log("window load");
	sessionStorage.clear();
	if(loginMessage != ""){
		//새로고침시 form재전송 방지
		history.replaceState("", "", "/login");
		Modal.info({msg: loginMessage});
	}
	if(expiredMessage != ""){
		await Modal.info({msg: expiredMessage});
		location.reload();
	}
	evtBind();
}

const evtBind = () => {
	const setLoginTab = () => {
		const $empNav = Util.getElement("#empNav");
		const $empTab = Util.getElement("#empTab");
		const $guestNav = Util.getElement("#guestNav");
		const $guestTab = Util.getElement("#guestTab");
		$empNav.onclick = () => {
			Util.addClass($empNav, "active");
			Util.addClass($empTab, "d-flex");
			Util.removeClass($empTab, "d-none");
			Util.removeClass($guestNav, "active");
			Util.removeClass($guestTab, "d-flex");
			Util.addClass($guestTab, "d-none");
		}
		$guestNav.onclick = () => {
			Util.addClass($guestNav, "active");
			Util.addClass($guestTab, "d-flex");
			Util.removeClass($guestTab, "d-none");
			Util.removeClass($empNav, "active");
			Util.removeClass($empTab, "d-flex");
			Util.addClass($empTab, "d-none");
		}
	}
	const setForgotPwModal = () => {
		const $lostPasswordBtn = Util.getElement("#lostPassword");
		$lostPasswordBtn.onclick = async () => {
			await Modal.get("lostPasswordModal").show();
		}
	}
	setLoginTab();
	setForgotPwModal();
}

function memberLogin(){
    let $loginForm = $("#memberLoginForm");
    let username = $loginForm.find("input[name=username]").val();
    let password = $loginForm.find("input[name=password]").val();
    if(username == "" || password == ""){
    	Modal.info({msg: "아이디와 비밀번호를 모두 입력해주세요."});
    }else{
        $loginForm.submit();
    }
}

async function guestLogin(){
	const $postForm = $("#guestPostForm");
    const $loginForm = $("#guestLoginForm");
    
    const compname = $postForm.find("input[name=compName]").val();
    const guestname = $postForm.find("input[name=guestName]").val();
    const title = $postForm.find("input[name=guestTitle]").val();
    if(compname == "" || guestname == "" || title == ""){
    	Modal.info({msg: "회사명, 이름, 직급을 모두 입력해 주세요."});
    }else{
    	try{
	    	const formData = $postForm.serialize();
	    	const guestKey = await AjaxCall.guest.postGuest(formData);
	    	$loginForm.find("input[name=username]").val(guestKey);
	        $loginForm.submit();
    	}catch(err){
    		$("body").Modal("error", err);
    	}
    }
}
function enterkey() {
    let a = $(window.event.target.form).children("a")[0]; // input event가 발생하는 곳의 form을 찾아 그 안에 있는 a태그를 가져온다.
    let userEvent = a.attributes.getNamedItem("onclick").value;
    if (window.event.keyCode == 13) {
    	a.click(); // 엔터키를 입력할 경우 해당 form 안의 a를 클릭해준다.
//     	login();
    }
}
</script>
</html>