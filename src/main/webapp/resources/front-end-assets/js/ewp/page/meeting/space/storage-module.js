/**
 * 북마크, 판서한 내용 백업및 로드를 위한 모듈
 */
export const StorageMng = function(options) {
	// 모듈 설정값
	var setting = $.extend({}, defaults, options);
	let instance;
	initMeetingConfig();
	
	return instance = StorageMng.fn = StorageMng.prototype = {
		/**
		 * bm : [1, 7, 15, ...] 북마크된 페이지 번호를 번호순으로 나열
		 * as : [1,1,3,8,9,1,2,3,3,...] action이 일어난 페이지 번호 저장
		 * ds : [[{}, {}, ...], [{}, {}, ...], ...] 해당 페이지 판서 정보
		 * book : [image, image, ...] 판서한 이미지 해당 페이지 index에 저장
		 */
		initFileConfig : function(fileKey){	
			let meetingImgStorage = JSON.parse(sessionStorage.getItem(setting.meetingKey+"-img-Storage"));
			let fileImgStorage = meetingImgStorage[fileKey];
			if(fileImgStorage == undefined || fileImgStorage == null) {
				fileImgStorage = {};
				meetingImgStorage[fileKey] = fileImgStorage;
			}
			sessionStorage.setItem(setting.meetingKey+"-img-Storage", JSON.stringify(meetingImgStorage));
			
			let meetingDataStorage = JSON.parse(localStorage.getItem(setting.meetingKey+"-meeting-Storage"));
			let fileDataStorage = meetingDataStorage["file"];
			let fileData = fileDataStorage[fileKey];
			if(fileData == undefined || fileData == null) {
				fileData = {
						"bm": [],//BookMark
						"as": {
							s: [],//스택
							l: 0//단계
						},//Action Stack
						"ds": {}//Draw Stack
				}
				fileDataStorage[fileKey] = fileData;
			}
			localStorage.setItem(setting.meetingKey+"-meeting-Storage", JSON.stringify(meetingDataStorage));
		},
		
		saveBookmark : function(fileKey, bm){
			let meetingDataStorage = JSON.parse(localStorage.getItem(setting.meetingKey+"-meeting-Storage"));
			let fileStorage = meetingDataStorage["file"];
			let fileData = fileStorage[fileKey];
			
			fileData["bm"] = bm.sort();
			localStorage.setItem(setting.meetingKey+"-meeting-Storage", JSON.stringify(meetingDataStorage));
		},
		
		getBookmark : function(fileKey){
			let meetingDataStorage = JSON.parse(localStorage.getItem(setting.meetingKey+"-meeting-Storage"));
			let fileStorage = meetingDataStorage["file"];
			let fileData = fileStorage[fileKey];
			return fileData["bm"];
		},
	}
	
	/**
	 * meetingImgStorage : 판서한 페이지 이미지 파일을 세션 스토리지에 저장한다
	 * meetingDataStorage : 판서한 좌표값과 그 페이지 정보, 북마크한 페이지 값을 로컬 스토리지에 저장한다
	 */
	function initMeetingConfig(){
		let meetingImgStorage = JSON.parse(sessionStorage.getItem(setting.meetingKey+"-img-Storage"));
		if(meetingImgStorage == undefined || meetingImgStorage == null) {
			meetingImgStorage = {};
		}
		sessionStorage.setItem(setting.meetingKey+"-img-Storage", JSON.stringify(meetingImgStorage));
		
		let meetingDataStorage = JSON.parse(localStorage.getItem(setting.meetingKey+"-meeting-Storage"));
		if(meetingDataStorage == undefined || meetingDataStorage == null) {
			//로컬 저장소에 기존 저장된 값이 없는 경우 초기화
			meetingDataStorage = makeMeetingStorage();
		}else if(meetingDataStorage.empKey != setting.empKey){
			//다른 회의나 동일 회의더라도 다른 사원의 로컬 스토리지가 이미 존재하는 경우 초기화한다
			meetingDataStorage = makeMeetingStorage();
		}
		localStorage.setItem(setting.meetingKey+"-meeting-Storage", JSON.stringify(meetingDataStorage));
	}
	
	function makeMeetingStorage(){
		return {
			"empKey": setting.empKey,
			"file": {}
		}
	}
	
	// 모듈 기본 설정값
	var defaults = {
		meetingKey : 0
		, empKey : 0
	}
};
