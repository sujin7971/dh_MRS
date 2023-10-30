/**
 * 
 */
import {Dom, ModalService} from '/resources/core-assets/essential_index.js';

document.addEventListener('DOMContentLoaded', async () => {
	domHandler.init();
	evtHandler.init();
});
const domHandler = {
	init(){
		this.setUserIcon();
		this.setUserImg();
	},
	// 사용자 역할에 맞는 아이콘 표시
	setUserIcon(){
		const inputList = Dom.getElementAll("[data-input=userName]");
		const iconClass = (userRole.includes("ROLE_MASTER_ADMIN"))?"adminS":(userRole.includes("ROLE_SYSTEM_ADMIN")?"admin":"");
		inputList.forEach($input => {
			Dom.addClass($input, iconClass);
		});
	},
	setUserImg(){
		const $userPic = Dom.getElement("#userPic");
		const $img = Dom.createElement("img", "img-profile", "my-4");
		$img.src = "/api/ewp/user/"+loginKey+"/img";
		$img.onload = () => {
			Dom.removeClass($userPic, "userPic");
			$userPic.appendChild($img);
		}
	}
}
const evtHandler = {
	init(){
		this.setExpand();
		this.setDropDown();
		this.setUserInfoBtn();
		this.setLogoutBtn();
	},
	// 확대,축소 버튼 설정
	setExpand(){
		const $expandBtn = Dom.getElement(".btn-screen.full");
		const $contractBtn = Dom.getElement(".btn-screen.win");
		const expandScreen = () => {
			if (docV.requestFullscreen)
		        docV.requestFullscreen();
		    else if (docV.webkitRequestFullscreen) // Chrome, Safari (webkit)
		        docV.webkitRequestFullscreen();
		    else if (docV.mozRequestFullScreen) // Firefox
		        docV.mozRequestFullScreen();
		    else if (docV.msRequestFullscreen) // IE or Edge
		        docV.msRequestFullscreen();
		}
		const contractScreen = () => {
			if (document.exitFullscreen)
			    document.exitFullscreen();
		    else if (document.webkitExitFullscreen) // Chrome, Safari (webkit)
		    	document.webkitExitFullscreen();
		    else if (document.mozCancelFullScreen) // Firefox
		    	document.mozCancelFullScreen();
		    else if (document.msExitFullscreen) // IE or Edge
		    	document.msExitFullscreen();
		}
		if($expandBtn && $contractBtn){
			$expandBtn.onclick = () => {
				expandScreen();
				$expandBtn.style.display = "none";
				$contractBtn.style.display = "";
			}
			$contractBtn.onclick = () => {
				contractScreen();
				$expandBtn.style.display = "";
				$contractBtn.style.display = "none";
			}
		}
	},
	// 메뉴 드롭다운 설정
	setDropDown(){
		const $menu = Dom.getElement("div.shy-menu");
		if($menu){
			const $wrapper = Dom.getElement(".wrapper");
			const $panel = Dom.getElement(".shy-menu-panel");
			const closePanel = () => {
				Dom.removeClass($menu, "is-open");
				$wrapper.onclick = null;
			}
			const openPanel = () => {
				Dom.addClass($menu, "is-open");
				$wrapper.onclick = () => {
					closePanel();
				}
			}
			$menu.querySelector(".shy-menu-hamburger").onclick = () => {
				if(Dom.hasClass($menu, "is-open")){
					closePanel();
				}else{
					openPanel();
				}
			}
		}
	},
	// 사용자 정보 모달 팝업 설정
	setUserInfoBtn(){
		const btnList = Dom.getElementAll("[data-btn=userName]");
		const showInfoModal = () => {
			ModalService.get("profile").show();
		};
		btnList.forEach($btn => {
			$btn.onclick = showInfoModal;
		})
	},
	// 로그아웃 설정
	setLogoutBtn(){
		const $btn = Dom.getElement("#logoutBtn");
		if($btn){
			$btn.onclick = async () => {
				const {action} = await ModalService.confirmBuilder().setMessage("로그아웃 하시겠습니까?").build().show();
				if(action == "OK"){
					location.href = "/logout";
				}
			}
		}
	},
}
