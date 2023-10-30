/**
 * 
 */
import {eventMixin, Util} from '/resources/core-assets/essential_index.js';

const modalCollector = {
	collection: new Set(),
	add($modal){
		this.collection.add($modal);
	},
	delete($modal){
		this.collection.delete($modal);
	},
	get(id){
		return Array.from(this.collection).find($modal => $modal.id == id);
	},
	getAll(){
		return this.collection;
	},
	hasSingleton(){
		const singleton = Array.from(this.collection).find($modal => $modal.singleton == true);
		return (singleton)?true:false;
	},
}

const modalSwitch = ($modal, action) => {
	if(action == "show"){
		if(modalCollector.hasSingleton() && $modal.singleton != true){
			return;
		}
		if($modal.singleton == true){
			modalCollector.getAll().forEach($modal => {
				$modal.close();
			});
		}
		modalCollector.add($modal);
	}else if(action == "hide"){
		modalCollector.delete($modal);
		if($modal.persistent != true){
			$modal.remove();
		}
	}
}

const modalGenerator = {
	promisifyModal(selector){
		const $modal = (typeof selector == "string")?Util.getElement(selector):selector;
		if(!$modal){
			return null;
		}else if($modal.promisify == true){
			return $modal;
		}
		const $btnList = $modal.querySelectorAll('[data-modal-btn]');
		$btnList.forEach($btn => {
			$btn.addEventListener('click', () => {
				$modal.resolve?.($btn.dataset.modalBtn);
				const closeOption = $btn.dataset.modalClose;
				if(!closeOption || $btn.dataset.modalClose != "manual"){
					$modal.hide();
				}
			});
			$btn.setText = (text) => {
				$btn.innerText = text;
				return $btn;
			}
			$btn.setColor = (color) => {
				switch(color){
					case "blue":
						Util.removeClass("$btn", "btn-red", "btn-white");
						Util.addClass("$btn", "btn-blue");
						break;
					case "red":
						Util.removeClass("$btn", "btn-blue", "btn-white");
						Util.addClass("$btn", "btn-red");
						break;
					case "white":
						Util.removeClass("$btn", "btn-red", "btn-blue");
						Util.addClass("$btn", "btn-white");
						break;
				}
				return $btn;
			}
		});
		$modal.getBtn = (name) => {
			const $selectedBtn = Array.from($btnList).filter($btn => $btn.dataset.modalBtn == name);
			return $selectedBtn?.pop();
		}
		$modal.show = (callback) => {
			$modal.style.display = "block";
			$modal.onshow?.();
			$modal.onswitch?.($modal, "show");
			callback?.();
			return new Promise((resolve, reject) => {
				$modal.resolve = resolve;
				$modal.reject = reject;
				$modal.close = () => {
					resolve("CLOSE");
					$modal.hide();
				}
			});
		}
		$modal.hide = () => {
			$modal.style.display = "none";
			$btnList.forEach($btn => {
				$btn.onclick = undefined;
			});
			$modal.onhide?.();
			$modal.onswitch?.($modal, "hide");
		}
		$modal.close = $modal.hide;
		$modal.enableBtn = (name) => {
			const $btn = $modal.querySelector('[data-modal-btn="'+name+'"]');
			if($btn){
				$btn.disabled = false;
			}
		}
		$modal.disableBtn = (name) => {
			const $btn = $modal.querySelector('[data-modal-btn="'+name+'"]');
			if($btn){
				$btn.disabled = true;
			}
		}
		$modal.on = (event, callback) => {
			switch(event){
				case "show":
					$modal.onshow = callback;
					break;
				case "hide":
					$modal.onhide = callback;
					break;
				case "switch":
					$modal.onswitch = callback;
					break;
			}
			return $modal;
		}
		$modal.off = (event) => {
			switch(event){
				case "show":
					$modal.onshow = null;
					break;
				case "hide":
					$modal.onhide = null;
					break;
				case "switch":
					$modal.onswitch = null;
					break;
			}
			return $modal;
		}
		$modal.promisify = true;
		return $modal;
	},
	buildTemplate(direction){
		const $modal = Util.createElement("div", "modal", "top-front");
		const $wrapper = Util.createElement("div", "modalWrap");
		$modal.appendChild($wrapper);
		const $content = Util.createElement("div", "modal_content");
		$wrapper.appendChild($content);
		
		const $title = Util.createElement("div", "modalTitle");
		$title.style.display = "none";
		$content.appendChild($title);
		$modal.getTitleDiv = () => {
			return $title;
		}
		const $body = Util.createElement("div", "modalBody", "flex-direction-column");
		$body.style.display = "none";
		$content.appendChild($body);
		$modal.getMsgDiv = () => {
			return $body;
		}
		const $detailDiv = Util.createElement("div", "modalInfo");
		$detailDiv.style.display = "none";
		$content.appendChild($detailDiv);
		$modal.getDetailDiv = () => {
			return $detailDiv;
		}
		
		const $btnDiv = Util.createElement("div", "modalBtnDiv");
		if(direction == "vertical"){
			Util.addClass($btnDiv, "flex-direction-column");
		}
		$content.appendChild($btnDiv);
		$modal.addBtn = ($btn) => {
			$btnDiv.appendChild($btn);
		}
		$modal.singleton = false;
		$modal.addBlackout = () => {
			$modal.style.background = "rgba(0,0,0,0.5)";
		}
		$modal.removeBlackout = () => {
			$modal.style.background = "";
		}
		return $modal;
	},
	buildButton(type){
		switch(type){
			case "OK":
				const $okBtn = Util.createElement("button", "btn", "btn-md", "btn-blue");
				$okBtn.innerHTML = "확 인"
				$okBtn.dataset.modalBtn = "OK";
				return $okBtn;
				break;
			case "CLOSE":
				const $closeBtn = Util.createElement("button", "btn", "btn-md", "btn-silver");
				$closeBtn.innerHTML = "닫 기";
				$closeBtn.dataset.modalBtn = "CLOSE";
				return $closeBtn;
				break;
			case "CANCEL":
				const $cancelBtn = Util.createElement("button", "btn", "btn-md", "btn-silver");
				$cancelBtn.innerHTML = "취 소";
				$cancelBtn.dataset.modalBtn = "CANCEL";
				return $cancelBtn;
				break;
			case "DELETE":
				const $delBtn = Util.createElement("button", "btn", "btn-md", "btn-red");
				$delBtn.innerHTML = "삭 제";
				$delBtn.dataset.modalBtn = "DELETE";
				return $delBtn;
				break;
			default: 
				const {
					isPrimary,
					key,
					text,
				} = type;
				const $selectBtn = Util.createElement("button", "btn", "btn-md");
				if(isPrimary == true){
					Util.addClass($selectBtn, "btn-blue");
				}else{
					Util.addClass($selectBtn, "btn-blue-border");
				}
				$selectBtn.innerHTML = text;
				$selectBtn.dataset.modalBtn = key;
				return $selectBtn;
				break;
		}
	},
	buildModal(data = {}){
		const {
			btnList,
			direction = "horizontal",
			target = Util.getElement("body"),
		} = data;
		const $modal = this.buildTemplate(direction);
		btnList.forEach(btnName => $modal.addBtn(this.buildButton(btnName)));
		$modal.setTitle = (title) => {
			const $title = $modal.getTitleDiv();
			$title.style.display = "";
			$title.innerHTML = title;
		}
		$modal.setMsg = ($msg) => {
			const $comment = Util.createElement("div", "commonMent");
			$comment.innerHTML = $msg;
			const $msgDiv = $modal.getMsgDiv();
			$msgDiv.querySelector(".commonMent")?.remove();
			$msgDiv.style.display = "";
			$msgDiv.appendChild($comment);
		}
		$modal.setDetail = (...$detailList) => {
			if($detailList.length == 0){
				return;
			}
			const $ul = Util.createElement("ul", "modalInfo");
			for(const $detail of $detailList){
				const $li = Util.createElement("li");
				$li.innerHTML = ($detail.message)?$detail.message:$detail;
				$ul.appendChild($li);
			}
			const $detailDiv = $modal.getDetailDiv();
			$detailDiv.style.display = "";
			$detailDiv.appendChild($ul);
		}
		$modal.setErrorInfo = (...$detailList) => {
			const $ul = Util.createElement("ul", "modalInfo");
			for(const $detail of $detailList){
				const $li = Util.createElement("li");
				$li.innerHTML = ($detail.message)?$detail.message:$detail;
				$ul.appendChild($li);
			}
			$modal.setDetail($ul);
		}
		$modal.persistent = false;
		target.appendChild($modal);
		return this.promisifyModal($modal).on(modalSwitch);
	},
}

const loadingGenerator = {
	animations: {
		doubleBounce: () => {
				const $container = Util.createElement("div", "sk-double-bounce");
				const $animationToken1 = Util.createElement("div", "sk-child", "sk-double-bounce1");
				const $animationToken2 = Util.createElement("div", "sk-child", "sk-double-bounce2");
				$container.appendChild($animationToken1);
				$container.appendChild($animationToken2);
				return $container;
		},
		pulse: () => {
			const $container = Util.createElement("div", "sk-spinner", "sk-spinner-pulse");
			return $container;
		},
		spinner: () => {
			const $container = Util.createElement("div", "sk-fading-circle");
			for(let i =0; i<12; i++){
				const $animationToken = Util.createElement("div", "sk-circle", "sk-circle"+(i+1));
				$container.appendChild($animationToken);
			}
			return $container;
		},
		foldingCube: () => {
			const $container = Util.createElement("div", "sk-folding-cube");
			const $animationToken1 = Util.createElement("div", "sk-cube", "sk-cube1");
			const $animationToken2 = Util.createElement("div", "sk-cube", "sk-cube2");
			const $animationToken3 = Util.createElement("div", "sk-cube", "sk-cube3");
			const $animationToken4 = Util.createElement("div", "sk-cube", "sk-cube4");
			$container.appendChild($animationToken1);
			$container.appendChild($animationToken2);
			$container.appendChild($animationToken4);
			$container.appendChild($animationToken3);
			return $container;
		},
		/*
        doubleBounce: {
        	html: '<div class="sk-double-bounce"><div class="sk-child sk-double-bounce1"></div><div class="sk-child sk-double-bounce2"></div></div>'
        },
        rotatingPlane: {
        	html: '<div class="sk-rotating-plane"></div>', 
        },
        wave: {
        	html: '<div class="sk-wave"> <div class="sk-rect sk-rect1"></div> <div class="sk-rect sk-rect2"></div> <div class="sk-rect sk-rect3"></div> <div class="sk-rect sk-rect4"></div> <div class="sk-rect sk-rect5"></div> </div>'
        },
        wanderingCubes: {
        	html: '<div class="sk-wandering-cubes"><div class="sk-cube sk-cube1"></div><div class="sk-cube sk-cube2"></div></div>'
        },
        chasingDots: {
        	html: '<div class="sk-chasing-dots"><div class="sk-child sk-dot1"></div><div class="sk-child sk-dot2"></div></div>'
        },
        threeBounce: {
        	html: '<div class="sk-three-bounce"><div class="sk-child sk-bounce1"></div><div class="sk-child sk-bounce2"></div><div class="sk-child sk-bounce3"></div></div>'
        },
        circle: {
        	html: '<div class="sk-circle"> <div class="sk-circle1 sk-child"></div> <div class="sk-circle2 sk-child"></div> <div class="sk-circle3 sk-child"></div> <div class="sk-circle4 sk-child"></div> <div class="sk-circle5 sk-child"></div> <div class="sk-circle6 sk-child"></div> <div class="sk-circle7 sk-child"></div> <div class="sk-circle8 sk-child"></div> <div class="sk-circle9 sk-child"></div> <div class="sk-circle10 sk-child"></div> <div class="sk-circle11 sk-child"></div> <div class="sk-circle12 sk-child"></div> </div>', 
        },
        cubeGrid: {
        	html: '<div class="sk-cube-grid"> <div class="sk-cube sk-cube1"></div> <div class="sk-cube sk-cube2"></div> <div class="sk-cube sk-cube3"></div> <div class="sk-cube sk-cube4"></div> <div class="sk-cube sk-cube5"></div> <div class="sk-cube sk-cube6"></div> <div class="sk-cube sk-cube7"></div> <div class="sk-cube sk-cube8"></div> <div class="sk-cube sk-cube9"></div> </div>'
        },
        fadingCircle: {
        	html: '<div class="sk-fading-circle"> <div class="sk-circle1 sk-circle"></div> <div class="sk-circle2 sk-circle"></div> <div class="sk-circle3 sk-circle"></div> <div class="sk-circle4 sk-circle"></div> <div class="sk-circle5 sk-circle"></div> <div class="sk-circle6 sk-circle"></div> <div class="sk-circle7 sk-circle"></div> <div class="sk-circle8 sk-circle"></div> <div class="sk-circle9 sk-circle"></div> <div class="sk-circle10 sk-circle"></div> <div class="sk-circle11 sk-circle"></div> <div class="sk-circle12 sk-circle"></div> </div>', 
        },
        foldingCube: {
        	html: '<div class="sk-folding-cube"> <div class="sk-cube1 sk-cube"></div> <div class="sk-cube2 sk-cube"></div> <div class="sk-cube4 sk-cube"></div> <div class="sk-cube3 sk-cube"></div> </div>', 
        },
        */
    },
    promisifyModal(selector){
		const $modal = (typeof selector == "string")?Util.getElement(selector):selector;
		if(!$modal){
			return null;
		}else if($modal.promisify == true){
			return $modal;
		}
		const $btnList = $modal.querySelectorAll('[data-modal-btn]');
		$modal.show = () => {
			Util.addClass($modal, "visible");
			Util.removeClass($modal, "hidden");
			$modal.onshow?.();
			$modal.onswitch?.($modal, "show");
			return new Promise((resolve, reject) => {
				$btnList.forEach($btn => {
					$btn.onclick = () => {
						resolve($btn.dataset.modalBtn);
						$modal.hide();
					}
				});
				$modal.close = () => {
					resolve("CLOSE");
					$modal.hide();
				}
			});
		}
		$modal.hide = () => {
			Util.removeClass($modal, "visible");
			Util.addClass($modal, "hidden");
			$btnList.forEach($btn => {
				$btn.onclick = undefined;
			});
			$modal.onhide?.();
			$modal.onswitch?.($modal, "hide");
		}
		$modal.close = $modal.hide;
		$modal.on = (event, callback) => {
			switch(event){
				case "show":
					$modal.onshow = callback;
					break;
				case "hide":
					$modal.onhide = callback;
					break;
				case "switch":
					$modal.onswitch = callback;
					break;
			}
			return $modal;
		}
		$modal.off = (event) => {
			switch(event){
				case "show":
					$modal.onshow = null;
					break;
				case "hide":
					$modal.onhide = null;
					break;
				case "hide":
					$modal.onswitch = null;
					break;
			}
			return $modal;
		}
		$modal.promisify = true;
		return $modal;
	},
    buildTemplate(){
		const $modal = Util.createElement("div", "modal", "loading-modal", "visible");
		const $infoBox = Util.createElement("div", "loading-info-box", "m-2");
		$modal.appendChild($infoBox);
		const $animationBox = Util.createElement("div", "loading-animation");
		$infoBox.appendChild($animationBox);
		const $text = Util.createElement("div", "loading-text");
		$infoBox.appendChild($text);
		$modal.setAnimation = (name) => {
			const $animation = this.animations[name]?.();
			$animationBox.appendChild($animation);
			$modal.animation = $animation;
		}
		$modal.setPosition = (position) => {
			$modal.style.position = position;
		}
		$modal.setText = (text) => {
			$text.innerHTML = text;
		}
		$modal.setTextColor = (color) => {
			$text.style.color = color;
		}
		$modal.setAnimationColor = (color) => {
			$modal.animation.setColor(color);
		}
		$modal.setBackgroundColor = (color) => {
			$modal.style.backgroundColor = color;
		}
		$modal.setBackgroundOpacity = (opacity) => {
			$modal.style.opacity = opacity;
		}
		$modal.persistent = false;
		$modal.addBlackout = () => {
			$modal.style.background = "rgba(0,0,0,0.5)";
			Util.removeClass($animationBox, "blackout");
		}
		$modal.removeBlackout = () => {
			$modal.style.background  = "none";
			Util.addClass($animationBox, "blackout");
		}
		return $modal;
	},
	buildLoading(data = {}){
		const {
			position = 'auto',
            text = '',
            color = '#fff',
            opacity = '0.7',
            backgroundColor = 'rgb(0,0,0)',
            animation = 'doubleBounce',
            target = Util.getElement("body"),
            blackout = true,
            isModal = true,
		} = data;
		const $modal = (modalCollector.get("loadingModal"))?modalCollector.get("loadingModal"):this.buildTemplate();
		$modal.setPosition(position);
		$modal.setAnimation(animation);
		$modal.setText(text);
		$modal.setTextColor(color);
		$modal.setBackgroundColor(backgroundColor);
		$modal.setBackgroundOpacity(opacity);
		if(blackout){
			$modal.addBlackout();
		}else {
			$modal.removeBlackout();
		}
		if(!isModal){
			Util.removeClass($modal, "modal");
		}
		target.appendChild($modal);
		return this.promisifyModal($modal).on("switch", modalSwitch);
	}
}

const modalProvider = {
	init(){
		this.target = Util.getElement("body");
		this.persistentMap = new Map();
		const modalList = Util.getElementAll(".modal");
		modalList.forEach(($modal, index) => {
			const $promisifiedModal = modalGenerator.promisifyModal($modal)?.on("switch", modalSwitch);
			if($promisifiedModal){
				$promisifiedModal.persistent = true;
				const id = ($promisifiedModal.id)?$promisifiedModal.id:"modal"+index;
				this.persistentMap.set(id, $promisifiedModal);
				$promisifiedModal.hide();
			}
		});
	},
	persistentModal(id){
		return this.persistentMap.get(id);
	},
	info(data = {}){
		const {
			id = "infoModal",
			title = "알 림",
			detail,
			msg,
			singleton = false,
		} = data;
		console.log("info modal", "title", title, "msg", msg, "detail", detail);
		const $modal = modalGenerator.buildModal({btnList: ["OK"]});
		$modal.id = id;
		$modal.singleton = singleton;
		if(title){
			$modal.setTitle(title);
		}
		if(msg){
			$modal.setMsg(msg);
		}
		if(detail){
			$modal.setDetail(...detail);
		}
		return $modal;
	},
	confirm(data = {}){
		const {
			id = "confirmModal",
			title,
			msg,
			detail,
			delMode = false,
			singleton = false,
		} = data;
		const btnList = (delMode == true)?["CANCEL", "DELETE"]:["CANCEL", "OK"];
		const $modal = modalGenerator.buildModal({btnList: btnList});
		$modal.id = id;
		$modal.singleton = singleton;
		if(title){
			$modal.setTitle(title);
		}
		if(msg){
			$modal.setMsg(msg);
		}
		if(detail){
			$modal.setDetail(...detail);
		}
		return $modal;
	},
	select(data = {}){
		const {
			id = "selectModal",
			title,
			msg,
			select = [],
			singleton = false,
			direction = "vertical",
		} = data;
		select.push("CLOSE");
		const $modal = modalGenerator.buildModal({btnList: select, direction: direction});
		$modal.id = id;
		$modal.singleton = singleton;
		if(title){
			$modal.setTitle(title);
		}
		if(msg){
			$modal.setMsg(msg);
		}
		return $modal;
	},
	error(data = {}){
		const {
			id = "errorModal",
			singleton = false,
			response: {
				status,
				responseText,
				message,
				detail,
				error,
			},
		} = data;
		const $modal = modalGenerator.buildModal({btnList: ["OK"]});
		$modal.id = id;
		$modal.singleton = singleton;
		switch(status){
			case 0:
				$modal.setTitle("연결 실패");
				$modal.setMsg("서버와의 연결이 끊어졌습니다. 인터넷 연결 상태를 확인하고 다시 시도해주세요.");
				$modal.onhide = ($elem) => {
					location.href = "/login";
				}
				$modal.singleton = true;
				break;
			case 500:
				$modal.setTitle("500 Internal Server Error");
				$modal.setMsg("서버에서 요청을 처리할 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.");
				break;
			case 400:
				$modal.setTitle("400 Bad Request");
				$modal.setMsg("요청문에 오류가 있거나 서버가 요청을 이해하지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.");
				break;
			case 422:
				$modal.setTitle("422 Unprocessable Entity");
				$modal.setMsg("요청에 대해 응답할 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.");
				break;
			case 401:
				$modal.setTitle("401 Unauthorized");
				$modal.setMsg((responseText)?responseText:"인증되지 않은 사용자의 요청입니다. 로그인 페이지로 이동합니다.");
				$modal.onhide = ($elem) => {
					location.href = "/login";
				}
				$modal.singleton = true;
				break;
			case 403:
				$modal.setTitle("403 Forbidden");
				$modal.setMsg("요청에 대한 권한이 없습니다.");
				break;
			case 404:
				$modal.setTitle("404 Not Found");
				$modal.setMsg("요청한 페이지를 찾을 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.");
				break;
			case 409:
				$modal.setTitle("409 Conflict");
				$modal.setMsg("요청한 서비스가 현재 사용 가능하지 않습니다. 다시 시도해 주세요.");
				break;
			default:
				$modal.setTitle("잠시 후 다시 시도해주세요");
				$modal.setMsg("서버로부터 올바른 응답을 받지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.");
				break;
		}
		if(message){
			$modal.setTitle(message);
		}
		if(detail){
			$modal.setMsg(detail);
		}
		if(error && error.length != 0){
			$modal.setErrorInfo(...error);
		}
		return $modal;
	},
	loading(data = {}){
		const {
			id = "loadingModal",
			target = this.target
		} = data;
		if(target !== this.target){
			data.blackout = false;
			data.isModal = false;
		}
		const $modal = loadingGenerator.buildLoading(data);
		$modal.id = id;
		return $modal;
	},
}

/**
 * 모달 서비스
 * 
 * #promisify(selector): 모달 프로미스화
 * #info({id: 아이디, singleton: 최초, title: 제목, msg: 메시지}): 안내 모달
 * #confirm({id: 아이디, singleton: 최초, title: 제목, msg: 메시지}): 확인 모달
 * #error({id: 아이디, singleton: 최초, response: {status:상태코드, message: 메시지, detail: 상세, error: 에러 리스트}}): 에러 모달
 * #get(id): 현재 팝업된 모달 Element 요청
 * #close(id): 해당 id를 가진 모달이 팝업 된 경우 닫음
 * #closeAll(): 팝업된 모든 모달 닫음
 */
const Modal = {
	__proto__: eventMixin,
	init(data = {}){
		const {
		} = data;
	},
	promisify(selector){
		return modalGenerator.promisifyModal(selector);
	},
	info(data = {}){
		return modalProvider.info(data).show();
	},
	confirm(data = {}){
		return modalProvider.confirm(data).show();
	},
	select(data = {}){
		return modalProvider.select(data).show();
	},
	error(data = {}){
		return modalProvider.error(data).show();
	},
	startLoading(data = {}){
		return modalProvider.loading(data).show();
	},
	endLoading(data = {}){
		modalCollector.get("loadingModal")?.hide();
	},
	get(id){
		return modalProvider.persistentModal(id);
	},
	getPopupList(){
		return modalCollector.getAll();
	},
	show(id){
		return modalProvider.persistentModal(id).show();
	},
	close(id){
		const $modal = modalCollector.get(id);
		$modal?.close();
	},
	closeAll(){
		modalCollector.getAll().forEach($modal => {
			$modal.close();
		});
	},
};
export default Modal;
/** initialize */
(() => {
	modalProvider.init();
})();