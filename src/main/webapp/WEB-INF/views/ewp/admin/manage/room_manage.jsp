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
	<title>스마트 회의시스템</title>
	<meta name="description" content="SMART MEETING MANAGEMENT SYSTEM">
	<meta name="author" content="BPLMS">
	<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">   
	<meta name="_csrf" content="${_csrf.token}"/>
	<meta name="_csrf_header" content="${_csrf.headerName}"/>   
	<!-- Favicon -->
	<link rel="shortcut icon" href="/resources/meetingtime/ewp/img/meetingtime_favicon.ico">
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
    <script src="/resources/library/moment/dist/moment-with-locales.min.js"></script>
    
    <link rel="stylesheet" href="/resources/front-end-assets/css/main/global-styles.css">
    <link rel="stylesheet" href="/resources/front-end-assets/css/main/custom.css">
    <link rel="stylesheet" href="/resources/front-end-assets/css/ewp/ewp-version-styles.css">
</head>
<body class="mm4">
<c:set var="branch" value="${sessionScope.branch }"></c:set>
<jsp:include page="/WEB-INF/partials/ewp/fragment/navigation.jsp"></jsp:include>
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
            <jsp:include page="/WEB-INF/views/ewp/admin/admin_nav.jsp"></jsp:include>
        </div>
        <div class="listSrchDiv user_list">
            <div class="row">
                <div class="answer width200">
                    <div class="selectDiv select-script">
                        <label for="selectbox" class="ellipsis">
                            <c:choose>
                                <c:when test="${officeCode eq 'all'}">사업소 전체</c:when>
                                <c:otherwise>
                                    <c:out value="${officeBook[officeCode]}"/>
                                </c:otherwise>
                            </c:choose>
                        </label>
                        <select id="selectbox" title="선택 구분" name="officeType" <c:if test="${ officeCode ne 'all' }">disabled</c:if>>
                            <option value="">사업소 전체</option>
                            <c:forEach var="book" items="${officeBook}" varStatus="status">
                                <option value="${book.key}" <c:if test="${ book.key eq officeCode }">selected</c:if>>
                                    <c:out value="${book.value}" />
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="answer width200">
                    <div class="selectDiv select-script">
                        <label for="selectbox" class="ellipsis">구분 전체</label>
                        <select id="selectbox" title="선택 구분" name="roomType">
                            <option value="">구분 전체</option>
                            <option value="MEETING_ROOM">회의실</option>
                            <option value="EDU_ROOM">강의실</option>
                            <option value="HALL">강당</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="answer width200">
                    <div class="selectDiv select-script">
                        <label for="selectbox" class="ellipsis">대여가능여부 전체</label>
                        <select id="selectbox" title="선택 구분" name="rentYN">
                            <option value="">대여가능여부 전체</option>
                            <option value="Y">대여가능</option>
                            <option value="N">대여불가</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="srchBtnDiv">
                <!--초기화 버튼은 2개. 모바일용, pc용-->
                <button class="btn btn-md btn-white mobileReset">초기화</button>
                <button class="btn btn-md btn-blue srch" data-btn="search">검 색</button>
                <button class="btn btn-md btn-white reset" data-btn="reset">초기화</button>
            </div>
        </div>
        <div class="roomListDiv overflow-auto">
            <div class="listHeaderDiv">
                <div class="row">
                    <div class="item no">No</div>
                    <div class="item branch justify-content-start">사업소</div>
                    <div class="item category">장소구분</div>
                    <div class="item room justify-content-start">명칭</div>
                    <div class="item permission" style="flex: 0 0 90px; justify-content: center; word-break: break-all;">대여가능부서</div>
                    <div class="item rent">대여가능<span>여부</span></div>
                    <div class="item reason justify-content-start">대여불가사유</div>
                    <div class="item memo justify-content-start">기타정보</div>
                    <div class="item delete">삭제</div>
                </div>
            </div>
            <div class="listBodyDiv overflow-auto">
                <div class="row placeholder-wave">
                    <div class="item no"><span class="placeholder w-100"></span></div>
                    <div class="item branch ellipsis"><span class="placeholder w-100"></span></div>
                    <div class="item category"><span class="placeholder w-100"></span></div>
                    <div class="item room"><span class="placeholder w-100"></span></div>
                    <div class="item permission"><span class="placeholder w-100"></span></div>
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
                <form id="roomF" onsubmit="return false;">
                    <input type="hidden" id="roomKey" name="roomKey">
                    <div class="row">
                        <div class="item"><span class="mandatoryInput">사업소</span></div>
                        <div class="answer">
                            <div class="selectDiv select-script input-lg">
                                <label for="selectPart1" class="ellipsis">
                                    <c:choose>
                                        <c:when test="${officeCode eq 'all'}">선택해주세요</c:when>
                                        <c:otherwise>
                                            <c:out value="${officeBook[officeCode]}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </label>
                                <select id="selectPart1" title="선택 구분" name="officeCode" <c:if test="${ officeCode ne 'all' }">disabled</c:if>>
                                    <option value="all">사업소 전체</option>
                                    <c:forEach var="book" items="${officeBook}" varStatus="status">
                                        <option value="${book.key}" <c:if test="${ book.key eq officeCode }">selected</c:if>>
                                            <c:out value="${book.value}" />
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="item"><span class="mandatoryInput">구분</span></div>
                        <div class="answer">
                            <div class="radioDiv">
                                <label for="cate1">
                                    <input type="radio" name="roomType" id="cate1" value="MEETING_ROOM">
                                    <span><span class="dot"></span>회의실</span>
                                </label>
                                <label for="cate2">
                                    <input type="radio" name="roomType" id="cate2" value="EDU_ROOM">
                                    <span><span class="dot"></span>강의실</span>
                                </label>
                                <label for="cate3">
                                    <input type="radio" name="roomType" id="cate3" value="HALL">
                                    <span><span class="dot"></span>강당</span>
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="row" style="position: relative;">
                        <div class="item"><span class="mandatoryInput">명 칭</span></div>
                        <div class="answer">
                            <input type="text" class="input-lg width100p" placeholder="제1회의실" name="roomName" onkeyup="countKey(this, 'name');">
                        </div>
                        <span class="input-limit nameKey" data-type="name"><span class="leng">0</span>/<span class="limit">25</span></span>
                    </div>
                    <div class="row" style="position: relative;">
                        <div class="item"><span>표 기</span></div>
                        <div class="answer">
                            <input type="text" class="input-lg width100p" placeholder="제1회의실" name="roomLabel" onkeyup="countKey(this, 'label');">
                        </div>
                        <span class="input-limit labelKey" data-type="label"><span class="leng">0</span>/<span class="limit">25</span></span>
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
                                <label for="rentYY"><input type="radio" name="rentYN" id="rentYY" checked value="Y"><span><span class="dot"></span>대여가능</span></label>
                                <label for="rentNN"><input type="radio" name="rentYN" id="rentNN" value="N"><span><span class="dot"></span>대여불가</span></label>
                            </div>
                        </div>
                    </div>
                    <div class="row" id="rentReason" style="display:none; position:relative;"><!-- 위 대여불가일 경우 보이게 해주세요 -->
                        <div class="item">
                            <span>
<!--                                                                    대여불가사유 -->
                            </span>
                        </div>
                        <div class="answer">
                            <textarea name="rentReason" class="width100p" placeholder="불가사유를 입력해 주세요. 500자 이내" style="resize:none;" onkeyup="countKey(this, 'reason');"></textarea>
                        </div>
                        <span class="input-limit reasonKey" data-type="reason"><span class="leng">0</span>/<span class="limit">500</span></span>
                    </div>
                    <div class="row" id="allowOfficeList" style="position: relative;">
                        <div class="item"><span class="">대여가능부서</span></div>
                        <div class="answer">
<!--                             <input type="text" class="input-lg width100p selectedUserDiv" name="rentalDepart"> -->
                            <div class="width100p" style="display:flex;flex-flow: wrap;background-color:#f1f1f1;border:1px solid #aaa;border-radius:3px;padding:5px 6px;font-size:14px;">
<!--                                 <div class="attend" style="display: inline-flex; margin: 6px 16px 6px 0;"> -->
<!--                                     <span style="white-space: nowrap;margin-right: 4px;">홍길동</span> -->
<!--                                     <div class="btn-del" title="삭제"></div> -->
<!--                                 </div> -->
<!--                                 <div class="attend" style="display: inline-flex; margin: 6px 16px 6px 0;"> -->
<!--                                     <span style="white-space: nowrap;margin-right: 4px;">홍길동</span> -->
<!--                                     <div class="btn-del" title="삭제"></div> -->
<!--                                 </div> -->
<!--                                 <div class="attend" style="display: inline-flex; margin: 6px 16px 6px 0;"> -->
<!--                                     <span style="white-space: nowrap;margin-right: 4px;">홍길동</span> -->
<!--                                     <div class="btn-del" title="삭제"></div> -->
<!--                                 </div> -->
                                
                            </div>
                            <button class="btn btn-lg btn-blue-border btn-add-lg ml-1" id="userSelectBtn1"></button>
                        </div>
                    </div>
                    <div class="row" style="position: relative;">
                        <div class="item"><span>기타정보</span></div>
                        <div class="answer flex-direction-column">
                            <textarea name="roomNote" class="width100p" placeholder="기타정보를 입력하세요. 150자 이내" style="resize:none;" onkeyup="countKey(this, 'info');"></textarea>
                        </div>
                        <span class="input-limit infoKey" data-type="info"><span class="leng">0</span>/<span class="limit">150</span></span>
                    </div>
                </form>
                </div>
            </div>
            <div class="modalBtnDiv">
                <div class="btn btn-md btn-silver" data-btn="cancel">취 소</div>
                <div class="btn btn-md btn-blue" id="roomPostBtn" data-btn="post">저 장</div>
                <div class="btn btn-md btn-blue" id="roomModiBtn" data-btn="modi">수정</div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/ewp/common/modal/addUserModal.jsp"></jsp:include>
<%-- ${ officeCode } --%>
</body>
<script type="module">
import {Util, Modal} from '/resources/core-assets/essential_index.js';
import {RoomCall as $RM} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';
window.Util = Util;
window.Modal = Modal;
window.$RM = $RM;
</script>
<script type="text/javascript">
let rowsize = 10
let modList;
let listsize = 0;

let officeList = {};
<c:forEach var="book" items="${officeBook}" varStatus="status">
	officeList["<c:out value='${book.key}'/>"] = "<c:out value='${book.value}'/>";
</c:forEach>

let officeCode = "<c:out value='${officeCode}'/>"

let allowList = {};

let searchData;

$(function(){
	evtBind();
	if(officeCode != "all") {
		searchData = {
			officeCode : officeCode
			, roomType : undefined
			, rentYN : ""
			, delYN : 'N'
		};
	}
	searchRoom(searchData);
});

/* 회의실 검색 */
async function searchRoom(data = {}) {
	let {
		officeCode,
		roomType,
		rentYN
	} = data;
	officeCode = (officeCode == "")?null:officeCode;
	rentYN = (rentYN == "")?null:rentYN;
	modList = null;
	try{
		const roomList = await $RM.Get.roomList({
			officeCode : officeCode
			, roomType : roomType
			, rentYN : rentYN
			, delYN : 'N'
		});
		const isScrollbarVisible = ($ele) => {
			 return $ele.scrollHeight > $ele.clientHeight;
		}
		$(".listBodyDiv").html("");
		if(roomList.length == 0) {
			generateHTML(null);
		} else {
			listsize = roomList.length;
			modList = roomList;
			let $listBody = document.getElementsByClassName("listBodyDiv")[0];
			while(true){
				// 장소 목록 불러오는 중 스크롤 바 추가 생성 시 불러오기 중단
				generateHTML(modList.slice(0, rowsize));
				modList = modList.slice(rowsize, modList.length);
				if(modList.length == 0 || isScrollbarVisible($listBody)){
					break;
				}
				
			}
		}
	}catch(err){
		console.error(err);
		Modal.error({response: err});
	}
}

/* 목록 생성 */
function generateHTML(list){
	let $main = $(".listBodyDiv");
	let $infoCon;
	
	let html = {
		infoCon: '<div class="row"></div>',
		infoBody: {
			no: '<div class="item no"></div>',
			branch: '<div class="item branch ellipsis"></div>',
			category: '<div class="item category"></div>',
			room: '<div class="item room"></div>',
			permission: '<div class="item permission"></div>',
			size: '<div class="item size"></div>',
			rent: '<div class="item rent"></div><!-- 대여가능 rentY / 대여불가 rentN-->',
			reason: '<div class="item reason"></div>',
			memo: '<div class="item memo"></div>',
			del: '<div class="item delete"><span class="btn-del"></span></div>',
			order: '<div class="item order"><span class="btn-move" title="드래그해서 순서를 변경하세요"></span></div>',
			none: '<div class="item null"><span></span></div>',
			empty : '<div class="item search-empty">검색 결과가 없습니다</div>'
		}
	}
	
	if(list == null) {
		$main.html("");
// 		$infoCon = $(html.infoCon);
// 		let $empty = $(html.infoBody.empty);
// 		$infoCon.append($empty);
// 		$main.append($infoCon);
		return;
	}

	for (let room of list){
		let param = {
			roomCode : room.roomCode
			, officeCode : room.officeCode
			, roomType : room.roomType
			, roomKey : room.roomKey
		};

		$infoCon = $(html.infoCon);
		$infoCon.data("roomKey", room.roomKey);
		
		// 행: 번호
		let $no = $(html.infoBody.no);
		$no.html(listsize--);
		$infoCon.append($no);
		
		// 행: 사업소
		let $branch = $(html.infoBody.branch);
		$branch.html(officeList[room.office]);
		$infoCon.append($branch);
		
		// 행: 구분
		let $category = $(html.infoBody.category);
		
		const cateFlagElem = document.createElement("span");
		
		const roomType = room.roomType;
		switch(roomType){
			case "EDU_ROOM":
				$(cateFlagElem).html("강의실");
				break;
			case "MEETING_ROOM":
				$(cateFlagElem).html("회의실");
				break;
			case "HALL":
				$(cateFlagElem).html("강당");
				break;
		}
		
		$category.html(cateFlagElem);
		$infoCon.append($category);
		
		// 행: 회의실
		let $room = $(html.infoBody.room);
		$room.html(room.roomName);
		$infoCon.append($room);
		
		// 행: 대여가능부서
		let $permission = $(html.infoBody.permission);
		const permissionFlagElem = document.createElement("span");
		$permission.append(permissionFlagElem);
		$permission.css({
			flex: "0 0 90px"
			, overflow:"hidden"
		});
		$infoCon.append($permission);
		
		// 행: 대여가능여부
		let $rent = $(html.infoBody.rent);
		const rentFlagElem = document.createElement("span");
		if(room.rentYN == "Y") {
			rentFlagElem.classList.add("rentY");
		} else {
			rentFlagElem.classList.add("rentN");
		}
		$rent.html(rentFlagElem);
		$infoCon.append($rent);
		
		// 행: 대여불가사유
		let $reason = $(html.infoBody.reason);
		let rentReason = "";
		if(room.rentReason != null && room.rentYN == "N") rentReason = room.rentReason;
		$reason.html("<pre style='white-space:break-spaces;font-family:Pretendard, malgun-gothic, sans-serif;'>" + rentReason + "</pre>");
// 		$reason.html(room.rentReason);
		$infoCon.append($reason);
		
		// 행: 기타정보
		let $memo = $(html.infoBody.memo);
		let roomNote = "";
		if(room.roomNote != null) roomNote = room.roomNote;
		$memo.html("<pre style='white-space:break-spaces;font-family:Pretendard, malgun-gothic, sans-serif;'>" + roomNote + "</pre>");
// 		$memo.html(room.roomNote);
		$infoCon.append($memo);
		
		// 행: 삭제
		let $del = $(html.infoBody.del);
		$infoCon.append($del);

		$main.append($infoCon);

		// 장소 리스트 append 후 각각의 장소에 대한 대여가능부서 조회 및 배치 후 이벤트 설정
		$RM.Get.roomPermissionList(param).then(function(permission) {
			// 장소 대여 가능 부서 존재 시 목록의 대여가능부서 상태 존재로 변경
			if(permission != null && permission.length > 0) {
				let permissions = [];
				$(permission).each(function(index, item) {
					permissions.push(item.deptName); 
				});
				const permissionList = document.createElement("span");
				$(permissionList).css({
					"overflow": "hidden"
					, "text-overflow": "ellipsis"
					, "white-space": "nowrap"
					, "word-break":"break-all"
				})
				$(permissionList).html(permissions.toString());
				$permission.html(permissionList)
			}

			// 장소 이름 클릭 이벤트 (상세보기팝업)
			$room.on("click", function(){
				// 모달 내 해당 장소 정보 등록
				$("#addRoomModal").find("input[name=roomKey]").val(room.roomKey);
				$("#addRoomModal").find("select[name=officeCode]").val(room.officeCode);
				$("#addRoomModal").find("label[for=selectPart1]").html($("#addRoomModal").find("option[value=" + room.officeCode + "]").html());
				$("#addRoomModal").find("input[name=roomType][value=" + room.roomType + "]").prop("checked", true);
				$("input[name=roomType]:checked").parent().css({marginLeft:"0",display:"flex"});
				$("input[name=roomType]:not(:checked)").parent().css({display:"none"});
				$("#addRoomModal").find("input[name=roomName]").val(Util.unescape(room.roomName));
				$("#addRoomModal").find("input[name=roomLabel]").val(Util.unescape(room.roomLabel));
				$("#addRoomModal").find("input[name=roomFloor]").val(room.roomFloor);
				$("#addRoomModal").find("input[name=roomSize]").val(room.roomSize);

				if(room.rentYN == "Y"){
					$("#addRoomModal").find("input[id=rentYY]").prop("checked", true);
					$("#rentReason").css({display:"none"});
					$(".input-limit.reasonKey").css({display:"none"});
				} else{
					$("#addRoomModal").find("input[id=rentNN]").prop("checked", true);
					$("#rentReason").css({display:"flex"});
					$(".input-limit.reasonKey").css({display:"block"});
				}

				$("#addRoomModal").find("textarea[name=rentReason]").val(Util.unescape(room.rentReason));

				allowList = {};
				$("#allowOfficeList").children(".answer").children("div").html("");

				// 대여가능 부서 insert
				$(permission).each(function(index, item) {
					allowList[item.deptId] = item.deptName;
					let permissionDiv = document.createElement("div");
					$(permissionDiv).addClass("attend");
					$(permissionDiv).css({
						display: "inline-flex"
						, margin: "6px 16px 6px 0px"
					})
					let span = "<span class='name' style='white-space:nowrap;margin-right:4px;'>" + item.deptName + "</span>";
					let delBtn = "<div class='btn-del' title='삭제'></div>";
					$(permissionDiv).append(span);
					$(permissionDiv).append(delBtn);
					$("#allowOfficeList").children(".answer").children("div").append(permissionDiv);

					// 대여가능부서 삭제 버튼 이벤트
					$(permissionDiv).children(".btn-del").click(function(e) {
						$(permissionDiv).remove();
						delete allowList[item.deptId];
					});
				});

				$("#addRoomModal").find("textarea[name=roomNote]").val(Util.unescape(room.roomNote));

				$("#roomModiBtn").css({display:"inline-flex"});
				$("#roomPostBtn").css({display:"none"});

				$("span.input-limit").each(function(idx, item) {
					let type = $(item).data("type");
					switch(type){
						case "reason":
							$(item).children(".leng").html($("textarea[name=rentReason]").val().length);
							break;
						case "info":
							$(item).children(".leng").html($("textarea[name=roomNote]").val().length);
							break;
						case "name":
							$(item).children(".leng").html($("input[name=roomName]").val().length);
							break;
						case "label":
							$(item).children(".leng").html($("input[name=roomLabel]").val().length);
							break;
					}
				});

				$("#addRoomModal").show();
				console.log("room", room, "getNowRoomData()", getNowRoomData())
			});

			// 삭제 버튼 클릭 이벤트
			$del.on("click", async () => {
				const res = await Modal.confirm({msg: room.roomName+"을(를) 삭제하시겠습니까?"});
				if(res == "OK"){
					// 장소 delYN Y로 변경
					$RM.Delete.roomOne(room).then(function(res){
						if(res.status == 200) {
							let param = {
								roomKey : room.roomKey 
								, roomType :room.roomType
							}
							// 해당 장소의 대여가능 부서 리스트 삭제
							$RM.Delete.roomPermissionAll(param).then(async (permissionRes) => {
								let status = permissionRes.status;
								if(status == 200){
									await Modal.info({msg: "장소 삭제 성공"});
									location.reload();
								} else {
									throw res;
								}
							}).catch(function(err){
								Modal.error({response: err});
							});
						} else {
							throw res;
						}
					}).catch(function(err){
						Modal.error({response: err});
					});
				}
			});
		});
	}
}

/* 이벤트 등록 */
function evtBind() { 
	// 조직도 발생 이벤트
	$("#userSelectBtn1").click(function(e) {
		initUserModal();
		$("#addUserModal .userListDiv").css({display:"none"});
		$("#addUserModal ul.addUserTabDiv > li:nth-child(2)").css({display:"none"});
		$("#addUserModal").show();
	});
	
	$("#addRoomModal input").keypress(function(e){
		if (e.keyCode == 13){
			e.preventDefault();
		}
	});
	
	// 조직도 버튼 이벤트
	$("#addUserModal .modalBtnDiv .btn").click(function(e) {
		if($(e.target).hasClass("btn-blue")) { // 대여가능부서 추가 버튼 클릭 시

			// 선택된 대여 가능 장소 전체 정보
			let selectedEmpList = $("#addUserModal select option:not([value='']):selected");

			// 최종적으로 선택된 대여가능 부서
			let selectedEmp = $("#addUserModal select option:not([value='']):selected").eq(-1);

			let exists = false;

			if(allowList[$(selectedEmp).val()]) exists = true;

			if(!exists) {
				// 부서가 등록되어있지 않다면 대여가능 부서 리스트에 새로 추가한 부서 추가
				if(selectedEmp.length > 0) {
					allowList[$(selectedEmp).val()] = $(selectedEmp).html();
					let div = document.createElement("div");
					$(div).addClass("attend");
					$(div).css({display:"inline-flex", margin:"6px 16px 6px 0"});
		
					$(div).append(
							'<span class="name" style="white-space: nowrap;margin-right: 4px;">' 
								+ $(selectedEmp).html() 
							+ '</span>'
							+ '<div class="btn-del" title="삭제"></div>'
							);
					$("div#allowOfficeList .answer > div").append(div);
					$(div).children(".btn-del").click(function() {
						$(div).remove();
						delete allowList[$(selectedEmp).val()];
					});
				}
			} else {
				Modal.info({msg: "이미 추가 한 부서 입니다."});
			}
		}
		$("#addUserModal").hide();
	});
	
	//모달내 버튼 클릭시 모달끄기
	$(".modal .btn").click(function() {
		if($(this).data("btn") == "cancel"){
			$("input[name=roomType]").parent().removeAttr("style");
			$(this).closest(".modal").hide();
		}
	});
	
	// 모바일화면 돋보기 아이콘 클릭
	$(".mobileSrchBtn").click(function() {
		toggleSrchDiv();
	});
	
	//esc이벤트
	$(document).keydown(function(event) {
		if (event.keyCode == 27 || event.which == 27) {
			$($(".modal_content").closest(".modal").get().reverse()).each(function(index, item) {
				if($(item).css("display") != "none") {
					$(item).hide();
					return false; // break용 return
				}
			});
		}
	});
	
	// 버튼 이벤트
	$(".btn").on("click", function(){
		let btn = $(this).data("btn");
		$("#roomF input[type='text'], #roomF textarea").each(function(index, item) {
			$(item).val($(item).val().trim());
		});

		switch(btn){
			case "addModal" : // 장소 등록·수정 팝업 생성
				$("#addRoomModal").find("input[name=roomName], input[name=roomSize], input[name=roomFloor], input[name=roomLabel], textarea[name=rentReason], textarea[name=roomNote]").val("");
				if($("select[name=officeCode]").attr("disabled") == "disabled") {
					$("select[name=officeCode]").val(officeCode);
				} else {
					$("select[name=officeCode]").val("");
				}
				let selectLabel = $("#addRoomModal").find("option:selected").html();
				if(selectLabel == null) selectLabel = "";
				$("#addRoomModal").find("label[for=selectPart1]").html(selectLabel);
				$("#addRoomModal").find("input[name=roomType], input[name=rentYN]").prop("checked", false);
				$("#rentReason, #roomModiBtn").css({display:"none"});
				$("#roomPostBtn").css({display:"inline-flex"});
				$(".leng").html("0");
				allowList = {};
				$("#allowOfficeList").children(".answer").children("div").html("");
				$("#addRoomModal").show();
				break;
			case "post" : // 장소 등록
				$("input[name=roomKey]").val("");
			case "modi" : // 장소 정보 수정
				if($("[data-btn=" + btn + "]") == true){
					break;
				}
				let validate = roomValidation();
				if (validate != true){
					Modal.info({msg: validate});
				} else {
					if($("#roomF").serializeArray().find(e => e.name === 'officeCode') == null) {
						if(btn == "modi") {
							putRoom($("#roomF").serialize() + "&officeCode=" + $("select[name=officeCode]").val());
						} else if(btn == "post") {
							postRoom($("#roomF").serialize() + "&officeCode=" + $("select[name=officeCode]").val());
						}
					} else {
						if(btn == "modi") {
							putRoom($("#roomF").serialize());
						} else if(btn == "post") {
							postRoom($("#roomF").serialize());
						}
					}
				}
				break;
			case "search" : // 검색
				let roomType;
				if($("select[name=roomType]").val() != "") {
					roomType = $("select[name=roomType]").val();
				}
				searchData = {
					officeCode : $("select[name=officeType]").val()
					, roomType : roomType
					, rentYN : $("select[name=rentYN]").val().charAt(0)
					, delYN : 'N'
				};
				searchRoom(searchData);
				break;
			case "reset" : // 검색 초기화
				if(officeCode != "all") {
					searchData = {
						officeCode : officeCode
						, roomType : undefined
						, rentYN : ""
						, delYN : 'N'
					};
				} else {
					searchData = undefined;
				}
				let searchBoxes = $(".listSrchDiv select");
				$(searchBoxes).each(function (index, item) {
					if($(item).attr("name") == "officeType") if(officeCode != "all") return;
					$(item).val(null);
					$(item).parent().children("label")[0].innerHTML = $(item).children("option[value='']").html();
				});

				searchRoom(searchData);
				break;
		}
	});
	
	// 무한 스크롤
	$(".listBodyDiv").on("scroll", function() {
		var scrollTop = $(this).scrollTop();
		var innerHeight = $(this).innerHeight();
		var scrollHeight = $(this).prop('scrollHeight');

		if (scrollTop + innerHeight >= scrollHeight - 10) {
			if(modList != null) {
				generateHTML(modList.slice(0, rowsize));
				modList = modList.slice(rowsize, modList.length);
			}
		}
	});
	
	// 대여가능여부에 따른 대여불가사유 view
	$("input[name=rentYN]").change(function(){
		if($("input[name=rentYN]:checked").val() == "N") {
			$("#rentReason").css({display:"flex"});
			$(".input-limit.reasonKey").css({display:"block"});
		} else {
			$("#rentReason").css({display:"none"});
			$(".input-limit.reasonKey").css({display:"none"});
		}
	});
}

// 장소 최초 등록
function postRoom(data) {
	$postBtn = $("[data-btn=post]");
	$postBtn.prop("disabled", true);
	// 장소 등록
	$RM.Post.roomOne(data).then(async (res) => {
		console.log("postRoom", res);
		let status = res.status;
		if(status == 200) {
			$("input[name=roomKey]").val(res.data.roomKey);
			$("input[name=roomType]").val(res.data.roomType);
			// 대여가능부서를 지정한 경우
			if($("#allowOfficeList > div.answer > div").children().length > 0) {
				// 대여가능부서 등록
				postRoomPermission(data).then(async (res) => {
					let status = res.status;
					if(status == 200){
						await Modal.info({msg: "장소 등록 성공"});
						location.reload();
					} else {
						throw res;
					}
				}).catch(function(err){
					console.log(err);
					Modal.error({response: err});
				}).finally(function(){
					$postBtn.prop("disabled", false);
				});
			} else {
				await Modal.info({msg: "장소 등록 성공"});
				location.reload();
			}
		} else {
			throw res;
		}
	}).catch(function(err){
		console.log(err);
		Modal.error({response: err});
	}).finally(function(){
		$postBtn.prop("disabled", false);
	});
}

// 장소 수정
function putRoom(data) {
	$postBtn = $("[data-btn=modi]");
	$postBtn.prop("disabled", true);
	// 장소 수정
	$RM.Put.roomOne(data).then(async (modiRes) => {
		let status = modiRes.status;
		if(status == 200){
			// 해당 장소에 대한 사용가능 부서 전체 삭제
			$RM.Delete.roomPermissionAll(getNowRoomData()).then(async (delRes) => {
				let status = delRes.status;
				if(status == 200){
					// 대여가능부서를 지정한 경우
					if($("#allowOfficeList > div.answer > div").children().length > 0) {
						// 해당 장소에 대한 대여가능부서 등록
						postRoomPermission(data).then(async (res) => {
							let status = res.status;
							if(status == 200){
								await Modal.info({msg: "장소 수정 성공"});
								location.reload();
							} else {
								throw res;
							}
						}).catch(function(err){
							console.log(err);
							Modal.error({response: err});
						}).finally(function(){
							$postBtn.prop("disabled", false);
						});
					} else {
						await Modal.info({msg: "장소 수정 성공"});
						location.reload();
					}
				} else {
					throw delRes;
				}
			}).catch(function(err){
				console.log(err);
				Modal.error({response: err});
			});
		} else {
			throw modiRes;
		}
	}).catch(function(err){
		console.log(err);
		Modal.error({response: err});
	}).finally(function(){
		$postBtn.prop("disabled", false);
	});
}

// 장소 대여 가능 부서 등록
function postRoomPermission(data) {
	return new Promise(function(resolve, reject){
		let param = [];
		// 등록한 장소의 정보 불러오기
		$RM.Get.roomOne(getNowRoomData()).then(function(res){
			console.log("allowList", allowList, "res", res)
			$.each( allowList, function(key, value) {
				let permission = {
					officeCode : res.officeCode
					, roomType : res.roomType
					, roomKey : res.roomKey
					, deptId : key
					, deptName : value
					, regUser : "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.user.userKey}"
				}
				param.push(permission);
			});
			console.log("param", param);
			// 해당 장소에 대한 사용가능부서 등록
			$RM.Post.roomPermissionList(param).then(function(res) {
				if(res.status == 200){
					resolve(res);
				} else {
					reject(res);
				}
			});
		}).catch(function(err){
			console.log(err);
			Modal.error({response: err});
		})
	});
}

/* 모바일 화면에서 검색바 표시/숨김  */
function toggleSrchDiv(){
	let display = $(".listSrchDiv").css("display");
	if(display == "flex"){
		$(".listSrchDiv").css("display","");
	} else {
		$(".listSrchDiv").css("display","flex");
	}
};

// 장소 등록 / 수정 값 체크
function roomValidation() {
	if($("select[name=officeCode]").val() == ""){
		return "사업소를 선택해주세요";
	}
	
	if($("input[name=roomType]:checked").val() == "" || $("input[name=roomType]:checked").val() == null) {
		return "장소 구분을 선택해주세요";
	}
	
	if($("input[name=roomName]").val() == "") {
		return "장소 명칭을 입력해주세요";
	}
	
	if($("input[name=rentYN]:checked").val() == "" || $("input[name=rentYN]:checked").val() == null) {
		return "대여가능여부를 선택해주세요";
	}
	
	return true;
}

function getNowRoomData(){
	return {
		roomKey: $("input[name=roomKey]").val(),
		roomType: $("input[name=roomType]:checked").val(),
	}
}

// 글자 수 체크
function countKey(e, type) {
	let $item = $(".input-limit." + type + "Key");
	let length = $(e).val().length;
	if($item.length > 0) { // input 에 해당하는 글자 수 count 요소가 있는지 체크
		$item.children("span.leng").html(length);
		if(length * 1 > $item.children("span.limit").html() * 1) {
			$item.children("span.leng").css({color : "red", fontWeight : "bold"});
		} else {
			$item.children("span.leng").removeAttr("style");
		}
	}
	
};
</script>
</html>