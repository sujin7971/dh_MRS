/**
 * 도넛 차트 가운데 라벨/값 표시 함수
 */
 Chart.plugins.register({ beforeDraw: function (chart) { 
 		//가운데 표시할 글
	 	if (chart.config.options.elements.center) { //Get ctx from string 
			var ctx = chart.chart.ctx; //Get options from the center object in options 
			var centerConfig = chart.config.options.elements.center; 
			var fontSize = centerConfig.fontSize || '50'; 
			var fontStyle = centerConfig.fontStyle || 'Arial';
			var txt = centerConfig.text; 
			var color = centerConfig.color || '#000'; 
			var sidePadding = centerConfig.sidePadding || 20; 
			var sidePaddingCalculated = (sidePadding/100) * (chart.innerRadius * 2) 
			//Start with a base font of 30px 
			ctx.font = fontSize + "px " + fontStyle; //Get the width of the string and also the width of the element minus 10 to give it 5px side padding 
			var stringWidth = ctx.measureText(txt).width; 
			var elementWidth = (chart.innerRadius * 2) - sidePaddingCalculated; // Find out how much the font can grow in width. 
			var widthRatio = elementWidth / stringWidth; 
			var newFontSize = Math.floor(30 * widthRatio);//폰트 크기 
			var elementHeight = (chart.innerRadius * 0.7); 
			// Pick a new font size so it will not be larger than the height of label. 
			var fontSizeToUse = Math.min(newFontSize, elementHeight); 
			//Set font settings to draw it correctly. 
			ctx.textAlign = 'center'; 
			ctx.textBaseline = 'middle'; 
			var centerX = ((chart.chartArea.left + chart.chartArea.right) / 2); 
			var centerY = ((chart.chartArea.top + chart.chartArea.bottom) / 7 * 3.2);//default / 2 
			//반도넛일 경우 
			if (chart.config.options.rotation === Math.PI && chart.config.options.circumference === Math.PI) { 
				centerY = ((chart.chartArea.top + chart.chartArea.bottom) / 1.3); 
			} 
			ctx.font = fontSizeToUse+"px " + fontStyle; ctx.fillStyle = color; 
			//Draw text in center 
			ctx.fillText(txt, centerX, centerY); 
		}
		//아래 표시할 글
		if (chart.config.options.elements.bottom) {
			var ctx = chart.chart.ctx; //Get options from the center object in options 
			var bottomConfig = chart.config.options.elements.bottom; 
			var fontSize = bottomConfig.fontSize || '50'; 
			var fontStyle = bottomConfig.fontStyle || 'Arial';
			var txt = bottomConfig.text; 
			var color = bottomConfig.color || '#000'; 
			var sidePadding = bottomConfig.sidePadding || 20; 
			var sidePaddingCalculated = (sidePadding/100) * (chart.innerRadius * 2) 
			//Start with a base font of 30px 
			ctx.font = fontSize + "px " + fontStyle; //Get the width of the string and also the width of the element minus 10 to give it 5px side padding 
			var stringWidth = ctx.measureText(txt).width; 
			var elementWidth = (chart.innerRadius * 2) - sidePaddingCalculated; // Find out how much the font can grow in width. 
			var widthRatio = elementWidth / stringWidth; 
			//var newFontSize = Math.floor(15 * widthRatio);//폰트 크기 
			var newFontSize = 15; 
			var elementHeight = (chart.innerRadius * 0.7); 
			// Pick a new font size so it will not be larger than the height of label. 
			var fontSizeToUse = Math.min(newFontSize, elementHeight); 
			//Set font settings to draw it correctly. 
			ctx.textAlign = 'center'; 
			ctx.textBaseline = 'middle'; 
			var centerX = ((chart.chartArea.left + chart.chartArea.right) / 2); 
			var centerY = ((chart.chartArea.top + chart.chartArea.bottom) / 7 * 4); 
			//반도넛일 경우 
			if (chart.config.options.rotation === Math.PI && chart.config.options.circumference === Math.PI) { 
				centerY = ((chart.chartArea.top + chart.chartArea.bottom) / 1.3); 
			} 
			ctx.font = fontSizeToUse+"px " + fontStyle; ctx.fillStyle = color; 
			//Draw text in center 
			ctx.fillText(txt, centerX, centerY); 
		} 
	} 
});

/**
 * 차트 설정
 */
function createConfig(serverData, colorSet){
	let dataArr = getDataArr(serverData);
	const config = {
		type: 'doughnut',
		data: {
			labels : serverData.legend,
			datasets:[{
				backgroundColor: colorSet, 
				data: dataArr
			}]},
		options: {
			responsive: true, 
			maintainAspectRatio: false,
			layout: {
		        padding: 60
		    },
			tooltips : {
				enabled : false
			},
			legend : {
				display : false
			},
			plugins: {
	            legend: false,
	            outlabels: {
	                text: '%l',
	                color: 'black',
	                backgroundColor : 'white',
	                stretch: 20,
	                font: {
	                    resizable: true,
	                    minSize: 12,
	                    maxSize: 18
	                }
	            }
	        },
			elements: { 
				center: { 
					text: dataArr[0], 
					fontStyle: 'Helvetica', //Default Arial 
					sidePadding: 15 //Default 20 (as a percentage) 
				},
				bottom: { 
					text: serverData.legend[0], 
					fontStyle: 'Helvetica', //Default Arial 
					sidePadding: 15 //Default 20 (as a percentage) 
				}
			}
		}
	};
	return config;
}

/**
 * 마우스 호버 이벤트 바인딩
 */
function hoverEvtBind(canvas){
	let options = canvas.options;
	canvas.options.onHover = function(evt) {
    let item = canvas.getElementAtEvent(evt);
	    if(item.length != 0){
		    let label = item[0]._model.label;
		    let index = item[0]._index;
		    let data = canvas.data.datasets[0].data[index];
		    canvas.options.elements.center.text = data;
		    canvas.options.elements.bottom.text = label;
		    
		    canvas.options.plugins.outlabels.color = 'white';
		    canvas.options.plugins.outlabels.backgroundColor = 'black';
		}
	}
	canvas.update();
}

/**
 * 서버로부터 전덜받은 값에서 차트에 표시할 값만 배열에 넣어 리턴
 */
function getDataArr(serverData){
	var dataArr = [];
	var list = serverData['data'];
	for(let i=0; i<list.length; i++){
		let listVal = list[i];
		dataArr.push(listVal.cnt);
	}
	console.log("dataArr",dataArr)
	return dataArr;
}