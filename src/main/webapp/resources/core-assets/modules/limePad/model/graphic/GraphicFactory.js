/**
 * 그래픽 객체를 통해 SVG를 생성 및 업데이트를 진행하고 처리한 그래픽 객체를 보관
 */

import Path from '../shape/path/Path.js';
import Line from '../shape/basic/Line.js';
import Textarea from '../shape/basic/Textarea.js';
import Rectangle from '../shape/basic/Rectangle.js';
import {Graphic} from './Graphic.js';
import {GraphicMachine} from './GraphicMachine.js';
export const GraphicFactory = {
	molding: function(context = {}){
		const {
			id = null,
			shape = null,
			level = 1,
		} = context;
		
		const packedElem = GraphicMachine.createSVG(id, shape);
		const graphic = new Graphic({id, shape, packedElem, level});
		shape.on("update", function(){
			setTimeout(() => GraphicMachine.updateSVG(shape, packedElem));
		});
		return graphic;
	},
	createEditBoundary: function(data = {}){
		const {
			x = 0,
			y = 0,
			width = 0,
			height = 0,
			scale = 1
		} = data;
		const boundaryNode = GraphicMachine.createElementNS("svg", {
			x: x,
			y: y,
			width: width,
			height: height,
			role: "boundary"
		});
		const boxElem = GraphicMachine.createElementNS("rect", {
			x: 0,
			y: 0,
			width: "100%",
			height: "100%",
			"pointer-events": "visiblePainted",
			role: "boundary-box",
			"class": "bBox",
			cursor: "move"
		});
		boundaryNode.appendChild(boxElem);
		const nwResizeElem = GraphicMachine.createElementNS("circle", {
			"pointer-events": "visiblePainted",
			cx: "0%",
			cy: "0%",
			"class": "bVertex resize",
			role: "boundary-vertex",
			"resize-direction": "nw",
			cursor: "nw-resize"
		});
		const neResizeElem = GraphicMachine.createElementNS("circle", {
			"pointer-events": "visiblePainted",
			cx: "100%",
			cy: "0%",
			"class": "bVertex resize",
			role: "boundary-vertex",
			"resize-direction": "ne",
			cursor: "ne-resize"
		});
		const swResizeElem = GraphicMachine.createElementNS("circle", {
			"pointer-events": "visiblePainted",
			cx: "0%",
			cy: "100%",
			"class": "bVertex resize",
			role: "boundary-vertex",
			"resize-direction": "sw",
			cursor: "sw-resize"
		});
		const seResizeElem = GraphicMachine.createElementNS("circle", {
			"pointer-events": "visiblePainted",
			cx: "100%",
			cy: "100%",
			"class": "bVertex resize",
			role: "boundary-vertex",
			"resize-direction": "se",
			cursor: "se-resize"
		});
		boundaryNode.appendChild(nwResizeElem);
		boundaryNode.appendChild(neResizeElem);
		boundaryNode.appendChild(swResizeElem);
		boundaryNode.appendChild(seResizeElem);
		const nResizeElem = GraphicMachine.createElementNS("circle", {
			"pointer-events": "visiblePainted",
			cx: "50%",
			cy: "0%",
			"class": "bVertex resize",
			role: "boundary-vertex",
			"resize-direction": "n",
			cursor: "ns-resize"
		});
		const sResizeElem = GraphicMachine.createElementNS("circle", {
			"pointer-events": "visiblePainted",
			cx: "50%",
			cy: "100%",
			"class": "bVertex resize",
			role: "boundary-vertex",
			"resize-direction": "s",
			cursor: "ns-resize"
		});
		const eResizeElem = GraphicMachine.createElementNS("circle", {
			"pointer-events": "visiblePainted",
			cx: "100%",
			cy: "50%",
			"class": "bVertex resize",
			role: "boundary-vertex",
			"resize-direction": "e",
			cursor: "ew-resize"
		});
		const wResizeElem = GraphicMachine.createElementNS("circle", {
			"pointer-events": "visiblePainted",
			cx: "0%",
			cy: "50%",
			"class": "bVertex resize",
			role: "boundary-vertex",
			"resize-direction": "w",
			cursor: "ew-resize"
		});
		boundaryNode.appendChild(nResizeElem);
		boundaryNode.appendChild(sResizeElem);
		boundaryNode.appendChild(eResizeElem);
		boundaryNode.appendChild(wResizeElem);
		
		boundaryNode.update = (data = {}) => {
			const bbox = boundaryNode.getBBox();
			const {
				x = bbox.x,
				y = bbox.y,
				width = bbox.width,
				height = bbox.height,
				scale = 1,
			} = data;
			const boxWidth = 4 / scale;
			const circleWidth = 2 / scale;
			const radius = 8 / scale;
			GraphicMachine.setElementAttributes(boundaryNode, {
				x: x,
				y: y,
				width: width,
				height: height,
			});
			boxElem.setAttribute("stroke-width", boxWidth);
			boundaryNode.querySelectorAll("circle").forEach(circle => {
				GraphicMachine.setElementAttributes(circle, {
					r: radius,
					"stroke-width": circleWidth
				});
			});
		}
		boundaryNode.update({
    		x: x,
    		y: y,
    		width: width,
    		height: height,
    		scale: scale
    	})
		return boundaryNode;
	}
}
