/**
 * 공통으로 사용할 상수 모음.
 * 
 * @author mckim
 * @version 1.0
 * @since 6.0
 */

const cogIcon = ["fas", "fa-cog", "fa-spin"]; //변환중
const alertIcon = ["fas", "fa-exclamation-triangle", "colorRed"]//오류
//상태값 (-2 ~ +2) 별 [deletable, downloadable, viewable]여부와 적용될 아이콘 정리
export const fileOptionTemplate = {
	MATERIAL: {
		cls: "kOrigin",
		state: [[false,false,false], [false,true,false], [false,true,false], [false,true,false], [false,true,true]],
		icon: [[], alertIcon, cogIcon, cogIcon, []]
	},
	COPY: {
		cls: "kMydocu",
		state: [[true,false,false], [true,false,false], [false,false,false], [false,false,false], [true,true,true]],
		icon: [[], alertIcon, cogIcon, cogIcon, []],
	},
	REPORT: {
		cls: "kReport",
		state: [[false,false,false], [false,false,false], [false,false,false], [false,false,false], [false,true,true]],
		icon: [[], alertIcon, cogIcon, cogIcon, []],
	},
	MEMO: {
		cls: "kMemo",
		state: [[true,false,false], [true,false,false], [false,false,false], [false,false,false], [true,true,true]],
		icon: [[], alertIcon, cogIcon, cogIcon, []],
	},
	PHOTO: {
		cls: "kPicture",
		state: [[false,false,false], [false,false,false], [false,false,false], [false,false,false], [false,true,true]],
		icon: [[], alertIcon, cogIcon, cogIcon, []],
	},
	VOICE: {
		cls: "kRecord",
		state: [[false,false,false], [false,false,false], [false,false,false], [false,false,false], [false,true,true]],
		icon: [[], alertIcon, cogIcon, cogIcon, []],
	}
}

const getConversionIcon = (file) => {
	const {
		conversionStatus
	} = file;
	switch(conversionStatus){
		case "NOT_STARTED":
		case "DOC_TO_PDF":
		case "PDF_TO_IMAGES":
		case "IMAGE_TO_WEBP":
		case "IMAGES_TO_PDF":
			return cogIcon
		case "FAILED": 
			return alertIcon;
		case "COMPLETED":
		default :
			return [];
	}
}

const isDownloadable = (file) => {
	const {
		fileStatus
	} = file;
	if(fileStatus == "EXISTS"){
		return true;
	}else{
		return false;
	}
}

const isViewable = (file) => {
	const {
		pdfGeneratedYN,
		webpGeneratedYN
	} = file;
	if(pdfGeneratedYN == 'Y' || webpGeneratedYN == 'Y'){
		return true;
	}else{
		return false;
	}
}

const isDeletable = (file) => {
	const {
		relationType,
	} = file;
	if(relationType == "MEETING_MEMO" || relationType == "MEETING_COPY"){
		return true;
	}else{
		return false;
	}
}

const getElementClass = (file) => {
	const {
		relationType,
	} = file;
	switch(relationType){
		case "MEETING_MATERIAL":
			return "kOrigin";
		case "MEETING_MEMO":
			return "kMemo";
		case "MEETING_COPY":
			return "kMydocu";
		case "MEETING_REPORT":
			return "kReport";
		case "MEETING_PHOTO":
			return "kPicture";
		case "MEETING_VOICE": 
			return "kRecord";
	}
}

export const getFileMetadata = (file) => {
	return {
		icon: getConversionIcon(file),
		elementClass: getElementClass(file),
		downloadable: isDownloadable(file),
		viewable: isViewable(file),
		deletable: isDeletable(file),
	}
}