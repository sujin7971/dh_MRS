/**
 * 
 */
import {eventMixin} from '../../model/mixin/eventMixin.js';
import {Comm} from '../../comm/limePad.method.js';
export default class ScreenService{
	constructor(data = {}) {
		const {
			instance = null,
			width = 300,
			height = 150,
			image = null,
			color = "white",
			scale = 1,
		} = data;
		this.instance = instance;
		
		this.nowWidth = width;
		this.nowHeight = height;
		this.scale = scale;
		
		
		this.resizeScreen({
			width: width,
			height: height,
		});
		this.deployScreenSizeObserver();
		this.setBackgroundColor(color);
		this.transformScale();
	}
	deployScreenSizeObserver(){
		const ro = new ResizeObserver((entries, observer) => {
			(this.fitOrientation)?this.fitOrientation():this.transformScale();
		});
		ro.observe(this.instance.sectionNode);
	}
	resizeScreen(data = {}){
		const {
			width = 300,
			height = 150,
		} = data;
		this.width = width;
		this.height = height;
		
		const svgNode = this.instance.svgNode;
		svgNode.setAttribute("width", width);
		svgNode.setAttribute("height", height);
		
		const screenNode = this.instance.screenNode;
		screenNode.style.width = width + "px";
		screenNode.style.height = height + "px";
	}
	fitScreenToWindow(){
		const boundWidth = this.width;
		const boundHeight = this.height;
		if(boundWidth > boundHeight){
			this.fitHorizontalOrientation();
		}else{
			this.fitVerticalOrientation();
		}
	}
	fitVerticalOrientation(){
		this.fitOrientation = this.fitVerticalOrientation;
		
		const displayHeight = this.instance.sectionNode.offsetHeight - 6;
		const boundHeight = this.height;
		const scale = (displayHeight / boundHeight);
		this.transformScale({scale: scale});
	}
	fitHorizontalOrientation(){
		this.fitOrientation = this.fitHorizontalOrientation;
		
		const displayWidth = this.instance.sectionNode.offsetWidth - 6;
		const boundWidth = this.width;
		
		const scale = (displayWidth / boundWidth);
		this.transformScale({scale: scale});
	}
	zoomin(ratio){
		if(!ratio){
			ratio = 0.05;
		}
		const scale = this.scale;
		this.changeScale(scale + ratio);
	}
	zoomout(ratio){
		if(!ratio){
			ratio = 0.05;
		}
		const scale = this.scale;
		this.changeScale(scale - ratio);
	}
	changeScale(scale){
		delete this.fitOrientation;
		let newScale = (Number(scale))?scale:this.scale;
		if(newScale > 4){
			newScale = 4;
		}else if(newScale < 0.3){
			newScale = 0.3;
		}
		this.transformScale({scale: newScale});
	}
	transformScale(data = {}){
		const {
			scale = this.scale
		} = data;
		if(this.scale != scale){
			this.trigger("transformScale", scale);
		}
		this.scale = scale;
		
		const _instance = this.instance;
		const sectionNode = _instance.sectionNode;
		const screenNode = _instance.screenNode;
		const containerNode = _instance.containerNode;
		screenNode.style.width = this.width * scale + "px";
		screenNode.style.height = this.height *scale + "px";
		
		const heightDiff = sectionNode.offsetHeight - screenNode.offsetHeight;
		
		screenNode.style.marginTop = (heightDiff > 0)?(heightDiff / 2) + "px":0;
		
		containerNode.style.transform = "scale("+scale+")";
		containerNode.setAttribute("scale", scale);
	}
	setBackgroundImage(url){
		const _class = this;
		const svgNode = _class.instance.svgNode;
		return new Promise(function(resolve, reject) {
			const sourceImg = new Image();
			sourceImg.onload = function() {
				svgNode.style.background = "url("+sourceImg.src+")";
				svgNode.style.backgroundRepeat = "no-repeat";
				svgNode.style.backgroundSize = "cover";

				svgNode.setAttribute("width", sourceImg.width);
				svgNode.setAttribute("height", sourceImg.height);
				
				_class.width = sourceImg.width;
				_class.height = sourceImg.height;
				_class.image = sourceImg;
				_class.transformScale();
				resolve();
		  	};
		  	sourceImg.onerror = function(err) {
		  		reject(err);
		  	}
		  	sourceImg.src = url;
		});
	}
	getBackgroundImage(){
		return this.image;
	}
	setBackgroundColor(data = {}){
		const {
			color = "#ffffff",
			opacity = 1,
		} = data;
		this.instance.svgNode.style.backgroundColor = color;
		this.instance.svgNode.style.background = "none";
	}
	switchCtrlmode(isctrl){
		const svgNode = this.instance.svgNode;
		if(this.instance.options.selectable == false){
			return;
		}
		if(isctrl){
			svgNode.classList.add("ctrl-mode");
			svgNode.setAttribute("pointer-events", "visiblePainted");
		}else{
			svgNode.classList.remove("ctrl-mode");
			svgNode.setAttribute("pointer-events", "none");
		}
	}
	switchErasermode(isctrl){
		const svgNode = this.instance.svgNode;
		const textboxNode = svgNode.getDeployNode(2);
		svgNode.classList.remove("ctrl-mode");
		if(isctrl){
			svgNode.setAttribute("pointer-events", "visiblePainted");
			svgNode.querySelectorAll("polyline").forEach(node => node.setAttribute("pointer-events", "visiblePainted"));
			textboxNode.style.pointerEvents = "none";
		}else{
			textboxNode.style.pointerEvents = "";
			svgNode.setAttribute("pointer-events", "none");
			svgNode.querySelectorAll("polyline").forEach(node => node.setAttribute("pointer-events", "none"));
		}
	}
}
Object.assign(ScreenService.prototype, eventMixin);