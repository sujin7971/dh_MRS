/**
 * 
 */
export const Comm = {
	getUUID: function(length) {
		if(!length){
			length = 16;
		}
		let uuid = ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
			(c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
		)
		return uuid.substring(0, length);
	},
	roundToTwo: function(num){
		return +(Math.round(num + "e+2")  + "e-2");
	},
	defer(f, ms) {
		return function() {
			setTimeout(() => f.apply(this, arguments), ms)
		};
	},
	/* 캔버스의 포인터 좌표 반환 */
	getPointerPos: function(ext, sizeRatio, rational){
		let x = Comm.roundToTwo(ext.offsetX) / (sizeRatio);
		let y = Comm.roundToTwo(ext.offsetY) / (sizeRatio);
		if(rational){
			return {
				x: x,
				y: y
			}
		}else{
			return {
				x: Math.floor(x),
				y: Math.floor(y)
			}
		}
		
	},
	/* 두 좌표간 거리 */
	getDistance: function(point1, point2) {
		return Math.sqrt(Math.pow((point1.x-point2.x),2) + Math.pow((point1.y-point2.y),2));
	},
}

