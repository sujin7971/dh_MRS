/**
 * 
 */
import {eventMixin, Util, Modal} from '/resources/core-assets/essential_index.js';
import {SearchBar} from '/resources/front-end-assets/js/ewp/comm/module_index.js';
import {StatCall as $STAT} from '/resources/front-end-assets/js/ewp/comm/ajax_index.js';

window.onload = async () => {
	statSearchManager.init();
	statSearchManager.search();
};

const statSearchManager = {
	init(){
		this.setStatTargetRadio();
		this.setStatPeriodPreset();
		this.setStatPeriodSelect();
	},
	setStatTargetRadio(){
		const $targetRadioList = Util.getElementAll('input[type=radio][name="target"]');
		$targetRadioList.forEach($radio => $radio.addEventListener('change', async () => {
			const value = $radio.value;
			this.target = value;
			this.search();
		}));
		this.getStatTarget = () => {
			for(const $radio of $targetRadioList){
				if($radio.checked == true){
					return $radio.value;
				}
			}
		}
	},
	setStatPeriodPreset(){
		const $periodPresetSelect = Util.getElement("#periodPresetSelect");
		const $periodPresetLabel = Util.getElement("#periodPresetLabel");
		
		const selectPreset = (value) => {
			const $option = $periodPresetSelect.querySelector('[value="'+value+'"]');
			$option.selected = true;
			const name = $option.text;
			$periodPresetLabel.innerText = name;
			
			const year = moment().format('YYYY');
			let startDate, endDate;
			switch (value) {
			  case "1q":
			    startDate = `${year}-01-01`;
			    endDate = `${year}-03-31`;
			    break;
			  case "2q":
			    startDate = `${year}-04-01`;
			    endDate = `${year}-06-30`;
			    break;
			  case "3q":
			    startDate = `${year}-07-01`;
			    endDate = `${year}-09-30`;
			    break;
			  case "4q":
			    startDate = `${year}-10-01`;
			    endDate = `${year}-12-31`;
			    break;
			  case "fhalf":
			    startDate = `${year}-01-01`;
			    endDate = `${year}-06-30`;
			    break;
			  case "shalf":
			    startDate = `${year}-07-01`;
			    endDate = `${year}-12-31`;
			    break;
			  case "yearly":
			    startDate = `${year}-01-01`;
			    endDate = `${year}-12-31`;
			    break;
			}
			if(startDate && moment(startDate).isValid() == true){
				SearchBar.setStartDate(startDate);
			}
			if(endDate && moment(endDate).isValid() == true){
				SearchBar.setEndDate(endDate);
			}
		}
		this.initPeriodPreset = () => {
			selectPreset(-1);
		}
		$periodPresetSelect.onchange = (evt) => {
			const value = evt.target.value;
			selectPreset(value);
		}
	},
	setStatPeriodSelect(){
		const nowDateM = moment();
		const startDate = nowDateM.clone().add(-1, "y").format("YYYY-MM-DD");
		const endDate = nowDateM.clone().add(-1, "d").format("YYYY-MM-DD");
		SearchBar.init({
			startDate: startDate,
			endDate: endDate,
		});
		SearchBar.enableResetBtn({
			startDate: startDate,
			endDate: endDate,
		});
		SearchBar.on("change", async (input, value) => {
			if(input == "startDate" || input == "endDate"){
				this.initPeriodPreset();
			}
		});
		SearchBar.initPeriodPicker({maxDate: endDate});
		SearchBar.on("search", (data = {}) => {
			this.search(data);
		});
	},
	search(){
		const searchParams = SearchBar.getSearchInput();
		statDataManager.setPeriod(searchParams);
		statDataDeployer.setStat();
	}
}

const statDataManager = {
	init(){
		this.paperlessStatForCompany = null;
		this.meetingCountAndAverageDurationStatForCompany = null;
		this.top5DepartmentWithMeetingForCompany = null;
		this.meetingMonthlyTrendForCompany = null;
		
		this.paperlessStatForPersonal = null;
		this.meetingCountAndTotalDurationStatForHosting = null;
		this.meetingCountAndTotalDurationStatForAttendance = null;
		this.top5DepartmentWithMeetingForOffice = null;
		this.meetingMonthlyTrendForPersonal = null;
	},
	setPeriod(data = {}){
		const {
			startDate = this.startDate,
			endDate = this.endDate,
		} = data;
		if(startDate != this.startDate || endDate != this.endDate){
			this.init();
		}
		this.startDate = startDate;
		this.endDate = endDate;
	},
	getPeriod(){
		return {
			startDate: this.startDate,
			endDate: this.endDate,
		}
	},
	async getPaperlessStatForCompany(){
		if(!this.paperlessStatForCompany){
			this.paperlessStatForCompany = await $STAT.Get.paperlessStatForCompany(this.getPeriod());
		}
		return this.paperlessStatForCompany;
	},
	async getMeetingCountAndAverageDurationStatForCompany(){
		if(!this.meetingCountAndAverageDurationStatForCompany){
			this.meetingCountAndAverageDurationStatForCompany = await $STAT.Get.meetingCountAndAverageDurationStatForCompany(this.getPeriod());
		}
		return this.meetingCountAndAverageDurationStatForCompany;
	},
	async getTop5DepartmentWithMeetingForCompany(){
		if(!this.top5DepartmentWithMeetingForCompany){
			this.top5DepartmentWithMeetingForCompany = await $STAT.Get.top5DepartmentWithMeetingForCompany(this.getPeriod());
		}
		return this.top5DepartmentWithMeetingForCompany;
	},
	async getMeetingMonthlyTrendForCompany(){
		if(!this.meetingMonthlyTrendForCompany){
			this.meetingMonthlyTrendForCompany = await $STAT.Get.meetingMonthlyTrendForCompany(this.getPeriod());
		}
		return this.meetingMonthlyTrendForCompany;
	},
	async getPaperlessStatForPersonal(){
		if(!this.paperlessStatForPersonal){
			this.paperlessStatForPersonal = await $STAT.Get.paperlessStatForPersonal(this.getPeriod());
		}
		return this.paperlessStatForPersonal;
	},
	async getMeetingCountAndTotalDurationStatForHosting(){
		if(!this.meetingCountAndTotalDurationStatForHosting){
			this.meetingCountAndTotalDurationStatForHosting = await $STAT.Get.meetingCountAndTotalDurationStatForHosting(this.getPeriod());
		}
		return this.meetingCountAndTotalDurationStatForHosting;
	},
	async getMeetingCountAndTotalDurationStatForAttendance(){
		if(!this.meetingCountAndTotalDurationStatForAttendance){
			this.meetingCountAndTotalDurationStatForAttendance = await $STAT.Get.meetingCountAndTotalDurationStatForAttendance(this.getPeriod());
		}
		return this.meetingCountAndTotalDurationStatForAttendance;
	},
	async getTop5DepartmentWithMeetingForOffice(){
		if(!this.top5DepartmentWithMeetingForOffice){
			this.top5DepartmentWithMeetingForOffice = await $STAT.Get.top5DepartmentWithMeetingForOffice(this.getPeriod());
		}
		return this.top5DepartmentWithMeetingForOffice;
	},
	async getMeetingMonthlyTrendForPersonal(){
		if(!this.meetingMonthlyTrendForPersonal){
			this.meetingMonthlyTrendForPersonal = await $STAT.Get.meetingMonthlyTrendForPersonal(this.getPeriod());
		}
		return this.meetingMonthlyTrendForPersonal;
	},
}

const statDataDeployer = {
	async setStat(){
		const target = statSearchManager.getStatTarget();
		statElementManager.showTitle();
		if(target == "company"){
			await this.setCompanyStat();
		}else if(target == "personal"){
			await this.setPersonalStat();
		}
	},
	async setCompanyStat(){
		await this.setPaperlessStatForCompany();
		await this.setMeetingSummaryStatForCompany();
		await this.setTop5DepartmentForCompany();
		await this.setMeetingMonthlyTrendForCompany();
	},
	async setPersonalStat(){
		await this.setPaperlessStatForPersonal();
		await this.setMeetingSummaryStatForPersonal();
		await this.setTop5DepartmentForPersonal();
		await this.setMeetingMonthlyTrendForPersonal();
	},
	async setPaperlessStatForCompany(){
		const stat = await statDataManager.getPaperlessStatForCompany();
		statElementManager.showParperlessStat(stat);
	},
	async setMeetingSummaryStatForCompany(){
		const stat = await statDataManager.getMeetingCountAndAverageDurationStatForCompany();
		statElementManager.showMeetingSummaryStatForCompany({
			totalCount: stat.statValue1,
			avgDuration: stat.statValue2,
		});
	},
	async setTop5DepartmentForCompany(){
		const statList = await statDataManager.getTop5DepartmentWithMeetingForCompany();
		statElementManager.showTop5DepartmentStat(statList);
	},
	async setMeetingMonthlyTrendForCompany(){
		const statList = await statDataManager.getMeetingMonthlyTrendForCompany();
		statElementManager.showMeetingMonthlyTrend(statList);
	},
	async setPaperlessStatForPersonal(){
		const stat = await statDataManager.getPaperlessStatForPersonal();
		statElementManager.showParperlessStat(stat);
	},
	async setMeetingSummaryStatForPersonal(){
		const hostingStat = await statDataManager.getMeetingCountAndTotalDurationStatForHosting();
		const attendanceStat = await statDataManager.getMeetingCountAndTotalDurationStatForAttendance();
		statElementManager.showMeetingSummaryStatForPersonal({
			hostingCount: hostingStat.statValue1,
			hostingDuration: hostingStat.statValue2,
			attendanceCount: attendanceStat.statValue1,
			attendanceDuration: attendanceStat.statValue2,
		});
	},
	async setTop5DepartmentForPersonal(){
		const statList = await statDataManager.getTop5DepartmentWithMeetingForOffice();
		statElementManager.showTop5DepartmentStat(statList);
	},
	async setMeetingMonthlyTrendForPersonal(){
		const statList = await statDataManager.getMeetingMonthlyTrendForPersonal();
		statElementManager.showMeetingMonthlyTrend(statList);
	},
}

const statElementManager = {
	showTitle(data = {}){
		const target = statSearchManager.getStatTarget();
		const targetName = (target=="company")?"전체":"개인";
		const {
			startDate,
			endDate,
		} = statDataManager.getPeriod();
		const title = `${startDate} ~ ${endDate} 회의관리시스템 전체 ${targetName} 사용 분석`;
		const $titleSpan = Util.getElement("#statTitle");
		$titleSpan.textContent = title;
	},
	showParperlessStat(stat){
		const getValueByStat = (paperless, stat) => {
			  // stat 값에 따라 적절한 값을 반환하는 로직 작성
			  switch (paperless) {
			    case 'pageReduction':
			    	return stat + "장";
			    case 'costReduction':
			    	return 20 * stat + "원";
			    case 'gasReduction':
			    	return (2.88 * stat).toFixed(2) + "g";
			    case 'waterReduction':
			    	return 10 * stat + "L";
			    default:
			    	return '';
			  }
		}
		const statElements = Util.getElementAll("[data-stat-paperless]");
		statElements.forEach(element => {
		    const paperless = element.getAttribute('data-stat-paperless');
		    const value = getValueByStat(paperless, stat); // stat에 따라 값을 가져오는 함수 호출
		    element.textContent = value; // 값을 해당 element에 할당
		});
	},
	showMeetingSummaryStatForCompany(stat){
		const statContainers = Util.getElementAll("[data-stat-summary]");
		const {
			totalCount = 0,
			avgDuration = 0,
		} = stat;
		const hours = Math.floor(avgDuration / 60); // 시간 계산
		const remainingMinutes = avgDuration % 60; // 남은 분 계산
		statContainers.forEach($container => {
		    const summary = $container.getAttribute('data-stat-summary');
		    if(summary == "total"){
		    	$container.style.display = "flex";
		    	const $display = $container.querySelector('[data-stat-display]')
		    	$display.textContent = `${totalCount}회 / ${hours}시간 ${remainingMinutes}분`;
		    }else{
		    	$container.style.display = "none";
		    }
		});
	},
	showMeetingSummaryStatForPersonal(stat){
		const statContainers = Util.getElementAll("[data-stat-summary]");
		const {
			hostingCount = 0,
			hostingDuration = 0,
			attendanceCount = 0,
			attendanceDuration = 0,
		} = stat;
		statContainers.forEach($container => {
		    const summary = $container.getAttribute('data-stat-summary');
		    if(summary == "hosting"){
		    	$container.style.display = "flex";
		    	const hours = Math.floor(hostingDuration / 60); // 시간 계산
				const remainingMinutes = hostingDuration % 60; // 남은 분 계산
		    	const $display = $container.querySelector('[data-stat-display]')
		    	$display.textContent = `${hostingCount}회 / ${hours}시간 ${remainingMinutes}분`;
		    }else if(summary == "attendance"){
		    	$container.style.display = "flex";
		    	const hours = Math.floor(attendanceDuration / 60); // 시간 계산
				const remainingMinutes = attendanceDuration % 60; // 남은 분 계산
		    	const $display = $container.querySelector('[data-stat-display]')
		    	$display.textContent = `${attendanceCount}회 / ${hours}시간 ${remainingMinutes}분`;
		    }else{
		    	$container.style.display = "none";
		    }
		});
	},
	showTop5DepartmentStat(statList){
		const backgroundColors = ['#8293B8', '#5387AB', '#72B1C8', '#8293B8', '#5387AB']
		const labels = new Array();
		const datas = new Array();
		
		statList.forEach(stat => {
			labels.push(stat.refName);
			datas.push(stat.statValue1);
		});
		
		const top5Chart = chartElementManager.getTop5Chart();
		top5Chart.data.labels = labels;
		top5Chart.data.datasets[0].data = datas;
		top5Chart.data.datasets[0].backgroundColor = backgroundColors;
		top5Chart.update();
	},
	showMeetingMonthlyTrend(statList){
		const labels = new Array();
		const nowdatas = new Array();
		const prevdatas = new Array();
		statList.forEach(stat => {
			labels.push(stat.refYear+"-"+stat.refMonth);
			nowdatas.push(stat.statValue1);
			prevdatas.push(stat.statValue2);
		});
		
		const monthlyTrendChart = chartElementManager.getMonthlyTrendChart();
		monthlyTrendChart.data.labels = labels;
		monthlyTrendChart.data.datasets[0].data = nowdatas;
		monthlyTrendChart.data.datasets[1].data = prevdatas;
		monthlyTrendChart.update();
	}
}

const chartElementManager = {
	getTop5Chart(){
		if(!this.top5Chart){
			const ctx = document.getElementById("top5Chart").getContext('2d');
			this.top5Chart = new Chart(ctx, {
			    type: 'horizontalBar',
			    data: {
			        labels: [],
			        datasets: [{
			            label: '회의 횟수',
			            data: [], 
			            backgroundColor: [],
			            borderWidth: 0
			        }]
			    },
			    options: {
			        maintainAspectRatio: false, // default value. false일 경우 포함된 div의 크기에 맞춰서 그려짐.
			        responsive: true,
			        scales: {
			            xAxes: [{
			                ticks: {
			                	callback: function(value) {if (value % 1   === 0) {return value;}},
			                    beginAtZero: true,
			                    fontSize : 12,
			                    fontColor:'#676767'
			                },
			                gridLines: {
			                    color:'#aa9999'
			                },
			            }],
			            yAxes: [{
			                ticks: {
			                    fontSize: 12,
			                    fontColor:'#676767'
			                },
			                gridLines: {
			                    color:'#aa9999'
			                },
			            }]

			        },
			        responsiveAnimationDuration: 0,
			        legend: { //차트 인덱스 옵션
			            display:false,
			            position: 'top',
			        }
			    }
			});
		}
		return this.top5Chart;
	},	
	getMonthlyTrendChart(){
		if(!this.monthlyTrendChart){
			const ctx = document.getElementById("monthlyTrendChart").getContext('2d');
			this.monthlyTrendChart = new Chart(ctx, {	
			    type: 'line',
			    data: {
			        labels: [],
			        datasets: [			
			            {
			                label: '선택기간 회의 횟수',
			                data: [],
			                borderWidth: 4,
			                borderColor:'#ff1d50',
			                tension: 0.4,	
			                fill: false,
			            },
			            {
			                label: '전년도 회의 횟수',
			                data: [],
			                borderWidth: 4,
			                borderColor:'rgba(0,0,0,0.1)',
			                tension: 0.4,
			                backgroundColor:'rgba(0,0,0,0.1)'
			                
			            }
			        ]
			    },
			    options: {
			        maintainAspectRatio: false, // default value. false일 경우 포함된 div의 크기에 맞춰서 그려짐.
			        responsive: true,
			        scales: {
			            xAxes: [{
			                ticks: {
			                    beginAtZero: true,
			                    fontSize : 14,
			                    fontColor:'#676767'
			                },
			                gridLines: {
			                    color: '#343e4d'
			                },
			            }],
			            yAxes: [{
			                ticks: {
			                	min : 0,
			                	callback: function(value) {if (value % 1   === 0) {return value;}},
			                    fontSize : 14,
			                    fontColor:'#676767'
			                },
			                gridLines: {
			                    color: '#343e4d'
			                },
			            }]

			        },
			        responsiveAnimationDuration: 0,
			        legend: { //차트 인덱스 옵션
			            display:false,
			            position: 'top',
			        }
			    }
				
			})
		}
		return this.monthlyTrendChart;
	}
}