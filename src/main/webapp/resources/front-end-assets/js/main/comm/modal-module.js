/**
 * 
 */
function _typeof(obj) { "@babel/helpers - typeof"; if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }
(function ($) {
	let html = {
		modal: '<div class="modal top-front"></div>',
		wrap: '<div class="modalWrap"></div>',
		con: '<div class="modal_content"></div>',
		title: '<div class="modalTitle"></div>',
		bodyCon: '<div class="modalBody flex-direction-column"></div>',
		body: '<div class="commonMent"></div>',
		btn: {
			con: '<div class="modalBtnDiv"></div>',
			yes: '<button type="button" class="btn btn-md btn-blue" data-btn="YES">예</button>',
			no: '<button type="button" class="btn btn-md btn-silver" data-btn="NO">아니오</button>',
			ok: '<button type="button" class="btn btn-md btn-blue" data-btn="OK">확 인</button>',
			cancel: '<button type="button" class="btn btn-md btn-silver" data-btn="NO">취 소</button>',
			close: '<button type="button" class="btn btn-md btn-silver" data-btn="CLOSE">닫 기</button>',
			lbtn: '<button type="button" class="btn btn-lg"></button>',
			mbtn: '<button type="button" class="btn btn-md"></button>',
		}
	}
	var modals = {
		choose: function(options){
			let $main = $(this);
			return new Promise(function(resolve,reject){
				if(typeof options === "string"){
					options = {
						msg: options
					}
				}
				let $noBtn = $(html.btn.no);
				let $yesBtn = $(html.btn.yes);
				let $modal = makeModalDOM(options,[$noBtn, $yesBtn]);
				$main.append($modal);
				$modal.show();
				
				$noBtn.on("click",function(){
					$modal.remove();
					resolve($(this).data("btn"));
				});
				
				$yesBtn.on("click",function(){
					$modal.remove();
					resolve($(this).data("btn"));
				});
			});
		},
		confirm: function(options){
			let $main = $(this);
			return new Promise(function(resolve,reject){
				if(typeof options === "string"){
					options = {
						msg: options
					}
				}
				let $cancelBtn = $(html.btn.cancel);
				let $okBtn = $(html.btn.ok);
				let $modal = makeModalDOM(options,[$cancelBtn, $okBtn]);
				$main.append($modal);
				$modal.show();
				
				$cancelBtn.on("click",function(){
					$modal.remove();
					resolve($(this).data("btn"));
				});
				
				$okBtn.on("click",function(){
					$modal.remove();
					resolve($(this).data("btn"));
				});
			});
		},
		message: function(options, singleton){
			let $main = $(this);
			return new Promise(function(resolve,reject){
				let param = {};
				if(typeof options === "string"){
					param.msg = options;
				}else{
					param = options;
				}
				let $okBtn = $(html.btn.ok);
				let $modal = makeModalDOM(param, [$okBtn]);
				if(singleton){
					$modal.attr("id", "singleton");
					$main.find("#singleton").remove();
				}
				$main.append($modal);
				$modal.show();
				$okBtn.on("click",function(){
					$modal.remove();
					resolve($(this).data("btn"), $modal);
				});
			});
		},
		select: function(options){
			let $main = $(this);
			return new Promise(function(resolve,reject){
				if(typeof options === "string"){
					options = {
						msg: options
					}
				}
				let selectBtnArr = [];
				for(let i = 0; i < options.select.length; i++){
					const {
						text = "",
						primary = false,
						key = i
					} = options.select[i];
					const $btn = $(html.btn.lbtn);
					if(primary){
						$btn.addClass("btn-blue");
					}else{
						$btn.addClass("btn-blue-border");
					}
					$btn.html(text);
					if(i != options.select.length - 1){
						$btn.addClass("mb-3")
					}
					$btn.on("click",function(){
						$modal.remove();
						resolve(key, $modal);
					});
					selectBtnArr.push($btn);
				}
				options.msg = selectBtnArr;
				let $closeBtn = $(html.btn.close);
				let $modal = makeModalDOM(options,[$closeBtn]);
				$main.append($modal);
				$modal.show();
				$closeBtn.on("click",function(){
					$modal.remove();
					resolve($(this).data("btn"), $modal);
				});
			});
		},
		error: function(options){
			let $main = $(this);
			return new Promise(function(resolve,reject){
				let $okBtn = $(html.btn.ok);
				let status = options.status;
				if(options){
					let message = options.message;
					let detail = options.detail;
					switch(status){
						case 0:
						case 500:
							//options.title = "500 SERVER ERROR";
							options.msg = "서버에서 요청을 처리할 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";
							break;
						case 400:
						case 422:
							if(message){
								//options.title = message;
								options.msg = (options.error && options.error.length != 0)?options.error[0].message:options.detail;
							}else{
								//options.title = "400 BAD REQUEST";
								options.msg = "요청문에 오류가 있거나 서버가 요청을 이해하지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";
							}
							break;
						case 401:
							//options.title = "401 Unauthorized";
							options.msg = (options.responseText)?options.responseText:"로그아웃된 사용자의 요청입니다. 로그인 페이지로 이동합니다.";
							break;
						case 403:
						case 404:
							//options.title = (message)?message:"404 NOT FOUND";
							options.msg = (detail)?detail:"요청한 페이지를 찾을 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";
							break;
						case 409:
							//options.title = message;
							options.msg = (options.error.length != 0)?options.error[0].message:options.detail;
							break;
						default:
							options.title = "잠시 후 다시 시도해주세요";
							options.msg = "서버로부터 올바른 응답을 받지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";
							break;
					}
				}
				let $modal = makeModalDOM(options,[$okBtn]);
				$main.append($modal);
				$modal.show();
				
				$okBtn.on("click",function(){
					if(status == 401){
						location.href = "/login";
					}else{
						$modal.remove();
						resolve(options.status);
					}
				});
			});
		}
	}
	
	function makeModalDOM(options, btnArr){
		let $modal = $(html.modal);
		let $wrap = $(html.wrap);
		let $con = $(html.con);
		let title = options?.title;
		if(title){
			let $title = $(html.title);
			$title.html(title);
			$con.append($title);
		}
		let $bodyCon = $(html.bodyCon);
		let msg = options?.msg;
		if(Array.isArray(msg)){
			for(let i = 0; i < msg.length; i++){
				$bodyCon.append(msg[i]);
			}
			$con.append($bodyCon);
		}else {
			let $body = $(html.body);
			$body.html(msg);
			$bodyCon.html($body);
			$con.append($bodyCon);
		}
		let $btnCon = $(html.btn.con);
		for(let i = 0; i < btnArr.length; i++){
			let $btn = btnArr[i];
			$btnCon.append($btn);
		}
		$con.append($btnCon);
		$wrap.html($con);
		$modal.html($wrap);
		
		return $modal;
	}
	
	$.fn.Modal = function(modal){
		if (modals[modal]) {
			return modals[modal].apply(this, Array.prototype.slice.call(arguments, 1)); // eslint-disable-next-line no-else-return
		} else if (_typeof(modal) === 'object' || !modal) {
      		return modals.update.apply(this, arguments);
    	}
    	return this;
	}
})(jQuery); 