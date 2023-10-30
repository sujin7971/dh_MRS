<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- 
* 파일함 페이지
 --%>
<!DOCTYPE HTML>
<c:set var = "userRole" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details}"/>
<c:set var = "loginType" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.loginType}"/>
<div class="wrapper">
	 <div class="titDiv">
		<div class="backBtnDiv"><i class="fal fa-long-arrow-left" onclick="history.back()" title="뒤로"></i></div>
        <div class="pageTit">파일함</div>
        <div class="mobileSrchBtn" onclick=""><i class="far fa-search"></i></div>
        <div class="comment">보안관련설정에서 공개대상 선택에 포함된 사용자에게 허락된 파일목록입니다.</div>
	</div>
	
	<div class="bodyDiv display-flex flex-direction-column overflow-auto">
		<div class="subTabDiv">
		<c:choose>
		<c:when test='${nav eq null or nav eq "archiveNav" }'>
			<div id="userTab" class="subTab" onclick="location.href='/ewp/meeting/archive/manage/user'">내 파일함</div>
			<%-- <div id="deptTab" class="subTab" onclick="location.href='/ewp/meeting/archive/manage/dept'">부서 파일함</div> --%>
		</c:when>
		<c:otherwise>
			<jsp:include page="/WEB-INF/views/ewp/admin/admin_nav.jsp"></jsp:include>
		</c:otherwise>
		</c:choose>
        </div>
        <div class="listSrchDiv">
            <div class="row">
                <div class="item">
                    <div class="selectDiv select-script">
                        <label for="searchTargetSelect" class="ellipsis" id="searchTargetLabel">제목</label>
                        <select id="searchTargetSelect" title="선택 구분">
                            <option value="title">제목</option>
                            <option value="skdHost">주관자</option>
                            <option value="originalName">파일명</option>
                        </select>
                    </div>
                </div>
                <div class="answer freeText">
                    <input type="text" id="searchWord" class="width100p input-md" maxlength="50" placeholder="50자 이내">
                </div>
            </div>
            <div class="row">
                <div class="item"><span>검색기간</span></div>
                <div class="answer date">
                    <div data-input="startDate" id="startDateDiv">
	                <input type="text" id="startDateInput" class="width100p input-md cursor-pointer bg-white" readonly></div>
	                <span class="period">~</span>
	                <div data-input="endDate" id="endDateDiv">
	                <input type="text" id="endDateInput" class="width100p input-md cursor-pointer bg-white" readonly></div>
                </div>
            </div>
            <div class="row">
            	<div class="answer checkDiv">
                	<label for="fileType" class="width100p" id="fileTypeLabel">
                        <input type="checkbox" id="fileType" value="COPY">
                        <span>내 판서본이 있는 회의 만</span>
                    </label>
                </div>
            </div>
            <div class="srchBtnDiv">
                <!--초기화 버튼은 2개. 모바일용, pc용-->
                <button type="button" class="btn btn-md btn-white mobileReset" id="resetMobileBtn">초기화</button>
                <button type="button" class="btn btn-md btn-blue srch" id="searchBtn">검 색</button>
                <button type="button" class="btn btn-md btn-silver reset" id="resetBtn">초기화</button>
            </div>
        </div>
        <div class="fileListDiv overflow-auto">
            <div class="listHeaderDiv">
                <div class="row">
                    <div class="item no">No</div>
                    <div class="item checkAll"><input type="checkbox" id="chckall"/></div>
                    <div class="item dateTime">회의일시</div>
                    <div class="item type">장소구분</div>
                    <div class="item title justify-content-start">제목 / 주관자</div>
                    <div class="item file justify-content-start">파일명</div>
                </div>
            </div>
            <div class="listBodyDiv overflow-y" id="listBox">
               
            </div>
		</div>
	</div>
	
	<div class="pageBtnDiv">
		<c:if test='${loginType eq "SSO" }'>
		<button type="button" class="btn btn-lg btn-blue" id="fileDownBtn">선택파일 다운로드</button>
		</c:if>
		<button type="button" class="btn btn-lg btn-red" id="fileDelBtn">선택파일 삭제</button>
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
<script>
const target = ("${target}" == "")?undefined:"${target}";
const searchWord = ("${searchWord}" == "")?undefined:"${searchWord}";
const roleType = ("${roleType}" == "")?undefined:"${roleType}";
const startDate = "${startDate}";
const endDate = ("${endDate}" == startDate)?moment(startDate).add(7 ,"d").format("YYYY-MM-DD"):"${endDate}";
</script>
