/**
 * 
 */
import { eventMixin, Util, Modal, AjaxBuilder } from '/resources/core-assets/essential_index.js';
import { UserSearchModal } from '/resources/front-end-assets/js/ewp/comm/module_index.js';
import { UserCall as $USER, AdminCall as $ADMIN } from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';

window.onload = () => {
	evtHandler.setAdminListBtn();
	sectionHandler.hideAllCardSection();
};

/**
 * 네비게이션에서 선택한 관리자 영역에 소속된 사용자목록 표시 및 추가/삭제 기능을 설정
 */
const sectionHandler = {
	async setSection(role) {
		// 선택한 사용자 역할에 속한 사용자 목록 카드를 보여줄 카드 섹션을 표시하고 그 노드를 가져옴
		const $section = this.showCardSection(role);
		switch (role) {
			case "sys-master":
				this.setMasterDomainSection($section);
				break;
			case "sys-admin":
				this.setSystemDomainSection($section);
				break;
			case "mng-req":
				this.setRequestManagerSection($section);
				break;
			case "mng-item-mr":
				this.setRoomManagerSection($section, {roomType: "MEETING_ROOM"});
				break;
			case "mng-item-er":
				this.setRoomManagerSection($section, {roomType: "EDU_ROOM"});
				break;
			case "mng-item-hr":
				this.setRoomManagerSection($section, {roomType: "HALL"});
				break;
		}
	},
	showCardSection(role) {
		const $section = ((role) => {
			switch (role) {
				case "sys-master":
				case "sys-admin":
				case "mng-req":
					return Util.getElement("section[data-section=" + role + "]");
				case "mng-item-mr":
				case "mng-item-er":
				case "mng-item-hr":
					return Util.getElement("section[data-section=mng-room]");
			}
		})(role);
		if ($section) {
			$section.style.display = "";
		}
		return $section;
	},
	hideAllCardSection() {
		const $sectionList = Util.getElementAll("section");
		$sectionList.forEach($section => $section.style.display = "none");
	},
	//최고관리자 설정
	async setMasterDomainSection($section) {
		const masterDomainList = await dataHandler.getMasterDomainList();
		const systemDomainList = await dataHandler.getSystemDomainList();
		const disabledList = systemDomainList.map(user => user.userId);
		const checkedList = masterDomainList.map(user => user.userId);
		userCardGenerator({
			$section: $section,
			userList: masterDomainList,
		});
	},
	//시스템관리자 설정
	async setSystemDomainSection($section) {
		$section.ondelete = (userId) => {
			AjaxBuilder({
				request: $ADMIN.Delete.systemDomain,
				param: userId,
				exception: 'success-only',
			}).success(async () => {
				dataHandler.deleteSystemDomain(userId);
			}).exe();
		}
		const $addBtn = $section.querySelector("[data-node=add]");
		$addBtn.onclick = async () => {
			const masterDomainList = await dataHandler.getMasterDomainList();
			const systemDomainList = await dataHandler.getSystemDomainList();
			const disabledList = masterDomainList.map(user => user.userId);
			const checkedList = systemDomainList.map(user => user.userId);
			UserSearchModal.update({
				selectType: 'checkbox',
				checked: checkedList,
				disabled: disabledList,
				onchange: async (selectedNodes) => {
					const userId = selectedNodes.pop().userKey;
					AjaxBuilder({
						request: $ADMIN.Post.systemDomain,
						param: userId,
						exception: 'success-only',
					}).success(async () => {
						const user = await $ADMIN.Get.userOne(userId);
						user.domainRole = "ROLE_SYSTEM_ADMIN";
						dataHandler.addSystemDomain(user);
						this.setSystemDomainSection($section);
					}).error(err => {
						Modal.error({ response: err });
					}).exe();
				}
			}).show();
		}
		const systemDomainList = await dataHandler.getSystemDomainList();
		userCardGenerator({
			$section: $section,
			userList: systemDomainList,
			deletable: true,
		});
	},
	//기간 신청 담당자 설정
	async setRequestManagerSection($section) {
		$section.ondelete = (userId) => {
			AjaxBuilder({
				request: $ADMIN.Delete.requestManager,
				param: userId,
				exception: 'success-only',
			}).success(async () => {
				dataHandler.deleteRequestManager(userId);
			}).error(err => {
				Modal.error({ response: err });
			}).exe();
		}
		const $addBtn = $section.querySelector("[data-node=add]");
		$addBtn.onclick = async () => {
			const managerList = await dataHandler.getRequestManagerList();
			const checkedList = managerList.map(user => user.userId);
			UserSearchModal.update({
				selectType: 'checkbox',
				checked: checkedList,
				disabled: checkedList,
				onchange: async (selectedNodes) => {
					const userId = selectedNodes.pop().userKey;
					AjaxBuilder({
						request: $ADMIN.Post.requestManager,
						param: userId,
						exception: 'success-only',
					}).success(async () => {
						const user = await $ADMIN.Get.userOne(userId);
						dataHandler.addRequestManager(user);
						this.setRequestManagerSection($section);
					}).exe();
				}
			}).show();
		}
		const managerList = await dataHandler.getRequestManagerList();
		userCardGenerator({
			$section: $section,
			userList: managerList,
			deletable: true,
		});
	},
	//장소 담당자 설정
	async setRoomManagerSection($section, param = {}) {
		const $officeSelect = $section.querySelector("#officeSelect");
		const {
			officeCode = $officeSelect.value,
			roomType = "MEETING_ROOM"
		} = param;
		const managerList = await dataHandler.getRoomManagerList(officeCode, roomType);
		$officeSelect.onchange = (event) => {
			const officeCode = event.target.value;
			this.setRoomManagerSection($section, {officeCode: officeCode, roomType: roomType});
		}
		$section.querySelector("#mng-title").innerText = ((roomType) => {
			switch (roomType) {
				case "MEETING_ROOM": return "회의실 담당자";
				case "EDU_ROOM": return "강의실 담당자";
				case "HALL": return "강당 담당자";
			}
		})(roomType);
		$section.ondelete = (userId) => {
			const officeCode = $officeSelect.value;
			AjaxBuilder({
				request: $ADMIN.Delete.roomManager,
				param: {
					officeCode: officeCode,
					roomType: roomType,
					userId: userId,
				},
				exception: 'success-only',
			}).success(async () => {
			}).error(err => {
				Modal.error({ response: err });
			}).exe();
		}
		const $addBtn = $section.querySelector("[data-node=add]");
		$addBtn.onclick = async () => {
			const checkedList = managerList.map(user => user.userId);
			UserSearchModal.update({
				selectType: 'checkbox',
				checked: checkedList,
				disabled: checkedList,
				onchange: async (selectedNodes) => {
					const userId = selectedNodes.pop().userKey;
					const officeCode = $officeSelect.value;
					AjaxBuilder({
						request: $ADMIN.Post.roomManager,
						param: {
							officeCode: officeCode,
							roomType: roomType,
							userId: userId,
						},
						exception: 'success-only',
					}).success(async () => {
						this.setRoomManagerSection($section, {officeCode: officeCode, roomType: roomType});
					}).exe();
				}
			}).show();
		}
		userCardGenerator({
			$section: $section,
			userList: managerList,
			deletable: true,
		});
	},
}
/**
 * 사용자 목록을 전달받아 해당 영역에 사용자 정보를 표시할 카드를 생성
 */
const userCardGenerator = (param = {}) => {
	const {
		$section,
		userList = [],
		deletable = false,
	} = param;
	const createCardRow = () => {
		const $row = Util.createElement("div", "bs-row", "row-cols-1", "row-cols-sm-1", "row-cols-md-1", "row-cols-lg-2", "row-cols-xl-2", "row-cols-xxl-3", "g-4");
		return $row;
	}
	const createCardColumn = (param = {user: {}}) => {
		const {
			userId,
			userName = "",
			officeName = "",
			deptHierarchyName = "",
			personalCellPhone = "",
			officeDeskPhone = "",
			email = ""
		} = param.user;
		const $column = Util.createElement("div", "col");
		const $card = Util.createElement("div", "card");
		$column.appendChild($card);
		const createCardBody = () => {
			const $cardBody = Util.createElement("div", "card-body", "d-flex", "flex-row");
			$card.appendChild($cardBody);
			const createUserPic = () => {
				const $picColumn = Util.createElement("div", "col-2", "d-flex", "justify-content-center");
				const $picImg = Util.createElement("img", "card-img", "card-img-top");
				$picImg.src = "/api/ewp/user/" + userId + "/img";
				$picColumn.appendChild($picImg);
				$cardBody.appendChild($picColumn);
			}
			const createUserDetail = () => {
				const $detailColumn = Util.createElement("div", "col-8", "p-3");
				const $name = Util.createElement("h3", "card-title");
				$name.innerText = userName;
				const $office = Util.createElement("h4", "card-text");
				$office.innerText = officeName;
				const $deptPos = Util.createElement("p", "card-text");
				$deptPos.innerText = deptHierarchyName;
				$detailColumn.appendChild($name);
				$detailColumn.appendChild($office);
				$detailColumn.appendChild($deptPos);
				$cardBody.appendChild($detailColumn);
			}
			const createDeleteButton = () => {
				const $buttonColumn = Util.createElement("div", "col-2", "text-right");
				const $delButton = Util.createElement("button", "btn-close");
				$delButton.type = "button";
				$delButton.onclick = async () => {
					const res = await Modal.confirm({ msg: "해당 사용자를 명단에서 제거하시겠습니까?", delMode: true });
					if (res == "DELETE") {
						$section.ondelete?.(userId);
						$column.remove();
					}
				}
				$buttonColumn.appendChild($delButton);
				$cardBody.appendChild($buttonColumn);
			}
			createUserPic();
			createUserDetail();
			if (deletable) {
				createDeleteButton();
			}
		}
		const createCardFooter = () => {
			const $cardFooter = Util.createElement("div", "card-footer");
			$card.appendChild($cardFooter);
			const createListGroup = () => {
				const $listGroup = Util.createElement("div", "list-unstyled", "list-group", "list-group-horizontal");
				const createList = (iconClass, text) => {
					const $li = Util.createElement("li", "mx-auto");
					const $icon = Util.createElement("i", "fas", "mx-1", "fa-" + iconClass);
					$li.appendChild($icon);
					const $text = Util.createTextNode(text);
					$li.appendChild($text);
					return $li;
				}
				const $phoneList = createList("mobile-alt", personalCellPhone);
				const $naesunList = createList("phone-office", officeDeskPhone);
				const $mailList = createList("envelope", email);
				$listGroup.appendChild($phoneList);
				$listGroup.appendChild(Util.createElement("div", "vr", "mx-1"));
				$listGroup.appendChild($naesunList);
				$listGroup.appendChild(Util.createElement("div", "vr", "mx-1"));
				$listGroup.appendChild($mailList);
				$cardFooter.appendChild($listGroup);
			}
			createListGroup();
		}
		createCardBody();
		createCardFooter();
		return $column;
	}
	$section.style.display = "";
	$section.loaded = true;
	const $cardContainer = $section.querySelector("article");
	$cardContainer.innerHTML = "";
	const $cardRow = createCardRow();
	for (const user of userList) {
		const $cardColumn = createCardColumn({
			user: user,
		});
		$cardRow.appendChild($cardColumn);
	}
	$cardContainer.appendChild($cardRow);

}
/**
 * 사용자 목록을 관리할 객체
 */
const dataHandler = {
	//최고 관리자 목록 조회
	async getMasterDomainList() {
		if (!this.masterDomainList) {
			this.masterDomainList = await $ADMIN.Get.masterDomainList();
		}
		return this.masterDomainList;
	},
	//시스템 관리자 목록 조회
	async getSystemDomainList() {
		if (!this.systemDomainList) {
			this.systemDomainList = await $ADMIN.Get.systemDomainList();
		}
		return this.systemDomainList;
	},
	//시스템 관리자 추가
	addSystemDomain(user) {
		this.systemDomainList.push(user);
	},
	//시스템 관리자 삭제
	deleteSystemDomain(userId) {
		this.systemDomainList = this.systemDomainList.filter(admin => admin.userId != userId);
	},
	//기간 신청 담당자 목록 조회
	async getRequestManagerList() {
		if (!this.requsetManagerList) {
			const requsetManagerList = await AjaxBuilder({
				request: $ADMIN.Get.requestManagerList,
			}).exe();
			this.requsetManagerList = requsetManagerList;
		}
		return this.requsetManagerList;
	},
	//기간 신청 담당자 추가
	addRequestManager(user) {
		this.requsetManagerList.push(user);
	},
	//기간 신청 담당자 삭제
	deleteRequestManager(userId) {
		this.requsetManagerList = this.requsetManagerList.filter(manager => manager.userId != userId);
	},
	async getRoomManagerList(officeCode, roomType) {
		const roomManagerList = await AjaxBuilder({
			request: $ADMIN.Get.roomManagerList,
			param: {
				officeCode: officeCode,
				roomType: roomType
			},
			loading: false
		}).exe();
		return roomManagerList;
	},
}
/**
 * DOM ELEMENT 이벤트 설정
 */
const evtHandler = {
	setAdminListBtn() {
		// 사이드바 요소 가져오기
		const $listGroup = Util.getElement('#sidebar');
		// 하이퍼링크 요소들을 가져오기
		const $links = Util.getElementAll('.nav-link');
		// 각 링크에 클릭 이벤트 추가
		$links.forEach($link => {
			const selectTab = ($selectedLink) => {
				// 모든 링크에서 active 클래스 제거
				$links.forEach($link => $link.classList.remove('active'));
				// 클릭된 링크에 active 클래스 추가
				$selectedLink.classList.add('active');
				// 카드 섹션 모두 숨김처리
				sectionHandler.hideAllCardSection();
				// 선택한 사용자 역할 가져옴
				const role = $selectedLink.dataset.role;
				sectionHandler.setSection(role);
			}
			$link.addEventListener('click', () => {
				selectTab($link);
			});
			if (Util.hasClass($link, "active")) {
				selectTab($link);
			}
		});
	},
}
