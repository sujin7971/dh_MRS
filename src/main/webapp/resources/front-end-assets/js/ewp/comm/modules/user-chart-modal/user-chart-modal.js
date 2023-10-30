/**
 * 직원 검색 모달창.
 * 트리탭과 프리텍스트탭 두종류의 탭 지원.
 * 
 * @param officeCode 사업소 키
 * @param enableTab 활성화할 탭 목록.
 * @param selectType 선택상자 타입("checkbox", "radio"). radio타입 인 경우 checked 파라미터로 넘어온 값이 checkbox일 때와 다르게 동작
 * @param checked 이미 선택된 사원 목록
 * @param disabled 선택 불가한 사원 목록
 
 * 
 * @author mckim
 */
import {eventMixin, Util} from '/resources/core-assets/essential_index.js';
import TreeTab from './search-modules/tree.js';
import FreeTextTab from './search-modules/freetext.js';
import {UserCall as $USER} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';

/*************상수*************/
const tabList = [
	TreeTab, FreeTextTab
];
/********************************/
const requestHandler = {
	async getSubDeptList(parentCode){
		const list = await $USER.Get.subDeptList(parentCode);
		return list;
	},
	async getDeptUserList(officeCode, deptId){
		const list = await $USER.Get.userList({
			officeCode: officeCode,
			deptId: deptId,
		});
		return list;
	},
	async getSearchUserList(userName){
		const list = await $USER.Get.userList({
			userName: userName
		});
		return list;
	}
}
const userDataHandler = {
	getUserBook(){
		if(!this.userBook){
			this.userBook = {};
		}
		return this.userBook;
	},
	addUser(user){
		const userBook = this.getUserBook();
		userBook[user.userKey] = user;
	},
	addUserList(userList){
		const userBook = this.getUserBook();
		userList.forEach(user => {
			userBook[user.userKey] = user;
		});
	},
	deleteUserList(keyList){
		const userBook = this.getUserBook();
		keyList.forEach(userKey => {
			delete userBook[userKey];
		});
	},
	getUserByKey(userKey){
		const userBook = this.getUserBook();
		return userBook[userKey];
	}
}
const modalHandler = {
	init(options = {}){
		const {
			officeCode,
			enableTab = ["tree", "text"],
			selectType = "checkbox",
			checked = [],
			disabled = [],
		} = options;
		this.enableTab = enableTab;
		this.officeCode = officeCode;
		this.selectedList = [];
		this.checked = checked;
		this.disabled = disabled;
		this.selectType = selectType;

		if(this.$modal){
			this.reset();
		}else{
			this.generate();
		}
	},
	reset(){
		this.clearTab();
		this.clearUserSearchResultList();
		this.clearAttendList();
	},
	generate(){
		this.$modal = Util.createElement("div", "modal");
		this.$modal.id = "addUserModal";
		
		this.$modal.style.display = "none";
		
		const $wrapper = Util.createElement("div", "modalWrap");
		const $content = Util.createElement("div", "modal_content");
		const $body = Util.createElement("div", "modalBody", "flex-direction-column");
		
		this.$modal.appendChild($wrapper);
		$wrapper.appendChild($content);
		$content.appendChild($body);
		// 모달 탭 생성
		const createModalBody = () => {
			const $tabNav = Util.createElement("ul", "addUserTabDiv");
			$body.appendChild($tabNav);
			const $tabBody = Util.createElement("div", "px-3", "pt-3", "d-flex", "flex-column");
			$body.appendChild($tabBody);
			this.selectTab = (index) => {
				const selectedTab = tabList[index];
				tabList.forEach(tab => {
					if(tab.isEnable){
						tab.close();
					}
				});
				this.clearTab();
				this.clearUserSearchResultList();
				selectedTab.open();
			}
			tabList.forEach((tab, index) => {
				if(this.enableTab.includes(tab.name)){
					tab.isEnable = true;
					
					const $nav = tab.generateNav();
					$tabNav.appendChild($nav);
					tab.nav = $nav;
					const $tab = tab.generateTab();
					$tabBody.appendChild($tab);
					
					$nav.onclick = () => {
						this.selectTab(index);
					}
				}else{
					tab.isEnable = false;
				}
			});
			this.clearTab = () => {
				tabList.forEach((tab, index) => {
					if(tab.isEnable){
						tab.clear();
					}
				});
			}
		}
		// 모달 본문 생성
		const createModalContent = () => {
			const $modalBody = Util.createElement("div", "px-3", "pt-1", "d-flex", "flex-column");
			$body.appendChild($modalBody);
			const $userListDiv = Util.createElement("div", "d-flex", "flex-column", "overflow-hidden", "border", "rounded", "border-third", "p-1");
			$userListDiv.style.minHeight = "10rem";
			$userListDiv.style.maxHeight = "20rem";
			$modalBody.appendChild($userListDiv);
			const $selectInfoDiv = Util.createElement("div", "d-flex", "flex-row", "border-bottom", "pb-1");
			const $searchResultDiv = Util.createElement("div", "pt-1", "overflow-auto");
			const setSelectInfoDiv = () => {
				$userListDiv.appendChild($selectInfoDiv);
				const $allChk = Util.createElement("input");
				$allChk.id = "allChk";
				$allChk.type = "checkbox";
				$allChk.style.visibility = "hidden";
				const $allChkLabel = Util.createElement("label", "ml-2", "my-auto");
				$allChkLabel.htmlFor = "allChk"; 
				$allChkLabel.style.visibility = "hidden";
				$allChkLabel.innerHTML = "전체";
				$selectInfoDiv.appendChild($allChk);
				$selectInfoDiv.appendChild($allChkLabel);
				const $cntDiv = Util.createElement("div", "ml-auto", "my-auto");
				$selectInfoDiv.appendChild($cntDiv);
				const $selectedCnt = Util.createElement("span");
				
				$cntDiv.appendChild($selectedCnt);
				const $cntSep = Util.createElement("span", "mx-1");
				$cntSep.innerHTML = "/";
				$cntDiv.appendChild($cntSep);
				const $allCnt = Util.createElement("span");
				
				$cntDiv.appendChild($allCnt);
				// 전체 선택 체크박스 클릭
				$allChk.onchange = () => {
					const isAllChecked = $allChk.checked;
					const list = this.getSearchResultList();
					list.forEach($li => {
						const $chkbox = $li.querySelector("input");
						if($chkbox.disabled == false && $chkbox.checked != isAllChecked){
							const event = new MouseEvent("click", {
								bubbles: false,
								cancelable: false,
							});
							$chkbox.dispatchEvent(event);
						}
					});
				}
				// 검색된 결과 총 개수
				this.setAllUserCnt = () => {
					const list = this.getSearchResultList();
					const allCnt = list.length;
					$allCnt.innerHTML = allCnt;
					$selectInfoDiv.dataset.allCnt = allCnt;
					if(allCnt == 0 || this.selectType == "radio"){
						$allChk.style.visibility = "hidden";
						$allChkLabel.style.visibility = "hidden";
					}else{
						$allChk.style.visibility = "visible";
						$allChkLabel.style.visibility = "visible";
					}
					this.setSelectableUserCnt();
				}
				// 검색된 결과중 선택 가능한 총 개수
				this.setSelectableUserCnt = () => {
					const list = this.getSearchResultList();
					const selectableCnt = list.filter($li => $li.querySelector("input").disabled == false).length;
					$selectInfoDiv.dataset.selectableCnt = selectableCnt;
					$selectInfoDiv.dataset.disabledCnt = $selectInfoDiv.dataset.allCnt - selectableCnt;
					if(selectableCnt == 0 || this.selectType == "radio"){
						$allChk.style.visibility = "hidden";
					}else{
						$allChk.style.visibility = "visible";
					}
				}
				// 검색된 결과중 선택된 총 개수
				this.setSelectedUserCnt = () => {
					const list = this.getSearchResultList();
					const selectedList = list.filter($li => $li.querySelector("input").checked == true);
					const selectedCnt = selectedList.length;
					const nowSelectedList = selectedList.filter($li => $li.querySelector("input").disabled == false);
					const nowSelectedCnt = nowSelectedList.length;
					$selectedCnt.innerHTML = selectedCnt;
					$selectInfoDiv.dataset.selectedCnt = selectedCnt;
					$selectInfoDiv.dataset.nowSelectedCnt = nowSelectedCnt;
					if($selectInfoDiv.dataset.selectableCnt == nowSelectedCnt){
						$allChk.checked = true;
						$allChk.indeterminate = false;
					}else if(nowSelectedCnt == 0){
						$allChk.checked = false;
						$allChk.indeterminate = false;
					}else{
						$allChk.checked = false;
						$allChk.indeterminate = true;
					}
				}
			}
			// 사용자 검색 결과를 담을 div설정
			const setSearchResultDiv = () => {
				$userListDiv.appendChild($searchResultDiv);
				// 현재 사용자 검색 결과로 표시된 리스트 반환
				this.getSearchResultList = () => {
					const list = $searchResultDiv.querySelectorAll("li");
					return Array.from(list);
				}
			}
			setSelectInfoDiv();
			setSearchResultDiv();
			const $attendListDiv = Util.createElement("div", "selectedUserDiv", "border", "rounded", "border-third", "overflow-auto", "empty-hidden");
			$attendListDiv.style.maxHeight = "10rem";
			$modalBody.appendChild($attendListDiv);
			// 사용자 선택 취소. 파라미터를 넘기지 않은 경우 모든 사용자 선택 취소.
			const deselectUser = (key) => {
				const filteredList = [];
				this.selectedList.forEach(user => {
					if(!key || user.userKey == key){
						const $checkbox = $searchResultDiv.querySelector("#user"+user.userKey);
						if($checkbox){
							$checkbox.checked = false;
						}
						const $attend = $attendListDiv.querySelector("#attend"+user.userKey);
						if($attend){
							$attend.remove();
						}
					}else{
						filteredList.push(user);
					}
				});
				this.selectedList = filteredList;
				this.setSelectedUserCnt();
			}
			this.clearUserSearchResultList = () => {
				$searchResultDiv.innerHTML = "";
				this.setAllUserCnt();
				this.setSelectedUserCnt();
			}
			this.clearAttendList = () => {
				this.selectedList = [];
				$attendListDiv.innerHTML = "";
			}
			
			//선택된 사용자 표시
			const selectUser = (userKey, nameplate) => {
				const $attend = Util.createElement("div", "attend");
				$attend.id = "attend"+userKey;
				
				const $span = Util.createElement("span");
				$span.innerHTML = nameplate;
				$attend.appendChild($span);
				
				const $del = Util.createElement("div", "btn-del");
				$del.setAttribute("title", "삭제");
				$attend.appendChild($del);
				
				$del.onclick = () => {
					deselectUser(userKey);
				}
				$attendListDiv.appendChild($attend);
				this.setSelectedUserCnt();
			}
			//검색된 사용자 체크버튼과 함께 표시
			const showUser = (deptId, userKey, nameplate) => {
				const $li = Util.createElement("li", "d-flex", "my-2");
				
				const $checkbox = Util.createElement("input");
				$checkbox.name = "user";
				$checkbox.type = this.selectType;
				$checkbox.value = userKey;
				$checkbox.id = "user"+userKey;
				$li.appendChild($checkbox);
				
				const $label = Util.createElement("label", "ml-2", "my-auto");
				$label.innerHTML = nameplate;
				$label.htmlFor = "user"+userKey;
				$li.appendChild($label);
				
				$searchResultDiv.appendChild($li);
				
				//이미 선택한 노드 체크
				if(this.checked.includes(userKey)){
					$checkbox.checked = true;
					//체크박스의 경우 선택된 노드 비활성화 처리
					if(this.selectType == "checkbox"){
						$checkbox.disabled = true;
					}
				}
				//선택불가한 노드 비활성화
				if(this.disabled.includes(userKey)){
					$checkbox.disabled = true;
					Util.addClass($li, "disabled");
				}
				//현 모달에서 선택한 노드 표시
				const selectedKeyList = this.selectedList.map(data => data.userKey);
				if(selectedKeyList.includes(userKey)){
					$checkbox.checked = true;
				}
				$checkbox.onchange = () => {
					if($checkbox.disabled == true){
						return;
					}
					if($checkbox.checked == true){
						if(this.selectType == "radio"){
							deselectUser();
						}
						this.selectedList.push({
							userKey: userKey,
							deptId: deptId,
							nameplate: nameplate,
						});
						selectUser(userKey, nameplate);
					}else{
						deselectUser(userKey);
					}
				}
			}
			tabList.forEach((tab, index) => {
				tab.on("search", async (param) => {
					const {
						deptId,
						searchText,
						nameplate = false,
					} = param;
					const userList = await $USER.Get.userList({
						officeCode: this.officeCode,
						deptId: deptId,
						userName: searchText
					});
					this.clearUserSearchResultList();
					if(!Util.isEmpty(userList)){
						userDataHandler.addUserList(userList);
						userList.forEach(user => {
							showUser(user.deptId, user.userKey, (nameplate)?user.nameplate:user.userName);
						});
						this.setAllUserCnt();
						this.setSelectedUserCnt();
					}
				});
			});
		}
		// 모달 하단 생성
		const createModalFooter = () => {
			const $btnDiv = Util.createElement("div", "modalBtnDiv");
			$content.appendChild($btnDiv);
			
			const $cancelBtn = Util.createElement("div", "btn", "btn-md", "btn-silver");
			$cancelBtn.innerHTML = "취 소";
			this.getCancelBtn = () => {
				return $cancelBtn;
			}
			$btnDiv.appendChild($cancelBtn);
			
			const $confirmBtn = Util.createElement("div", "btn", "btn-md", "btn-blue");
			$confirmBtn.innerHTML = "확 인";
			this.getConfirmBtn = () => {
				return $confirmBtn;
			}
			$btnDiv.appendChild($confirmBtn);
		}
		createModalBody();
		createModalContent();
		createModalFooter();
		this.selectTab(0);
		Util.getElement("body").appendChild(this.$modal);
	},
	destroy(){
		const $modal = this.$modal;
		$modal.remove();
		delete this.$modal;
		
		this.selectedList = [];
	},
	update(options = {}){
		this.init(options);
	},
	show(){
		if(!this.$modal){
			this.init();
		}
		this.$modal.style.display = "block";
	},
	hide(){
		this.reset();
		this.$modal.style.display = "none";
	},
	/**
	 * 선택된 참석자들 목록 리턴
	 */
	getSelectedList(){
		return this.selectedList;
	},
};

export default {
	__proto__: eventMixin,
	init(options = {}){
		const {
			onchange
		} = options;
		modalHandler.init(options);
		
		const $confirmBtn = modalHandler.getConfirmBtn();
		$confirmBtn.onclick = () => {
			const selectedList = modalHandler.getSelectedList();
			modalHandler.hide();
			if(selectedList.length > 0){
				this.trigger("change", selectedList);
				onchange?.(selectedList);
			}
		}
		const $cancelbtn = modalHandler.getCancelBtn();
		$cancelbtn.onclick = () => {
			modalHandler.hide();
		}
		return this;
	},
	update(options = {}){
		return this.init(options);
	},
	show(){
		modalHandler.show();
	},
	getUser(userKey){
		return userDataHandler.getUserByKey(userKey);
	}
}