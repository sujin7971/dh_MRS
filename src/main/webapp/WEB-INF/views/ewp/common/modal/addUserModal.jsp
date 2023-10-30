<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- modal 참석자선택 -->
<div id="addUserModal" class="modal">
	<input type="hidden" id="officeCode"/>
    <div class="modalWrap">
        <div class="modal_content">
            <!-- <div class="modalTitle">참석자 추가</div> -->
            <div class="modalBody flex-direction-column">
                <ul class="addUserTabDiv">
                    <li class="active" onclick="fn_tabOpen('tab01',this)">조직도</li>
                    <li onclick="fn_tabOpen('tab02',this)" class="">직원검색</li>
                </ul>
                <article>
                    <div id="tab01" class="flex-direction-column open">
                        <div class="partSelectDiv">
                            <select id="dept1"><option value=''>사업부선택</option></select>
							<select id="dept2"><option value=''>부서선택</option></select>
							<select id="dept3"><option value=''>팀선택</option></select>
                        </div>
						<div class="userListDiv">
							<ul>

							</ul>
						</div>
						<div class="selectedUserDiv">
								
						</div>
                    </div>
                    <div id="tab02" class="flex-direction-column close">
                        <div class="singleSrchDiv">
							<div class="inputBox">
								<input type="text" id="searchValue" placeholder="이름을 입력해주세요">
							</div>
							<div class="srchBtn" onclick="javascript:employeeSearch();">
								<i class="far fa-search"></i>
							</div>
                        </div>
						<div class="userListDiv">
							<ul>

							</ul>
						</div>
                    </div>
                </article>
            </div>
            <div class="modalBtnDiv">
                <div class="btn btn-md btn-silver">취 소</div>
                <div class="btn btn-md btn-blue">확 인</div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
var $searchTarget; /**회의 진행자: 직원리스트 radio. 참석자,참관자: 직원리스트 checkbox */
var $selectUser = []; /**선택한 유저 담는 배열*/
const $userModal = $("#addUserModal");
const $userModalLoginKey = "${sessionScope.SPRING_SECURITY_CONTEXT.authentication.details.user.userKey}";

/**직원검색 모달 초기화 */
function initUserModal(data = {}){
	const {
		searchTarget,
		officeCode,
	} = data;
	
	$searchTarget = data.searchTarget;
	$userModal.find(".partSelectDiv select option[value!='']").remove();
	$userModal.find(".userListDiv li").remove();
	$userModal.find(".selectedUserDiv div").remove();
	
	deptSelect({loginKey: $userModalLoginKey, officeCode: officeCode,deptId:'50001253',drawTarget:'dept1'});

}

/**부서 목록 호출 */
function deptSelect(data = {}){
	const {
		officeCode,
		deptId,
		drawTarget,
	} = data;
	console.log("deptSelect", deptId);
	let idx = $(".partSelectDiv select").index( $("#" + drawTarget) );
	if( idx == 1){
		$userModal.find("#" + drawTarget)
			  .next()
			  .find("option[value!='']")
			  .remove();
	}
	$userModal.find("#" + drawTarget + " option[value!='']").remove();
	
	$.ajax({
		url : "/api/ewp/dept/"+deptId+"/sub",
		async : false,
		success : function(res) {
			for(let dept of res){
				$userModal.find("#" + drawTarget).append(
						"<option value='"+dept.deptId+"'>" + dept.deptName + "</option>"
						)
			}
			ev_selectDept("#" + drawTarget);
		},
		complete: function(){
			//let deptId = deptId;
			let deptId = '50232765';
			employeeSelect({loginkey: $userModalLoginKey, officeCode: officeCode, dept: deptId, target: 'tab01'});
		}
	});
}

/**부서에 속한 사원 목록 호출 */
function employeeSelect(data = {}){
	const {
		loginKey,
		officeCode,
		dept,
		target,
	} = data;
	
	data.loginKey = $userModalLoginKey;
	$userModal.find("#"+target+" .userListDiv ul").html('');

	$.ajax({
		url : "/api/ewp/employee/"+data.loginKey+"/select?officeCode="+officeCode+"&dept="+dept,
		async : false,
		success : function(res) {
			res.forEach(emp =>
				$userModal.find("#"+target+" .userListDiv ul").append(
						"<li>" +
						"<input type='" + ($searchTarget == 'HOST' ? 'radio' : 'checkbox') + "' id='"+emp.userId+"' value='" +emp.userId+ "'>" +
						"<label for='"+emp.userId+"'>"+ (emp.userName + ' ' + emp.jikgubNm) +"</label>" +
						"</li>"
							)
						);
		},
		error: function(err){
		},
		complete: function(){
			ev_selectUser("#"+target);
		}
	});
}

/**직원 전체검색을 통한 사원 목록 호출 */
function employeeSearch(){
	
	$userModal.find("#tab02 .userListDiv ul").html('');
	let officeCode = $userModal.find("#officeCode").val() != ''? $userModal.find("#officeCode").val() : '1000';
	let searchValue = $userModal.find("#tab02 #searchValue").val();
	
	$.ajax({
		url : "/api/ewp/employee/"+$userModalLoginKey+"/search?officeCode="+officeCode+"&searchValue="+searchValue,
		async : false,
		success : function(res) {
			res.forEach(emp =>
				$userModal.find("#tab02 .userListDiv ul").append(
						"<li>" +
						"<input type='" + ($searchTarget == 'HOST' ? 'radio' : 'checkbox') + "' id='"+emp.userId+"' value='" +emp.userId+ "'>" +
						"<label for='"+emp.userId+"'>"+ (emp.userName + ' ' + emp.jikgubNm) +"</label>" +
						"</li>"
							)
						)
		},
		error: function(err){
		}
	});
}
/*조직도에서 사업부, 부서, 팀 선택시 직원 보여주는 이벤트 등록*/
function ev_selectDept(deptTag){

	$(deptTag).unbind().bind("change",function(e){
		let $selectVal = $(e.target).find("option:selected").val();
		console.log("dept select", $selectVal);
		let officeCode = $userModal.find("#officeCode").val() != ''? $userModal.find("#officeCode").val() : '1000';
		let $nextDeptSelect = $(e.target).next();
		if($nextDeptSelect.length != 0){
			deptSelect( {loginKey: $userModalLoginKey, officeCode: officeCode, deptId:$selectVal, drawTarget:$nextDeptSelect[0].id} );
		}
	});
	
}

/*조직도에서 직원 선택시 선택 란에 추가하는 이벤트 등록*/
function ev_selectUser(tabDiv){
	
	$userModal.find(tabDiv+" .userListDiv ul li input")
		.unbind()
		.bind("click",function(e){
			//console.log(e);
			let userId = e.target.value;
			let userName = $(e.target).siblings().text();
			if( $selectUser.find(obj => obj.userId == userId) == undefined){
				$selectUser.push( {userId: userId, userName: userName} );
				$userModal.find(tabDiv + ' .selectedUserDiv')
					  .append(
						"<div class='attend'>"
						  + "<span>" + userName + "</span>"
						  + "<div class='btn-del' title='삭제' onclick='javascript:ev_exceptUser(this)'></div>"
					  + "</div>"
					  );0
			};
		});
	
}

/*조직도에서 선택란에 추가된 직원 삭제하는 이벤트 등록*/
function ev_exceptUser(button){
	//console.log(button);
	let selectedUserName = $(button).siblings().text();
	let selectedUserObjIndex =  $selectUser.findIndex(obj => obj.userName == selectedUserName);
	$selectUser.splice(selectedUserObjIndex, 1);
	$(button).parent().remove();
}
/*tab 전환*/
function fn_tabOpen(id,t){
	$(t).parent().children().removeClass("active");
	$(t).addClass("active");
	$("#"+id).parent().children().removeClass("open").addClass("close");
	$("#"+id).removeClass("close").addClass("open");
};

//직원검색 모달 show시 initUserModal 호출
//initUserModal( {searchTarget:'HOST', officeCode: '1000'} );
</script>