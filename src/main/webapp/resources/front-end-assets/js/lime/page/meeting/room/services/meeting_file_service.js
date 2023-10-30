/**
 * 
 */
import {eventMixin, Util, Modal, AjaxBuilder} from '/resources/core-assets/essential_index.js';
import {Final, FileManager} from '/resources/front-end-assets/js/lime/comm/module_index.js';
import {MeetingCall as $MEETING} from '/resources/front-end-assets/js/lime/ajax/ajax_index.js';



export default {
	__proto__: eventMixin,
	async init(data = {}){
		const {
			instanceProvider,
			deletable = false,
		} = data;
		FileManager.init({
			addConfirm: true,
			delConfirm: true,
		});
		FileManager.on("added", async (data) => {
			const {
				files
			} = data;
			Modal.startLoading({
				text: "파일을 업로드 중입니다...",
				animation: "foldingCube",
			});
			const formData = new FormData;
			for(const file of files){
				formData.append("list",file)
			}
			await AjaxBuilder({
				request: $MEETING.Post.meetingFileList,
				param: {
					meetingId: instanceProvider.meetingId, 
					fileType: "MATERIAL",
					fileList: formData
				},
				loading: false,
				exception: 'success-only',
			}).success(res => {
				const uploadErrors = res.error;
				const errorList = uploadErrors?.map(error => "업로드 실패: "+error.data.uploadedFileName);
				Modal.info({
					msg: "회의 첨부파일을 업로드했습니다.",
					detail: errorList,
				});
				this.trigger("change");
			}).error(res => {
				Modal.error({
					response: res,
				});
			}).finally(() => {
				Modal.endLoading();
			}).exe();
		});
		FileManager.on("deleted", async (data) => {
			const {
				files
			} = data;
			await AjaxBuilder({
				request: $MEETING.Delete.meetingFileList,
				param: {
					meetingId: instanceProvider.meetingId,
					fileList: files,
				},
				exception: 'success-only',
			}).success(res => {
				const delErrors = res.error;
				const errorList = delErrors?.map(error => "삭제 실패: "+error.data.uploadedFileName);
				Modal.info({
					msg: "회의 첨부파일을 삭제했습니다.",
					detail: errorList,
				});
				this.trigger("change");
			}).error(res => {
				Modal.error({
					response: res,
				});
			}).finally(() => {
				Modal.endLoading();
			}).exe();
		});
		this.instanceProvider = instanceProvider;
		const meetingId = this.instanceProvider.meetingId;
		const fileList = await $MEETING.Get.meetingMaterialFileList(meetingId);
		this.setFileList(fileList);
	},
	setFileList(fileList){
		if(this.fileMap){
			this.fileMap.clear();
		}else{
			this.fileMap = new Map();
		}
		fileList.forEach(file => this.fileMap.set(file.fileId, file));
		this.showFileList();
	},
	getFileList(){
		return Array.from(this.fileMap.values());
	},
	getFile(fileId){
		return this.fileMap.get(fileId);
	},
	updateFile(file){
		this.fileMap.set(file.fileId, file);
		this.showFileList();
		FileManager.setFiles(this.getFileList());
	},
	showFileList(){
		const fileList = this.getFileList();
		const typeCollection = Final.fileOptionTemplate;
		const generateFileRow = (file) => {
			const $li = Util.createElement("li", "align-center");
			const $span = Util.createElement("span");
			$span.innerHTML = file.uploadedFileName;
			const {
				icon,
				elementClass,
				deletable,
				downloadable,
				viewable,
			} = Final.getFileMetadata(file);
			if(viewable == true){
				Util.addClass($span, "cursor-pointer", "href-link");
				$span.onclick = () =>{
					this.trigger("select", {
						file: file,
						pageno: 1,
					});
				}
			}else{
				const $iconDiv = Util.createElement("span", "my-auto", "mr-2");
				const $icon = Util.createElement("i", ...icon);
				$iconDiv.appendChild($icon);
				$li.appendChild($iconDiv);
				Util.addClass($span, "text-muted");
			}
			
			$li.appendChild($span);
			return $li;
		}
		const fileProgressCheck = (file) => {
			const checkInterval = setInterval(async () => {
				const updatedFile = await $MEETING.Get.meetingFileOne(file.fileId);
				if(file.conversionStatus == 300 || file.conversionStatus == 400){
					clearInterval(checkInterval);
					this.updateFile(updatedFile);
				}else{
					clearInterval(checkInterval);
				}
			},2000);
		}
		const $listBox = Util.getElement("#fileListBox");
		$listBox.innerHTML = "";
		for(const file of fileList){
			if(file.state == -1){
				continue;
			}else if(file.state != 2){
				//fileProgressCheck(file);
			}
			const $row = generateFileRow(file);
			$listBox.appendChild($row);
		}
	},
	async showFileMng(){
		const attendRole = this.instanceProvider.attendRole;
		FileManager.update({
			deletable: (attendRole == "FACILITATOR" || attendRole == "ASSISTANT")?true:false,
		});
		FileManager.setFiles(this.getFileList());
		const res = await Modal.show("fileModal");
	},
	enableSelect(){
		Util.removeClass(".docuLeftSection", "disabled");
		Util.removeClass(".fileSection", "disabled");
		Util.removeClass(".docuPagingDiv", "disabled");
	},
	disableSelect(){
		Util.addClass(".docuLeftSection", "disabled");
		Util.addClass(".fileSection", "disabled");
		Util.addClass(".docuPagingDiv", "disabled");
	},
	enableUpload(){
		const $fileMngBtn = Util.getElement("#fileMngBtn");
		$fileMngBtn.style.display = "inline-flex";
		this.off("change");
		this.on("change", async () => {
			this.instanceProvider.getSocketService().sendSyncMsg({
				resourceType: "FILE"
			});
		});
		$fileMngBtn.onclick = async () => {
			$fileMngBtn.disabled = true;
			this.showFileMng();
			$fileMngBtn.disabled = false;
		}
	},
	disableUpload(){
		const $fileMngBtn = Util.getElement("#fileMngBtn");
		$fileMngBtn.onclick = null;
		$fileMngBtn.style.display = "none";
		this.off("change");
	},
}
