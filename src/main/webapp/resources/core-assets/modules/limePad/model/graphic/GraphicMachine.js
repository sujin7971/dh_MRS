/**
 * context 객체를 graphic 클래스의 객체로 변환처리한다.
 * 
 */
import {Graphic} from './Graphic.js';
export const GraphicMachine = {
	createElementNS: function(tag, attributes){
		return createElementNS(tag, attributes);
	},
	setElementAttributes: function(elements, attributes){
		setElementAttributes(elements, attributes);
	},
	createSVG: function(id, shape){
		/*if(!(shape instanceof Shape)){
			console.error("지원하는 Class가 아닙니다");
			return;
		}*/
		const containerGroupElem = createElementNS("g", {
			id: id,
			role: "container",
		});
		const coordNodeElem = createElementNS("svg", {
			role: "coordinator",
			preserveAspectRatio: "none",
			overflow: "visible"
		});
		containerGroupElem.appendChild(coordNodeElem);
		containerGroupElem.coordNode = coordNodeElem;

		const shapeType = shape.type;
		let graphicElem;
		switch(shapeType){
			case "path":
			case "line":
			case "polyline":
			case "rect":{
				graphicElem = createShapeElement(shapeType);
			}
				break;
			case "textarea":{
				graphicElem = createShapeElement("rect");
				const foreignElem = createShapeElement(shapeType);
				containerGroupElem.foreignNode = foreignElem;
				coordNodeElem.appendChild(foreignElem);
			}
				break;
		}
		containerGroupElem.graphicNode = graphicElem;
		coordNodeElem.appendChild(graphicElem);
		this.updateSVG(shape, containerGroupElem);
		return containerGroupElem;
	},
	updateSVG: function(shape, graphicElement){
		const containerGroupElem = graphicElement;
		const coordNodeElem = containerGroupElem.coordNode;
		const graphicElem = containerGroupElem.graphicNode;
		const foreignElem = containerGroupElem.foreignNode;
		const styleAttributes = shape.getStyleAttribute()
		setElementAttributes(graphicElem, styleAttributes);
		
		const coordinateAttributes = shape.getCoordinateAttribute();
		
		const shapeType = shape.type;
		switch(shapeType){
			case "line":
				{
					const anodePoint = getAnodePoint([coordinateAttributes.startPos, coordinateAttributes.endPos]);
					const width = (anodePoint.maxX - anodePoint.minX);
					const height = (anodePoint.maxY - anodePoint.minY);
					setElementAttributes(coordNodeElem, {
						x: coordinateAttributes.posX + anodePoint.minX,
						y: coordinateAttributes.posY + anodePoint.minY
					});
					setElementAttributes(graphicElem, {
						x1: 0,
						y1: 0,
						x2: width,
						y2: height,
					});
				}
				break;
			case "rect":
				{
					const anodePoint = getAnodePoint([coordinateAttributes.startPos, coordinateAttributes.endPos]);
					const width = (anodePoint.maxX - anodePoint.minX);
					const height = (anodePoint.maxY - anodePoint.minY);
					setElementAttributes(coordNodeElem, {
						x: coordinateAttributes.posX + anodePoint.minX,
						y: coordinateAttributes.posY + anodePoint.minY
					});
					setElementAttributes(graphicElem, {
						x: 0,
						y: 0,
						width: width,
						height: height,
					});
				}
				break;
			case "textarea":
				{
					const anodePoint = getAnodePoint([coordinateAttributes.startPos, coordinateAttributes.endPos]);
					const width = (anodePoint.maxX - anodePoint.minX);
					const height = (anodePoint.maxY - anodePoint.minY);
					
					setElementAttributes(coordNodeElem, {
						x: coordinateAttributes.posX + anodePoint.minX,
						y: coordinateAttributes.posY + anodePoint.minY
					});
					setElementAttributes(graphicElem, {
						x: 0,
						y: 0,
						width: width,
						height: height,
					});
					
					const fontAttributes = shape.getFontAttribute();
					setElementAttributes(foreignElem, {
						x: 0,
						y: 0,
						width: width,
						height: height,
					});
					const textareaElem = foreignElem.textareaElem;
					textareaElem.style.color = fontAttributes["color"];
					textareaElem.style.fontSize = fontAttributes["font-size"] + "px";
					textareaElem.style.fontFamily = fontAttributes["font-family"];
					const text = shape.getText();
					textareaElem.value = text;
				}
				break;
			case "path":
				{
					const simplifyVertices = simplifyPointGroupList(coordinateAttributes.pointGroup);
					const width = (simplifyVertices.maxX - simplifyVertices.minX);
					const height = (simplifyVertices.maxY - simplifyVertices.minY);
					const d = getPathsDataAttribute(simplifyVertices.pointGroupList);
					setElementAttributes(coordNodeElem, {
						x: coordinateAttributes.posX + simplifyVertices.minX,
						y: coordinateAttributes.posY + simplifyVertices.minY,
						width: (coordinateAttributes.width)?coordinateAttributes.width:width,
						height: (coordinateAttributes.height)?coordinateAttributes.height:height,
						viewBox: "0 0 "+width+" "+height,
					});
					setElementAttributes(graphicElem, {
						x: 0,
						y: 0,
						d: d
					});
				}
				break;
			case "polyline":
				{
					const points = getPointsAttribute(coordinateAttributes.pointGroup);
					setElementAttributes(graphicElem, {
						x: 0,
						y: 0,
						points: points
					});
				}
				break;
		}
	}
} 

function flatDeep(arr, d = 1) {
	return d > 0 ? arr.reduce((acc, val) => acc.concat(Array.isArray(val) ? flatDeep(val, d - 1) : val), [])
                : arr.slice();
}

function simplifyPointGroupList(pointGroupList){
	const cloneList = pointGroupList.concat();
	const flattened = flatDeep(cloneList);
	const anodePoint = getAnodePoint(flattened);
	return {
		...anodePoint,
		pointGroupList: cloneList.map(g => g.map(p => [p[0] - anodePoint.minX, p[1] - anodePoint.minY]))
	}
}

function getAnodePoint(pointGroup){
	if(pointGroup.length == 0){
		return {
			minX: 0,
			maxX: 0,
			minY: 0,
			maxY: 0,
		};
	}
	let minX = pointGroup[0][0];
	let maxX = pointGroup[0][0];
	let minY = pointGroup[0][1];
	let maxY = pointGroup[0][1];
	if(pointGroup.length < 2){
		return {
			minX: minX,
			maxX: minX,
			minY: minY,
			maxY: minY,
		};
	}
	pointGroup.forEach(p => {
		const nowX = p[0];
		const nowY = p[1];
		
		minX = (minX > nowX)?nowX:minX;
		maxX = (maxX < nowX)?nowX:maxX;
		minY = (minY > nowY)?nowY:minY;
		maxY = (maxY < nowY)?nowY:maxY;
	});
	return {
		minX: minX,
		maxX: maxX,
		minY: minY,
		maxY: maxY,
	};
}

function getPathsDataAttribute(pointGroupList){
	let d = "";
	for(const pointGroup of pointGroupList){
		d += makePathsDataAttribute(pointGroup);
	}
	return d;
}
function getPointsAttribute(pointGroup){
	let p = pointGroup[0][0] + "," + pointGroup[0][1] + " ";
	for(let i = 1; i < pointGroup.length; i++) {
		let point = pointGroup[i];
		
		p += point[0] + "," + point[1] + " ";
	}
	return p;
}
function makePathsDataAttribute(pointGroup){
	let previousPoint = pointGroup[0];
	let d = "M"+previousPoint+" ";
	if(pointGroup.length == 1){
		pointGroup.push(pointGroup[0]);
	}
	for(let i = 1; i < pointGroup.length; i++){
		const nextPoint = pointGroup[i];
		const controlPoint = quadraticControlPoint(previousPoint, nextPoint);
		d += "Q"+controlPoint+" ";
		d += nextPoint;
		previousPoint = nextPoint;
	}
	return d;
	function quadraticControlPoint(previous, next){
		let c = (Number(previous[0]) + Number(next[0])) / 2;
	    let d = (Number(previous[1]) + Number(next[1])) / 2;
	    return [c, d];
	}
}

function createElementNS(tag, attributes){
	const element = document.createElementNS('http://www.w3.org/2000/svg', tag);
	setElementAttributes(element, attributes);
	return element;
}

function setElementAttributes(element, attributes){
	if(attributes != null) {
        for (let key in attributes){
        	const value = attributes[key];
        	if(value != 0 && (value == null || value == undefined || value == "")){
        		element.removeAttribute(key);
        	}else{
        		element.setAttribute(key, value);
        	}
        }
    }
}
function createShapeElement(shapeType, attributes){
	switch(shapeType){
		case "line":
		case "path":
		case "polyline":
		case "rect":{
			return createElementNS(shapeType, attributes);
			}
			break;
		case "textarea":{
			const foreignObjectElem = createElementNS("foreignObject");
			const textareaElem = document.createElement("textarea");
			textareaElem.style.cssText = 'width: 100%;height: 100%;background: transparent;fill: none;border: none;resize: none;overflow: hidden;'
			foreignObjectElem.textareaElem = textareaElem;
			textareaElem.getContainer = function(){
				return foreignObjectElem.getContainer();
			}
			foreignObjectElem.appendChild(textareaElem);
			return foreignObjectElem;
			}
			break;
	}
}
