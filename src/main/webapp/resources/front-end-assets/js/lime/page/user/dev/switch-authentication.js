/**
 * 
 */
import {Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {UserCall as $USER} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';

$(async () => {
	domHandler.init();
	evtHandler.init();
});

const domHandler = {
	init(){
		this.setUsernameInput();
	},
	getUsernameInput(){
		if(!this.usernameInput){
			this.usernameInput  = Util.getElement('input[name=username]');
		}
		return this.usernameInput;
	},
	getUsernameValue(){
		return this.getUsernameInput()?.value;
	},
	setUsernameValue(text){
		const $input = this.getUsernameInput();
		if($input){
			$input.value = text;
		}
	},
	getLoginTypeInput(){
		const $loginTypeInput = Util.getElement('input[name=loginType]:checked');
		return $loginTypeInput;
	},
	getLoginTypeValue(){
		return this.getLoginTypeInput()?.value;
	},
	setUsernameInput(){
		const $usernameInput = this.getUsernameInput();
		$usernameInput.oninput = () => {
			Util.acceptNumber($usernameInput);
		}
	}
}
const evtHandler = {
	init(){
		this.setSwitchAuthenticationBtn();
	},
	setSwitchAuthenticationBtn(){
		const $switchBtn = Util.getElement('#switchAuthenticaionBtn');
		if(!$switchBtn){
			return;
		}
		$switchBtn.onclick = async () => {
			const username = domHandler.getUsernameValue();
			if(Util.isEmpty(username)){
				await Modal.info({msg: "인증 정보를 교체할 대상 사번을 입력해주세요"});
				return;
			}
			const loginType = domHandler.getLoginTypeValue();
			AjaxBuilder({
				request: $USER.Post.switchUserAuthenticationToken,
				param: {
					userId: username,
					loginType: loginType,
				},
				exception: 'success-only'
			}).success(async (res) => {
				await Modal.info({msg: "인증 정보를 요청한 사용자 정보로 교체했습니다"});
				location.href = "/lime/home";
			}).error(async (err) => {
				await Modal.error({response: err});
				domHandler.setUsernameValue("");
			}).exe();
		}
	}
}
