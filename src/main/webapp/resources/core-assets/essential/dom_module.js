/**
 * DOM 제어 함수 모음.
 * 
 * @author mckim
 * @version 1.0
 * @since 6.0
 * @see 이 모듈 또는 클래스의 코드는 함부로 수정하거나 변경해서는 안 됩니다. 만약 이 모듈이나 클래스에 기능을 추가하거나 수정이 필요하다면, 개발1팀에 문의하시기 바랍니다.
 */
import * as Util from '/resources/core-assets/essential/util_module.js';
/** document객체를 캐싱  */
const $DOC = document;

/* 클래스가 적용된 ELEMENT 생성 및 반환 */
export const createElement = (name, ...classList) => {
	const $elem = $DOC.createElement(name);
	addClass($elem, ...classList);
	return $elem;
}

/* 단순 문자열 텍스트를 ELEMENT로 변환 */
export const createTextNode = (text) => {
	const $elem = $DOC.createTextNode(text);
	return $elem;
}

/* DOM구조를 가진 텍스트를 ELEMENT로 변환 */
export const createFromString = (str) => {
	const $template = createElement("div");
	$template.innerHTML = str.trim();
	return $template.firstChild;
}
/* input ELEMENT 생성 및 반환 */
export const createInput = (options = {}) => {
	const {
		value = "",
		type = "text",
		disabled = false,
		readonly = false,
	} = options;
	const $input = createElement("input");
	$input.type = type;
	$input.value = value;
	$input.disabled = disabled;
	$input.setAttribute("readonly", readonly);
	return $input;
}
/* 선택요소에 기본값 표시를 위해 사용할 option Element를 생성 */
export const createDefaultOption = (options = {}) => {
	const {
		value = null,
		label = "선택",
		selectable = false
	} = options;
	const $label = createElement("option");
	$label.value = (value) ? value : "";
	$label.innerHTML = label;
	if(!selectable){
		$label.setAttribute("disabled", !selectable);
		$label.setAttribute("hidden", !selectable);
	}
	$label.setAttribute("selected", true);
	return $label;
}
export const createOption = (options = {}) => {
	const {
		value = null,
		label = "선택"
	} = options;
	const $label = createElement("option");
	$label.value = (value) ? value : "";
	$label.innerHTML = label;
	return $label;
}
/* 해당 ELEMENT의 클래스 추가 */
export const addClass = (selector, ...classList) => {
	const $elem = getElement(selector);
	for (const className of classList) {
		if (Util.isEmpty(className)) {
			continue;
		}
		$elem?.classList.add(className);
	}
	return $elem;
}

/* 해당 ELEMENT의 클래스 삭제 */
export const removeClass = (selector, ...classList) => {
	const $elem = getElement(selector);
	for (const className of classList) {
		if (Util.isEmpty(className)) {
			continue;
		}
		$elem?.classList.remove(className);
	}
	return $elem;
}
/* 해당 ELEMENT에 클래스 포함 여부 판별 */
export const hasClass = (selector, className) => {
	const $elem = getElement(selector);
	if (!$elem || Util.isEmpty(className)) {
		return false;
	}
	return $elem.classList.contains(className);
}

/* 해당 쿼리를 만족하는 DOM ELEMENT 반환 */
export const getElement = (selector) => {
	return selector instanceof HTMLElement ? selector : $DOC.querySelector(selector);
}

/* 해당 쿼리를 만족하는 모든 DOM ELEMENT 반환 */
export const getElementAll = (selector) => {
	return selector instanceof HTMLElement ? selector : $DOC.querySelectorAll(selector);
}
/* Select요소가 value가 설정된 option을 가지고있는지 판별 */
export const isSelectWithOptionExists = (selector, value) => {
	const $select = getElement(selector);
	if (!$select || $select.nodeName.toLowerCase() !== 'select') {
		return false;
	}
	const options = $select.querySelectorAll('option');
	for (let i = 0; i < options.length; i++) {
		if (options[i].value === String(value)) {
			return true;
		}
	}
	return false;
}

const slideUp = ($target, duration = 500) => {
	$target.style.transitionProperty = 'height, margin, padding';
	$target.style.transitionDuration = duration + 'ms';
	$target.style.boxSizing = 'border-box';
	$target.style.height = $target.offsetHeight + 'px';
	$target.offsetHeight;
	$target.style.overflow = 'hidden';
	$target.style.height = 0;
	$target.style.paddingTop = 0;
	$target.style.paddingBottom = 0;
	$target.style.marginTop = 0;
	$target.style.marginBottom = 0;
	return new Promise((resolve, reject) => {
		window.setTimeout(() => {
			$target.style.display = 'none';
			$target.style.removeProperty('height');
			$target.style.removeProperty('padding-top');
			$target.style.removeProperty('padding-bottom');
			$target.style.removeProperty('margin-top');
			$target.style.removeProperty('margin-bottom');
			$target.style.removeProperty('overflow');
			$target.style.removeProperty('transition-duration');
			$target.style.removeProperty('transition-property');
			resolve();
		}, duration);
	});
}
const slideDown = ($target, duration = 500) => {
	$target.style.removeProperty('display');
	let display = window.getComputedStyle($target).display;

	if (display === 'none')
		display = 'block';

	$target.style.display = display;
	let height = $target.offsetHeight;
	$target.style.overflow = 'hidden';
	$target.style.height = 0;
	$target.style.paddingTop = 0;
	$target.style.paddingBottom = 0;
	$target.style.marginTop = 0;
	$target.style.marginBottom = 0;
	$target.offsetHeight;
	$target.style.boxSizing = 'border-box';
	$target.style.transitionProperty = "height, margin, padding";
	$target.style.transitionDuration = duration + 'ms';
	$target.style.height = height + 'px';
	$target.style.removeProperty('padding-top');
	$target.style.removeProperty('padding-bottom');
	$target.style.removeProperty('margin-top');
	$target.style.removeProperty('margin-bottom');
	return new Promise((resolve, reject) => {
		window.setTimeout(() => {
			$target.style.removeProperty('height');
			$target.style.removeProperty('overflow');
			$target.style.removeProperty('transition-duration');
			$target.style.removeProperty('transition-property');
			resolve();
		}, duration);
	});
}
/**
 * 해당 요소에 슬라이드 토글 효과를 적용합니다.
 * @param {string|HTMLElement} selector - 슬라이드 효과를 적용할 요소의 CSS 선택자입니다.
 * @param {number|string} [duration=200] - 슬라이드 효과의 지속 시간을 설정합니다.
 * 정수값인 경우 밀리초 단위의 지속 시간을 나타냅니다.
 * 'fast'로 설정하면 200ms, 'slow'로 설정하면 600ms의 지속 시간을 가집니다.
 * 기본값은 200ms입니다.
 * @param {string|HTMLElement} [toggleElement=selector] - 슬라이드 효과를 호출하는 요소의 CSS 선택자입니다.
 * 해당 요소 외의 Body내 다른 Element에 click이벤트가 발생한 경우, 자동으로 slideUp을 실행합니다.
 * @returns {Promise} - 슬라이드 효과의 완료를 나타내는 Promise 객체입니다.
*/
export const slideToggle = (selector, options = {}) => {
	let {
		duration = 200,
		toggleElement = selector,
		position = "absolute",
	} = options;
	const $target = getElement(selector);
	$target.style.position = position;
	const $toggleElement = getElement(toggleElement);
	duration = Number.isInteger(duration) ? duration : ((str) => {
		switch (str) { case 'fast': return 200; case 'slow': return 600; default: return 400; }
	})(duration);
	const autoSlideUp = (event) => {
		const $eventElement = event.target;
		if ($eventElement !== $toggleElement 
		&& !$toggleElement.contains($eventElement) 
		&& !$toggleElement.isSameNode($eventElement) 
		&& !$target.contains($eventElement)) {
			// 대상 요소와 관련이 없는 경우 slideUp 실행
			slideUp($target, duration);
			document.body.removeEventListener('click', autoSlideUp);
		}
	}
	$target.slideDown = () => {
		slideDown($target, duration);
	}
	$target.slideUp = () => {
		slideUp($target, duration);
	}
	if (window.getComputedStyle($target).display === 'none') {
		document.body.addEventListener('click', autoSlideUp);
		return slideDown($target, duration);
	} else {
		document.body.removeEventListener('click', autoSlideUp);
		return slideUp($target, duration);
	}
}

/**
 * 주어진 경로의 자바스크립트 파일을 동적으로 로드합니다.
 *
 * @param {string} path - 로드할 자바스크립트 파일의 경로입니다.
 */
export const loadJavaScriptDynamically = (path) => {
	const script = document.createElement('script');
	
	script.src = path;
	
	document.head.appendChild(script);
}

/**
 * 주어진 경로의 CSS 파일을 동적으로 로드합니다.
 *
 * @param {string} path - 로드할 CSS 파일의 경로입니다.
 */
export const loadCssDynamically = (path) => {
	const link = document.createElement('link');
	
	link.rel = 'stylesheet';
	link.type = 'text/css';
	link.href = path;
	
	document.head.appendChild(link);
}