/**
 * Observer 클래스는 데이터 변경을 추적하려는 객체가 구현해야 하는 인터페이스를 정의합니다.
 * Observer의 서브 클래스에서는 update 메서드를 구현하여 데이터 변경에 대응하는 로직을 작성해야 합니다.
 */
class Observer {
	/**
	 * 데이터가 변경될 때 호출되는 메서드입니다.
	 * @param {Object} data - 변경된 데이터
	 */
	update(data) {
		// 서브 클래스에서 구현
	}
}

/**
 * DataSubject 클래스는 데이터를 관리하고 변경 사항을 Observer 객체들에게 통지하는 기능을 제공합니다.
 */
class DataSubject {
	constructor() {
		this.observers = [];
		this.state = {};
	}
	/**
	 * Observer 객체를 관찰자 목록에 추가합니다.
	 * @param {Observer} observer - Observer 클래스를 상속받은 객체
	 */
	add(observer) {
	    if (observer instanceof Observer) {
	      this.observers.push(observer);
	    } else {
	      console.error("The observer should be an instance of Observer class.");
	    }
	}
	/**
	 * Observer 객체를 관찰자 목록에서 제거합니다.
	 * @param {Observer} observer - 제거할 Observer 객체
	 */
	remove(observer) {
	    const index = this.observers.indexOf(observer);
	    if (index > -1) {
	      this.observers.splice(index, 1);
	    }
	}
	/**
	 * 관찰자들에게 변경된 데이터를 통지합니다.
	 * @param {Object} data - 변경된 데이터
	 */
	notify(data) {
	    for (const observer of this.observers) {
	      observer.update(data);
	    }
	}
	/**
	 * 현재 상태를 업데이트하고 관찰자들에게 변경 사항을 통지합니다.
	 * @param {Object} newState - 상태에 반영할 새로운 데이터
	 */
	update(newState) {
		this.state = { ...this.state, ...newState };
	    this.notify(this.state);
	}
	/**
	 * 현재 상태를 반환합니다.
	 * @return {Object} 현재 상태
	 */
	getState() {
		return this.state;
	}
}

export { DataSubject, Observer };