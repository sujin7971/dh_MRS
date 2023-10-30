/**
 * 
 */
import {eventMixin, Util, Dom} from '/resources/core-assets/essential_index.js';
class OrgTab {
	constructor(options = {}){
		const {
			title,
			id,
		} = options;
		this.title = title;
		this.id = id;
	}
	generateNav(){
		const $nav = Dom.createElement("li");
		this._nav = $nav;
		$nav.id = this.id+"-tab";
		$nav.innerHTML = this.title;
		return $nav;
	}
	generateTab(){
		const $tab = Util.createElement("div", "singleSrchDiv", "d-flex");
		this._tab = $tab;
		$tab.id = this.id;
		return $tab;
	}
	open(){
		const $nav = this._nav;
		Dom.addClass($nav, "active");
		const $tab = this._tab;
		$tab.style.display = "flex";
	}
	close(){
		const $nav = this._nav;
		Dom.removeClass($nav, "active");
		const $tab = this._tab;
		$tab.style.display = "none";
	}
}
class BoxTab extends OrgTab {
	constructor(options = {}){
		super(options);
	}
	generateTab(){
		const $tab = super.generateTab();
		return $tab;
	}
}
class SearchTab extends OrgTab {
	constructor(options = {}){
		super(options);
	}
	generateTab(){
		const $tab = super.generateTab();
		return $tab;
	}
	search(){
		
	}
}