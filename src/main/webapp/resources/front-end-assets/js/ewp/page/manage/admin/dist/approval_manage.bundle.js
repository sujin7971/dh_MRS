(()=>{"use strict";const e=e=>null==e||"object"==typeof e&&0===Object.keys(e).length||!(!Array.isArray(e)||0!==e.length)||"string"==typeof e&&0===e.trim().length,t=(e=16)=>((e<1||e>16)&&(e=16),([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g,(e=>(e^crypto.getRandomValues(new Uint8Array(1))[0]&15>>e/4).toString(16))).substring(0,e)),a=(t,a)=>{if(!e(t)&&!e(a))for(const e in a)(t.hasOwnProperty(e)||t[e])&&Object.assign(t,{[e]:a[e]})},s=document,o=(e,...t)=>{const a=s.createElement(e);return i(a,...t),a},n=e=>{const t=o("div");return t.innerHTML=e.trim(),t.firstChild},i=(t,...a)=>{const s=r(t);for(const t of a)e(t)||s?.classList.add(t);return s},r=e=>e instanceof HTMLElement?e:s.querySelector(e),l={on(e,t){if(this._eventHandlers||(this._eventHandlers={}),"string"==typeof e)this._eventHandlers[e]||(this._eventHandlers[e]=[]),this._eventHandlers[e].push(t);else if("object"==typeof e)for(let t in e){let a=t,s=e[t];this._eventHandlers[a]||(this._eventHandlers[a]=[]),this._eventHandlers[a].push(s)}return this},off(e,t){if(!this._eventHandlers)return;if(!e&&!t)return void(this._eventHandlers={});if(e&&!t)return void delete this._eventHandlers[e];let a=this._eventHandlers?.[e];if(a){let e=a.indexOf(t);-1!==e&&a.splice(e,1)}return this},unbind(...e){this.off(...e)},trigger(e,...t){if(this._eventHandlers?.[e])if("string"==typeof e)this._eventHandlers[e].forEach((e=>e.apply(this,t)));else if("object"==typeof e)for(let t in e){let a=t,s=e[t];this._eventHandlers[a].forEach((e=>e.apply(this,s)))}}};class d{static TEMPLATE='\n\t<div class="modal" data-modal="09bcbd7d">\n\t\t<div class="modalWrap">\n\t\t\t<div class="modal_content">\n\t\t\t\t<div class="modalTitle" data-modal-role="title"></div>\n\t\t\t\t<div class="modalBody" data-modal-role="message"></div>\n\t\t\t\t<div class="modalBtnDiv" data-modal-role="btn-container"></div>\n\t\t\t</div>\n\t\t</div>\n\t</div>\n\t';constructor(e){const{modalElement:t,exclusive:a=!1,autoHide:s=!0,autoDestroy:o=!0}=e;this.update({modalElement:t,exclusive:a,autoHide:s,autoDestroy:o}),r("body").appendChild(this.modalElement)}update(e){const{modalElement:t=this.modalElement,exclusive:a=this.exclusive,autoHide:s=this.autoHide,autoDestroy:o=this.autoDestroy}=e;this.exclusive=a,this.autoHide=s,this.autoDestroy=o,this.modalElement=t,this.id=t.dataset.modal,this.setButton()}show(){if(!this.resolve)return this.payload=void 0,this.modalElement.style.display="block",this.trigger("show"),new Promise(((e,t)=>{this.resolve=e,this.reject=t}))}async hide(e={}){const{action:t="AUTO",source:a="USER",status:s="SUCESS",payload:o}=e;this.modalElement.style.display="none",this.trigger("hide");const n={action:t,status:s,source:a,timestamp:Date.now(),payload:o};o&&(n.payload=o),this.resolve?.(n),this.resolve=null,this.reject=null}setTitle(e){const t=this.modalElement.querySelector(".modalTitle");return t&&(t.innerHTML=e),this}setMessage(e){const t=this.modalElement.querySelector(".modalBody");return t&&(t.innerHTML=e),this}addButton(e){const t=this.modalElement.querySelector(".modalBtnDiv");if(!t)return this;for(const a of e.flat()){const{label:e="닫기",action:s="close",color:n="blue"}=a,i=["btn","btn-md"];switch(n){case"red":i.push("btn-red");break;case"yellow":i.push("btn-yellow");break;case"blue":i.push("btn-blue");break;default:i.push("btn-silver")}const r=o("button",...i);r.type="button",r.innerText=e,r.dataset.modalAction=s,t.appendChild(r)}return this.setButton(),this}setButton(){const e=this.modalElement.querySelectorAll("[data-modal-action]");this.buttons=e,e.forEach((e=>{const t=e.dataset.modalAction?.toUpperCase()||"NONE",a=e.dataset.modalClose||!0;e.onclick=()=>{this.trigger("action",t,e),!0===this.autoHide&&!0===a&&this.hide({soure:"AUTO",action:t})}}))}isExclulsive(){return this.exclusive}getId(){return this.id}getElement(){return this.modalElement}displayButton(...e){this.buttons.forEach((t=>{(0==e.length||e.includes(t.name))&&(t.style.display="")}))}concealButton(...e){this.buttons.forEach((t=>{(0==e.length||e.includes(t.name))&&(t.style.display="none")}))}enableButton(...e){this.buttons.forEach((t=>{(0==e.length||e.includes(t.name))&&(t.disabled=!1)}))}disableButton(...e){this.buttons.forEach((t=>{(0==e.length||e.includes(t.name))&&(t.disabled=!0)}))}destroy(){this.modalElement?.remove?.(),this.off?.()}getType(){return"NORMAL"}}Object.assign(d.prototype,l);class c extends d{static instance;static TEMPLATE='\n\t<div class="modal" data-modal="09bcbd7d">\n\t\t<div class="modalWrap">\n\t\t\t<div class="modal_content flex-row align-items-center">\n\t\t\t\t<div class="modalIcon overflow-hidden p-2" data-modal-role="icon-container"></div>\n\t\t\t\t<div class="modalBody" data-modal-role="message"></div>\n\t\t\t</div>\n\t\t</div>\n\t</div>\n\t';constructor(e){if(c.instance)return c.instance.update(e),c.instance;super(e),c.instance=this}setIcon(e="spinner"){const t=(e=>{switch(e){case"spinner":return'<div class="spinner-border text-primary"></div>';case"grow":return'<div class="spinner-grow text-primary"></div>';case"roller":return'<div class="lds-roller"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div>';default:return'<div class="lds-ring"><div></div><div></div><div></div><div></div></div>'}})(e),a=this.modalElement.querySelector("[data-modal-role=icon-container]"),s=n(t);a.appendChild(s)}process(e){return this.asyncFunction=e,this}getType(){return"LOADING"}}const u=((m={add(e){if(!(e instanceof d))throw new Error("Modal 인스턴스만 추가할 수 있습니다.");this.delete(e.getId()),this.modalCollection.add(e),e.on("hide",(()=>{!0===e.autoDestroy&&(e.destroy(),u.delete(e))})),e.on("show",(()=>{"NORMAL"===e.getType()&&this.getAll().forEach((e=>{"LOADING"===e.getType()&&e.hide({action:"SYSTEM",source:"MODAL"})})),e.isExclulsive()&&this.getAll().forEach((e=>{e.hide({action:"SYSTEM",source:"MODAL"})}))}))},delete(e){if(e instanceof d)this.modalCollection.delete(e);else{const t=this.get(e);this.modalCollection.delete(t)}},get(e){return Array.from(this.modalCollection).find((t=>t.id===e))},getAll(){return Array.from(this.modalCollection)},getLoadingModal(){return Array.from(this.modalCollection).find((e=>e instanceof c))},hasExclusive(){return Array.from(this.modalCollection).some((e=>!0===e.isExclulsive()))}}).modalCollection=new Set,document.addEventListener("DOMContentLoaded",(()=>{var e;((e="[data-modal]")instanceof HTMLElement?e:s.querySelectorAll(e)).forEach((e=>{const t=e.dataset.modal;if(!m.get(t)){const t=new d({modalElement:e,autoDestroy:!1});m.add(t)}}))})),m);var m;class p{static Model=d;constructor(e={}){const{exclusive:a=!1,autoHide:s=!0,autoDestroy:o=!0}=e;this.builderContext={},this.builderContext.exclusive=a,this.builderContext.autoHide=s,this.builderContext.autoDestroy=o,this.builderContext.buttons=[];const i=n(p.Model.TEMPLATE);return this.builderContext.modalElement=i,i.dataset.modal=t(8),this}setTitle(e){if(!this.builderContext)throw new Error("속성을 설정하기 전에 'builder' 메서드를 호출해야 합니다.");return this.builderContext.title=e,this}setMessage(e){if(!this.builderContext)throw new Error("속성을 설정하기 전에 'builder' 메서드를 호출해야 합니다.");return this.builderContext.message=e,this}addButton(...e){if(!this.builderContext)throw new Error("속성을 설정하기 전에 'builder' 메서드를 호출해야 합니다.");return this.builderContext.buttons.push(...e.flat()),this}build(){if(!this.builderContext)throw new Error("먼저 'builder' 메서드를 호출한 다음에 'build'를 호출해야 합니다.");const{title:e="",message:t="",buttons:a=[]}=this.builderContext,s=new p.Model(this.builderContext);return s.setTitle(e),s.setMessage(t),s.addButton(a),u.add(s),s}}class h extends p{static Model=c;constructor(e={}){super({exclusive:!1,autoHide:!0,autoRemove:!0});const a=n(h.Model.TEMPLATE);return this.builderContext.modalElement=a,a.dataset.modal=t(8),this}setTitle(){return this}setIcon(e){if(!this.builderContext)throw new Error("속성을 설정하기 전에 'builder' 메서드를 호출해야 합니다.");return this.builderContext.icon=e,this}addButton(){return this}build(){if(!this.builderContext)throw new Error("Must call 'builder' before 'build'");const{message:e="",icon:t="spinner"}=this.builderContext;r("body").appendChild(this.builderContext.modalElement);const a=new h.Model(this.builderContext);return a.setMessage(e),a.setIcon(t),u.add(a),a}}const g={ModalBuilder:{Model:class extends d{setMessage(e){const t=this.modalElement.querySelector("[data-modal-role=message]");if(t.innerHTML="",e){const a=o("div","commonMent");a.innerHTML=e,t.appendChild(a)}return this}}},ModalService:{deleteBuilder:(e={})=>new p(e).setTitle("확인").setMessage("삭제하시겠습니까?").addButton([{label:"아니오",action:"no",color:"silver"},{label:"삭제",action:"yes",color:"red"}]),errorBuilder(e){const{status:t,responseText:a,message:s,detail:o,error:n}=e,i=s||(e=>{switch(e){case 400:return"400 Bad Request";case 401:return"401 Unauthorized";case 403:return"403 Forbidden";case 404:return"404 Not Found";case 409:return"409 Conflict";case 422:return"422 Unprocessable Entity";default:return"잠시 후 다시 시도해주세요"}})(t),r=o||(e=>{switch(e){case 400:return"요청문에 오류가 있거나 서버가 요청을 이해하지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";case 401:return"인증되지 않은 사용자의 요청입니다. 로그인 페이지로 이동합니다.";case 403:return"요청에 대한 권한이 없습니다.";case 404:return"요청한 페이지를 찾을 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";case 409:return"요청한 서비스가 현재 사용 가능하지 않습니다. 다시 시도해 주세요.";case 422:return"요청에 대해 응답할 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";default:return"서버로부터 올바른 응답을 받지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요."}})(t),l=(e=>401===e)(t);return new p({...e,exclusive:l}).setTitle(i).setMessage(r).addButton([{label:"확인",action:"close",color:"silver"}])}}},f={builder:(e={})=>new p(e),infoBuilder:(e={})=>new p(e).setTitle("알림").addButton([{label:"확인",action:"CLOSE",color:"silver"}]),confirmBuilder:(e={})=>new p(e).setTitle("확인").addButton([{label:"아니오",action:"NO",color:"silver"},{label:"예",action:"YES",color:"blue"}]),deleteBuilder:(e={})=>new p(e).setTitle("확인").setMessage("삭제하시겠습니까?").addButton([{label:"아니오",action:"NO",color:"silver"},{label:"삭제",action:"YES",color:"yellow"}]),errorBuilder(e={}){const{status:t,payload:a={}}=e,s=a.message||(e=>{switch(e){case 400:return"400 Bad Request";case 401:return"401 Unauthorized";case 403:return"403 Forbidden";case 404:return"404 Not Found";case 409:return"409 Conflict";case 422:return"422 Unprocessable Entity";default:return"잠시 후 다시 시도해주세요"}})(t),o=a.errors?.pop().message||(e=>{switch(e){case 400:return"요청문에 오류가 있거나 서버가 요청을 이해하지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";case 401:return"인증되지 않은 사용자의 요청입니다. 로그인 페이지로 이동합니다.";case 403:return"요청에 대한 권한이 없습니다.";case 404:return"요청한 페이지를 찾을 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";case 409:return"요청한 서비스가 현재 사용 가능하지 않습니다. 다시 시도해 주세요.";case 422:return"요청에 대해 응답할 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.";default:return"서버로부터 올바른 응답을 받지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요."}})(t),n=(e=>401===e)(t);return new p({...e,exclusive:n}).setTitle(s).setMessage(o).addButton([{label:"확인",action:"CLOSE",color:"silver"}])},loadingBuilder:(e={})=>new h(e).setMessage("잠시만 기다려주세요..."),get:e=>u.get(e),getLoadingModal:()=>u.getLoadingModal(),delete(e){e instanceof d&&u.delete(e)},add(e){e instanceof d&&u.add(e)},getModalModel:()=>d},v=f;document.addEventListener("DOMContentLoaded",(()=>{g&&(g.ModalService&&a(f,g.ModalService),g.ModalBuilder&&a(p,g.ModalBuilder),g.Modal&&a(d,g.Modal))}));class y{constructor({triggerEvents:e=[],nonSubmitNames:t=[],valueProcessor:a={},valueMutator:s={}}={}){this.clearForm(),this.triggerEvents=e,this.nonSubmitNames=t,this.valueProcessor=a,this.valueMutator=s}clearForm(){this.forms={},this.buttons={}}addFormElements(e){const t=r(e);if(!t)return void console.error("Form not found:",e);let a;return a=t instanceof HTMLFormElement?Array.from(t.elements):t.querySelectorAll("input, textarea, select, button"),a.forEach((e=>{"BUTTON"===e.tagName?this.addButtonForm(e):this.addSubmitForm(e)})),this}addSubmitForm(e){const t=r(e);if(t&&t.name){const e=async e=>{const a=t.name,s=this.getForm(a);await this.trigger(e.type,e,{name:a,element:t,type:t.type,value:t.value,form:s})};this.forms[t.name]||(this.forms[t.name]={type:t.type,elements:[]}),this.forms[t.name].elements.push(t);const a=(e=>{switch(e.type){case"radio":case"checkbox":return e.checked?e.value:null;case"select-one":return Array.from(t.selectedOptions,(e=>e.value)).pop();case"select-multiple":return Array.from(t.selectedOptions,(e=>e.value));default:return e.value}})(t);if(this.forms[t.name].defaultValue){const e=this.forms[t.name].defaultValue;this.forms[t.name].defaultValue=[].concat(e).concat(a)}else this.forms[t.name].defaultValue=a;["change","click"].concat(this.triggerEvents).forEach((async a=>{t.removeEventListener(a,e),t.addEventListener(a,e)}))}return this}addButtonForm(e){const t=r(e);if(t&&t.name&&"BUTTON"===t.tagName){const e=e=>{this.trigger("click",e,{name:t.name,element:t,type:t.type})};t.removeEventListener("click",e),t.addEventListener("click",e),this.buttons[t.name]=t}return this}removeFormElements(e){const t=r(e);if(!t)return void console.error("Form not found:",e);let a;return a=t instanceof HTMLFormElement?t.elements:t.querySelectorAll("input, textarea, select, button"),a.forEach((e=>{"BUTTON"===e.tagName?this.removeButtonForm(e):this.removeSubmitForm(e)})),this}removeSubmitForm(e){const t=r(e);return t&&t.name&&this.forms[t.name]&&delete this.forms[t.name],this}removeButtonForm(e){const t=r(e);return t&&t.name&&"BUTTON"===t.tagName&&delete this.buttons[t.name],this}getFormValues(e){const t={};for(let a in this.forms)e&&this.nonSubmitNames.includes(a)||(t[a]=this.getForm(a).getValue());return t}getFormData(){const e=new FormData,t=this.getFormValues();for(let a in t){if(this.nonSubmitNames.includes(a))continue;const s=t[a];null!==s&&(Array.isArray(s)?s.forEach((t=>e.append(a,t))):e.append(a,s))}return e}getForm(t){const a=this.forms[t];if(!a)return null;const{elements:s,defaultValue:o,type:n}=a,i=async(a,o=["change"])=>{"function"==typeof this.valueMutator[t]&&(a=await this.valueMutator[t](a));const i=[];switch(n){case"radio":case"checkbox":e(a)?s.forEach((e=>{e.checked=!1,i.push(e)})):s.forEach((e=>{e.checked=e.value===a,e.checked&&i.push(e)}));break;case"select-one":case"select-multiple":for(let e of s[0].options)e.value===a?e.selected=!0:e.selected=!1;i.push(s[0]);break;default:s[0].value=a,i.push(s[0])}o.forEach((async e=>{const t=new Event(e);i.forEach((e=>e.dispatchEvent(t)))}))};let r=s[0],l={setValue:i,setDefault:(e,t=["change"])=>{a.defaultValue=e,i(e)},getValue:()=>{if(!s)return null;let e=(()=>{switch(n){case"radio":case"checkbox":return s.filter((e=>e.checked)).map((e=>e.value));case"select-one":case"select-multiple":return Array.from(s[0].selectedOptions,(e=>e.value));default:return[s[0].value]}})();return e=e.length>1?e:e.pop(),"function"==typeof this.valueProcessor[t]&&(e=this.valueProcessor[t](e)),e},getElement:()=>{switch(n){case"radio":case"checkbox":return s;default:return s[0]}},setAttribute:(e,t)=>{for(let a of s)a.setAttribute(e,t)},removeAttribute:e=>{for(let t of s)t.removeAttribute(e)},setDisabled:e=>{for(let t of s)t.disabled=e;e?this.nonSubmitNames.push(t):this.nonSubmitNames=this.nonSubmitNames.filter((e=>e!=t))}};return"SELECT"===r.tagName&&(l.getSelectedText=()=>-1!=r.selectedIndex?r.options[r.selectedIndex].text:null,l.isEmpty=()=>!!e(r.options),l.getOptionValues=()=>Array.from(r.options).map((e=>e.value)).filter((t=>!e(t)))),l}getForms(...e){return e.map((e=>this.getForm(e)))}getButton(e){return this.buttons[e]}setValues(e,t=["change"]){for(let a in e)this.getForm(a)?.setValue(e[a],t)}setDefaultValues(e,t=["change"]){for(let a in e)this.getForm(a)?.setDefault(e[a],t)}setAttribute(e,t,a){this.getForm(e)?.setAttribute(t,a)}removeAttribute(e,t){this.getForm(e)?.removeAttribute(t)}getValue(e){return this.getForm(e)?.getValue()}setDisabled=(e,t)=>{this.getForm(e)?.setDisabled(t)};reset(e=["change"]){for(let t in this.forms){const{elements:a,defaultValue:s}=this.forms[t];this.getForm(t)?.setValue(s,e)}}}Object.assign(y.prototype,l);const b=y,C=v.getModalModel();class D extends C{constructor(e={}){super(e);const t=this.modalElement.querySelector("[data-modal-calendar]"),a=$(t).datepicker({dateFormat:"yy-mm-dd"});this._datepicker=a}show(e={}){const{selectedDate:t=moment().format("YYYY-MM-DD"),minDate:a,maxDate:s}=e;return a&&this._datepicker.datepicker("option","minDate",e.minDate),s&&this._datepicker.datepicker("option","maxDate",e.maxDate),moment(t).isValid()?this._datepicker.datepicker("setDate",t):this._datepicker.datepicker("setDate",moment().format("YYYY-MM-DD")),super.show()}getSelectedDate(){const e=this._datepicker.datepicker("getDate");return moment(e).format("YYYY-MM-DD")}}class w{constructor(e={}){const{}=e,t=v.get("datePicker")||(()=>{const e=n('\n<div class="modal" data-modal="datePicker">\n\t<div class="modalWrap">\n        <div class="modal_content">\n            <div class="modalBody flex-direction-column align-items-center">\n               <div class="calendarDiv" data-modal-calendar></div>\n            </div>\n            <div class="modalBtnDiv">\n                <button type="button" data-modal-action="close" class="btn btn-md btn-silver">취 소</button>\n                <button type="button" data-modal-action="ok" class="btn btn-md btn-blue">확 인</button>\n            </div>\n        </div>\n    </div>\n</div>\n');return r("body").appendChild(e),(e=>{const t=new D({modalElement:e,autoDestroy:!1});return v.add(t),t})(e)})();this.modal=t,this.inputs=[]}addInput(e,t={}){const a=r(e);return a&&(a.value=t.defaultValue||a.value,a.onclick=async()=>{const{minDate:e,maxDate:s}=t,{action:o}=await this.modal.show({minDate:e,maxDate:s,selectedDate:a.value});if("OK"==o){const e=this.modal.getSelectedDate();a.value=e,this.trigger("change",{name:a.name,value:formattedDate})}},this.inputs.push(a),this._updatePeriodOptions()),a.style.cursor="pointer",a.onkeydown=e=>(e.preventDefault(),!1),this}_updatePeriodOptions(){2==this.inputs.length&&(this.inputs[0].onclick=async()=>{const e=this.inputs[0],t=this.inputs[1],a=t.value,{action:s}=await this.modal.show({selectedDate:e.value});if("OK"==s){const s=this.modal.getSelectedDate();moment(s).isAfter(moment(a))&&(t.value=s,this.trigger("change",{name:t.name,value:s})),e.value=s,this.trigger("change",{name:e.name,value:s})}},this.inputs[1].onclick=async()=>{const e=this.inputs[1],t=this.inputs[0],a=t.value,{action:s}=await this.modal.show({selectedDate:e.value});if("OK"==s){const s=this.modal.getSelectedDate();moment(s).isBefore(moment(a))&&(t.value=s,this.trigger("change",{name:t.name,value:s})),e.value=s,this.trigger("change",{name:e.name,value:s})}})}}Object.assign(w.prototype,l);const N=w,T=document.querySelector("meta[name='_csrf']")?.content,E=document.querySelector("meta[name='_csrf_header']")?.content,M=e=>Object.fromEntries(Object.entries(e).filter((([e,t])=>null!=t))),L=(e,t="GET",a=null,s={},o={})=>{const n=((e,t)=>{const a=new URLSearchParams(M(t)).toString();return a?`${e}?${a}`:e})(e,s),i={"Content-Type":"application/json","X-Requested-With":"XMLHttpRequest"};T&&E&&!["GET","HEAD","OPTIONS","TRACE"].includes(t.toUpperCase())&&(i[E]=T);const r={method:t,headers:{...i,...o}};return a&&("multipart/form-data"===r.headers["Content-Type"]?(delete r.headers["Content-Type"],r.body=a instanceof FormData?a:new FormData):r.headers["Content-Type"].includes("application/x-www-form-urlencoded")?r.body=a instanceof FormData?a:new URLSearchParams(a).toString():r.body=a&&JSON.stringify(a)),console.log("Request","requestUrl:",n,"data:",a,"Options:",r),fetch(n,r).then((e=>{if(!e.ok)throw e;if("0"===e.headers.get("Content-Length"))return null;const t=e.headers.get("Content-Type");if(!t||!t.includes("application/json"))return e.text();try{return e.json()}catch(e){return null}})).catch((e=>{throw console.error("Fetch error:",e),e}))},S=(e,t={})=>L(e,"GET",null,M(t),{}),x=(e,{data:t=null,queryParams:a={},headers:s={}}={})=>L(e,"PUT",t,M(a),s),Y={userOne:e=>S(`/api/ewp/admin/system/user/${e}`),officeApprovalPolicy:e=>S(`/api/ewp/manager/approval/office/${e}/policy`),masterDomainList:()=>S("/api/ewp/admin/system/roster/master-admin/list"),systemDomainList:()=>S("/api/ewp/admin/system/roster/system-admin/list"),roomManagerList:(e={})=>{const{officeCode:t,roomType:a}=e;return S("/api/ewp/admin/system/roster/room-manager/list",{officeCode:t,roomType:a})},requestManagerList:(e={})=>{const{officeCode:t,roomType:a}=e;return S("/api/ewp/admin/system/roster/request-manager/list",{officeCode:t})},assignOneForApproval:e=>S(`/api/ewp/manager/approval/meeting/assign/${e}`,{skdKey:e}),assignListForApproval:(e={})=>{const{officeCode:t,approvalStatus:a,roomType:s,roomKey:o,title:n,host:i,attendeeName:r,elecYN:l,secretYN:d,startDate:c,endDate:u,pageNo:m,pageCnt:p}=e;return S("/api/ewp/manager/approval/meeting/assign/list",{officeCode:t,approvalStatus:a,roomType:s,roomKey:o,title:n,host:i,attendeeName:r,elecYN:l,secretYN:d,startDate:c,endDate:u,pageNo:m,pageCnt:p})}},A={officeAutoApproval:(e={})=>{const{officeCode:t,autoYN:a}=e;return x(`/api/ewp/manager/approval/office/${t}/policy/${a}`)},meetingAssignTitleAndHost:(e={})=>{const{skdKey:t,title:a,skdHost:s}=e;return x(`/api/ewp/manager/approval/assign/${t}`,{queryParams:{title:a,skdHost:s}})}},H={roomOne:(e={})=>{const{roomType:t,roomKey:a}=e;return S(`/api/ewp/room/${t}/${a}`)},roomList:(e={})=>{const{officeCode:t,roomType:a="ALL_ROOM",rentYN:s,delYN:o}=e;return S(`/api/ewp/admin/system/room/${a}/list`,{officeCode:t,rentYN:s,delYN:o})},rentableList:(e={})=>{const{officeCode:t,roomType:a}=e;return S(`/api/ewp/room/${a}/rentable/list`,{officeCode:t})},publicListForDisplay:(e={})=>{const{officeCode:t,roomType:a}=e;return S(`/api/ewp/public/room/${a}/display/list`,{officeCode:t})},roomPermissionList:e=>{const{roomType:t,roomKey:a}=e;return S(`/api/ewp/admin/system/room/${t}/${a}/permission/list`)}},k={assignOne:e=>S(`/api/ewp/meeting/assign/${e}`,{skdKey:e}),assignList:(e={})=>{const{officeCode:t,approvalStatus:a,roomType:s,roomKey:o,title:n,host:i,attendeeName:r,elecYN:l,secretYN:d,startDate:c,endDate:u,pageNo:m,pageCnt:p,orderColumn:h,orderDirection:g="ASC"}=e;return S("/api/ewp/meeting/assign/list",{officeCode:t,approvalStatus:a,roomType:s,roomKey:o,title:n,host:i,attendeeName:r,elecYN:l,secretYN:d,startDate:c,endDate:u,pageNo:m,pageCnt:p,orderColumn:h,orderDirection:g})},assignListCnt:(e={})=>{const{officeCode:t,approvalStatus:a,roomType:s,roomKey:o,title:n,host:i,attendeeName:r,elecYN:l,secretYN:d,startDate:c,endDate:u,pageNo:m,pageCnt:p}=e;return S("/api/ewp/meeting/assign/list/cnt",{officeCode:t,approvalStatus:a,roomType:s,roomKey:o,title:n,host:i,attendeeName:r,elecYN:l,secretYN:d,startDate:c,endDate:u,pageNo:m,pageCnt:p})},assignListForPlanned:(e={})=>{const{startDate:t,endDate:a}=e;return S("/api/ewp/meeting/assign/planned/list",{startDate:t,endDate:a})},assignStatForPlanned:(e={})=>{const{startDate:t,endDate:a}=e;return S("/api/ewp/meeting/assign/planned/stat",{startDate:t,endDate:a})},assignListForRegister:(e={})=>{const{pageNo:t,pageCnt:a}=e;return S("/api/ewp/meeting/assign/register/list",{pageNo:t,pageCnt:a})},assignListForDisplay:(e={})=>{const{officeCode:t,roomType:a,roomKey:s,startDate:o,endDate:n}=e;return S("/api/ewp/meeting/assign/display/list",{officeCode:t,roomType:a,roomKey:s,startDate:o,endDate:n})},assignPublicListForDisplay:(e={})=>{const{officeCode:t,roomType:a,roomKey:s,startDate:o,endDate:n}=e;return S("/api/ewp/public/meeting/assign/display/list",{officeCode:t,roomType:a,roomKey:s,startDate:o,endDate:n})},userArchiveList:(e={})=>{const{skdHost:t,title:a,originalName:s,roleType:o,startDate:n,endDate:i,pageNo:r,pageCnt:l}=e;return S("/api/ewp/meeting/archive/manage/user",{skdHost:t,title:a,originalName:s,roleType:o,startDate:n,endDate:i,pageNo:r,pageCnt:l})},deptArchiveList:(e={})=>{const{skdHost:t,title:a,originalName:s,startDate:o,endDate:n,pageNo:i,pageCnt:r}=e;return S("/api/ewp/meeting/archive/manage/dept",{skdHost:t,title:a,originalName:s,startDate:o,endDate:n,pageNo:i,pageCnt:r})},adminArchiveList:(e={})=>{const{skdHost:t,title:a,originalName:s,startDate:o,endDate:n,pageNo:i,pageCnt:r}=e;return S("/api/ewp/admin/master/meeting/archive/manage",{skdHost:t,title:a,originalName:s,startDate:o,endDate:n,pageNo:i,pageCnt:r})},userAssignList:(e={})=>{const{approvalStatus:t,meetingStatus:a,roomType:s,title:o,host:n,writerKey:i,attendeeName:r,elecYN:l,secretYN:d,startDate:c,endDate:u,pageNo:m,pageCnt:p,orderColumn:h,orderDirection:g="DESC"}=e;return S("/api/ewp/meeting/assign/manage/user",{approvalStatus:t,meetingStatus:a,roomType:s,title:o,host:n,writerKey:i,attendeeName:r,elecYN:l,secretYN:d,startDate:c,endDate:u,pageNo:m,pageCnt:p,orderColumn:h,orderDirection:g})},deptAssignList:(e={})=>{const{approvalStatus:t,meetingStatus:a,roomType:s,title:o,host:n,attendeeName:i,elecYN:r,secretYN:l,startDate:d,endDate:c,pageNo:u,pageCnt:m,orderColumn:p,orderDirection:h="DESC"}=e;return S("/api/ewp/meeting/assign/manage/dept",{approvalStatus:t,meetingStatus:a,roomType:s,title:o,host:n,attendeeName:i,elecYN:r,secretYN:l,startDate:d,endDate:c,pageNo:u,pageCnt:m,orderColumn:p,orderDirection:h})}};window.onload=()=>{O.init({officeCode,approvalStatus,roomType,host,startDate,endDate,pageNo})};const F={async init(e,t){const a={};await Promise.all(e.filter((e=>"0"!==e)).map((async e=>{const s=await H.rentableList({officeCode:e,roomType:t});a[e]=s}))),this.roomMap=a},getRoomList(e){return this.roomMap[e]},getRoomListAll(){return Object.values(this.roomMap).reduce(((e,t)=>e.concat(t)),[])}},O={async init(t={}){const{officeCode:a,approvalStatus:s,roomType:n,host:i,startDate:l,endDate:d,pageNo:c=1,pageCnt:u=10}=t;this.pageNo=1,this.pageCnt=10,this.pagination=new Pagination({container:$("#pagination"),maxVisibleElements:9,enhancedMode:!1,pageClickCallback:e=>{this.pageMove(e)}});const m=this.searchHelper=new b({valueProcessor:{officeCode:e=>"0"==e?null:e,approvalStatus:e=>"0"==e?null:e,roomKey:e=>"0"==e?null:e,title:t=>e(t)?null:t,host:t=>e(t)?null:t,attendeeName:t=>e(t)?null:t,elecYN:e=>"Y"!=e?null:e,secretYN:e=>"Y"!=e?null:e}});m.addFormElements("#searchForm").on({change:(t,a)=>{const{name:s,value:n,element:i,form:l}=a;switch(s){case"officeCode":r("#officeLabel").innerText=l.getSelectedText(),(async()=>{const t=r("#autoConfirm");t.disabled=!0;const a=O.getValue("officeCode");if(console.log("setAutoApprovalSwitch",a),e(a))return t.onchange=null,void(t.checked=!1);t.onchange=async()=>{t.disabled=!0;let e="N";1==t.checked&&(e="Y");try{await A.officeAutoApproval({officeCode:a,autoYN:e});const s=v.infoBuilder().build();1==t.checked?s.setMessage("예약등록되는 건들을 시스템이 자동승인처리 합니다.").show():s.setMessage("예약 자동승인을 해제 하시면 예약등록되는 건들을 수동으로 승인처리 하셔야 합니다.").show()}catch(e){console.error(e),t.checked=!t.checked,v.errorBuilder(e).build().show()}finally{t.disabled=!1}};try{const e=await Y.officeApprovalPolicy(a);t.checked="AUTH_AUTO"==e,t.disabled=!1}catch(e){v.errorBuilder(e).build().show()}})();const t=m.getForm("roomKey").getElement(),a=((e={})=>{const{value:t=null,label:a="선택",selectable:s=!1}=e,n=o("option");return n.value=t||"",n.innerHTML=a,s||(n.setAttribute("disabled",!s),n.setAttribute("hidden",!s)),n.setAttribute("selected",!0),n})({value:"0",label:"전체",selectable:!0});t.innerHTML="",t.appendChild(a),("0"==n?F.getRoomListAll():F.getRoomList(n)).forEach((e=>{const a=((e={})=>{const{value:t=null,label:a="선택"}=e,s=o("option");return s.value=t||"",s.innerHTML=a,s})({value:e.roomKey,label:e.roomName});t.appendChild(a)}));break;case"approvalStatus":r("#approvalStatusLabel").innerText=l.getSelectedText()}},click:(e,t)=>{const{name:a}=t;switch(a){case"mobileReset":case"reset":this.reset();break;case"search":this.search()}}});const p=m.getForm("officeCode");await F.init(p.getOptionValues(),n),m.setDefaultValues({officeCode:a,approvalStatus:s,roomType:n,title:"",host:i,attendeeName:"",elecYN:"",secretYN:"",startDate:l,endDate:d}),(new N).addInput(m.getForm("startDate").getElement()).addInput(m.getForm("endDate").getElement()),this.search()},getValue(e){return this.searchHelper.getValue(e)},async search(){const{officeCode:e=this.officeCode,approvalStatus:t=this.approvalStatus,title:a=this.title,host:s=this.host,attendeeName:o=this.attendeeName,roomType:n=this.roomType,roomKey:i=this.roomKey,elecYN:r=this.elecYN,secretYN:l=this.secretYN,startDate:d=this.startDate,endDate:c=this.endDate}=this.searchHelper.getFormValues();this.officeCode=e,this.approvalStatus=t,this.roomType=n,this.roomKey=i,this.title=a,this.host=s,this.attendeeName=o,this.elecYN=r,this.secretYN=l,this.startDate=d,this.endDate=c,this.totalCnt=null,await this.loadPage()},reset(){this.pageNo=1,this.searchHelper.reset(),this.search()},async pageMove(e){this.pageNo=e,await this.loadPage()},async loadPage(){(this.totalCnt||await this.setPagination())&&await this.showList()},async setPagination(){B.clear(),$("#pagination").empty();try{const e=await k.assignListCnt({officeCode:this.officeCode,approvalStatus:this.approvalStatus,roomType:this.roomType,roomKey:this.roomKey,title:this.title,host:this.host,attendeeName:this.attendeeName,elecYN:this.elecYN,secretYN:this.secretYN,startDate:this.startDate,endDate:this.endDate,pageNo:this.pageNo,pageCnt:this.pageCnt});return this.totalCnt=e,this.pageNo=1,this.pagination.make(this.totalCnt,this.pageCnt),!0}catch(e){return v.errorBuilder(e).build().show(),this.pageNo=null,!1}},async showList(){try{const e=await Y.assignListForApproval({officeCode:this.officeCode,approvalStatus:this.approvalStatus,roomType:this.roomType,roomKey:this.roomKey,title:this.title,host:this.host,attendeeName:this.attendeeName,elecYN:this.elecYN,secretYN:this.secretYN,startDate:this.startDate,endDate:this.endDate,pageNo:this.pageNo,pageCnt:this.pageCnt});B.generate(e)}catch(e){v.errorBuilder(e).build().show()}}},B={clear(){r("#listBox").innerHTML=""},getContainer:()=>r("#listBox"),generate(e){this.clear();const t=r("#listBox");for(const a of e){const e=this.createRow(a);t.appendChild(e)}},createRow(e){const t=o("div","row");return(()=>{const a=o("div","item","no");a.innerHTML=e.skdKey,t.appendChild(a)})(),(()=>{const a=o("div","item","status"),s=o("span");a.appendChild(s),i(t,(e=>{switch(e){case"REQUEST":return"s0";case"APPROVED":return"s1";case"CANCELED":return"s4";case"REJECTED":return"s5"}})(e.appStatus)),t.appendChild(a)})(),(()=>{const a=o("div","item","type"),s=o("span");switch(a.appendChild(s),e.room?.roomType){case"MEETING_ROOM":i(t,"mr");break;case"EDU_ROOM":i(t,"lr");break;case"HALL":i(t,"at")}t.appendChild(a)})(),(()=>{const a=o("div","item","elecDocs"),s=o("span");a.appendChild(s),"Y"==e.elecYN&&i(t,"elec"),t.appendChild(a)})(),(async()=>{const a=o("div","item","security"),s=o("span");a.appendChild(s),"Y"==e.secretYN&&i(t,"secu"),t.appendChild(a)})(),(()=>{const a=o("div","item","dateTimeRoom"),s=o("div");a.appendChild(s);const n=o("span","date");n.innerHTML=moment(e.holdingDate).format("YYYY.MM.DD");const i=o("span","time");i.innerHTML=moment(e.beginDateTime).format("HH:mm")+"~"+moment(e.finishDateTime).format("HH:mm"),s.appendChild(n),s.appendChild(i);const r=o("div");a.appendChild(r),r.innerHTML=e.room?.roomName,t.appendChild(a)})(),(()=>{const a=o("div","item","title","text-truncate"),s=o("a","w-100","d-inline-block","text-truncate");a.appendChild(s),s.innerHTML=e.title,s.href="/ewp/manager/approval/meeting/assign/"+e.skdKey,t.appendChild(a)})(),(()=>{const a=o("div","item","host","text-truncate"),s=o("span","headerTip");s.innerHTML="주관자: ",a.appendChild(s);const n=o("div","w-100","d-inline-block","text-truncate");n.innerHTML=e.skdHost,a.appendChild(n),t.appendChild(a)})(),(()=>{const a=o("div","item","attendUser","align-items-center","overflow-hidden");a.style.maxHeight="1.6em";const s=o("span","headerTip");if(s.innerHTML="참석자: ",a.appendChild(s),"Y"==e.elecYN){const t=e.attendeeList;for(const e of t){const t=o("span");t.innerHTML=e.userName,a.appendChild(t)}a.onpointerenter=()=>{a.title=t.map((e=>e.userName))},a.onpointerleave=()=>{a.title=""}}t.appendChild(a)})(),(()=>{const a=o("div","item","regDate"),s=o("span","headerTip");s.innerHTML="등록일: ",a.appendChild(s);const n=moment(e.regDateTime),i=o("span","date");i.innerHTML=n.format("YYYY.MM.DD");const r=o("span","time");r.innerHTML=n.format("HH:mm"),a.appendChild(i),a.appendChild(r),t.appendChild(a)})(),(()=>{const a=o("div","item","approvalComment","text-truncate"),s=o("span","w-100","d-inline-block","text-truncate");s.innerHTML=e.appComment?e.appComment:"",a.onpointerenter=()=>{a.title=s.innerHTML},a.onpointerleave=()=>{a.title=""},a.appendChild(s),t.appendChild(a)})(),t}}})();