/**
 * 
 */
import {eventMixin, Util, Modal, AjaxBuilder, FormHelper} from '/resources/core-assets/essential_index.js';
import {OrgCall as $ORG} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';
$(async () => {
	MemberHandler.init();
	DeptHandler.init();
});
const OrganDataResolver = {
	//최고 관리자 목록 조회
	async getDeptList(){
		const deptList = await $ORG.Get.selectAllDeptInfoList();
		DeptHandler.setModalFormDeptList(deptList);
		MemberHandler.setModalFormDeptList(deptList);
		return deptList;
	},
	async addDeptOne(deptInfo){
		this.deptList.push(deptInfo);
	},
	async deleteDeptOne(deptId){
		this.deptList = this.deptList.filter(deptInfo => deptInfo.deptId != deptId);
	},
}
const DeptHandler = {
	async init(){
		this.modal = Modal.get("addDeptModal");
		this.formHelper = new FormHelper();
		this.formHelper.addFormElements("#deptForm");
		this.bindElementsEvent();
		
		await this.showDeptTree();
		await this.setSelectedDept();
	},
	bindElementsEvent(){
		this.enableDeptAddBtn = () => {
			const $btn = Util.getElement("#addDeptBtn");
			$btn.disabled = false;
			$btn.onclick = () => {
				this.showAddModal();
			}
		}
		this.disableDeptAddBtn = () => {
			const $btn = Util.getElement("#addDeptBtn");
			$btn.disabled = true;
			$btn.onclick = null;
		}
		this.enableDeptDeleteBtn = () => {
			const $btn = Util.getElement("#deleteDeptBtn");
			$btn.disabled = false;
			$btn.onclick = async () => {
				const selectedDept = this.getSelectedDept();
				const reply = await Modal.confirm({msg: selectedDept.deptName + " 부서를 명단에서 제거하시겠습니까?", delMode: true});
				if(reply != "DELETE"){
					return;
				}
				const result = await $ORG.Delete.deleteDeptOne(selectedDept.deptId);
				if(result.status != 200){
					console.log("result", result)
					Modal.error({response: result});
					return;
				}
				await this.showDeptTree();
				this.setSelectedDept({
		    		deptId: 0,
		    		deptName: "미지정"
		    	});
			}
		}
		this.disableDeptDeleteBtn = () => {
			const $btn = Util.getElement("#deleteDeptBtn");
			$btn.disabled = true;
			$btn.onclick = null;
		}
		const setInvalidDeptLinkBtn = () => {
			const $link = Util.getElement("#invalidDeptLink");
			$link.onclick = () => {
				this.disableDeptAddBtn();
				this.disableDeptDeleteBtn();
				this.setSelectedDept({
					deptId: 0,
					deptName: "미지정",
				});
			}
		}
		setInvalidDeptLinkBtn();
	},
	getModal(){
		return this.modal;
	},
	getFormHelper(){
		return this.formHelper;
	},
	setModalFormDeptList(deptList){
		console.log("this.getFormHelper()", this.getFormHelper())
		const $deptIdInput = this.getFormHelper().getForm("parentId").getElement();
		$deptIdInput.innerHTML = "";
		for(const dept of deptList){
			const $option = Util.createElement("option");
			$option.innerText = dept.deptName;
			$option.value = dept.deptId;
			$deptIdInput.append($option);
		}
	},
	async showAddModal(){
		const $modal = this.getModal();
		const selectedDept = this.getSelectedDept();
		this.getFormHelper().reset();
		this.getFormHelper().setValues({
			parentId: selectedDept.deptId
		});
		const reply = await $modal.show();
		if(reply != "OK"){
			return;
		}
		const result = await $ORG.Post.insertDeptOne(this.getFormHelper().getFormData());
		console.log(this.getFormHelper().getFormValues());
		if(result.status != 200){
			Modal.error({response: result});
			return;
		}else{
			await this.showDeptTree();
			const deptList = await OrganDataResolver.getDeptList();
			const newDeptId = Math.max(...deptList.map((item) => item.deptId * 1));
			const newDeptNm = this.getFormHelper().getFormValues().deptName;
			this.setSelectedDept({
	    		deptId: newDeptId,
	    		deptName: newDeptNm
	    	});
		}
	},
	async showDeptTree(){
		const convertDeptListToTree = (departments) => {
			const departmentMap = {};
			  let result = [];

			  for (const department of departments) {
			    const { deptId, deptName, parentId } = department;
			    const departmentObj = { title: deptName, key: deptId };

			    if (parentId && departmentMap[parentId]) {
			      if (!departmentMap[parentId].children) {
			        departmentMap[parentId].children = [];
			      }
			      departmentMap[parentId].children.push(departmentObj);
			    } else {
			      result.push(departmentObj);
			    }

			    departmentMap[deptId] = departmentObj;
			  }
			  return result;
		}
		const deptList = await OrganDataResolver.getDeptList();
		const deptTree = convertDeptListToTree(deptList);
		if(this.tree){
			$("#deptTree").fancytree("option", "source", deptTree);
		}else{
			const $tree = $("#deptTree").fancytree({
				extensions: ['edit', 'filter', "glyph"],
				selectMode: 1,
				icon: false,
			    quicksearch: true,
			    filter: {
			        autoApply: true,   // Re-apply last filter if lazy data is loaded
			        autoExpand: true, // Expand all branches that contain matches while filtered
			        counter: true,     // Show a badge with number of matching child nodes near parent icons
			        fuzzy: false,      // Match single characters in order, e.g. 'fb' will match 'FooBar'
			        hideExpandedCounter: true,  // Hide counter badge if parent is expanded
			        hideExpanders: false,       // Hide expanders if all child nodes are hidden by filter
			        highlight: true,   // Highlight matches by wrapping inside <mark> tags
			        leavesOnly: false, // Match end nodes only
			        nodata: true,      // Display a 'no data' status node if result is empty
			        mode: "dimm"       // Grayout unmatched nodes (pass "hide" to remove unmatched node instead)
			    },
			    glyph: {
			        preset: "awesome5",
			        map: {}
			    },
			    source: deptTree,
			    activate: (event, data) => {
			    	const node = data.node;
			    	const {
			    		key,
			    		title
			    	} = node;
			    	this.enableDeptAddBtn();
					this.enableDeptDeleteBtn();
			    	this.setSelectedDept({
			    		deptId: key,
			    		deptName: title
			    	});
			    },
			});
			this.tree = $.ui.fancytree.getTree("#deptTree");
		}
	},
	async setSelectedDept(data = {}){
		const {
			deptId = 1,
			deptName = "라임에스엔씨"
		} = data;
		this.deptId = deptId;
		this.deptName = deptName;
		const $memberTitle = Util.getElement("#memberTitle");
		$memberTitle.innerText = `${deptName} 직원`;
		
		const tree = $("#deptTree").fancytree("getTree");
		const node = tree.getNodeByKey(deptId);
		if(this.getSelectedDept().deptId == 0) $("#invalidDeptLink").addClass("focused");
		else $("#invalidDeptLink").removeClass("focused");
		if(node != null) {
			node.setSelected(true);
			node.setActive();
		} else if($("#deptTree").fancytree("getActiveNode")) tree.getActiveNode().setActive(false);
		await this.showDeptMember();
	},
	getSelectedDept(){
    	return  {
    		deptId: this.deptId,
    		deptName: this.deptName
    	}
	},
	async showDeptMember(){
		const selectedDept = this.getSelectedDept();
		const userList = await $ORG.Get.userList({deptId: selectedDept.deptId});
		userCardGenerator({
			userList: userList,
			editable: true,
			deletable: true,
		});
	},
}
/**
 * 사용자 목록을 전달받아 해당 영역에 사용자 정보를 표시할 카드를 생성
 */
const userCardGenerator = (param = {}) => {
	const {
		userList = [],
		deletable = false,
		editable = false,
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
				const $buttonColumn = Util.createElement("div", "col-1", "text-right");
				const $delButton = Util.createElement("button", "btn-close");
				$delButton.type = "button";
				$delButton.onclick = async () => {
					const reply = await Modal.confirm({msg: "해당 사용자를 명단에서 제거하시겠습니까?", delMode: true});
					if(reply != "DELETE"){
						return;
					}
					const result = await $ORG.Delete.userOne($card.userId);
					if(result.status != 200){
						Modal.error({response: result});
						return;
					}
					$column.remove();
				}
				$buttonColumn.appendChild($delButton);
				$cardBody.appendChild($buttonColumn);
			}
			const createEditButton = () => {
				if(!editable){
					return;
				}
				const $buttonColumn = Util.createElement("div", "col-1", "text-right");
				const $editButton = Util.createElement("button", "btn-edit");
				$editButton.type = "button";
				$editButton.onclick = async () => {
					MemberHandler.showEditModal($card.getUserData());
				}
				$buttonColumn.appendChild($editButton);
				$cardBody.appendChild($buttonColumn);
			}
			createUserPic();
			createUserDetail();
			createEditButton();
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
	const $cardContainer = Util.getElement("#memberCardSection");
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

const MemberHandler = {
	init(){
		this.modal = Modal.get("addMemberModal");
		this.formHelper = new FormHelper();
		this.formHelper.addFormElements("#memberForm");
		
		const $btn = Util.getElement("#addMemberBtn");
		$btn.onclick = async () => {
			this.showAddModal();
		}
	},
	getModal(){
		return this.modal;
	},
	getFormHelper(){
		return this.formHelper;
	},
	setModalFormDeptList(deptList){
		const $deptIdInput = this.getFormHelper().getForm("deptId").getElement();
		$deptIdInput.innerHTML = "";
		for(const dept of deptList){
			const $option = Util.createElement("option");
			$option.innerText = dept.deptName;
			$option.value = dept.deptId;
			$deptIdInput.append($option);
		}
	},
	async showAddModal(){
		const $modal = this.getModal();
		const selectedDept = DeptHandler.getSelectedDept();
		this.getFormHelper().reset();
		this.getFormHelper().setValues(selectedDept);
		const reply = await $modal.show();
		if(reply != "OK"){
			return;
		}
		const formVal = this.getFormHelper().getFormValues();
		if(formVal.userName.trim == null || formVal.userName.trim == "") Modal.error({response: "이름이 비어있습니다."});
		else if(formVal.userId.trim == null || formVal.userId.trim == "") Modal.error({response: "사번이 비어있습니다."});
		const result = await $ORG.Post.userOne(this.getFormHelper().getFormData());
		if(result.status != 200){
			console.log(result);
			Modal.error({response: result});
			return;
		}
		const deptForm = this.getFormHelper().getForm("deptId");
		const deptId = deptForm.getValue();
		const deptName = deptForm.getSelectedText();
		DeptHandler.setSelectedDept({deptId: deptId, deptName: deptName});
	},
	async showEditModal(userInfo){
		const $modal = this.getModal();
		this.getFormHelper().setValues(userInfo);
		const reply = await $modal.show();
		if(reply != "OK"){
			return;
		}
		const result = await $ORG.Put.userOne(this.getFormHelper().getFormData());
		if(result.status != 200){
			Modal.error({response: result});
			return;
		}
		const userValues = this.getFormHelper().getFormValues();
		const $card = Util.getElement("#card-"+userValues.userId);
		$card.setUserData(userValues);
	},
}
