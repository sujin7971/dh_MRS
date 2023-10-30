/**
 * 
 */
import {eventMixin, Util} from '/resources/core-assets/essential_index.js';
export default {
	__proto__: eventMixin,
	name: "text",
	title: "직원검색",
	id: "user_search",
	generateNav(){
		const $nav = Util.createElement("li");
		this.$nav = $nav;
		$nav.id = this.id+"-tab";
		$nav.innerHTML = this.title;
		return $nav;
	},
	generateTab(){
		const $tab = Util.createElement("div", "singleSrchDiv", "d-flex");
		this.$tab = $tab;
		$tab.id = this.id;
		const $inputBox = Util.createElement("div", "inputBox");
		$tab.appendChild($inputBox);
		const $input = Util.createElement("input");
		$inputBox.appendChild($input);
		$input.type = "text";
		$input.setAttribute("placeholder", "이름을 입력해주세요");
		$input.maxLength = 10;
		$input.oninput = () => {
			Util.acceptExcludeSpecial($input);
		}
		$input.onkeyup = (evt) => {
			if (evt.keyCode == 13) {
				this.search();
		    }
		}
		const $searchBtn = Util.createElement("div", "srchBtn");
		$tab.appendChild($searchBtn);
		const $icon = Util.createElement("i", "far", "fa-search");
		$icon.onclick = () => {
			this.search();
		}
		$searchBtn.appendChild($icon);
		this.clear = () => {
			$input.value = "";
		}
		this.search = () => {
			const searchText = $input.value;
			if(!searchText || searchText == ""){
				return;
			}
			this.trigger("search", {
				searchText: searchText,
				nameplate: true,
			});
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