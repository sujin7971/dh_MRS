import {eventMixin, Util, Modal} from '/resources/core-assets/essential_index.js';
import {IDBConnector, LimePad} from '/resources/core-assets/module_index.js';
import Mutex from '/resources/library/async-mutex/es6/Mutex.js';
import {thumbHTML, thumbMenuHTML} from './html-package.js';
import {MeetingCall as $MEETING} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';
/**
 * 판서할 파일 호출 및 페이지 선택 이벤트 설정 모듈
 * -호출 가능 함수
 * 1 setStorageMng(스토리지모듈) : 스토리지 모듈 설정
 * 2 setLoadBookletCallback(파일번호) : 판서할 파일 호출 완료시 콜백할 함수 설정
 * 3 setSelectPageCallback(파일번호, 페이지번호) : 페이지 선택시 콜백할 함수 설정
 * 4 setSelectToolCallback(선택한 도구) : 판서 도구 선택시 콜백할 함수 설정
 * 4 initBookletMng(파일목록) : 회의자료를 화면에 표시하고 이벤트 설정
 * 5 orientationChange : 모바일 화면 전환시 호출 이벤트
 */
export const BookletMng = function(instanceProvider) {
	const loginId = instanceProvider.loginId;
	const meetingId = instanceProvider.meetingId;
	const FileService = instanceProvider.getFileService();
	const mutex = new Mutex();
	const dbConnector = new IDBConnector({DB_NAME: loginId+"_"+meetingId+"_DRAWING_ARCHIVE"});
	let undoStack = [];
	let redoStack = [];
	let pagechecksum = "";
	let instance; // BookletMng 객체
	let limePad; // 판서 기능 관리
	let toast; // 토스트 메시지
	let $selectedThumbCon = $(".thumbnailDiv").show();
	
	/* 현재 선택된 회의 자료와 페이지 정보 */
	let doc = {
		"file": null,
		"fileId":0,//파일번호
		"pageno":0,//페이지번호
		"pageLength":0,//페이지수
		"allPageList":[],//모든 페이지 번호 목록
		"bmPageList":[]//북마크된 페이지 번호 목록
	}
	
	// 현재 사용중인 페이지 목록. 전체인 경우 doc["allPageList"] / 북마크인경우 세션에서 가져온 북마크 페이지 리스트
	let nowPageList;
	let toastTimeout;
	init();
	function init(){
		booklet_evt_binder();
		toast =new Toastr({
			theme:'moon'
		});
		setBookmarkEvt();
		setPad();
	}
	
	function setPad(){
		limePad = new LimePad({
			section : "padSection",
			width: 0,
			height: 0,
		});
		const $toolPanel = $("#toolCon");
		limePad.getToolInterface().forEach($icon => $toolPanel.append($icon));
		limePad.on("transformScale", function(scale){
			clearTimeout(toastTimeout);
			toastTimeout = setTimeout(function(){
				toast.show(Math.round(scale * 100) + " %");
			}, 200);
		});
		limePad.on("pointerMove", function(tool, pos){
			//console.log("pointerMove", tool, pos);
		});
		limePad.on("laserStart", function(pos){
			instance.trigger("laserStart", pos);
		});
		limePad.on("laserMove", function(pos){
			instance.trigger("laserMove", pos);
		});
		limePad.on("laserEnd", function(){
			instance.trigger("laserEnd");
		});
		setPadControlMode();
		limePad.start();
	}
	
	function setPadControlMode(){
		limePad.on("addContext", saveContextOnDB);
		limePad.on("swipe", swipeBooklet);
	}
	
	function setPadPassiveMode(){
		limePad.off("addContext", saveContextOnDB);
		limePad.off("swipe", swipeBooklet);
	}
	
	async function saveContextOnDB(graphicContext){
		const pageno = doc["pageno"];
		graphicContext.page = pageno;
		await dbConnector.insert(doc["fileId"], graphicContext);
		redoStack = [];
		undoStack.push(graphicContext);
		setUndoRedoState();
	}
	
	function swipeBooklet(direction){
		if(direction == "top"){
			$("#prev").trigger("click");
		}else if(direction == "bottom"){
			$("#next").trigger("click");
		}
	}
	
	/* 불러올 책자 선택 */
	function showFileStateMsg(state){
		let msg = "";
		if(state == -1){
			msg = "변환에 실패한 파일입니다. 첨부한 파일이 올바른 HWP, PDF, MS OFFICE 문서가 맞는지 확인해 주세요.";
		}else if(state == 0 || state == 1){
			msg = "변환 진행 중인 파일입니다. 변환이 완료된 파일만 회의 진행 중 판서가 가능합니다.";
		}
		Modal.info({msg: msg});
	}
	
	function clearBooklet(){
		doc["file"] = null;
		doc["fileId"] = 0;
		doc["pageno"] = 0;
		$(".list.thumbnailDiv").empty();
		$(".list.bookmarkDiv").empty();
		$(".thumbnailBtnDiv div").addClass("disabled");
		hidePaginationCtrl();
		limePad.clear();
	}
	
	async function loadPage(data = {}){
		return new Promise(async (resolve, reject) => {
			const {
				file = doc["file"],
				pageno = 1,
				save = true
			} = data;
			if(file == null || pageno == null){
				reject();
				return;
			}
			unbindThumbScrollEvt();
			let fileChange = false;
			let pageChange = false;
			if(doc["file"] != file){
				fileChange = true;
				pageChange = true;
			}else if(doc["pageno"] != pageno){
				pageChange = true;
			}
			if(fileChange || pageChange){
				try{
				Modal.startLoading();
				await limePad.stop();
				
				instance.trigger("selectFile", file.fileId, pageno);
				if(doc["pageno"] && save){
					await uploadPageChange();
				}
				
				if(fileChange){
					if(file == "MEMO"){
						await selectMemo();
						await setBookmarkThumbList("MEMO");
						limePad.selectTool("pen");
					}else{
						await selectFile(file, pageno);
						await setBookmarkThumbList(file.fileId);
						limePad.selectTool("pointer");
					}
					closeSlide();
				}
				if(pageChange){
					try{
						await selectPage(pageno);
					}catch(err){
						reject(err);
					}
					if(file == "MEMO"){
						await setMemoThumbList(pageno);
					}else{
						await setFileThumbList(file.fileId, pageno);
					}
					setTimeout(() => {
						const selectedThumbElemList = document.querySelectorAll('li[data-page="'+pageno+'"]');
						for(const selectedThumbElem of selectedThumbElemList){
							selectedThumbElem.classList.add = "selected";
							selectedThumbElem.scrollIntoView({behavior: "smooth", block: "center"});
						}
					});
				}
				if(fileChange){
					limePad.fitScreenToWindow();
				}
				limePad.start();
				Modal.endLoading();
				toast.show(pageno + "페이지");
				}catch(err){
					Modal.endLoading();
					reject(err);
				}
			}
			bindThumbScrollEvt();
			resolve();
		});
		/* 선택한 책자 불러오기 */
		/* 캐치문 필요 */
		async function selectFile(file, pageno){
			return new Promise(async (resolve, reject) => {
				try{
					doc["file"] = file;
					doc["fileId"] = file.fileId;
					await dbConnector.setStore(file.fileId);
					await loadFileGraphicContext();
					const pageLength = file.pageCount;
					console.log("file", file, "pageLength", pageLength)
					doc["pageLength"] = pageLength
					initPaginationCtrl();
					
					$(".list.thumbnailDiv").empty();
					$(".list.bookmarkDiv").empty();
					$(".thumbnailBtnDiv div").removeClass("disabled");
					resolve();
				}catch(err){
					reject(err);
				}
			});
		}
		
		async function selectMemo(){
			return new Promise(async (resolve, reject) => {
				try{
					doc["file"] = "MEMO";
					doc["fileId"] = "MEMO";
					await dbConnector.setStore("MEMO");
					await loadFileGraphicContext();
					const pageLength = await $MEETING.Get.memoPageLength(meetingId);
					doc["pageLength"] = pageLength
					initPaginationCtrl();
					
					$(".list.thumbnailDiv").empty();
					$(".list.bookmarkDiv").empty();
					$(".thumbnailBtnDiv div").removeClass("disabled");
					resolve();
				}catch(err){
					reject(err);
				}
			});
		}
		
		/* 페이지네이션/섬네일 선택 이벤트시 트리거 */
		function selectPage(pageno){
			return new Promise(async function(resolve, reject) {
				doc["pageno"] = (pageno)?pageno:1;
				const fileId = doc["fileId"];
				const $thumb = $('.list.thumbnailDiv');
				const $book = $('.list.bookmarkDiv');
				let src;
				await loadPageGraphicContext();
				if(fileId == "MEMO"){
					src = $MEETING.Get.memoSourceImage({
						meetingId: meetingId,
					});
				}else{
					src = $MEETING.Get.notePageSourceImage({
						meetingId: meetingId,
						fileId: fileId,
						pageNo: pageno
					});
				}
				limePad.setBackground({url: src}).then(function(){
					$("#currentPage").val(pageno);
					$thumb.find("li").removeClass("selected");
					$thumb.find("#tm"+pageno).addClass("selected");
					$book.find("li").removeClass("selected");
					$book.find("#bm"+pageno).addClass("selected");
					resolve();
				}).catch(function(err){
					reject(err);
				});
			});
		}
	}
	
	async function loadFileGraphicContext(){
		const context = await dbConnector.getAllData(doc["fileId"]);
		console.log("loadFileGraphicContext", "fileId", doc["fileId"], "GraphicContext", context);
		if(context){
			undoStack = context;
			redoStack = [];
		}
		setUndoRedoState();
	}
	
	/* 페이징 관련 값 및 이벤트 초기화 */
	function initPaginationCtrl(){
		const pageLength = doc["pageLength"];
		const pageno = doc["pageno"];
		let allPageList = [];
		for(let i = 1; i <= pageLength; i++){
			allPageList.push(i);
		}
		doc["allPageList"] = allPageList;
		doc["bmPageList"] = allPageList;
		nowPageList = allPageList;
		
		$(".docuToolDiv").removeClass("d-none");
		const $pagingCtrl = $(".docuPagingDiv");
		const $pageInput = $pagingCtrl.find("#currentPage");
		$pageInput.val(allPageList.indexOf(pageno) + 1)
		$pagingCtrl.find("i").unbind("click");
		$("#tot").html("/ " + (pageLength));
		$pageInput[0].oninput = (evt) => {
			Util.acceptNumber($pageInput[0]);
		}
		showPaginationCtrl();
	}
	
	function showPaginationCtrl(){
		let $pagingCtrl = $(".docuPagingDiv");
		$pagingCtrl.removeClass("d-none");
		bindPaginationEvt();
	}
	
	function hidePaginationCtrl(){
		let $pagingCtrl = $(".docuPagingDiv");
		$pagingCtrl.addClass("d-none");
	}
	
	/* 페이징 버튼 이벤트 설정 */
	function bindPaginationEvt(){
		$("#backward").unbind("click");
		$("#forward").unbind("click");
		$("#next").unbind("click");
		$("#prev").unbind("click");
		$("#currentPage").unbind("change");
		
		$("#backward").on("click",function(){
			pageLoadAndScroll(1);
		});
		$("#forward").on("click",function(){
			const pageLength = doc["pageLength"];
			pageLoadAndScroll(pageLength);
		});
		$("#next").on("click", function() {
			const pageLength = doc["pageLength"];
			let pageno = Number($("#currentPage").val()) + 1;
			if (Number($("#currentPage").val()) >= pageLength){
				pageno = pageLength;
			}
			pageLoadAndScroll(pageno);
		});
		$("#prev").on("click", function() {
			let pageno = Number($("#currentPage").val()) - 1;
			if (Number($("#currentPage").val()) <= 1){
				pageno = 1;
			}
			pageLoadAndScroll(pageno);
		});
		
		$("#currentPage").on("change", function(){
			const pageLength = doc["pageLength"];
			let pageno = $(this).val();
			if(pageno > pageLength){
				pageno = pageLength;
			}else if(pageno < 1){
				pageno = 1;
			}
			$(this).val(pageno);
			//console.log("currentPage change", pageno);
			pageLoadAndScroll(pageno);
		});
		async function pageLoadAndScroll(pageno){
			//console.log("pageLoadAndScroll", pageno);
			Modal.startLoading();
			const fileId = doc["fileId"];
			const $thumbCon = $(".thumbnailDiv");
			if(fileId == "MEMO"){
				await setMemoThumbList(pageno);
			}else{
				await setFileThumbList(fileId, pageno);
			}
			try{
				await loadPage({pageno: nowPageList[pageno - 1]});
				setTimeout(() => {
					const $selectedThumb = $selectedThumbCon.find('[data-page='+pageno+']');
					$selectedThumb.addClass("selected");
					$selectedThumb[0].scrollIntoView({behavior: "smooth", block: "center"});
				});
			}catch(err){
				Modal.error({response: err});
			}
			Modal.endLoading();
		}
	}
	
	/* 저장된 판서기록 설정 */
	async function loadPageGraphicContext(){
		let pageno = doc["pageno"];
		const pageGraphicContextList = await dbConnector.getDataFromIndex(doc["fileId"], "page", pageno);
		await limePad.loadGraphicContext(pageGraphicContextList);
		const graphicData = limePad.getGraphicContext();
		const ctx = new Checksum("fnv32", 0);
		ctx.updateStringly(graphicData);
		pagechecksum = ctx.result.toString(16);
	}
	
	async function setBookmarkThumbList(fileId){
		const bookmarkList = await dbConnector.getBookmark(fileId);
		if(!bookmarkList || bookmarkList.length == 0){
			return;
		}
		bookmarkList.sort((a, b) => (a - b));
		for(let i = 0; i < bookmarkList.length; i++){
			const pageno = bookmarkList[i];
			let $bookCon = (fileId == "MEMO")?generateMemoThumbDOM({pageno: pageno, includeOption: false}):generateFileThumbDOM(fileId, pageno);
			$bookCon.addClass("bookmark");
			$bookCon.attr("id","bm"+pageno);
			$('.list.bookmarkDiv').append($bookCon);
		}
	}
	
	/* pageno를 포함하는 범위까지 썸네일 설정 */
	async function setFileThumbList(fileId, pageno){
		unbindThumbScrollEvt();
		//console.log("setFileThumbList", "fileId", fileId, "pageno", pageno);
		openThumbnail();
		const $thumbDiv = Util.getElement(".list.thumbnailDiv");
		while(true){
			const startPage = $thumbDiv.children.length + 1;
			const endPage = getEndPage(startPage, 10);
			//console.log("pageno", pageno, "startPage", startPage, "endPage", endPage);
			if(pageno < startPage || startPage > endPage){
				break;
			}
			await setFileThumbInRange(fileId, startPage, endPage);
			const isScrollbarVisible = Util.isScrollbarVisible($thumbDiv);
			//console.log("isScrollbarVisible", isScrollbarVisible);
			if(!isScrollbarVisible){
				/*스크롤이 보이지 않는 경우 스크롤이 표시되거나 범위를 벗어날 때 까지 썸네일 설정*/
				await setFileThumbList(fileId, pageno + 10);
			}
		}
		bindThumbScrollEvt();
	}
	
	/* 페이지 범위내 섬네일 생성 */
	function setFileThumbInRange(fileId, startPage, endPage){
		//console.log("setFileThumbInRange", "fileId", fileId, "startPage", startPage, "endPage", endPage);
		return new Promise(async (resolve, reject) => {
			const bookmark = await dbConnector.getBookmark(fileId);
			doc["bmPageList"] = bookmark;
			const $thumblist = [];
			for(let i = startPage; i <= endPage; i++){
				let pageno = i;
				let $thumbCon = generateFileThumbDOM(fileId, pageno);
				$thumblist.push($thumbCon);
				if(doc["pageno"] == i){
					$thumbCon.addClass("selected");
				}
				$thumbCon.attr("id","tm"+pageno);
				$('.list.thumbnailDiv').append($thumbCon);
				if(bookmark && bookmark.includes(pageno)){
					$thumbCon.addClass("bookmark");
				}
			}
			/* 모든 썸네일 이미지가 로드 될 때 까지 대기 */
			const interval = setInterval(() => {
				const loadFinish = $thumblist.reduce((loaded, $thumb) => loaded && $thumb.load, true);
				if(loadFinish == true){
					clearInterval(interval);
					resolve();
				}
			}, 100);
		});
	}
	
	/* 썸네일 DOM 생성 */
	function generateFileThumbDOM(fileId, pageno){
		let html = thumbHTML;
		let $imgCon = $(html.imgCon);
		let $img = $(html.img);
		$imgCon.html($img);
		$imgCon.attr("data-page",pageno);
		const src = $MEETING.Get.notePageDrawImage({
			meetingId: meetingId,
			fileId: fileId,
			pageNo: pageno,
			salt: pagechecksum,
		});
		$img.attr("src", src);
		$imgCon.load = false;
		$img.on("load", () => {
			//썸네일 이미지 로드 완료
			$imgCon.load = true;
		});
		$imgCon.on("click", async function(){
			try{
				loadPage({pageno: pageno});
			}catch(err){
				Modal.error({response: err});
			}
		});
		return $imgCon;
	}
	let salt;
	/* 메모장 썸네일 설정 */
	async function setMemoThumbList(pageno){
		unbindThumbScrollEvt();
		while(true){
			let startPage = $(".list.thumbnailDiv").children().length + 1;
			let endPage = getEndPage(startPage, 9999);
			await setMemoThumbInRange(startPage, endPage);
			if(pageno < startPage || startPage > endPage){
				break;
			}
		}
		setAddMemoThumb();
		openThumbnail();
		bindThumbScrollEvt();
	}
	
	/* 메모장 페이지 범위내 섬네일 생성 */
	function setMemoThumbInRange(startPage, endPage){
		return new Promise(async (resolve, reject) => {
			const bookmark = await dbConnector.getBookmark("MEMO");
			doc["bmPageList"] = bookmark;
			for(let i = startPage; i <= endPage; i++){
				let pageno = i;
				let $thumbCon = generateMemoThumbDOM({pageno: pageno});
				if(doc["pageno"] == i){
					$thumbCon.addClass("selected");
				}
				$thumbCon.attr("id","tm"+pageno);
				$('.list.thumbnailDiv').append($thumbCon);
				if(bookmark && bookmark.includes(pageno)){
					$thumbCon.addClass("bookmark");
				}
			}
			resolve();
		});
	}
	
	/* 메모장 썸네일 DOM 생성 */
	function generateMemoThumbDOM(data = {}){
		const {
			pageno = 1,
			includeOption = true
		} = data;
		let html = thumbHTML;
		let $imgCon = $(html.imgCon);
		let $img = $(html.img);
		$imgCon.html($img);
		$imgCon.attr("data-page",pageno);
		const src = $MEETING.Get.memoPageDrawImage({
			meetingId: meetingId,
			pageNo: pageno,
			salt: new Date().getTime()
		});
		$img.attr("src", src);
		$imgCon.on("click", async function(){
			let selectedPageno = $(this).data("page");
			try{
				await loadPage({pageno: selectedPageno});
			}catch(err){
				Modal.error({response: err});
			}
		});
		
		if(includeOption){
			const $menuCon = $(html.menu.con);
			const $menuIcon = $(html.menu.icon);
			$menuIcon.attr("btn-role", "menu");
			
			$menuCon.append($menuIcon);
			$imgCon.append($menuCon);
			$menuCon.on("click", function(evt){
				const target = evt.target;
				const btnRole = target.getAttribute("btn-role");
				evt.preventDefault();
				evt.stopPropagation();
				switch(btnRole){
					case "menu":
						$(".thumbMenuDiv").remove();
						const html = thumbMenuHTML;
						const $overlay = $(html.overlay);
						$menuCon.append($overlay);
						const $base = $(html.base);
						const $con = $(html.con);
						$base.append($con);
						
						const $addPage = $(html.item);
						$addPage.html("페이지 추가");
						$addPage.attr("btn-role", "add");
						$con.append($addPage);
						
						const pageLength = doc["pageLength"];
						if(pageLength > 1){
							const $delPage = $(html.item);
							$delPage.html("페이지 삭제");
							$delPage.attr("btn-role", "del");
							$con.append($delPage);
						}
						$menuCon.append($base);
						$overlay.on("click", function(){
							$overlay.remove();
							$base.remove();
						});
						break;
					case "add":
						addMemoPage(pageno + 1);
						break;
					case "del":
						deleteMemoPage(pageno);
						break;
				}
			});
		}
		return $imgCon;
	}
	/* 메모장 페이지 추가 */
	async function addMemoPage(pageno){
		const nowLength = doc["pageLength"];
		const newLength = nowLength + 1;
		Modal.startLoading();
		rightShiftBookmark(pageno);
		await $MEETING.Post.memoPage({
			meetingId: meetingId,
			pageNo: pageno,
		});
		await dbConnector.shiftRightIndex(doc["fileId"], "page", pageno);
		doc["pageLength"] = newLength;
		doc["file"] = null;
		if(doc["pageno"] >= pageno){//추가할 페이지가 현재 페이지 위치를 대체하거나 더 뒤인경우
			doc["pageno"] = doc["pageno"] + 1;//저장을 처리할 현재 페이지 번호 1 증가
		}
		try{
			await loadPage({file:"MEMO", pageno: pageno});
		}catch(err){
			Modal.error({response: err});
		}
		Modal.endLoading();
	}
	/* 메모장 페이지 삭제 */
	async function deleteMemoPage(pageno){
		const nowLength = doc["pageLength"];
		const newLength = nowLength - 1;
		Modal.startLoading();
		leftShiftBookmark(pageno);
		await $MEETING.Delete.memoPage({
			meetingId: meetingId,
			pageNo: pageno,
		});
		await dbConnector.deleteDataFromIndex(doc["fileId"], "page", pageno);
		await dbConnector.shiftLeftIndex(doc["fileId"], "page", pageno);
		doc["pageLength"] = newLength;
		doc["file"] = null;
		if(doc["pageno"] == pageno){//삭제할 페이지가 현재 페이지 인 경우
			doc["pageno"] = null;//저장 처리 예외
		}else if(doc["pageno"] > pageno){//삭제할 페이지가 현재 페이지보다 뒤인경우
			doc["pageno"] = doc["pageno"] - 1;//저장을 처리할 현재 페이지 번호 1 감소
		}
		try{
			await loadPage({file:"MEMO", pageno: Math.max(pageno - 1, 1)});
		}catch(err){
			Modal.error({response: err});
		}
		Modal.endLoading();
	}
	/* 썸네일 목록 마지막에 메모장 페이지 추가 버튼 설정 */
	function setAddMemoThumb(){
		$(".addPaper").remove();
		const $addThumb = $('<li><div class="addPaper">+</div></li>');
		$('.list.thumbnailDiv').append($addThumb);
		$addThumb.css("cursor", "pointer");
		$addThumb.on("click", async function(){
			const nowLength = doc["pageLength"];
			const newPage = nowLength + 1;
			addMemoPage(newPage);
		});
	}
	
	/* 되돌리기/다시하기 이벤트 설정 */
	function setUndoRedoState(){
		disableRedoUndoEvt();
		if(undoStack.length != 0){
			enableUndoEvt();
		}
		if(redoStack.length != 0){
			enableRedoEvt();
		}
	}
	
	/* 되돌리기/다시하기 버튼 비활성화 */
	function disableRedoUndoEvt(){
		let $undo = $(".tool.tUndo");
		let $redo = $(".tool.tRedo");
		
		$undo.unbind("click");
		$redo.unbind("click");
		
		$undo.addClass("disabled");
		$redo.addClass("disabled");
	}
	
	/* 되돌리기 버튼 이벤트 설정 */
	function enableUndoEvt(){
		let $undo = $(".tool.tUndo");
		$undo.removeClass("disabled");
		$undo.on("click", function(){
			mutex.runExclusive(async () => {
				await undo();
			});
		});
	}
	
	/* 다시하기 버튼 이벤트 설정 */
	function enableRedoEvt(){
		let $redo = $(".tool.tRedo");
		$redo.removeClass("disabled");
		$redo.on("click", function(){
			mutex.runExclusive(async () => {
				await redo();
			});
		});
	}
	/* 되돌리기 */
	function undo(){
		return new Promise(async (resolve, reject) => {
			const nowPage = doc["pageno"];
			const context = undoStack.pop();
			if(!context){
				resolve();
			}
			if(nowPage != context.page){
				try{
					await loadPage({pageno: context.page});
				}catch(err){
					reject(err);
				}
			}
			redoStack.push(context);
			await dbConnector.deleteLast(doc["fileId"]);
			limePad.undo();
			setUndoRedoState();
			resolve();
		});
	}
	/* 다시하기 */
	function redo(){
		return new Promise(async (resolve, reject) => {
			const nowPage = doc["pageno"];
			const context = redoStack.pop();
			if(!context){
				resolve();
			}
			if(nowPage != context.page){
				try{
					await loadPage({pageno: context.page});
				}catch(err){
					reject(err);
				}
			}
			undoStack.push(context);
			await dbConnector.insert(doc["fileId"], context);
			limePad.redo(context);
			setUndoRedoState();
			resolve();
		});
	}
	
	/* 북마크 버튼 이벤트 설정 */
	function setBookmarkEvt(){
		let $mark = $(".tool.tBookmark");
		$mark.on("click", function(){
			setPageBookmark();
		});
	}
	
	function openThumbnail(){
		var $pageDeck = $(".bodyDiv .docuLeftSection");
        var $thumbDiv = $(".bodyDiv .docuLeftSection .thumbnailDiv");
        var $bookmarkDiv = $(".bodyDiv .docuLeftSection .bookmarkDiv");
        
        /*if(!$thumbDiv.is(":visible")) {
        	$pageDeck.slideDown(0);
        	$thumbDiv.slideDown(0);
        	$bookmarkDiv.slideUp(0);
        }*/
        if(!$selectedThumbCon.is(":visible")){
        	$pageDeck.slideDown(0);
        	$selectedThumbCon.slideDown(0);
        }
	}
	
	function closeThumbnail(){
		var $pageDeck = $(".bodyDiv .docuLeftSection");
        var $thumbDiv = $(".bodyDiv .docuLeftSection .thumbnailDiv");
        var $bookmarkDiv = $(".bodyDiv .docuLeftSection .bookmarkDiv");
        
        if($thumbDiv.is(":visible")) {
        	$pageDeck.slideUp(0);
        	$thumbDiv.slideUp(0);
        }
	}
	
	function booklet_evt_binder(){
		let $canvasCon = $("#pageContainer");
		let isMarker;
		
		var $pageDeck = $(".bodyDiv .docuLeftSection");
        var $thumbDiv = $(".bodyDiv .docuLeftSection .thumbnailDiv");
        var $bookmarkDiv = $(".bodyDiv .docuLeftSection .bookmarkDiv");
        $pageDeck.slideUp(0);
    	$thumbDiv.slideUp(0);
    	$bookmarkDiv.slideUp(0);
		// 썸네일 펼침 닫힘
	    $(".btnThumbnail").click(function(evt) {
	    	if($(this).hasClass("disabled")){
	    		return;
	    	}
	        closeSlide();
	        evt.stopPropagation();
	        if(isSlideClose() && $thumbDiv.is(":visible") ){
	        	closeThumbnail();
	        }else{
	        	openThumbnail();
	        };
	    });
	    
	    function openThumbnail(){
	    	$pageDeck.slideDown(0);
        	$thumbDiv.slideDown(0);
        	$bookmarkDiv.slideUp(0);
        	
        	$selectedThumbCon = $thumbDiv;
        	
        	nowPageList = doc["allPageList"];
            showPaginationCtrl();
		}
	    
	    function closeThumbnail(){
	    	$pageDeck.slideUp(0);
        	$thumbDiv.slideUp(0);
		}
		
	    // 북마크 펼침 닫힘
		$(".btnBookmark").click(function(evt) {
			if($(this).hasClass("disabled")){
	    		return;
	    	}
	        closeSlide();
	        evt.stopPropagation();
	        if( isSlideClose() && $bookmarkDiv.is(":visible") ){
	        	closeBookmark();
	        }else{
	        	openBookmark();
	        };
	    });
		
		function openBookmark(){
			$pageDeck.slideDown(0);
        	$thumbDiv.slideUp(0);
            $bookmarkDiv.slideDown(0);
            
            $selectedThumbCon = $bookmarkDiv;
            
            nowPageList = doc["bmPageList"];
            hidePaginationCtrl();
		}
		
		function closeBookmark(){
			$pageDeck.slideUp(0);
        	$bookmarkDiv.slideUp(0);
		}
		
		// 좌측 회의정보 슬라이드
		$('.openCloseBtn').on('click', toggleSlide);
		
		function toggleSlide(){
		    if($('.infoSection').offset().left<0){
		    	openSlide();
		    }else{
		    	closeSlide();
		    };       
		}
		
		function isSlideClose(){
			if($('.infoSection').offset().left<0){
				return true;
			}else{
				return false;
			}
		}
	}
	
	function bindThumbScrollEvt(){
		unbindThumbScrollEvt();
		// 무한 스크롤
		$(".thumbnailDiv").on("scroll", async function() {
			const scrollTop = $(this).scrollTop();
			const innerHeight = $(this).innerHeight();
			const scrollHeight = $(this).prop('scrollHeight');
			//console.log("scrollTop", scrollTop, "innerHeight", innerHeight, "scrollHeight", scrollHeight);
	        if (scrollTop + innerHeight >= scrollHeight - 10) {
	        	const fileId = doc["fileId"];
	        	const startPage = $(this).children().length + 1;
	        	unbindThumbScrollEvt();
	        	if(fileId == "MEMO"){
					await setMemoThumbList(startPage);
				}else{
					await setFileThumbList(fileId, startPage);
				}
				bindThumbScrollEvt();
	        }
		});
	}
	
	function unbindThumbScrollEvt(){
		// 무한 스크롤
		$(".thumbnailDiv").unbind("scroll");
	}
	
	function openSlide(){
		$('.infoSection').animate({left:0}, 100 );
        $('.openCloseBtn').removeClass("iOpen");  
        $('.openCloseBtn').addClass("iClose");      
	}
	
	function closeSlide(){
        $('.infoSection').animate({left:$('.infoSection').width()*-1}, 100 ); 
        $('.openCloseBtn').removeClass("iClose");  
        $('.openCloseBtn').addClass("iOpen");       
	}
	
	/* 북마크 버튼 클릭시 처리할 이벤트 */
	function setPageBookmark(){
		let pageno = doc["pageno"];
		let bookmark = doc["bmPageList"];
		if(bookmark.includes(pageno)){
			unbookmarkPage(pageno);
		}else{
			bookmarkPage(pageno);
		}
	}
	/* 북마크 추가 */
	function bookmarkPage(pageno){
		let fileId = doc["fileId"];
		let bookmark = doc["bmPageList"];
		bookmark.push(pageno);
		bookmark.sort(function(a, b) { // 오름차순
			return a - b;
		});
		$("#tm"+pageno).addClass("bookmark");
		let $bookmark = $(".list.bookmarkDiv");
		$bookmark.html("");
		for(let i = 0; i < bookmark.length; i++){
			let $imgCon = (fileId == "MEMO")?generateMemoThumbDOM({pageno: bookmark[i], includeOption: false}):generateFileThumbDOM(fileId, bookmark[i]);
			if(pageno == bookmark[i]){
				$imgCon.addClass("selected");
			}
			$imgCon.addClass("bookmark");
			$imgCon.attr("id","bm"+bookmark[i]);
			$bookmark.append($imgCon);
		}
		doc["bmPageList"] = bookmark;
		dbConnector.setBookmark(fileId, bookmark);
	}
	/* 북마크 제거 */
	function unbookmarkPage(pageno){
		let fileId = doc["fileId"];
		let bookmark = doc["bmPageList"];
		bookmark = bookmark.filter(function(e) { return e != pageno });
		$("#tm"+pageno).removeClass("bookmark");
		$("#bm"+pageno).remove();
		doc["bmPageList"] = bookmark;
		dbConnector.setBookmark(fileId, bookmark);
	}
	/* 페이지 추가시 기존 북마크중 페이지 번호가 변경되는 페이지에 대해 북마크 페이지 맞춤 */
	function rightShiftBookmark(addpage){
		const fileId = doc["fileId"];
		const nowBookmark = doc["bmPageList"];
		const shiftedBookmark = [];
		for(const page of nowBookmark){
			if(page < addpage){
				shiftedBookmark.push(page);
			}else if(page >= addpage){
				shiftedBookmark.push(page + 1);
			}
		}
		doc["bmPageList"] = shiftedBookmark;
		dbConnector.setBookmark(fileId, shiftedBookmark);
	}
	/* 페이지 식제시 기존 북마크중 페이지 번호가 변경되는 페이지에 대해 북마크 페이지 맞춤 */
	function leftShiftBookmark(delpage){
		const fileId = doc["fileId"];
		const nowBookmark = doc["bmPageList"];
		const shiftedBookmark = [];
		for(const page of nowBookmark){
			if(page < delpage){
				shiftedBookmark.push(page);
			}else if(page > delpage){
				shiftedBookmark.push(page - 1);
			}
		}
		doc["bmPageList"] = shiftedBookmark;
		dbConnector.setBookmark(fileId, shiftedBookmark);
	}
	
	/*
	 * 섬네일/북마크 리스트 페이지네이션을 위해 일정 크기로 페이지를 나눌때 그 범위내의 마지막 페이지 번호를 계산하여 반환
	 * ex)전체 페이지 37, 페이지네이션 크기 10, 시작 페이지 31 => 마지막 번호는 37
	 */
	function getEndPage(startPage, sliceSize){
		let pageLength = doc["pageLength"];
		let remainPage = pageLength - startPage + 1;
		if(remainPage - sliceSize >= 0){
			return startPage + sliceSize - 1
		}else{
			return pageLength;
		}
	}
	
	/*
	 * 페이지 변환 이벤트(책자 변경, 썸네일 클릭, 페이지네이션) 발생시 트리거
	 * 새로운 페이지 화면을 보여주기 전에 처리할 작업진행(페이지 저장, 페이지 삭제)
	 * promise를 통해 비동기 작업을 순서대로 진행
	 */
	function uploadPageChange(){
		return new Promise(function(resolve, reject) {
			if(doc["file"] == null){
				resolve();
			}
			const graphicData = limePad.getGraphicContext();
			const ctx = new Checksum("fnv32", 0);
			ctx.updateStringly(graphicData);
			const nowpagechecksum = ctx.result.toString(16);
			if(pagechecksum && pagechecksum != nowpagechecksum){
				//console.log("pagechecksum", pagechecksum, "nowpagechecksum", nowpagechecksum)
				if(limePad.isEmpty()){
					// 판서 변화가 있는데 색상값을 가진 픽셀이 없는 경우 = 판서한 내용 지워짐
					removePageImage().then(function(){
			    		resolve();
			    	}).catch(function(err){
			    		reject(err);
			    	})
				}else{
					// 판서 변화가 있고 색상값을 가진 픽셀이 있는 경우 = 새로운 판서 작성
					limePad.getPadImage().then(function(image){
			    		return savePageImage(image);
			    	}).then(function(){
			    		resolve();
			    	}).catch(function(err){
			    		reject(err);
			    	})
				}
			}else{
				resolve();
			}
		});
	}
	
	/* 서버에 저장된 페이지 삭제 */
	function removePageImage(){
		return new Promise(async (resolve, reject) => {
			let fileId = doc["fileId"];
			let pageno = doc["pageno"];
			let sourceImg = limePad.getSourceImg();
			
			//썸네일 목록과 북마크 목록의 이미지를 원본 이미지로 교체한다
			var tm = $("#tm"+pageno);
			var bm = $("#bm"+pageno);
			tm.find("img").attr("src", sourceImg.src);
			if(bm != null){
				bm.find("img").attr("src", sourceImg.src);
			}
			try{
				if(fileId == "MEMO"){
					await $MEETING.Put.memoPage({
						meetingId: meetingId,
						pageNo: pageno,
					});
				}else{
					await $MEETING.Put.notePage({
						meetingId: meetingId,
						fileId: fileId,
						pageNo: pageno,
					});
				}
				resolve();
			}catch(err){
				reject(err);
			}
		});
	}
	
	/* 서버에 전송할 페이지 화면을 캔버스 모듈로부터 가져온다 */
	function getPageImage(){
		return new Promise(function(resolve, reject) {
			limePad.getPadImage().then(function(image){
				resolve(image);
			}).catch(function(err){
				reject(err);
			});
		});
	}
	
	/* 저장할 페이지를 서버에 전송 */
	function savePageImage(image){
		return new Promise(async (resolve, reject) => {
			let fileId = doc["fileId"];
			let pageno = doc["pageno"];
			
			//썸네일 목록과 북마크 목록의 이미지를 새로운 이미지로 교체한다
			var tm = $("#tm"+pageno);
			var bm = $("#bm"+pageno);
			tm.find("img").attr("src", image);
			if(bm != null){
				bm.find("img").attr("src", image);
			}
			
			//서버에 이미지 전송을 위해 불필요 정보 제거
			image = image.replace(/^data:image\/(png|jpg|gif);base64,/, "");
			try{
				if(fileId == "MEMO"){
					await $MEETING.Put.memoPage({
						meetingId: meetingId,
						pageNo: pageno,
						image: image
					});
				}else{
					await $MEETING.Put.notePage({
						meetingId: meetingId,
						fileId: fileId,
						pageNo: pageno,
						image: image,
					});
				}
				resolve();
			}catch(err){
				reject(err);
			}
		});
	}
	
	function enableFileSelection(){
		$(".docuLeftSection").removeClass("disabled");
		$(".fileSection").removeClass("disabled");
		$(".docuPagingDiv").removeClass("disabled");
	}
	
	function disableFileSelection(){
		$(".docuLeftSection").addClass("disabled");
		$(".fileSection").addClass("disabled");
		$(".docuPagingDiv").addClass("disabled");
	}
	
	return instance = BookletMng.fn = BookletMng.prototype = {
		__proto__: eventMixin,
		clear: function(){
			clearBooklet();
		},
		//화면 공유 상태가 아님
		setNormalMode: function(){
			//limePad.start();
			setPadPassiveMode();
			limePad.stop();
			limePad.update({
				options: {
					selectable: true,
					disableTool: [],
					hideTool: ["highlight"]
				},
			});
			limePad.start();
			limePad.selectTool("pointer");
			setPadControlMode();
			$(".recoveryToolCon").removeClass("disabled");
			enableFileSelection();
			$("#fileMngBtn").removeClass("disabled");
			$("#memoBtn").removeClass("disabled");
		},
		//화면 공유 송신
		setControlerMode: function(){
			console.log("setControlerMode")
			setPadPassiveMode();
			limePad.stop();
			limePad.update({
				options: {
					selectable: false,
					disableTool: ["polyline", "path", "line", "rectangle", "textarea", "eraser"],
					hideTool: [],
				},
			});
			limePad.start();
			limePad.selectTool("laser");
			$(".recoveryToolCon").addClass("disabled");
			enableFileSelection();
			$("#fileMngBtn").addClass("disabled");
			$("#memoBtn").addClass("disabled");
		},
		//화면 공유 수신
		setPassiveMode: function(){
			setPadPassiveMode();
			limePad.stop();
			limePad.update({
				options: {
					selectable: false,
					disableTool: ["polyline", "path", "line", "rectangle", "textarea", "eraser"],
					hideTool: ["highlight"]
				},
			});
			limePad.start();
			limePad.selectTool("pointer");
			$(".recoveryToolCon").addClass("disabled");
			disableFileSelection();
			$("#fileMngBtn").addClass("disabled");
			$("#memoBtn").addClass("disabled");
		},
		//잠시 중단
		setHoldMode: function(){
			setPadPassiveMode();
			limePad.stop();
			limePad.update({
				options: {
					selectable: true,
					disableTool: [],
					hideTool: ["highlight"]
				},
			});
			limePad.start();
			limePad.selectTool("pointer");
			setPadControlMode();
			$(".recoveryToolCon").removeClass("disabled");
			disableFileSelection();
			$("#fileMngBtn").addClass("disabled");
			$("#memoBtn").removeClass("disabled");
		},
		showWaterMark: function(text){
			$(".controlWaterMark").remove();
			const $con = $('<div class="controlWaterMark untouchable"></div>');
			const $span = $('<span></span>');
			$span.text(text);
			$con.append($span);
			
			$("#docuSection").append($con);
		},
		deleteWaterMark: function(){
			$(".controlWaterMark").remove();
		},
		laser: function(data){
			switch(data.type){
				case "laserStart":
					limePad.laserStart(data.pos);
					break;
				case "laserMove":
					limePad.laserMove(data.pos);
					break;
				case "laserEnd":
					limePad.laserEnd();
					break;
			}
		},
		getPageData: function(){
			return doc;
		},
		/* 표시할 파일 및 페이지 선택 */
		selectFile: async function(data = {}){
			return new Promise(async (resolve, reject) => {
				mutex.runExclusive(async () => {
					try{
						await loadPage(data);
						resolve();
					}catch(err){
						reject(err);
					}
				});
			});
		},
		savePage : function(){
			return uploadPageChange();
		}
	}
}
