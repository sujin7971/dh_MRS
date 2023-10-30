/**
 * 
 */
import {ControlTool} from '../Tool.js';
export default class HighlightTool extends ControlTool{
	constructor(data = {}){
		super(data);
		const {
			node = null,
			color = "#fff",
			wradius = 16,
			hradius = 16,
		} = data;
		this.node = node;
		this.wradius = wradius;
		this.hradius = hradius;
		this.color = color;
		
		this.posX = 0;
		this.posY = 0;
	}
	update(data = {}){
		const {
			node = this.node,
			color = this.color,
			wradius = this.wradius,
			hradius = this.hradius,
		} = data;
		this.node = node;
		this.wradius = wradius;
		this.hradius = hradius;
		this.color = color;
	}
	updatePos(pos = {}){
		const {
			posX = this.posX,
			posY = this.posY,
		} = pos;
		this.posX = posX;
		this.posY = posY;
	}
	startDraw(){
		const _class = this;
		this.endDraw();
		this.interval = setInterval(function(){
			drawLaser({
				node: _class.node,
				wradius: _class.wradius,
				hradius: _class.hradius,
				color: _class.color,
				posX: _class.posX,
				posY: _class.posY
			});
		}, 10);
		function drawLaser(data = {}){
			const {
				node,
				wradius,
				hradius,
				color,
				posX,
				posY
			} = data;

//			const $laser = $('<div></div>').css({
//				'position':'absolute',
//				'width': wradius,
//				'height': hradius,
//				'border-radius':'50%',
//				'background': color,
//				'box-shadow':'0 0 10px' + color +', 0 0 8px' + color + ', 0 0 6px'  + color + ', 0 0 4px'  + color,
//				'top': posY + 'px',
//				'left': posX + 'px',
//				"touch-action": "none",
//				"pointer-events": "none"
//			}).appendTo(node).fadeOut('slow',function(){
//				$(this).remove();
//			});

			if($(node).children("#laserCircle").length <= 0) {
				const $laser = $('<div class="laserCircle" id="laserCircle"></div>').css({
					'position':'absolute',
					'width': wradius,
					'height': hradius,
					'border-radius':'50%',
					'background': color,
					'border' : '3px solid' + color,
					'top': (posY - hradius / 2) + 'px',
					'left': (posX - wradius / 2) + 'px',
					"touch-action": "none",
					"pointer-events": "none"
				}).appendTo(node);
				
			} else {
				const $laser = $('#laserCircle'); 
				$('#laserCircle').finish().css({
					'top': (posY - hradius / 2) + 'px',
					'left': (posX - wradius / 2) + 'px',
					"opacity": 1,
					'display': 'block'
				});
			}
			
		}
	}
	endDraw(){
		$('#laserCircle').fadeOut('slow',function(){
//			$(this).remove();
		});
		clearInterval(this.interval);
	}
}
