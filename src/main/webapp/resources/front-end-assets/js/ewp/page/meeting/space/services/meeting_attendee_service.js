/**
 * 
 */
import {eventMixin, Util, Modal} from '/resources/core-assets/essential_index.js';

export default {
	__proto__: eventMixin,
	init(data = {}){
		const {
			instanceProvider
		} = data;
		this.instanceProvider = instanceProvider;
		this.hostKey = null;
		this.assistantKey = null;
		this.attendeeMap = new Map();
		this.attendeeElemMap = new Map();
	}
}

const attendeeRowGenerator = {
	buildTemplate(){
		if(this.template)
		const $li = Util.createElement("li");
		const $infoBtn = Util.createElement("div", "infoLinkBtn");
		$li.appendChild($infoBtn);
		const $username = Util.createElement("div", "userName", "ellipsis");
		$li.appendChild($username);
		const $switchBtn = Util.createElement("div", "switchBtn", "switchOff");
		$li.appendChild($switchBtn);
		$li.setName = (name) => {
			$username.innerHTML = name;
		}
		$li.switchConnect = (connectYN) => {
			if(connectYN == 'Y'){
				Util.addClass($li, "connect");
			}else{
				Util.removeClass($li, "connect");
			}
		}
		$li.switchAttend = (attendYN) => {
			if(attendYN == 'Y'){
				Util.addClass($li, "a1");
				Util.addClass($switchBtn, "switchOn");
				Util.removeClass($switchBtn, "switchOff");
			}else{
				Util.removeClass($li, "a1");
				Util.removeClass($switchBtn, "switchOn");
				Util.addClass($switchBtn, "switchOff");
			}
		}
	},
	buildSelectTemplate(){
		
	}
}