var ModalService;(()=>{"use strict";var e={d:(t,s)=>{for(var o in s)e.o(s,o)&&!e.o(t,o)&&Object.defineProperty(t,o,{enumerable:!0,get:s[o]})},o:(e,t)=>Object.prototype.hasOwnProperty.call(e,t),r:e=>{"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})}},t={};(()=>{e.r(t),e.d(t,{default:()=>g});const s=e=>null==e||"object"==typeof e&&0===Object.keys(e).length||!(!Array.isArray(e)||0!==e.length)||"string"==typeof e&&0===e.trim().length,o=(e=16)=>((e<1||e>16)&&(e=16),([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g,(e=>(e^crypto.getRandomValues(new Uint8Array(1))[0]&15>>e/4).toString(16))).substring(0,e)),n=(e,t)=>{if(!s(e)&&!s(t))for(const s in t)(e.hasOwnProperty(s)||e[s])&&Object.assign(e,{[s]:t[s]})},i=document,r=(e,...t)=>{const s=i.createElement(e);return a(s,...t),s},l=e=>{const t=r("div");return t.innerHTML=e.trim(),t.firstChild},a=(e,...t)=>{const o=d(e);for(const e of t)s(e)||o?.classList.add(e);return o},d=e=>e instanceof HTMLElement?e:i.querySelector(e),c={on(e,t){if(this._eventHandlers||(this._eventHandlers={}),"string"==typeof e)this._eventHandlers[e]||(this._eventHandlers[e]=[]),this._eventHandlers[e].push(t);else if("object"==typeof e)for(let t in e){let s=t,o=e[t];this._eventHandlers[s]||(this._eventHandlers[s]=[]),this._eventHandlers[s].push(o)}return this},off(e,t){if(!this._eventHandlers)return;if(!e&&!t)return void(this._eventHandlers={});if(e&&!t)return void delete this._eventHandlers[e];let s=this._eventHandlers?.[e];if(s){let e=s.indexOf(t);-1!==e&&s.splice(e,1)}return this},unbind(...e){this.off(...e)},trigger(e,...t){if(this._eventHandlers?.[e])if("string"==typeof e)this._eventHandlers[e].forEach((e=>e.apply(this,t)));else if("object"==typeof e)for(let t in e){let s=t,o=e[t];this._eventHandlers[s].forEach((e=>e.apply(this,o)))}}};class u{static TEMPLATE='\n\t<div class="modal" data-modal="09bcbd7d">\n\t\t<div class="modalWrap">\n\t\t\t<div class="modal_content">\n\t\t\t\t<div class="modalTitle" data-modal-role="title"></div>\n\t\t\t\t<div class="modalBody" data-modal-role="message"></div>\n\t\t\t\t<div class="modalBtnDiv" data-modal-role="btn-container"></div>\n\t\t\t</div>\n\t\t</div>\n\t</div>\n\t';constructor(e){const{modalElement:t,exclusive:s=!1,autoHide:o=!0,autoDestroy:n=!0}=e;this.update({modalElement:t,exclusive:s,autoHide:o,autoDestroy:n})}update(e){const{modalElement:t=this.modalElement,exclusive:s=this.exclusive,autoHide:o=this.autoHide,autoDestroy:n=this.autoDestroy}=e;this.exclusive=s,this.autoHide=o,this.autoDestroy=n,this.modalElement=t,this.id=t.dataset.modal,this.setButton()}show(){if(!this.resolve)return this.payload=void 0,this.modalElement.style.display="block",this.trigger("show"),new Promise(((e,t)=>{this.resolve=e,this.reject=t}))}async hide(e={}){const{action:t="AUTO",source:s="USER",status:o="SUCESS"}=e;this.modalElement.style.display="none";const n=await(this.payload?.())||null;this.trigger("hide"),this.resolve?.({action:t,status:o,source:s,timestamp:Date.now(),payload:n}),this.resolve=null,this.reject=null}setTitle(e){const t=this.modalElement.querySelector(".modalTitle");return t&&(t.innerHTML=e),this}setMessage(e){const t=this.modalElement.querySelector(".modalBody");return t&&(t.innerHTML=e),this}addButton(e){const t=this.modalElement.querySelector(".modalBtnDiv");if(!t)return this;for(const s of e.flat()){const{label:e="닫기",action:o="close",color:n="blue"}=s,i=["btn","btn-md"];switch(n){case"red":i.push("btn-red");break;case"yellow":i.push("btn-yellow");break;case"blue":i.push("btn-blue");break;default:i.push("btn-silver")}const l=r("button",...i);l.type="button",l.innerText=e,l.dataset.modalAction=o,t.appendChild(l)}return this.setButton(),this}setButton(){this.modalElement.querySelectorAll("[data-modal-action]").forEach((e=>{const t=e.dataset.modalAction?.toUpperCase()||"NONE";e.onclick=()=>{this.trigger("action",t),!0===this.autoHide&&this.hide({soure:"AUTO",action:t})}}))}setPayload(e){this.payload="function"==typeof e?e:()=>e}isExclulsive(){return this.exclusive}getId(){return this.id}getElement(){return this.modalElement}displayButton(...e){this.buttons.forEach((t=>{e.includes(t.name)&&(t.style.display="")}))}concealButton(...e){this.buttons.forEach((t=>{e.includes(t.name)&&(t.style.display="none")}))}enableButton(...e){this.buttons.forEach((t=>{e.includes(t.name)&&(t.disabled=!1)}))}disableButton(...e){this.buttons.forEach((t=>{e.includes(t.name)&&(t.disabled=!0)}))}destroy(){this.modalElement?.remove?.(),this.off?.()}getType(){return"NORMAL"}}Object.assign(u.prototype,c);class h extends u{static instance;static TEMPLATE='\n\t<div class="modal" data-modal="09bcbd7d">\n\t\t<div class="modalWrap">\n\t\t\t<div class="modal_content flex-row align-items-center">\n\t\t\t\t<div class="modalIcon overflow-hidden" data-modal-role="icon-container"></div>\n\t\t\t\t<div class="modalBody" data-modal-role="message"></div>\n\t\t\t</div>\n\t\t</div>\n\t</div>\n\t';constructor(e){if(h.instance)return h.instance.update(e),h.instance;super(e),h.instance=this}setIcon(e){const t=(e=>"roller"===e?'<div class="lds-roller"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div>':'<div class="lds-ring"><div></div><div></div><div></div><div></div></div>')(e),s=this.modalElement.querySelector("[data-modal-role=icon-container]"),o=l(t);s.appendChild(o)}process(e){return this.asyncFunction=e,this}getType(){return"LOADING"}}const m=window.ModalManager||((v={add(e){if(!(e instanceof u))throw new Error("Modal 인스턴스만 추가할 수 있습니다.");this.delete(e.getId()),this.modalCollection.add(e),e.on("hide",(()=>{!0===e.autoDestroy&&(e.destroy(),m.delete(e))})),e.on("show",(()=>{"NORMAL"===e.getType()&&this.getAll().forEach((e=>{"LOADING"===e.getType()&&e.hide({action:"SYSTEM",source:"MODAL"})})),e.isExclulsive()&&this.getAll().forEach((e=>{e.hide({action:"SYSTEM",source:"MODAL"})}))}))},delete(e){if(e instanceof u)this.modalCollection.delete(e);else{const t=this.get(e);this.modalCollection.delete(t)}},get(e){return Array.from(this.modalCollection).find((t=>t.id===e))},getAll(){return Array.from(this.modalCollection)},getLoadingModal(){return Array.from(this.modalCollection).find((e=>e instanceof h))},hasExclusive(){return Array.from(this.modalCollection).some((e=>!0===e.isExclulsive()))}}).modalCollection=new Set,document.addEventListener("DOMContentLoaded",(()=>{var e;((e="[data-modal]")instanceof HTMLElement?e:i.querySelectorAll(e)).forEach((e=>{const t=new u({modalElement:e,autoDestroy:!1});v.add(t)}))})),window.ModalManager=v,v);var v;class b{static Model=u;constructor(e={}){const{exclusive:t=!1,autoHide:s=!0,autoDestroy:n=!0}=e;this.builderContext={},this.builderContext.exclusive=t,this.builderContext.autoHide=s,this.builderContext.autoDestroy=n,this.builderContext.buttons=[];const i=l(b.Model.TEMPLATE);return this.builderContext.modalElement=i,i.dataset.modal=o(8),this}setTitle(e){if(!this.builderContext)throw new Error("속성을 설정하기 전에 'builder' 메서드를 호출해야 합니다.");return this.builderContext.title=e,this}setMessage(e){if(!this.builderContext)throw new Error("속성을 설정하기 전에 'builder' 메서드를 호출해야 합니다.");return this.builderContext.message=e,this}addButton(...e){if(!this.builderContext)throw new Error("속성을 설정하기 전에 'builder' 메서드를 호출해야 합니다.");return this.builderContext.buttons.push(...e.flat()),this}build(){if(!this.builderContext)throw new Error("먼저 'builder' 메서드를 호출한 다음에 'build'를 호출해야 합니다.");const{title:e="",message:t="",buttons:s=[]}=this.builderContext;d("body").appendChild(this.builderContext.modalElement);const o=new b.Model(this.builderContext);return o.setTitle(e),o.setMessage(t),o.addButton(s),m.add(o),o}}class f extends b{static Model=h;constructor(e={}){super({exclusive:!1,autoHide:!0,autoRemove:!0});const t=l(f.Model.TEMPLATE);return this.builderContext.modalElement=t,t.dataset.modal=o(8),this}setTitle(){return this}setIcon(e){if(!this.builderContext)throw new Error("속성을 설정하기 전에 'builder' 메서드를 호출해야 합니다.");return this.builderContext.icon=e,this}addButton(){return this}build(){if(!this.builderContext)throw new Error("Must call 'builder' before 'build'");const{message:e="",icon:t="roller"}=this.builderContext;d("body").appendChild(this.builderContext.modalElement);const s=new f.Model(this.builderContext);return s.setMessage(e),s.setIcon(t),m.add(s),s}}const y={ModalBuilder:{Model:class extends u{setMessage(e){const t=this.modalElement.querySelector("[data-modal-role=message]");if(t.innerHTML="",e){const s=r("div","commonMent");s.innerHTML=e,t.appendChild(s)}return this}}},ModalService:{deleteBuilder:(e={})=>new b(e).setTitle("확인").setMessage("삭제하시겠습니까?").addButton([{label:"아니오",action:"no",color:"silver"},{label:"삭제",action:"yes",color:"red"}]),errorBuilder(e){const{status:t,responseText:s,message:o,detail:n,error:i}=e,r=o||(e=>{switch(e){case 400:return"400 Bad Request";case 401:return"401 Unauthorized";case 403:return"403 Forbidden";case 404:return"404 Not Found";case 409:return"409 Conflict";case 422:return"422 Unprocessable Entity";default:return"잠시 후 다시 시도해주세요"}})(t),l=n||(e=>{switch(e){case 400:return"요청문에 오류가 있거나 서버가 요청을 이해하지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";case 401:return"인증되지 않은 사용자의 요청입니다. 로그인 페이지로 이동합니다.";case 403:return"요청에 대한 권한이 없습니다.";case 404:return"요청한 페이지를 찾을 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";case 409:return"요청한 서비스가 현재 사용 가능하지 않습니다. 다시 시도해 주세요.";case 422:return"요청에 대해 응답할 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";default:return"서버로부터 올바른 응답을 받지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요."}})(t),a=(e=>401===e)(t);return new b({...e,exclusive:a}).setTitle(r).setMessage(l).addButton([{label:"확인",action:"close",color:"silver"}])}}},p={builder:(e={})=>new b(e),infoBuilder:(e={})=>new b(e).setTitle("알림").addButton([{label:"확인",action:"CLOSE",color:"silver"}]),confirmBuilder:(e={})=>new b(e).setTitle("확인").addButton([{label:"아니오",action:"NO",color:"silver"},{label:"예",action:"YES",color:"blue"}]),deleteBuilder:(e={})=>new b(e).setTitle("확인").setMessage("삭제하시겠습니까?").addButton([{label:"아니오",action:"NO",color:"silver"},{label:"삭제",action:"YES",color:"yellow"}]),errorBuilder(e={}){const{status:t,payload:s={}}=e,o=s.message||(e=>{switch(e){case 400:return"400 Bad Request";case 401:return"401 Unauthorized";case 403:return"403 Forbidden";case 404:return"404 Not Found";case 409:return"409 Conflict";case 422:return"422 Unprocessable Entity";default:return"잠시 후 다시 시도해주세요"}})(t),n=s.errors?.pop().message||(e=>{switch(e){case 400:return"요청문에 오류가 있거나 서버가 요청을 이해하지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";case 401:return"인증되지 않은 사용자의 요청입니다. 로그인 페이지로 이동합니다.";case 403:return"요청에 대한 권한이 없습니다.";case 404:return"요청한 페이지를 찾을 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";case 409:return"요청한 서비스가 현재 사용 가능하지 않습니다. 다시 시도해 주세요.";case 422:return"요청에 대해 응답할 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";default:return"서버로부터 올바른 응답을 받지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요."}})(t),i=(e=>401===e)(t);return new b({...e,exclusive:i}).setTitle(o).setMessage(n).addButton([{label:"확인",action:"CLOSE",color:"silver"}])},loadingBuilder:(e={})=>new f(e).setMessage("잠시만 기다려주세요"),get:e=>m.get(e),getLoadingModal:()=>m.getLoadingModal(),delete(e){e instanceof u&&m.delete(e)},add(e){e instanceof u&&m.add(e)},getModalModel:()=>u},g=p;document.addEventListener("DOMContentLoaded",(()=>{(e=>{const t=document.createElement("link");t.rel="stylesheet",t.type="text/css",t.href="/resources/core-assets/essential/lime-modal/lime-modal.css",document.head.appendChild(t)})(),y&&(y.ModalService&&n(p,y.ModalService),y.ModalBuilder&&n(b,y.ModalBuilder),y.Modal&&n(u,y.Modal))}))})(),ModalService=t})();