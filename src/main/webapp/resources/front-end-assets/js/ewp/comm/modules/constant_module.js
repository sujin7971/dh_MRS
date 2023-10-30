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
