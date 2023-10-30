<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var = "userRole" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details}"/>
<%-- modal 회의 진행자의 회의종료시 회의 진행자화면 얼럿 --%>
<div id="hostFinish_hostModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalBody">
                <div class="commonMent">
                    <b>회의를 종료하시겠습니까?</b>
                </div>
            </div>
            <ul class="modalInfo">
            	<li>출석체크되지 않은 참석자는 회의록 검토 과정에서 제외됩니다.</li>
                <li>회의 보안설정에 따라 판서본<%-- 과 사인 --%>은 저장되지 않을 수 있습니다.</li>
                <li>진행자는 전체회의 종료를 통해 예정된 시간에 관계없이 회의를 즉시 종료할 수 있습니다.</li>
            </ul>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-silver" data-modal-btn="CLOSE">취 소</button>
                <button class="btn btn-md btn-blue" data-modal-btn="EXIT">회의 나가기</button>
                <button class="btn btn-md btn-blue" data-modal-btn="FINISH">전체회의 종료</button>
            </div>
        </div>
    </div>
</div>
<%-- modal 회의 진행자의 회의취소시 회의 진행자화면 얼럿 --%>
<div id="hostCancel_hostModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalBody">
                <div class="commonMent">
                    <b>회의를 취소하시겠습니까?</b>
                </div>
            </div>
            <ul class="modalInfo">
            	<li>취소한 회의는 재진행이 불가합니다.</li>
                <li>업로드한 자료 및 판서 기록은 모두 삭제됩니다.</li>
            </ul>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-silver" data-modal-btn="CLOSE">취 소</button>
                <button class="btn btn-md btn-blue" data-modal-btn="EXIT">회의 나가기</button>
                <button class="btn btn-md btn-blue" data-modal-btn="CANCEL">회의 취소</button>
            </div>
        </div>
    </div>
</div>
<%-- modal 회의 진행자의 전체회의종료시 참석자화면 얼럿 --%>
<div id="hostFinish_guestModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalBody">
                <div class="commonMent">
                	<span data-timer="exit" class="finishCount">60</span>
                    <b>회의 진행자에 의해 회의가 종료됩니다.</b>
                </div>
            </div>
            <ul class="modalInfo">
                <c:choose>
		            <c:when test='${userRole.hasRole("ROLE_GUEST")}'>
		                <li>판서본은 저장되지 않습니다.</li>
		            </c:when>
		            <c:otherwise>
		            	<li>출석체크되지 않은 참석자는 회의록 검토 과정에서 제외됩니다.</li>
		                <li>회의 보안설정에 따라 판서본<%-- 과 사인 --%>은 저장되지 않을 수 있습니다.</li>
		            </c:otherwise>
	            </c:choose>
            </ul>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-blue" data-modal-btn="EXIT">회의 나가기</button>
            </div>
        </div>
    </div>
</div>
<%-- modal 회의 진행자의 회의취소시 참석자화면 얼럿 --%>
<div id="hostCancel_guestModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalBody">
                <div class="commonMent">
                    <b>회의 진행자에 의해 회의가 취소됩니다.</b>
                </div>
            </div>
            <ul class="modalInfo">
                <li>판서본은 저장되지 않습니다.</li>
            </ul>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-blue" data-modal-btn="EXIT">회의 나가기</button>
            </div>
        </div>
    </div>
</div>
<%-- modal 참석자의 회의종료시 참석자화면 얼럿(회의 진행자에게는 얼럿하지 않음) --%>
<div id="guestFinish_guestModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalBody">
                <div class="commonMent">
                    <b>회의를 나가시겠습니까?</b>
                </div>
            </div>
            <ul class="modalInfo">
	            <c:choose>
		            <c:when test='${userRole.hasRole("ROLE_GUEST")}'>
		                <li>판서본은 저장되지 않습니다.</li>
		            </c:when>
		            <c:otherwise>
		            	<li>출석체크되지 않은 참석자는 회의록 검토 과정에서 제외됩니다.</li>
		                <li>회의 보안설정에 따라 판서본<%-- 과 사인 --%>은 저장되지 않을 수 있습니다.</li>
		            </c:otherwise>
	            </c:choose>
            </ul>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-silver" data-modal-btn="CLOSE">취 소</button>
                <button class="btn btn-md btn-blue" data-modal-btn="EXIT">회의 나가기</button>
            </div>
        </div>
    </div>
</div>

<%-- modal 회의시간 자동종료시 회의 진행자화면 얼럿 --%>
<div id="timeout_hostModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalBody">
                <div class="commonMent">
                	<span data-timer="exit" class="finishCount">60</span>
                    <b>회의시간이 종료되었습니다.</b>
                </div>
            </div>
            <ul class="modalInfo">
                <li>출석체크되지 않은 참석자는 회의록 검토 과정에서 제외됩니다.</li>
                <li>회의 보안설정에 따라 판서본<%-- 과 사인 --%>은 저장되지 않을 수 있습니다.</li>
            </ul>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-blue" data-modal-btn="EXIT">회의 나가기</button>
            </div>
        </div>
    </div>
</div>
<%-- modal 회의시간 자동종료 팝업에서 회의 진행자가 회의종료선택시, 또는 회의 진행자와 작성자가 없는 상태에서의 자동종료시 참석자얼럿 --%>
<div id="timeoutHost_guestModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalBody">
                <div class="commonMent">
                	<span data-timer="exit" class="finishCount">60</span>
                    <b>회의시간이 종료되었습니다.</b>
                </div>
            </div>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-blue" data-modal-btn="EXIT">회의 나가기</button>
            </div>
        </div>
    </div>
</div>
