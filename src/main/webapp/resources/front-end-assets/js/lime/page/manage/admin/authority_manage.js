/**
 * 
 */
import {eventMixin, Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {UserSearchModal} from '/resources/front-end-assets/js/lime/comm/module_index.js';
import {OrgCall as $ORG} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

$(async () => {
	domHandler.init();
	evtHandler.init();
});

const domHandler = {
	init(){
		this.hideAllCardSection()
	},
	showCardSection(role){
		const $section = Util.getElement("section[data-section="+role+"]");
		if($section){
			$section.style.display = "";
		}
		return $section;
	},
	hideAllCardSection(){
		const $sectionList = Util.getElementAll("section");
		$sectionList.forEach($section => $section.style.display = "none");
	}
}

/**
 * 네비게이션에서 선택한 관리자 영역에 소속된 사용자목록 표시 및 추가/삭제 기능을 설정
 */
const sectionHandler = {
	async setSection(role){
		// 선택한 사용자 역할에 속한 사용자 목록 카드를 보여줄 카드 섹션을 표시하고 그 노드를 가져옴
		const $section = domHandler.showCardSection(role);
		switch(role){
			case "sys-master":
				this.setMasterDomainSection($section);
				break;
			case "sys-admin":
				this.setSystemDomainSection($section);
				break;
		}
	},
	//최고관리자 설정
	async setMasterDomainSection($section){
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
	async setSystemDomainSection($section){
		$section.ondelete = (userId) => {
			AjaxBuilder({
				request: $ORG.Delete.systemDomain,
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
					const userId = selectedNodes.pop().userId;
					AjaxBuilder({
						request: $ORG.Post.systemDomain,
						param: userId,
						exception: 'success-only',
					}).success(async () => {
						const user = await $ORG.Get.userOne(userId);
						user.domainRole = "ROLE_SYSTEM_ADMIN";
						dataHandler.addSystemDomain(user);
						this.setSystemDomainSection($section);
					}).error(err => {
						Modal.error({response: err});
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
	const createCardRow =() => {
		const $row = Util.createElement("div", "bs-row", "row-cols-1", "row-cols-sm-1", "row-cols-md-1", "row-cols-lg-2", "row-cols-xl-2", "row-cols-xxl-3", "g-4");
		return $row;
	}
	const createCardColumn = (param) => {
		const {
			user,
		} = param;
		const $column = Util.createElement("div", "col");
		const $card = Util.createElement("div", "card");
		$card.id = "card-"+user.userId;
		$card.userId = user.userId;
		$card.deptId = user.deptId;
		$column.appendChild($card);
		const createCardBody = () => {
			const $cardBody = Util.createElement("div", "card-body", "d-flex", "flex-row");
			$card.appendChild($cardBody);
			const createUserPic = () => {
				const $picColumn = Util.createElement("div", "col-2", "d-flex", "justify-content-center");
				const $picImg = Util.createElement("img", "card-img", "card-img-top");
				$picImg.src = "/resources/meetingtime/lime/img/user.jpg";
				$picColumn.appendChild($picImg);
				$cardBody.appendChild($picColumn);
			}
			const createUserDetail = () => {
				const $detailColumn = Util.createElement("div", "col-8", "p-3");
				const $name = Util.createElement("h3", "card-title");
				const $title = Util.createElement("p", "card-text");
				$detailColumn.appendChild($name);
				$detailColumn.appendChild($title);
				$cardBody.appendChild($detailColumn);
				$card.setUserName = (value) => {
					$card.userName = value;
					$name.innerText = value;
				}
				$card.setTitleName = (value) => {
					$card.titleName = value;
					$title.innerText = value;
				}
			}
			const createDeleteButton = () => {
				if(!deletable){
					return;
				}
				const $buttonColumn = Util.createElement("div", "col-2", "text-right");
				const $delButton = Util.createElement("button", "btn-close");
				$delButton.type = "button";
				$delButton.onclick = async () => {
					const reply = await Modal.confirm({msg: "해당 사용자를 명단에서 제거하시겠습니까?", delMode: true});
					if(reply != "DELETE"){
						return;
					}
					const userId = $card.userId;
					AjaxBuilder({
						request: $ORG.Delete.systemDomain,
						param: userId,
						exception: 'success-only',
					}).success(async () => {
						$column.remove();
						dataHandler.deleteSystemDomain(userId);
					}).exe();
				}
				$buttonColumn.appendChild($delButton);
				$cardBody.appendChild($buttonColumn);
			}
			createUserPic();
			createUserDetail();
			createDeleteButton();
		}
		const createCardFooter = () => {
			const $cardFooter = Util.createElement("div", "card-footer");
			$card.appendChild($cardFooter);
			const createListGroup = () => {
				const $listGroup = Util.createElement("div", "list-unstyled", "list-group", "list-group-horizontal");
				const createList = (iconClass) => {
					const $li = Util.createElement("li", "mx-auto");
					const $icon = Util.createElement("i", "fas", "mx-1", "fa-"+iconClass);
					$li.appendChild($icon);
					const $text = Util.createElement("span");
					$li.appendChild($text);
					$li.setText = (text) => {
						$text.innerText = text;
					}
					return $li;
				}
				const $phoneList = createList("mobile-alt");
				$card.setPersonalCellPhone = (value) => {
					$card.personalCellPhone = value;
					$phoneList.setText(value);
				}
				const $naesunList = createList("phone-office");
				$card.setOfficeDeskPhone = (value) => {
					$card.officeDeskPhone = value;
					$naesunList.setText(value);
				}
				const $mailList = createList("envelope");
				$card.setEmail = (value) => {
					$card.email = value;
					$mailList.setText(value);
				}
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
		$card.getUserData = () => {
			return {
				userId: $card.userId,
				userName: $card.userName,
				titleName: $card.titleName,
				deptId: $card.deptId,
				personalCellPhone: $card.personalCellPhone,
				officeDeskPhone: $card.officeDeskPhone,
				email: $card.email
			};
		}
		$card.setUserData = (userInfo) => {
			const {
				userName = $card.userName || "",
				titleName = $card.titleName || "",
				personalCellPhone = $card.personalCellPhone || "",
				officeDeskPhone = $card.officeDeskPhone || "",
				email = $card.email || "",
			} = userInfo;
			$card.setUserName(userName);
			$card.setTitleName(titleName);
			$card.setPersonalCellPhone(personalCellPhone);
			$card.setOfficeDeskPhone(officeDeskPhone);
			$card.setEmail(email);
		}
		$card.setUserData(user);
		return $column;
	}
	$section.style.display = "";
	$section.loaded = true;
	const $cardContainer = $section.querySelector("article");
	$cardContainer.innerHTML = "";
	const $cardRow = createCardRow();
	for(const user of userList){
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
	async getMasterDomainList(){
		if(!this.masterDomainList){
			this.masterDomainList = await $ORG.Get.masterDomainList();
		}
		return this.masterDomainList;
	},
	//시스템 관리자 목록 조회
	async getSystemDomainList(){
		if(!this.systemDomainList){
			this.systemDomainList = await $ORG.Get.systemDomainList();
		}
		return this.systemDomainList;
	},
	//시스템 관리자 추가
	addSystemDomain(user){
		this.systemDomainList.push(user);
	},
	//시스템 관리자 삭제
	deleteSystemDomain(userId){
		this.systemDomainList = this.systemDomainList.filter(admin => admin.userId != userId);
	},
}
/**
 * DOM ELEMENT 이벤트 설정
 */
const evtHandler = {
	init(){
		this.setAdminListBtn();
	},
	setAdminListBtn(){
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
				domHandler.hideAllCardSection();
				// 선택한 사용자 역할 가져옴
				const role = $selectedLink.dataset.role;
				sectionHandler.setSection(role);
			}
			$link.addEventListener('click', () => {
				selectTab($link);
			});
			if(Util.hasClass($link, "active")){
				selectTab($link);
			}
		});
	},
}
