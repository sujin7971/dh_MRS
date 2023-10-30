export const eventMixin = {
  /**
   *  이벤트 구독
   *  사용패턴: menu.on('select', function(item) { ... }
  */
  on(eventName, handler) {
    if (!this._eventHandlers) this._eventHandlers = {};
    if (!this._eventHandlers[eventName]) {
      this._eventHandlers[eventName] = [];
    }
    this._eventHandlers[eventName].push(handler);
  },

  /**
   *  구독 취소
   *  사용패턴: menu.off('select', handler)
   */
  off(eventName, handler) {
    let handlers = this._eventHandlers?.[eventName];
    if (!handlers) return;
    for (let i = 0; i < handlers.length; i++) {
      if (handlers[i] === handler) {
        handlers.splice(i--, 1);
      }
    }
  },

  /**
   *  주어진 이름과 데이터를 기반으로 이벤트 생성
   *  사용패턴: this.trigger('select', data1, data2);
   */
  trigger(eventName, ...args) {
    if (!this._eventHandlers?.[eventName]) {
      return; // no handlers for that event name
    }

    // 핸들러 호출
    this._eventHandlers[eventName].forEach(handler => handler.apply(this, args));
  }
};