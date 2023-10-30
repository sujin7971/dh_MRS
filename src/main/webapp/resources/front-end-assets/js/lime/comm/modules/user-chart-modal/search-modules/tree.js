/**
 * 
 */
import {eventMixin, Util} from '/resources/core-assets/essential_index.js';
import {OrgCall as $ORG} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

const rootDeptId = '0';//트리 최상위 루트 번호
const rootLevel = -1;//트리 초기 뎁스
const lowerLevel = 2;//트리 최하위 뎁스

export default {
	__proto__: eventMixin,
	name: "tree",
	title: "조직도",
	id: "user_tree",
	parentCode: rootDeptId,
	nowLevel: rootLevel,
	generateNav(){
		const $nav = Util.createElement("li");
		this.$nav = $nav;
		$nav.id = this.id+"-tab";
		$nav.innerHTML = this.title;
		return $nav;
	},
	generateTab(){
		const $tab = Util.createElement("div", "partSelectDiv", "flex-column");
		this.$tab = $tab;
		$tab.id = this.id;
		// 셀렉트 박스(노드와 이름, 문구를 포함한 객체) 리스트
		const selectBoxList = [
			{name: "사업부", text: "사업부선택"}, 
			{name: "부서", text: "부서선택"}, 
			{name: "팀", text: "팀선택"}
		];
		// 옵션 생성 함수
		const createOption = (value, text) => {
			const $option = Util.createElement("option");
			$option.value = value;
			$option.innerHTML = text;
			return $option;
		}
		// 셀렉트 초기화
		const clearSelect = (level) => {
			const selectBox = selectBoxList[level];
			const $select = selectBox.node;
			$select.innerHTML = "";
			$select.disabled = true;
			const $option = createOption(0, selectBox.text);
			$option.style.display = "none";
			$select.appendChild($option);
		}
		// 셀렉트 옵션 설정
		const setSelectOption = (dataList) => {
			clearSelect(this.nowLevel)
			const selectBox = selectBoxList[this.nowLevel];
			const $select = selectBox.node;
			if(dataList.length == 0){
				return;
			}
			dataList.forEach(data => {
				const {
					deptId,
					deptName,
				} = data;
				const $option = createOption(deptId, deptName);
				$select.appendChild($option);
			});
			$select.disabled = false;
		}
		// 해당하는 레벨의 셀렉트 박스 가져오기
		const getSelectBox = (level) => {
			return selectBoxList[level];
		}
		// 하위 부서 목록 불러오기
		const loadSubDeptList = async () => {
			const parentCode = this.parentCode;
			this.nowLevel += 1;
			const list = await $ORG.Get.selectSubDeptList(parentCode);
			setSelectOption(list);
		}
		selectBoxList.forEach((selectBox, level) => {
			const $selectBox = Util.createElement("div", "d-flex", "flex-row");
			const $select = Util.createElement("select");
			selectBox.node = $select;
			$selectBox.appendChild($select);
			$tab.appendChild($selectBox);
			//$node.level = level;
			
			// 셀렉트 선택
			$select.onchange = async () => {
				const value = $select.value;
				if(this.nowLevel > level){
					for(let i = level + 1; i <= this.nowLevel; i++){
						clearSelect(i);
					}
				}
				this.nowLevel = level;
				this.parentCode = value;
				//최하위 부서를 선택하는 경우 다음 하위 부서 로드 하지 않음
				if(this.nowLevel != lowerLevel){
					await loadSubDeptList();
				}
				this.trigger("search", {
					deptId: value
				});
			}
			clearSelect(level);
		});
		loadSubDeptList();
		this.clear = () => {
			selectBoxList.forEach((selectBox, level) => {
				clearSelect(level);
			});
			this.parentCode = rootDeptId;
			this.nowLevel = rootLevel;
			loadSubDeptList();
		}
		this.setDeptList = (subDeptList) => {
			setSelectOption(subDeptList);
		}
		return $tab;
	},
	open(){
		Util.addClass(this.$nav, "active");
		Util.addClass(this.$tab, "d-flex");
		Util.removeClass(this.$tab, "d-none");
	},
	close(){
		Util.removeClass(this.$nav, "active");
		Util.removeClass(this.$tab, "d-flex");
		Util.addClass(this.$tab, "d-none");
	}
}