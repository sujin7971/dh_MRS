/**
 * 
 */
const editTool = {
	con: '<div id="editTool"></div>',
	header: {
		con: '<div class="editToolHeader"></div>',
		paleteIcon: '<i class="fas fa-palette"></i>',
		delIcon: '<i class="fas fa-trash-alt"></i>'
	},
	palete: {
		con: '<div class="editToolProperty subDiv" style="display:none">',
		font: {
			con: '<div class="fontSizeDiv"><div class="item">크기</div></div>',
			item: '<div class="tt"></div>'
		},
		color: {
			con: '<div class="colorDiv"><div class="item">색상</div></div>',
			item: '<div class="cc"></div>'
		},
		close: {
			con: '<div class="closeBtnDiv sUp"></div>',
			icon: '<i class="fal fa-angle-up"></i>'
		}
	}
}
export const HTML = {
	editTool: editTool,
}