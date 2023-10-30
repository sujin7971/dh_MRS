/**
 * 
 */
export const updateBtnHTML = '<button class="btn btn-xs btn-blue-border margin-top-4 margin-left-8">개요수정</button>';
export const extBtnHTML = '<button class="btn btn-xs btn-blue-border margin-top-4 margin-left-8 btnTimeExtension">회의시간연장</button>';
export const reportBtnHTML = '<button class="btn btn-xs btn-blue-border margin-top-4 margin-left-8">회의록작성</button>';
export const attendeeHTML =  {
	infoCon : '<li></li>',
	infoBody : {
		infoBtn : '<div class="infoLinkBtn"></div>',
		name : '<div class="userName ellipsis"></div>',
		sign : '<div class="attendSign"></div>',
		attendBtn : '<div class="switchBtn switchOff" title="참석/불참"></div>'
	},
	assistantBody : {
		nameInput : '<input type="text" class="assistantBtn" placeholder="클릭하여 진행자를 지정하세요" readonly>',
		assignBtn : '<div class="btn-add assistantBtn margin-left-4 margin-right-auto" title="보조 진행자 지정"></div>'
	}
}
export const thumbHTML = {
		imgCon : '<li></li>',
		img : '<img>',
		menu: {
			con: '<div class="thumbMenu"></div>',
			icon: '<i class="fas fa-ellipsis-v"></i>'
		}
	}

export const thumbMenuHTML = {
	overlay: '<div class="overlay"></div>',
	base: '<div class="menuToolDiv thumbMenuDiv"></div>',
	con: '<div class="subDiv"></div>',
	item: '<button type="button" class="item"></button>'
}
export const fileHTML = {
		li : '<li class="align-center"></li>',
		span : '<span></span>',
		option : '<option></option>',
		icon : {
			cog : '<i class="fas fa-cog fa-spin text-center colorBlue my-auto"></i>',
			triangle : '<i class="fas fa-exclamation-triangle colorRed my-auto"></i>',
		}
	}