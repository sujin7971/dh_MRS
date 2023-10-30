export * from './dom_module.js';
/**
 * 공통으로 사용할 유틸 함수 모음.
 * 
 * @author mckim
 * @version 1.0
 * @since 6.0
 * @see 이 모듈 또는 클래스의 코드는 함부로 수정하거나 변경해서는 안 됩니다. 만약 이 모듈이나 클래스에 기능을 추가하거나 수정이 필요하다면, 개발1팀에 문의하시기 바랍니다.
 */
import * as Dom from '/resources/core-assets/essential/dom_module.js';
/**
 * 주어진 문자열을 카멜 케이스로 변환합니다.
 *
 * @param {string} str - 변환할 문자열입니다.
 * @returns {string} 카멜 케이스로 변환된 문자열을 반환합니다.
 */
export const camelize = (str) => {
	return str.replace(/(?:^\w|[A-Z]|\b\w|\s+)/g, (match, index) => {
		if (+match === 0) return ""; // or if (/\s+/.test(match)) for white spaces
		return index === 0 ? match.toLowerCase() : match.toUpperCase();
	});
}

/* 입력 길이 제한 */
export const acceptWithinLength = (input, event = {}) => {
	if (input.value.length >= input.maxLength) {
		event.returnValue = false;
	}
}

/* 공백문자 입력 제한 */
export const acceptNonblank = (input, event = {}) => {
	if (input.value.length >= input.maxLength) {
		event.returnValue = false;
	}
	input.value = input.value.replace(" ", "");
}

/* 연락처 길이 및 숫자외 문자 입력 제한 */
export const acceptNumber = (input, event = {}) => {
	if (input.value.length >= input.maxLength) {
		event.returnValue = false;
	}
	input.value = input.value.replace(/[^0-9]/g, '').replace(/(\..*)\./g, '$1');
	input.value = input.value.replace(" ", "");
}

/* 숫자,특수문자 입력 제한 */
export const acceptText = (input, event = {}) => {
	if (input.value.length >= input.maxLength) {
		event.returnValue = false;
	}
	input.value = input.value.replace(/[0-9\{\}\[\]\/?.,;:|\)*~`!^\-_+┼<>@\#$%&\'\"\\\(\=]/gi, '');
	input.value = input.value.replace(" ", "");
}

/* 특수문자 입력 제한 */
export const acceptExcludeSpecial = (input, event = {}) => {
	if (input.value.length >= input.maxLength) {
		event.returnValue = false;
	}
	input.value = input.value.replace(/[ \{\}\[\]\/?.,;:|\)*~`!^\-_+┼<>@\#$%&\'\"\\\(\=]/gi, '');
}

/* 이메일 형식 입력 제한 */
export const acceptEmail = (input, event = {}) => {
	if (input.value.length >= input.maxLength) {
		event.returnValue = false;
	}
	input.value = input.value.replace(/[\{\}\[\]\/?,;:|\)*~`!^\-_+┼<>\#$%&\'\"\\\(\=]/gi, '');
	input.value = input.value.replace(" ", "");
}

/* 한글 입력값 제거 */
export const removeHangulInput = (input, event = {}) => {
	if (input.value.length >= input.maxLength) {
		event.returnValue = false;
	}
	input.value = input.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
}
/**
 * 입력 요소의 값에서 콤마를 허용하며, 콤마를 제외한 숫자만을 유지합니다.
 * 또한 입력 요소의 최대 길이를 초과하는 경우 입력을 막습니다.
 *
 * @param {HTMLInputElement} input - 입력 요소입니다.
 */
export const acceptNumberWithComma = (input) => {
	input.value = input.value.replace(/,/g, ''); // remove existing commas
	if (input.value.length >= input.maxLength) {
		event.returnValue = false;
	}
	input.value = input.value.replace(/[^0-9]/g, '').replace(/(\..*)\./g, '$1');
	input.value = input.value.replace(" ", "");
	input.value = input.value.replace(/\B(?=(\d{3})+(?!\d))/g, ','); // add new commas
}
/**
 * 콤마로 구분된 숫자 문자열에서 콤마를 제거한 값을 반환합니다.
 *
 * @param {string} value - 콤마로 구분된 숫자 문자열입니다.
 * @returns {string} 콤마가 제거된 숫자 문자열입니다.
 */
export const removeComma = (value) => {
	return value.replace(/,/g, '');
}
/**
 * 주어진 전화번호를 형식에 맞게 변환하여 반환합니다.
 * 전화번호 형식은 xxx-xxxx-xxxx 입니다.
 *
 * @param {string} phoneNumber - 형식을 적용할 전화번호 문자열입니다.
 * @returns {string} 형식이 적용된 전화번호 문자열입니다.
 */
export const formatPhoneNumber = (phoneNumber) => {
	// 정규식을 사용하여 "-"를 추가한 형식으로 변형
	return phoneNumber.replace(/(\d{2,3})(\d{3,4})(\d{4})/, '$1-$2-$3');
}
/**
 * 숫자로만 이루어진 전화번호 문자열을 "-"을 추가하여 분할하여 배열로 반환합니다.
 *
 * @param {string} phoneNumber - 분할할 전화번호 문자열입니다.
 * @returns {string[]} "-"을 추가하여 분할된 전화번호 부분으로 이루어진 배열입니다.
 *                     분할이 제대로 이루어지지 않았을 경우 빈 배열을 반환합니다.
 */
export const splitPhoneNumber = (phoneNumber) => {
	const chunks = phoneNumber.match(/(\d{2,3})(\d{3,4})(\d{4})/);
	if (chunks) {
		return chunks.slice(1);
	} else {
		return [];
	}
}
/**
 * 입력된 input 요소의 값의 길이를 형식에 맞게 표시합니다.
 * 현재 길이와 최대 길이를 selector를 통해 전달받은 element 내에 표시하며,
 * 형식은 기본적으로 `${nowLength}/${maxLength}` 형태입니다.
 * 템플릿 리터럴 형식을 사용하여 커스텀 형식을 지정할 수 있습니다.
 *
 * @param {HTMLInputElement} $input - 길이를 표시할 input 요소입니다.
 * @param {string} selector - 길이를 표시할 element의 선택자입니다.
 * @param {string} [format] - 길이 표시 형식입니다. 기본값은 `${nowLength}/${maxLength}`입니다.
 */
export const displayInputLength = ($input, selector, format) => {
	if (!$input || !$input.name) {
		console.error('Input not found:', $input);
		return;
	}
	const name = $input.name;
	const maxLength = $input.maxLength;
	const nowLength = $input.value.length;
	const $length = Dom.getElement(selector) || Dom.getElement(`[data-length=${name}]`);
	if (!$length) {
		console.error('Element not found:', selector);
		return;
	}
	$length.innerText = format ? format : `${nowLength}/${maxLength}`;
}

/**
 * 주어진 값이 비어 있는지 여부를 판별합니다.
 *
 * @param {*} v - 판별할 값입니다.
 * @returns {boolean} 값이 비어있는 경우 true, 그렇지 않은 경우 false를 반환합니다.
 */
export const isEmpty = (v) => {
	if (typeof v === 'undefined') {
		return true;
	}
	if (v === null) {
		return true;
	}
	if (typeof v === 'object' && Object.keys(v).length === 0) {
		return true;
	}
	if (Array.isArray(v) && v.length === 0) {
		return true;
	}
	if (typeof v === 'string' && v.trim().length === 0) {
		return true;
	}
	return false;
}

/**
 * 랜덤 문자열 UUID를 생성하여 반환합니다. 길이를 지정하지 않은 경우 16개의 문자열 조합을 생성합니다.
 *
 * @param {number} [length=16] - 생성할 UUID의 길이입니다.
 * @returns {string} 생성된 UUID 문자열입니다.
 */
export const getUUID = (length = 16) => {
	if (length < 1 || length > 16) {
		length = 16;
	}
	const uuid = ([1e7] + -1e3 + -4e3 + -8e3 + -1e11).replace(/[018]/g, (c) =>
		(c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
	);
	return uuid.substring(0, length);
}

/**
 * 파일 크기를 식별하기 쉬운 형태로 변환하여 반환합니다.
 *
 * @param {number} size - 비트 형식의 파일 크기입니다.
 * @returns {string} 규격화된 파일 크기 형식을 반환합니다.
 */
export const getReadableFileSize = (size) => {
	if (size == 0) {
		return 0;
	}
	const s = ['Byte', 'KB', 'MB', 'GB', 'TB', 'PB'];
	const e = Math.floor(Math.log(size) / Math.log(1024));
	return (size / Math.pow(1024, e)).toFixed(2) + " " + s[e];
}

/**
 * 대상 문자열에서 XSS 방지를 위해 변형된 특수 문자를 원래대로 복구합니다.
 *
 * @param {string} str - 변형된 특수 문자가 포함된 문자열입니다.
 * @returns {string} 특수 문자가 복구된 문자열입니다.
 */
export const unescape = (str) => {
	if (typeof str === "string") {
		return str.replace(/&amp;/g, "&").replace(/&lt;/g, "<").replace(/&gt;/g, ">").replace(/&quot;/g, '"').replace(/&#39;/g, "'");
	} else {
		return str;
	}
}

/**
 * 주어진 Element에 스크롤바가 있는지 판별합니다.
 *
 * @param {Element} $ele - 스크롤바 유무를 확인할 Element입니다.
 * @returns {boolean} 스크롤바가 있는 경우 true, 없는 경우 false를 반환합니다.
 */
export const isScrollbarVisible = ($ele) => {
	return $ele.scrollHeight > $ele.clientHeight;
}

/**
 * 서버 연결 상태를 체크합니다.
 *
 * @returns {Promise<boolean>} 서버에 연결 가능한 경우 true, 아닌 경우 false를 반환하는 Promise 객체입니다.
 */
export const isServerOnline = async () => {
	try {
		const response = await fetch('/api/common/server-status');
		return response.ok;
	} catch (error) {
		return false;
	}
}

/**
 * 클라이언트 네트워크 상태를 체크합니다.
 *
 * @returns {boolean} 네트워크에 연결된 상태인 경우 true, 아닌 경우 false를 반환합니다.
 */
export const isClientOnline = () => {
	return window.navigator.onLine;
}

/**
 * 주어진 요소에 수직 스크롤 이벤트를 설정하고,
 * 스크롤이 최하단에 도달할 때 콜백 함수를 호출합니다.
 *
 * @param {HTMLElement} element - 스크롤 이벤트를 적용할 요소입니다.
 * @param {Function} callback - 스크롤 최하단 도달 시 호출할 콜백 함수입니다.
 */
export const setVerticalScroll = (element, callback) => {
	element.prevScroll = 0;
	element.onscroll = async () => {
		const scrollHeight = element.scrollHeight;
		const maxScrollY = element.offsetHeight;
		const currentScrollY = element.scrollTop;
		if (maxScrollY + currentScrollY >= scrollHeight && currentScrollY - element.prevScroll > 0) {
			callback?.();
		}
		element.prevScroll = currentScrollY;
	}
	element.unsetVerticalScroll = () => {
		element.onscroll = null;
	}
}
/**
 * 주어진 객체로부터 FormData를 생성하여 반환합니다.
 *
 * @param {Object} values - key와 value로 구성된 객체입니다.
 * @returns {FormData} 생성된 FormData 객체입니다.
 */
export const createFormData = (values = {}) => {
	const formData = new FormData();
	const formValues = values;
	for (let name in formValues) {
		const value = formValues[name];
		if (value !== null) {
			if (Array.isArray(value)) {
				value.forEach(item => formData.append(name, item));
			} else {
				formData.append(name, value);
			}
		}
	}
	return formData;
}

/**
 * 주어진 프로미스를 제한 시간 내에 실행하는 함수입니다.
 * 만약 프로미스가 지정된 시간 내에 해결되지 않거나 거부되지 않으면,
 * 'Request timed out'라는 메시지를 가진 에러가 throw됩니다.
 *
 * @param {Promise} promise - 실행할 프로미스입니다.
 * @param {number} [timeout=3000] - 타임아웃 기간(밀리초)입니다. 기본값은 3000ms(3초)입니다.
 * @returns {Promise} 주어진 프로미스가 해결 또는 거부되거나 타임아웃이 발생한 경우에 대한 프로미스를 반환합니다.
 *                    타임아웃이 발생한 경우 에러와 함께 거부됩니다.
 */
export const promiseWithTimeout = (promise, timeout = 3000) => {
	let timeoutId;
	const timeoutPromise = new Promise((_, reject) => {
		timeoutId = setTimeout(() => {
			reject(new Error('Request timed out'));
		}, timeout);
	});

	return Promise.race([promise, timeoutPromise]).finally(() => {
		clearTimeout(timeoutId);
	});
};
/**
 * target 객체의 속성을 source 객체의 해당 속성으로 교체합니다.
 * target과 source 중 하나라도 비어있는 경우, 함수는 아무런 동작도 수행하지 않습니다.
 *
 * @param {object} target - 속성이 교체될 대상 객체입니다.
 * @param {object} source - 속성을 제공하는 객체입니다.
 */
export const replaceProperties = (target, source) => {
	if (isEmpty(target) || isEmpty(source)) {
		return;
	}
	for (const key in source) {
		if (target.hasOwnProperty(key) || target[key]) {
			Object.assign(target, { [key]: source[key] });
		}
	}
}

/**
 * html2canvas와 jsPDF 라이브러리를 사용하여 페이지의 특정 영역을 PDF 파일로 변환하여 반환해주는 함수입니다.
 * 두 라이브러리가 전역 객체에서 찾을 수 없는 경우, 에러를 발생시킵니다.
 * @async
 * @param {HTMLElement[]} fragmentList - PDF로 변환할 DOM 요소들의 배열입니다.
 * @param {object} options - PDF 변환 옵션을 담은 객체입니다.
 * - scale: 화면을 그릴 때 사용할 해상도입니다. 높을수록 세부적인 요소를 더 잘 캡처할 수 있지만, 더 많은 컴퓨터 자원을 사용합니다. 기본값은 2입니다.
 * - nextPageElements: 새 페이지에서 시작하도록 강제할 DOM 요소들의 배열입니다.
 * @throws {Error} jsPDF 라이브러리를 찾을 수 없을 때
 * @throws {Error} html2canvas 라이브러리를 찾을 수 없을 때
 * @returns {jsPDF} jsPDF 인스턴스를 반환합니다. 이 인스턴스는 다양한 형식으로 변환될 수 있습니다.
 */
export const saveAsPdf = async (fragmentList = [], options = {}) => {
	const {
		scale = 2,
		nextPageElements = []
	} = options;
	const jsPDF = window.jsPDF || window.jspdf.jsPDF;
	const html2canvas = window.html2canvas;
	if (!jsPDF) {
		throw new Error("jsPDF를 찾을 수 없습니다.");
	}
	if (!html2canvas) {
		throw new Error("html2canvas를 찾을 수 없습니다.");
	}
	async function generateCanvas(i, fragmentElement, scale = 2) {
		const pdfWidth = fragmentElement.offsetWidth * 0.2645; // px -> mm로 변환
		const pdfHeight = fragmentElement.offsetHeight * 0.2645;
		const heightCalc = (PAGE_WIDTH * pdfHeight) / pdfWidth; // 비율에 맞게 높이 조절

		const canvas = await html2canvas(fragmentElement, { scale: scale });
		const img = canvas.toDataURL('image/png'); // 이미지 형식 지정

		return { num: i, element: fragmentElement, image: img, height: heightCalc };
	}
	const PAGE_WIDTH = 200, // 너비(mm) (a4에 맞춤)
		PAGE_HEIGHT = 297, // 높이(mm) (a4에 맞춤)
		PADDING = 5; //상하좌우 여백(mm)
	const doc = new jsPDF("p", "mm", "a4");

	const images = await Promise.all(fragmentList
		.map(($ele, i) => generateCanvas(i, $ele, scale))
	);

	const sorted = images.sort((a, b) => (a.num < b.num ? -1 : 1));
	let curHeight = PADDING; // 위 여백 (이미지가 들어가기 시작할 y축)
	sorted.forEach(entry => {
		const {
			element,
			image,
			height,
		} = entry;
		if (curHeight + height > PAGE_HEIGHT - PADDING * 2 || nextPageElements.includes(element)) {
			// a4 높이에 맞게 남은 공간이 이미지높이보다 작을 경우 페이지 추가
			doc.addPage(); // 페이지를 추가함
			curHeight = PADDING; // 이미지가 들어갈 y축을 초기 여백값으로 초기화
		}
		doc.addImage(image, 'jpeg', PADDING, curHeight, PAGE_WIDTH, height); // 이미지 넣기
		curHeight += height; // y축 = 기존y축 + 새로들어간 이미지 높이
	})
	return doc;
}

/**
 * 두 개의 배열을 특정 기준에 따라 병합하고, 새로운 배열을 반환하는 함수입니다.
 * comparator 인자를 통해 병합 기준을 전달받으며, 이 인자가 단순 문자열이면 두 배열의 객체가 동일한 속성명과 값을 가진 경우 병합합니다.
 * comparator가 함수인 경우, 이 함수는 두 배열의 객체를 인자로 받아, true를 반환하면 해당 객체들을 병합합니다.
 * includeUnmatched 인자는 두 번째 배열의 객체 중 첫 번째 배열의 객체와 병합되지 않은 남은 객체를 반환값에 포함할지를 결정합니다.
 * @param {Array} baseArray - 병합될 기준이 되는 배열입니다.
 * @param {Array} provideArray - 기준 배열에 병합될 대상 배열입니다.
 * @param {(string|function)} comparator - 병합 기준이 될 문자열 또는 함수입니다.
 * - 문자열인 경우: 이 속성값이 두 객체에서 동일할 경우 병합합니다.
 * - 함수인 경우: 두 객체를 인자로 받아, true를 반환하면 병합합니다.
 * @param {boolean} includeUnmatched - 병합되지 않은 두 번째 배열의 객체를 반환값에 포함할지의 여부입니다.
 * @returns {Array} 병합된 새 배열을 반환합니다.
 */
export const mergeArraysByComparator = (baseArray, provideArray, comparator, includeUnmatched = false) => {
	if (typeof comparator === 'string') {
		// comparator가 문자열인 경우 해당 속성명을 기준으로 병합
		const prop = comparator; // 기존 comparator 값을 저장
  		comparator = (obj1, obj2) => obj1[prop] === obj2[prop];
	} else if (typeof comparator !== 'function') {
		// comparator가 함수가 아니면 기본적으로 모든 객체를 병합
		comparator = () => true;
	}

	const mergedArray = [];

	for (const obj1 of baseArray) {
		let found = false;
		for (const obj2 of provideArray) {
			if (comparator(obj1, obj2)) {
				// comparator 함수가 true를 반환하는 경우 병합
				mergedArray.push({ ...obj1, ...obj2 });
				found = true;
				break;
			}
		}
		if (!found) {
			mergedArray.push(obj1);
		}
	}

	// includeUnmatched가 true인 경우 provideArray에만 있는 객체들을 병합하지 않고 그대로 추가
	if (includeUnmatched) {
		for (const obj2 of provideArray) {
			let found = false;
			for (const obj1 of baseArray) {
				if (comparator(obj1, obj2)) {
					found = true;
					break;
				}
			}
			if (!found) {
				mergedArray.push(obj2);
			}
		}
	}
	return mergedArray;
}