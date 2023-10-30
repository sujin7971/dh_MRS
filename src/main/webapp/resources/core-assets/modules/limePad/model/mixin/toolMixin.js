export const toolMixin = {
	createInterface(){
		if(this.element){
			return;
		}
		const tool = this;
		const html = {
			toolCon : '<div class="tool"></div>',
			icon : '<i></i>',
		}
		const $toolCon = $(html.toolCon);
		$toolCon.attr("title", tool.title);
		
		let $icon = $(html.icon);
		$icon.addClass(tool.icon);
		$toolCon.append($icon);
		
		$toolCon.on("click", function() {
			tool.trigger("select");
		});
		this.element = $toolCon;
		return $toolCon;
	},
	select(){
		const $element = this.element;
		if($element){
			$element.addClass("selected");
		}
	},
	unselect(){
		const $element = this.element;
		if($element){
			$element.removeClass("selected");
		}
	},
	enable(){
		this.activate = true;
		const $element = this.element?.removeClass("disabled");
	},
	disable(){
		this.activate = false;
		const $element = this.element?.addClass("disabled");
	},
	show(){
		const $element = this.element?.removeClass("d-none");
	},
	hide(){
		const $element = this.element?.addClass("d-none");
	},
};
