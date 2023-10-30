<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- 
* 회의목록 페이지
 --%>
<!DOCTYPE HTML>
<c:set var = "userRole" value = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details}"/>
<div class="wrapper">
	 <div class="titDiv">
		<div class="backBtnDiv"><i class="fal fa-long-arrow-left" onclick="history.back()" title="뒤로"></i></div>
        <div class="pageTit">회의목록</div>
        <div class="mobileSrchBtn" onclick=""><i class="far fa-search"></i></div>
	</div>
	
	<div class="bodyDiv display-flex flex-direction-column meeting-list">
		<div class="subTabDiv">
			<div id="userTab" class="subTab" onclick="location.href='/ewp/meeting/assign/manage/user'">나의 회의목록</div>
			<%-- <div id="deptTab" class="subTab" onclick="location.href='/ewp/manage/assign/dept'">부서 회의목록</div> --%>
        </div>
        <div class="listSrchDiv file_list" id="searchForm">
            <!-- <div class="row">
                <div class="item"><span>승인상태</span></div>
                <div class="answer">
                    <div class="selectDiv select-script">
                        <label for="approvalStatusSelect" class="ellipsis" id="approvalStatusLabel">전체</label>
                        <select id="approvalStatusSelect" title="선택 구분">
                            <option value="0">전체</option>
                            <option value="REQUEST">승인대기</option>
                            <option value="APPROVED">승인완료</option>
                            <option value="CANCELED">사용취소</option>
                            <option value="REJECTED">승인불가</option>
                        </select>
                    </div>
                </div>
            </div> -->
            <div class="row">
                <div class="item"><span>진행상태</span></div>
                <div class="answer">
                    <div class="selectDiv select-script">
                        <label for="meetingStatusSelect" class="ellipsis" id="meetingStatusLabel">전체</label>
                        <select name="meetingStatus" id="meetingStatusSelect" title="선택 구분">
                            <option value="0">전체</option>
                            <option value="UNAPPROVAL">승인대기</option>
                            <option value="APPROVED">승인완료</option>
                            <option value="START">사용중</option>
                            <option value="END">사용완료</option>
                            <option value="CANCEL">사용취소</option>
                            <option value="DROP">승인불가</option>
                        </select>
                    </div>
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
            <div class="row">
                <div class="item"><span>장소구분</span></div>
                <div class="answer">
                    <div class="selectDiv">
                        <label for="roomTypeSelect" class="ellipsis" id="roomTypeLabel">전체</label>
	                    <select name="roomType" id="roomTypeSelect">
	                    	<option value="ALL_ROOM">전체</option>
							<option value="MEETING_ROOM">회의실</option>
							<option value="EDU_ROOM">강의실</option>
							<option value="HALL">강당</option>
						</select>
                    </div>
                </div>
            </div>
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
                    <div class="item status">진행상태</div>
                    <div class="item type">장소구분</div>
                    <div class="item elecDocs">전자회의</div>
                    <div class="item security-container">보안</div>
                    <div class="item dateTimeRoom justify-content-start">일시 / 장소</div>
                    <div class="item title">제목</div> 
                    <div class="item host justify-content-start">주관자</div>
                    <div class="item attendUser justify-content-start">참석자</div>                    
                    <div class="item item regDate justify-content-start">등록일</div>
                    <div class="item approvalComment justify-content-start">담당자 메모</div>
                </div>
            </div>
            <div class="listBodyDiv overflow-y" id="listBox">
               
            </div>
		</div>
	</div>
</div>
<jsp:include page="/WEB-INF/partials/ewp/modal/meeting/guideForAccessDeniedModal.jsp"></jsp:include>
<script>
const approvalStatus = ("${approvalStatus}" == "")?undefined:"${approvalStatus}";
const meetingStatus = ("${meetingStatus}" == "")?undefined:"${meetingStatus}";
const roomType = ("${roomType}" == "")?undefined:"${roomType}";
const title = "${title}";
const host = "${host}";
const attendeeName = "${attendeeName}";
const elecYN = '${elecYN}';
const secretYN = '${secretYN}';

const startDate = "${startDate}";
const endDate = ("${endDate}" == startDate)?moment(startDate).add(7 ,"d").format("YYYY-MM-DD"):"${endDate}";
</script>
