/**
 * 
 */
import {eventMixin, Util, Modal} from '/resources/core-assets/essential_index.js';

$(async () => {
	domHandler.init();
	evtHandler.init();
});

const domHandler = {
	init(){
		this.setUserIcon();
		//this.setUserImg();
	},
	// 사용자 역할에 맞는 아이콘 표시
	setUserIcon(){
		const inputList = Util.getElementAll("[data-input=userName]");
		const iconClass = (userRole.includes("ROLE_MASTER_ADMIN"))?"adminS":(userRole.includes("ROLE_SYSTEM_ADMIN")?"admin":"");
		inputList.forEach($input => {
			Util.addClass($input, iconClass);
		});
	},
	setUserImg(){
		const $userPic = Util.getElement("#userPic");
		const $img = Util.createElement("img", "img-profile", "my-4");
		$img.src = "/api/lime/user/"+loginId+"/img";
		$img.onload = () => {
			Util.removeClass($userPic, "userPic");
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
		const $expandBtn = Util.getElement(".btn-screen.full");
		const $contractBtn = Util.getElement(".btn-screen.win");
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
		const $menu = Util.getElement("div.shy-menu");
		if($menu){
			const $wrapper = Util.getElement(".wrapper");
			const $panel = Util.getElement(".shy-menu-panel");
			const closePanel = () => {
				Util.removeClass($menu, "is-open");
				$wrapper.onclick = null;
			}
			const openPanel = () => {
				Util.addClass($menu, "is-open");
				$wrapper.onclick = () => {
					closePanel();
				}
			}
			$menu.querySelector(".shy-menu-hamburger").onclick = () => {
				if(Util.hasClass($menu, "is-open")){
					closePanel();
				}else{
					openPanel();
				}
			}
		}
	},
	// 사용자 정보 모달 팝업 설정
	setUserInfoBtn(){
		const btnList = Util.getElementAll("[data-btn=userName]");
		const showInfoModal = () => {
			Modal.show("userInfoModal");
		};
		btnList.forEach($btn => {
			$btn.onclick = showInfoModal;
		})
	},
	// 로그아웃 설정
	setLogoutBtn(){
		const $btn = Util.getElement("#logoutBtn");
		if($btn){
			$btn.onclick = () => {
				Modal.confirm({msg: "로그아웃 하시겠습니까?"}).then(function(btn){
					if(btn == "OK"){
						location.href = "/logout";
					}
				});
			}
		}
	},
}
