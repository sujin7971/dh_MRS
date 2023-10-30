
export const Constant = {
	ResizeFactor: function(direction){
		const resizeFactor = {
			x: 0,
			y: 0,
			width: 0,
			height: 0
		}
		switch(direction){
			case "nw"://북서
				resizeFactor.x = 1;
				resizeFactor.y = 1;
				resizeFactor.width = -1;
				resizeFactor.height = -1;
				break;
			case "ne"://북동
				resizeFactor.x = 0;
				resizeFactor.y = 1;
				resizeFactor.width = 1;
				resizeFactor.height = -1;
				break;
			case "sw"://남서
				resizeFactor.x = 1;
				resizeFactor.y = 0;
				resizeFactor.width = -1;
				resizeFactor.height = 1;
				break;
			case "se"://남동
				resizeFactor.x = 0;
				resizeFactor.y = 0;
				resizeFactor.width = 1;
				resizeFactor.height = 1;
				break;
			case "n"://북
				resizeFactor.x = 0;
				resizeFactor.y = 1;
				resizeFactor.width = 0;
				resizeFactor.height = -1;
				break;
			case "s"://남
				resizeFactor.x = 0;
				resizeFactor.y = 0;
				resizeFactor.width = 0;
				resizeFactor.height = 1;
				break;
			case "e"://동
				resizeFactor.x = 0;
				resizeFactor.y = 0;
				resizeFactor.width = 1;
				resizeFactor.height = 0;
				break;
			case "w"://서
				resizeFactor.x = 1;
				resizeFactor.y = 0;
				resizeFactor.width = -1;
				resizeFactor.height = 0;
				break;
		}
		return resizeFactor;
	},
	DefaultFontFamily: "HelveticaNeue-Light,AppleSDGothicNeo-Light,Pretendard,Malgun Gothic,맑은 고딕,sans-serif"
}

