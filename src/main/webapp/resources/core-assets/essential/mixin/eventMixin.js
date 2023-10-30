/**
 * Dom Element가 아닌 Object에 이벤트 등록/해제 및 트리거링을 통한 콜백 함수 실행 기능을 제공하는 믹스인입니다.
 * 클래스 또는 객체에 이벤트 기능을 추가하고자 할 때 아래와 같이 사용할 수 있습니다:
 * 클래스의 경우: Object.assign(TargetObject.prototype, mixin);
 * 객체의 경우: object[proto] = mixin;
 * @author mckim
 * @version 2.1
 * @see 이 모듈 또는 클래스의 코드는 함부로 수정하거나 변경해서는 안 됩니다. 만약 이 모듈이나 클래스에 기능을 추가하거나 수정이 필요하다면, 개발1팀에 문의하시기 바랍니다.
 */
const eventMixin = {
	/*
	 * 이벤트를 구독하는 메서드입니다.
	 * 다음 두 가지 패턴으로 사용할 수 있습니다:
	 * on("event", callback): 단일 이벤트를 구독
	 * on({ event: callback }): 여러 이벤트를 동시에 구독
	 * @param {string|object} eventName - 구독할 이벤트의 이름 또는 이벤트 이름과 콜백 함수를 가진 객체
	 * @param {function} [handler] - 이벤트 발생 시 실행할 콜백 함수
	 * @returns {object} 이벤트를 구독한 객체
	 */
	on(eventName, handler) {
		if (!this._eventHandlers) this._eventHandlers = {};
		if (typeof eventName === "string") {
			// 단일 이벤트 등록
			if (!this._eventHandlers[eventName]) {
				this._eventHandlers[eventName] = [];
			}
			this._eventHandlers[eventName].push(handler);
		} else if (typeof eventName === "object") {
			// 다중 이벤트 등록 { event: callback }
			for (let key in eventName) {
				let event = key;
				let callback = eventName[key];
				if (!this._eventHandlers[event]) {
					this._eventHandlers[event] = [];
				}
				this._eventHandlers[event].push(callback);
			}
		}
		return this;
	},
	/**
	* 이벤트 구독을 취소하는 메서드입니다.
	* 다음 세 가지 패턴으로 사용할 수 있습니다:
	* off("event", callback): 특정 이벤트의 특정 콜백 구독 취소
	* off("event"): 특정 이벤트의 모든 콜백 구독 취소
	* off(): 모든 이벤트의 모든 콜백 구독 취소
	* @param {string} [eventName] - 구독 취소할 이벤트의 이름
	* @param {function} [handler] - 구독 취소할 콜백 함수
	* @returns {object} 이벤트 구독 취소한 객체
	*/
	off(eventName, handler) {
		if (!this._eventHandlers) return;

		// 모든 구독 취소
		if (!eventName && !handler) {
			this._eventHandlers = {};
			return;
		}

		// 특정 이벤트의 모든 핸들러 취소
		if (eventName && !handler) {
			delete this._eventHandlers[eventName];
			return;
		}

		// 특정 이벤트의 특정 핸들러 취소
		let handlers = this._eventHandlers?.[eventName];
		if (handlers) {
			let index = handlers.indexOf(handler);
			if (index !== -1) {
				handlers.splice(index, 1);
			}
		}
		return this;
	},
	
	/**
	 * 이 메서드는 off 메서드로 대체될 예정이며, 호환성 유지를 위해 현재는 남겨져 있습니다.
	 * 특정 이벤트에 대한 모든 구독을 취소합니다.
	 * 사용 패턴: unbind("event")
	 * 
	 * @deprecated 대신 off("event") 메서드를 사용하세요.
	 *
	 * @param {string} eventName - 구독 취소할 이벤트의 이름
	 */
	unbind(...args) {
		this.off(...args);
	},
	
	/**
	* 주어진 이름과 데이터를 기반으로 이벤트를 생성하는 메서드입니다.
	* 다음 두 가지 패턴으로 사용할 수 있습니다:
	* trigger("event", data1, data2): 단일 이벤트 생성
	* trigger({ event: [data1, data2] }): 여러 이벤트 동시에 생성
	* @param {string|object} eventName - 생성할 이벤트의 이름 또는 이벤트 이름과 데이터를 가진 객체
	* @param {any} args - 이벤트 데이터
	*/
	trigger(eventName, ...args) {
		if (!this._eventHandlers?.[eventName]) {
			return; // 해당 이벤트에 대한 핸들러 없음
		}
		if (typeof eventName === "string") {
			// 단일 이벤트 트리거
			this._eventHandlers[eventName].forEach(handler => handler.apply(this, args));
		} else if (typeof eventName === "object") {
			// 다중 이벤트 트리거 { event: [data1, data2] }
			for (let key in eventName) {
				let event = key;
				let data = eventName[key];
				this._eventHandlers[event].forEach(handler => handler.apply(this, data));
			}
		}
	},
};

export default eventMixin;