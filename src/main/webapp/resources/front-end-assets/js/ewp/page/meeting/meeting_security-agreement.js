/**
 * 
 */
import {Dom, AjaxBuilder, ModalService} from '/resources/core-assets/essential_index.js';
import {setupSignModal} from '/resources/front-end-assets/js/ewp/partials/modal/modal_provider_index.js';
import {AssignCall as $AS, AttendeeCall as $ATT} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';
window.onload = async () => {
	const signModal = setupSignModal();
	const assign = await $AS.Get.assignOne(skdKey);
	const {
		title,
		agreementDate = moment().format("YYYY년 MM월 DD일"),
	} = assign;
	const $pharse1 = Dom.getElement("#phrase1");
	$pharse1.innerText = `본인은 ${agreementDate} 한국동서발전(주) ${title} 회의를 함에 있어`;
	const $pharse2 = Dom.getElement("#phrase2");
	$pharse2.innerText = `${agreementDate}`;
	
	const $signLoc = Dom.getElement("#signLoc");
	$signLoc.style.position = "absolute";
	$signLoc.style.pointerEvents = "none";
	const $signBtn = Dom.getElement("#signBtn");
	$signBtn.onclick = async () => {
		const {action, payload} = await signModal.show();
		if(action == "OK" && payload){
			const signSrc = payload;
			
			$signLoc.innerHTML = "";
			const $container = $signLoc.parentNode;
			
			const $signDiv = Dom.createElement("div", "signImg");
			const $sign = Dom.createElement("img");
			$sign.style.height = $container.offsetHeight+"px";
			$sign.src = signSrc;
			$signDiv.appendChild($sign);
			$signLoc.appendChild($signDiv);
			
			const $agreementBtn = Dom.getElement("#agreementBtn");
			$agreementBtn.disabled = false;
			$agreementBtn.onclick = async () => {
				$agreementBtn.disabled = true;
				AjaxBuilder({
					request: $ATT.Post.insertSecurityAgreementOne,
					param: {
						meetingKey: meetingKey,
						signSrc: signSrc
					},
					exception: 'success-only'
				}).success((res) => {
					location.href = `/ewp/meeting/assign/${skdKey}`
				}).error((err) => {
					ModalService.errorBuilder(err).build().show();
					$agreementBtn.disabled = false;
				}).exe();
			}
		}
	}
};
