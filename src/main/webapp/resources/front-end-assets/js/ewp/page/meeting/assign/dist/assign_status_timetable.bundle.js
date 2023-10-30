(()=>{"use strict";const e=(e,t={})=>{e.value.length>=e.maxLength&&(t.returnValue=!1)},t=e=>null==e||"object"==typeof e&&0===Object.keys(e).length||!(!Array.isArray(e)||0!==e.length)||"string"==typeof e&&0===e.trim().length,i=document,o=(e,...t)=>{const o=i.createElement(e);return s(o,...t),o},s=(e,...i)=>{const o=a(e);for(const e of i)t(e)||o?.classList.add(e);return o},n=(e,...i)=>{const o=a(e);for(const e of i)t(e)||o?.classList.remove(e);return o},a=e=>e instanceof HTMLElement?e:i.querySelector(e),r=e=>e instanceof HTMLElement?e:i.querySelectorAll(e),l={on(e,t){if(this._eventHandlers||(this._eventHandlers={}),"string"==typeof e)this._eventHandlers[e]||(this._eventHandlers[e]=[]),this._eventHandlers[e].push(t);else if("object"==typeof e)for(let t in e){let i=t,o=e[t];this._eventHandlers[i]||(this._eventHandlers[i]=[]),this._eventHandlers[i].push(o)}return this},off(e,t){if(!this._eventHandlers)return;if(!e&&!t)return void(this._eventHandlers={});if(e&&!t)return void delete this._eventHandlers[e];let i=this._eventHandlers?.[e];if(i){let e=i.indexOf(t);-1!==e&&i.splice(e,1)}return this},unbind(...e){this.off(...e)},trigger(e,...t){if(this._eventHandlers?.[e])if("string"==typeof e)this._eventHandlers[e].forEach((e=>e.apply(this,t)));else if("object"==typeof e)for(let t in e){let i=t,o=e[t];this._eventHandlers[i].forEach((e=>e.apply(this,o)))}}},c={collection:new Set,add(e){this.collection.add(e)},delete(e){this.collection.delete(e)},get(e){return Array.from(this.collection).find((t=>t.id==e))},getAll(){return this.collection},hasSingleton(){return!!Array.from(this.collection).find((e=>1==e.singleton))}},d=(e,t)=>{if("show"==t){if(c.hasSingleton()&&1!=e.singleton)return;1==e.singleton&&c.getAll().forEach((e=>{e.close()})),c.add(e)}else"hide"==t&&(c.delete(e),1!=e.persistent&&e.remove())},h={promisifyModal(e){const t="string"==typeof e?a(e):e;if(!t)return null;if(1==t.promisify)return t;const i=t.querySelectorAll("[data-modal-btn]");return i.forEach((e=>{e.addEventListener("click",(()=>{t.resolve?.(e.dataset.modalBtn),e.dataset.modalClose&&"manual"==e.dataset.modalClose||t.hide()})),e.setText=t=>(e.innerText=t,e),e.setColor=t=>{switch(t){case"blue":n("$btn","btn-red","btn-white"),s("$btn","btn-blue");break;case"red":n("$btn","btn-blue","btn-white"),s("$btn","btn-red");break;case"white":n("$btn","btn-red","btn-blue"),s("$btn","btn-white")}return e}})),t.getBtn=e=>{const t=Array.from(i).filter((t=>t.dataset.modalBtn==e));return t?.pop()},t.show=e=>(t.style.display="block",t.onshow?.(),t.onswitch?.(t,"show"),e?.(),new Promise(((e,i)=>{t.resolve=e,t.reject=i,t.close=()=>{e("CLOSE"),t.hide()}}))),t.hide=()=>{t.style.display="none",i.forEach((e=>{e.onclick=void 0})),t.onhide?.(),t.onswitch?.(t,"hide")},t.close=t.hide,t.enableBtn=e=>{const i=t.querySelector('[data-modal-btn="'+e+'"]');i&&(i.disabled=!1)},t.disableBtn=e=>{const i=t.querySelector('[data-modal-btn="'+e+'"]');i&&(i.disabled=!0)},t.on=(e,i)=>{switch(e){case"show":t.onshow=i;break;case"hide":t.onhide=i;break;case"switch":t.onswitch=i}return t},t.off=e=>{switch(e){case"show":t.onshow=null;break;case"hide":t.onhide=null;break;case"switch":t.onswitch=null}return t},t.promisify=!0,t},buildTemplate(e){const t=o("div","modal","top-front"),i=o("div","modalWrap");t.appendChild(i);const n=o("div","modal_content");i.appendChild(n);const a=o("div","modalTitle");a.style.display="none",n.appendChild(a),t.getTitleDiv=()=>a;const r=o("div","modalBody","flex-direction-column");r.style.display="none",n.appendChild(r),t.getMsgDiv=()=>r;const l=o("div","modalInfo");l.style.display="none",n.appendChild(l),t.getDetailDiv=()=>l;const c=o("div","modalBtnDiv");return"vertical"==e&&s(c,"flex-direction-column"),n.appendChild(c),t.addBtn=e=>{c.appendChild(e)},t.singleton=!1,t.addBlackout=()=>{t.style.background="rgba(0,0,0,0.5)"},t.removeBlackout=()=>{t.style.background=""},t},buildButton(e){switch(e){case"OK":const t=o("button","btn","btn-md","btn-blue");return t.innerHTML="확 인",t.dataset.modalBtn="OK",t;case"CLOSE":const i=o("button","btn","btn-md","btn-silver");return i.innerHTML="닫 기",i.dataset.modalBtn="CLOSE",i;case"CANCEL":const n=o("button","btn","btn-md","btn-silver");return n.innerHTML="취 소",n.dataset.modalBtn="CANCEL",n;case"DELETE":const a=o("button","btn","btn-md","btn-red");return a.innerHTML="삭 제",a.dataset.modalBtn="DELETE",a;default:const{isPrimary:r,key:l,text:c}=e,d=o("button","btn","btn-md");return s(d,1==r?"btn-blue":"btn-blue-border"),d.innerHTML=c,d.dataset.modalBtn=l,d}},buildModal(e={}){const{btnList:t,direction:i="horizontal",target:s=a("body")}=e,n=this.buildTemplate(i);return t.forEach((e=>n.addBtn(this.buildButton(e)))),n.setTitle=e=>{const t=n.getTitleDiv();t.style.display="",t.innerHTML=e},n.setMsg=e=>{const t=o("div","commonMent");t.innerHTML=e;const i=n.getMsgDiv();i.querySelector(".commonMent")?.remove(),i.style.display="",i.appendChild(t)},n.setDetail=(...e)=>{if(0==e.length)return;const t=o("ul","modalInfo");for(const i of e){const e=o("li");e.innerHTML=i.message?i.message:i,t.appendChild(e)}const i=n.getDetailDiv();i.style.display="",i.appendChild(t)},n.setErrorInfo=(...e)=>{const t=o("ul","modalInfo");for(const i of e){const e=o("li");e.innerHTML=i.message?i.message:i,t.appendChild(e)}n.setDetail(t)},n.persistent=!1,s.appendChild(n),this.promisifyModal(n).on(d)}},m={animations:{doubleBounce:()=>{const e=o("div","sk-double-bounce"),t=o("div","sk-child","sk-double-bounce1"),i=o("div","sk-child","sk-double-bounce2");return e.appendChild(t),e.appendChild(i),e},pulse:()=>o("div","sk-spinner","sk-spinner-pulse"),spinner:()=>{const e=o("div","sk-fading-circle");for(let t=0;t<12;t++){const i=o("div","sk-circle","sk-circle"+(t+1));e.appendChild(i)}return e},foldingCube:()=>{const e=o("div","sk-folding-cube"),t=o("div","sk-cube","sk-cube1"),i=o("div","sk-cube","sk-cube2"),s=o("div","sk-cube","sk-cube3"),n=o("div","sk-cube","sk-cube4");return e.appendChild(t),e.appendChild(i),e.appendChild(n),e.appendChild(s),e}},promisifyModal(e){const t="string"==typeof e?a(e):e;if(!t)return null;if(1==t.promisify)return t;const i=t.querySelectorAll("[data-modal-btn]");return t.show=()=>(s(t,"visible"),n(t,"hidden"),t.onshow?.(),t.onswitch?.(t,"show"),new Promise(((e,o)=>{i.forEach((i=>{i.onclick=()=>{e(i.dataset.modalBtn),t.hide()}})),t.close=()=>{e("CLOSE"),t.hide()}}))),t.hide=()=>{n(t,"visible"),s(t,"hidden"),i.forEach((e=>{e.onclick=void 0})),t.onhide?.(),t.onswitch?.(t,"hide")},t.close=t.hide,t.on=(e,i)=>{switch(e){case"show":t.onshow=i;break;case"hide":t.onhide=i;break;case"switch":t.onswitch=i}return t},t.off=e=>{switch(e){case"show":t.onshow=null;break;case"hide":t.onhide=null;break;case"hide":t.onswitch=null}return t},t.promisify=!0,t},buildTemplate(){const e=o("div","modal","loading-modal","visible"),t=o("div","loading-info-box","m-2");e.appendChild(t);const i=o("div","loading-animation");t.appendChild(i);const a=o("div","loading-text");return t.appendChild(a),e.setAnimation=t=>{const o=this.animations[t]?.();i.appendChild(o),e.animation=o},e.setPosition=t=>{e.style.position=t},e.setText=e=>{a.innerHTML=e},e.setTextColor=e=>{a.style.color=e},e.setAnimationColor=t=>{e.animation.setColor(t)},e.setBackgroundColor=t=>{e.style.backgroundColor=t},e.setBackgroundOpacity=t=>{e.style.opacity=t},e.persistent=!1,e.addBlackout=()=>{e.style.background="rgba(0,0,0,0.5)",n(i,"blackout")},e.removeBlackout=()=>{e.style.background="none",s(i,"blackout")},e},buildLoading(e={}){const{position:t="auto",text:i="",color:o="#fff",opacity:s="0.7",backgroundColor:r="rgb(0,0,0)",animation:l="doubleBounce",target:h=a("body"),blackout:m=!0,isModal:p=!0}=e,u=c.get("loadingModal")?c.get("loadingModal"):this.buildTemplate();return u.setPosition(t),u.setAnimation(l),u.setText(i),u.setTextColor(o),u.setBackgroundColor(r),u.setBackgroundOpacity(s),m?u.addBlackout():u.removeBlackout(),p||n(u,"modal"),h.appendChild(u),this.promisifyModal(u).on("switch",d)}},p={init(){this.target=a("body"),this.persistentMap=new Map,r(".modal").forEach(((e,t)=>{const i=h.promisifyModal(e)?.on("switch",d);if(i){i.persistent=!0;const e=i.id?i.id:"modal"+t;this.persistentMap.set(e,i),i.hide()}}))},persistentModal(e){return this.persistentMap.get(e)},info(e={}){const{id:t="infoModal",title:i="알 림",detail:o,msg:s,singleton:n=!1}=e;console.log("info modal","title",i,"msg",s,"detail",o);const a=h.buildModal({btnList:["OK"]});return a.id=t,a.singleton=n,i&&a.setTitle(i),s&&a.setMsg(s),o&&a.setDetail(...o),a},confirm(e={}){const{id:t="confirmModal",title:i,msg:o,detail:s,delMode:n=!1,singleton:a=!1}=e,r=1==n?["CANCEL","DELETE"]:["CANCEL","OK"],l=h.buildModal({btnList:r});return l.id=t,l.singleton=a,i&&l.setTitle(i),o&&l.setMsg(o),s&&l.setDetail(...s),l},select(e={}){const{id:t="selectModal",title:i,msg:o,select:s=[],singleton:n=!1,direction:a="vertical"}=e;s.push("CLOSE");const r=h.buildModal({btnList:s,direction:a});return r.id=t,r.singleton=n,i&&r.setTitle(i),o&&r.setMsg(o),r},error(e={}){const{id:t="errorModal",singleton:i=!1,response:{status:o,responseText:s,message:n,detail:a,error:r}}=e,l=h.buildModal({btnList:["OK"]});switch(l.id=t,l.singleton=i,o){case 0:l.setTitle("연결 실패"),l.setMsg("서버와의 연결이 끊어졌습니다. 인터넷 연결 상태를 확인하고 다시 시도해주세요."),l.onhide=e=>{location.href="/login"},l.singleton=!0;break;case 500:l.setTitle("500 Internal Server Error"),l.setMsg("서버에서 요청을 처리할 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.");break;case 400:l.setTitle("400 Bad Request"),l.setMsg("요청문에 오류가 있거나 서버가 요청을 이해하지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.");break;case 422:l.setTitle("422 Unprocessable Entity"),l.setMsg("요청에 대해 응답할 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.");break;case 401:l.setTitle("401 Unauthorized"),l.setMsg(s||"인증되지 않은 사용자의 요청입니다. 로그인 페이지로 이동합니다."),l.onhide=e=>{location.href="/login"},l.singleton=!0;break;case 403:l.setTitle("403 Forbidden"),l.setMsg("요청에 대한 권한이 없습니다.");break;case 404:l.setTitle("404 Not Found"),l.setMsg("요청한 페이지를 찾을 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.");break;case 409:l.setTitle("409 Conflict"),l.setMsg("요청한 서비스가 현재 사용 가능하지 않습니다. 다시 시도해 주세요.");break;default:l.setTitle("잠시 후 다시 시도해주세요"),l.setMsg("서버로부터 올바른 응답을 받지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.")}return n&&l.setTitle(n),a&&l.setMsg(a),r&&0!=r.length&&l.setErrorInfo(...r),l},loading(e={}){const{id:t="loadingModal",target:i=this.target}=e;i!==this.target&&(e.blackout=!1,e.isModal=!1);const o=m.buildLoading(e);return o.id=t,o}},u={__proto__:l,init(e={}){const{}=e},promisify:e=>h.promisifyModal(e),info:(e={})=>p.info(e).show(),confirm:(e={})=>p.confirm(e).show(),select:(e={})=>p.select(e).show(),error:(e={})=>p.error(e).show(),startLoading:(e={})=>p.loading(e).show(),endLoading(e={}){c.get("loadingModal")?.hide()},get:e=>p.persistentModal(e),getPopupList:()=>c.getAll(),show:e=>p.persistentModal(e).show(),close(e){const t=c.get(e);t?.close()},closeAll(){c.getAll().forEach((e=>{e.close()}))}};p.init();const g=["success-only","http-errors-only","no-exceptions"];class f{constructor(e={}){const{request:t,param:i,loading:o=!0,debug:s=!0,exception:n=g[1],exceptionModal:a=!0}=e;this.request=t,this.param=i,this.loading=o,this.debug=s,this.exception=n}success(e){return this.successExe=e,this}error(e){return this.errorExe=e,this}finally(e){return this.finallyExe=e,this}async exe(){try{this.loading&&u.startLoading();const e=await this.request(this.param);if(this.loading&&u.endLoading(),this.exception==g[0]&&200!=e.status)throw e;return this.successExe?.(e),e}catch(e){return this.loading&&u.endLoading(),this.debug&&console.log(e),this.exceptionModal&&u.error({response:e}),this.errorExe?.(e),null}finally{this.finallyExe?.()}}}const y=(e={})=>new f(e),T={__proto__:l,init(e={}){const{officeCode:t="1000",approvalStatus:i=0,meetingStatus:o=0,roomType:s="ALL_ROOM",roomKey:n,title:a="",host:r="",attendeeName:l="",searchTarget:c="title",searchWord:d="",holdingDate:h=moment().format("YYYY-MM-DD"),startDate:m=moment().format("YYYY-MM-DD"),endDate:p=moment().add(7,"d").format("YYYY-MM-DD")}=e;this.officeCode=t,this.approvalStatus=i,this.meetingStatus=o,this.roomType=s,this.roomKey=n,this.title=a,this.host=r,this.attendeeName=l,this.searchTarget=c,this.searchWord=d,this.holdingDate=h,this.startDate=m,this.endDate=p,this.enableSearchBtn(),this.enableResetBtn(),this.enableSearchToggleBtn()},initOffice(){const e=a("#officeSelect");if(e){const t=a("#officeLabel"),i=i=>{const o=e.querySelector('[value="'+i+'"]');o.selected=!0;const s=o.text;t.innerText=s};e.onchange=e=>{const t=e.target.value;i(t),this.trigger("change","office",t)},this.selectOffice=i,this.getOffice=()=>0==e.value?null:e.value,this.selectOffice(this.officeCode)}else this.getOffice=()=>this.officeCode},initApprovalStatus(){const e=a("#approvalStatusSelect"),t=a("#approvalStatusLabel"),i=i=>{const o=e.querySelector('[value="'+i+'"]');o.selected=!0;const s=o.text;t.innerText=s};e.onchange=e=>{const t=e.target.value;i(t),this.trigger("change","approvalStatus",t)},this.selectAppStatus=i,this.getAppStatus=()=>0==e.value?null:e.value,this.selectAppStatus(this.approvalStatus)},initMeetingStatus(){const e=a("#meetingStatusSelect"),t=a("#meetingStatusLabel"),i=i=>{const o=e.querySelector('[value="'+i+'"]');o.selected=!0;const s=o.text;t.innerText=s};e.onchange=e=>{const t=e.target.value;i(t),this.trigger("change","meetingStatus",t)},this.selectMeetingStatus=i,this.getMeetingStatus=()=>0==e.value?null:e.value,this.selectMeetingStatus(this.meetingStatus)},initSearchTarget(){const e=a("#searchTargetSelect"),t=a("#searchTargetLabel"),i=i=>{const o=e.querySelector('[value="'+i+'"]');o.selected=!0;const s=o.text;t.innerText=s};e.onchange=e=>{const t=e.target.value;i(t),this.trigger("change","searchTarget",t)},this.selectTarget=i,this.getSearchTarget=()=>e.value,this.selectTarget(this.searchTarget)},initSearchWord(){const t=a("#searchWord");t.maxLength=60,t.oninput=()=>{e(t)},t.onkeyup=e=>{13==e.keyCode&&this.trigger("search",this.getSearchInput())},t.onchange=()=>{this.trigger("change","searchWord",t.value)},this.setSearchWord=e=>{t.value=e},this.getSearchWord=()=>""==t.value?null:t.value,this.setSearchWord(this.searchWord)},initPeriodPicker(e={}){const{maxDate:t}=e,i=a("#startDateDiv"),o=a("#startDateInput");o.value=this.startDate;const s=a("#endDateDiv"),n=a("#endDateInput");n.value=this.endDate,this.setStartDate=e=>{o.value=e},this.setEndDate=e=>{n.value=e},this.getStartDate=e=>o.value,this.getEndDate=e=>n.value;const r=$("#datepicker");r.datepicker({dateFormat:"yy-mm-dd"}),1==moment(t).isValid()&&r.datepicker("option","maxDate",t);const l=a("#datePickerModal");l.querySelector(".btn-blue"),l.querySelector(".btn-silver"),i.onclick=async()=>{const e=this.getStartDate(),t=this.getEndDate();if(1==moment(e).isValid()&&r.datepicker("setDate",e),"OK"==await u.get("datePickerModal").show()){const e=r.datepicker("getDate"),i=moment(e),o=i.format("YYYY-MM-DD");i.isAfter(t)&&(this.setEndDate(o),this.trigger("change","endDate",o)),this.setStartDate(o),this.trigger("change","startDate",o)}},s.onclick=async()=>{const e=this.getStartDate(),t=this.getEndDate();if(1==moment(t).isValid()&&r.datepicker("setDate",t),"OK"==await u.get("datePickerModal").show()){const t=r.datepicker("getDate"),i=moment(t),o=i.format("YYYY-MM-DD");i.isBefore(e)&&(this.setStartDate(o),this.trigger("change","startDate",o)),this.setEndDate(o),this.trigger("change","endDate",o)}}},initDatePicker(){const e=a("#dateDiv"),t=a("#dateInput");t.value=this.holdingDate,this.setDate=e=>{t.value=e},this.getDate=e=>t.value;const i=$("#datepicker");i.datepicker({dateFormat:"yy-mm-dd"});const o=moment().format("YYYY-MM-DD");e.onclick=async()=>{i.datepicker("option","minDate",o);const e=this.getDate();if(1==moment(e).isValid()&&i.datepicker("setDate",e),"OK"==await u.get("datePickerModal").show()){const e=i.datepicker("getDate"),t=moment(e).format("YYYY-MM-DD");this.setDate(t),this.trigger("change","holdingDate",t)}}},initFileType(){const e=a("#fileType");e&&(e.onchange=()=>{const e=this.getFileType();this.trigger("change","roleType",e)},this.switchFileType=t=>{e.checked=t},this.getFileType=()=>e.checked?e.value:null)},initRoomTypeSelect(){const e=a("#roomTypeSelect");if(e){const t=a("#roomTypeLabel"),i=i=>{this.trigger("change","roomType",i),e.value=i;const o=e.options[e.selectedIndex].text;t.innerText=o};e.onchange=e=>{const t=e.target.value;i(t)},this.selectRoomType=i,this.getRoomType=()=>e.value,this.selectRoomType(this.roomType)}else this.getRoomType=()=>this.roomType},initRoomTypeCheck(){const e=r('input[type=radio][name="roomType"]');e.forEach((e=>e.addEventListener("change",(async()=>{const t=e.value;this.trigger("change","roomType",t),this.loadRoomList?.()})))),this.checkRoomType=t=>{e.forEach((e=>{e.value==t?e.checked=!0:e.checked=!1}))},this.getRoomType=()=>{for(const t of e)if(1==t.checked)return t.value},this.checkRoomType(this.roomType)},initRoomSelect(){const e=a("#roomSelect"),t=a("#roomLabel"),i=(e,t)=>{const i=document.createElement("option");return i.value=e||-1,i.innerText=t,i},o=i=>{if(i){const o=e.querySelector('[value="'+i+'"]');o.selected=!0;const s=o.text;t.innerText=s}else e.selectedIndex=null,e.value=-1,t.innerText="전체";this.trigger("change","roomKey",i)};e.onchange=e=>{const t=e.target.value;o(t)},this.selectRoom=o,this.getRoom=()=>-1==e.value?void 0:e.value,this.setRoomSelectBox=t=>{e.innerHTML="";const o=i(null,"전체");e.appendChild(o);for(const o of t){const t=i(o.roomKey,o.roomName);e.appendChild(t)}};const s=i(null,"전체");e.appendChild(s),this.selectRoom(this.roomKey)},initElecYN(){const e=a("#switchElec");e.onchange=()=>{const e=this.getElecYN();this.trigger("change","elecYN",e)},this.switchElecYN=t=>{e.checked=t},this.getElecYN=()=>1==e.checked?"Y":null},initSecretYN(){const e=a("#switchSecret");e.onchange=()=>{const e=this.getSecretYN();this.trigger("change","secretYN",e)},this.switchSecretYN=t=>{e.checked=t},this.getSecretYN=()=>1==e.checked?"Y":null},initTitle(){const t=a("#titleInput");t&&(t.oninput=()=>{e(t)},t.onkeyup=e=>{13==e.keyCode&&this.trigger("search",this.getSearchInput())},t.onchange=()=>{this.trigger("change","title",t.value)},this.setTitle=e=>{t.value=e},this.getTitle=()=>""==t.value?null:t.value,this.setTitle(this.title))},initHost(){const e=a("#hostInput");e.oninput=()=>{((e,t={})=>{e.value.length>=e.maxLength&&(t.returnValue=!1),e.value=e.value.replace(/[ \{\}\[\]\/?.,;:|\)*~`!^\-_+┼<>@\#$%&\'\"\\\(\=]/gi,"")})(e)},e.onkeyup=e=>{13==e.keyCode&&this.trigger("search",this.getSearchInput())},e.onchange=()=>{this.trigger("change","host",e.value)},this.setHost=t=>{e.value=t},this.getHost=()=>""==e.value?null:e.value,this.setHost(this.host)},initAttendeeName(){const e=a("#attendeeInput");e.oninput=()=>{((e,t={})=>{e.value.length>=e.maxLength&&(t.returnValue=!1),e.value=e.value.replace(/[0-9\{\}\[\]\/?.,;:|\)*~`!^\-_+┼<>@\#$%&\'\"\\\(\=]/gi,""),e.value=e.value.replace(" ","")})(e)},e.onkeyup=e=>{13==e.keyCode&&this.trigger("search",this.getSearchInput())},e.onchange=()=>{this.trigger("change","attendee",e.value)},this.setAttendeeName=t=>{e.value=t},this.getAttendeeName=()=>""==e.value?null:e.value,this.setAttendeeName(this.attendeeName)},getSearchInput(){const e=this.getOffice?.(),t=this.getSearchTarget?.(),i=this.getSearchWord?.(),o=this.getAppStatus?.(),s=this.getMeetingStatus?.(),n=this.getDate?.(),a=this.getStartDate?.(),r=this.getEndDate?.(),l=this.getRoomType?.(),c=this.getRoom?.(),d=this.getFileType?.(),h=this.getElecYN?.(),m=this.getSecretYN?.(),p=this.getTitle?.(),u=this.getHost?.(),g=this.getAttendeeName?.();return{officeCode:e,approvalStatus:o,meetingStatus:s,searchTarget:t,searchWord:i,holdingDate:n,startDate:a,endDate:r,roomType:l,roomKey:c,roleType:d,elecYN:h,secretYN:m,title:p,host:u,attendeeName:g}},initSearchInput(e={}){const t=moment(),{holdingDate:i=t.format("YYYY-MM-DD"),startDate:o=t.clone().add(-2,"w").format("YYYY-MM-DD"),endDate:s=t.clone().add(2,"w").format("YYYY-MM-DD")}=e;this.selectOffice?.(this.officeCode),this.setSearchTarget?.("title"),this.setSearchWord?.(""),this.selectAppStatus?.("0"),this.selectMeetingStatus?.("0"),this.setDate?.(i),this.setStartDate?.(o),this.setEndDate?.(s),this.selectRoomType?.("ALL_ROOM"),this.checkRoomType?.("MEETING_ROOM"),this.selectRoom?.(),this.switchFileType?.(!1),this.switchElecYN?.(!1),this.switchSecretYN?.(!1),this.setTitle?.(""),this.setHost?.(""),this.setAttendeeName?.("")},enableSearchBtn(){const e=a("#searchBtn");e&&(e.onclick=()=>{this.trigger("search",this.getSearchInput())})},enableResetBtn(e={}){const t=a("#resetBtn");t&&(t.onclick=()=>{this.initSearchInput(e),this.trigger("reset",this.getSearchInput())});const i=a("#resetMobileBtn");i&&(i.onclick=()=>{this.initSearchInput(e),this.trigger("reset",this.getSearchInput());const t=a(".listSrchDiv");"flex"==t.style.display?t.style.display="none":t.style.display="flex"})},enableSearchToggleBtn(){const e=a(".mobileSrchBtn");e&&(e.onclick=()=>{const e=a(".listSrchDiv");"flex"==e.style.display?e.style.display="none":e.style.display="flex"})}},b=document.querySelector("meta[name='_csrf']")?.content,v=document.querySelector("meta[name='_csrf_header']")?.content,D=e=>Object.fromEntries(Object.entries(e).filter((([e,t])=>null!=t))),S=(e,t="GET",i=null,o={},s={})=>{const n=((e,t)=>{const i=new URLSearchParams(D(t)).toString();return i?`${e}?${i}`:e})(e,o),a={"Content-Type":"application/json","X-Requested-With":"XMLHttpRequest"};b&&v&&!["GET","HEAD","OPTIONS","TRACE"].includes(t.toUpperCase())&&(a[v]=b);const r={method:t,headers:{...a,...s}};return i&&("multipart/form-data"===r.headers["Content-Type"]?(delete r.headers["Content-Type"],r.body=i instanceof FormData?i:new FormData):r.headers["Content-Type"].includes("application/x-www-form-urlencoded")?r.body=i instanceof FormData?i:new URLSearchParams(i).toString():r.body=i&&JSON.stringify(i)),console.log("Request","requestUrl:",n,"data:",i,"Options:",r),fetch(n,r).then((e=>{if(!e.ok)throw e;if("0"===e.headers.get("Content-Length"))return null;const t=e.headers.get("Content-Type");if(!t||!t.includes("application/json"))return e.text();try{return e.json()}catch(e){return null}})).catch((e=>{throw console.error("Fetch error:",e),e}))},k=(e,t={})=>S(e,"GET",null,D(t),{}),w=(e,{data:t=null,queryParams:i={},headers:o={}}={})=>S(e,"POST",t,D(i),o),C=(e,{data:t=null,queryParams:i={},headers:o={}}={})=>S(e,"PUT",t,D(i),o),M=(e,{data:t=null,queryParams:i={},headers:o={}}={})=>S(e,"DELETE",t,D(i),o),L=e=>k(`/api/ewp/dept/${e}/sub`),N={Get:{deptOne:e=>k(`/api/ewp/dept/${e}`),subDeptList:L,userList:(e={})=>{const{officeCode:t,deptId:i,deptName:o,userKey:s,userName:n}=e;return k("/api/ewp/user/list",{officeCode:t,deptId:i,deptName:o,userName:n})},subDeptList:L,userAuthorityForMeeting:e=>k(`/api/ewp/user/authority/meeting/${e}`),adminAuthorityForMeeting:e=>k(`/api/ewp/admin/authority/meeting/${e}`),approvalManagerPublicList:(e={})=>{const{officeCode:t,roomType:i}=e;return k("/api/ewp/public/roster/approval-manager/list",{officeCode:t,roomType:i})}},Post:{switchUserAuthenticationToken:(e={})=>{const{userId:t,loginType:i}=e;return w(`/api/ewp/dev/switch-authentication/user/${t}/loginType/${i}`)}},Put:{},Delete:{}},E={Get:{roomOne:(e={})=>{const{roomType:t,roomKey:i}=e;return k(`/api/ewp/room/${t}/${i}`)},roomList:(e={})=>{const{officeCode:t,roomType:i="ALL_ROOM",rentYN:o,delYN:s}=e;return k(`/api/ewp/admin/system/room/${i}/list`,{officeCode:t,rentYN:o,delYN:s})},rentableList:(e={})=>{const{officeCode:t,roomType:i}=e;return k(`/api/ewp/room/${i}/rentable/list`,{officeCode:t})},publicListForDisplay:(e={})=>{const{officeCode:t,roomType:i}=e;return k(`/api/ewp/public/room/${i}/display/list`,{officeCode:t})},roomPermissionList:e=>{const{roomType:t,roomKey:i}=e;return k(`/api/ewp/admin/system/room/${t}/${i}/permission/list`)}},Post:{roomOne:(e={})=>w("/api/ewp/admin/system/room",{data:e,headers:{"Content-Type":"application/x-www-form-urlencoded"}}),roomPermissionList:e=>w("/api/ewp/admin/system/room/permission/list",{data:e,headers:{"Content-Type":"application/json; charset=utf-8"}})},Put:{roomOne:(e={})=>C("/api/ewp/admin/system/room",{data:e,headers:{"Content-Type":"application/x-www-form-urlencoded"}})},Delete:{roomOne:(e={})=>{const{roomType:t,roomKey:i}=e;return M(`/api/ewp/admin/system/room/${t}/${i}`)},roomPermissionAll:(e={})=>{const{roomType:t,roomKey:i}=e;return console.log("data",e),M(`/api/ewp/admin/system/room/${t}/${i}/permission/all`,{data:e})}}},H={assignOne:e=>k(`/api/ewp/meeting/assign/${e}`,{skdKey:e}),assignList:(e={})=>{const{officeCode:t,approvalStatus:i,roomType:o,roomKey:s,title:n,host:a,attendeeName:r,elecYN:l,secretYN:c,startDate:d,endDate:h,pageNo:m,pageCnt:p,orderColumn:u,orderDirection:g="ASC"}=e;return k("/api/ewp/meeting/assign/list",{officeCode:t,approvalStatus:i,roomType:o,roomKey:s,title:n,host:a,attendeeName:r,elecYN:l,secretYN:c,startDate:d,endDate:h,pageNo:m,pageCnt:p,orderColumn:u,orderDirection:g})},assignListCnt:(e={})=>{const{officeCode:t,approvalStatus:i,roomType:o,roomKey:s,title:n,host:a,attendeeName:r,elecYN:l,secretYN:c,startDate:d,endDate:h,pageNo:m,pageCnt:p}=e;return k("/api/ewp/meeting/assign/list/cnt",{officeCode:t,approvalStatus:i,roomType:o,roomKey:s,title:n,host:a,attendeeName:r,elecYN:l,secretYN:c,startDate:d,endDate:h,pageNo:m,pageCnt:p})},assignListForPlanned:(e={})=>{const{startDate:t,endDate:i}=e;return k("/api/ewp/meeting/assign/planned/list",{startDate:t,endDate:i})},assignStatForPlanned:(e={})=>{const{startDate:t,endDate:i}=e;return k("/api/ewp/meeting/assign/planned/stat",{startDate:t,endDate:i})},assignListForRegister:(e={})=>{const{pageNo:t,pageCnt:i}=e;return k("/api/ewp/meeting/assign/register/list",{pageNo:t,pageCnt:i})},assignListForDisplay:(e={})=>{const{officeCode:t,roomType:i,roomKey:o,startDate:s,endDate:n}=e;return k("/api/ewp/meeting/assign/display/list",{officeCode:t,roomType:i,roomKey:o,startDate:s,endDate:n})},assignPublicListForDisplay:(e={})=>{const{officeCode:t,roomType:i,roomKey:o,startDate:s,endDate:n}=e;return k("/api/ewp/public/meeting/assign/display/list",{officeCode:t,roomType:i,roomKey:o,startDate:s,endDate:n})},userArchiveList:(e={})=>{const{skdHost:t,title:i,originalName:o,roleType:s,startDate:n,endDate:a,pageNo:r,pageCnt:l}=e;return k("/api/ewp/meeting/archive/manage/user",{skdHost:t,title:i,originalName:o,roleType:s,startDate:n,endDate:a,pageNo:r,pageCnt:l})},deptArchiveList:(e={})=>{const{skdHost:t,title:i,originalName:o,startDate:s,endDate:n,pageNo:a,pageCnt:r}=e;return k("/api/ewp/meeting/archive/manage/dept",{skdHost:t,title:i,originalName:o,startDate:s,endDate:n,pageNo:a,pageCnt:r})},adminArchiveList:(e={})=>{const{skdHost:t,title:i,originalName:o,startDate:s,endDate:n,pageNo:a,pageCnt:r}=e;return k("/api/ewp/admin/master/meeting/archive/manage",{skdHost:t,title:i,originalName:o,startDate:s,endDate:n,pageNo:a,pageCnt:r})},userAssignList:(e={})=>{const{approvalStatus:t,meetingStatus:i,roomType:o,title:s,host:n,writerKey:a,attendeeName:r,elecYN:l,secretYN:c,startDate:d,endDate:h,pageNo:m,pageCnt:p,orderColumn:u,orderDirection:g="DESC"}=e;return k("/api/ewp/meeting/assign/manage/user",{approvalStatus:t,meetingStatus:i,roomType:o,title:s,host:n,writerKey:a,attendeeName:r,elecYN:l,secretYN:c,startDate:d,endDate:h,pageNo:m,pageCnt:p,orderColumn:u,orderDirection:g})},deptAssignList:(e={})=>{const{approvalStatus:t,meetingStatus:i,roomType:o,title:s,host:n,attendeeName:a,elecYN:r,secretYN:l,startDate:c,endDate:d,pageNo:h,pageCnt:m,orderColumn:p,orderDirection:u="DESC"}=e;return k("/api/ewp/meeting/assign/manage/dept",{approvalStatus:t,meetingStatus:i,roomType:o,title:s,host:n,attendeeName:a,elecYN:r,secretYN:l,startDate:c,endDate:d,pageNo:h,pageCnt:m,orderColumn:p,orderDirection:u})}};window.onload=()=>{moment.locale("ko"),R.init({interval:INTERVAL_MINUTE}),x.init(),Y.init({interval:INTERVAL_MINUTE,officeCode,roomType,roomKey,holdingDate})};const Y={async init(e={}){const{officeCode:t,roomType:i,roomKey:o,holdingDate:s,interval:n}=e;this.interval=n,T.init({officeCode:t,roomType:i,holdingDate:s}),T.initOffice(),T.initRoomTypeCheck(),T.initDatePicker(),T.on("change",(async(e,t)=>{"office"==e&&(this.setApprovalManager(),await this.setRoomList()),"roomType"==e&&(this.setApprovalManager(),await this.setRoomList()),"holdingDate"==e&&await this.search({holdingDate:t})})),this.setApprovalManager(),await this.setRoomList()},async setApprovalManager(){const{officeCode:e,roomType:i}=T.getSearchInput();await y({request:N.Get.approvalManagerPublicList,param:{officeCode:e,roomType:i}}).success((e=>{const i=a("#managerName"),o=a("#managerTel");if(t(e))i.innerHTML="미지정",o.innerHTML="-";else{const t=e[0];i.innerHTML=t.userName,o.innerHTML=t.officeDeskPhone}})).error((()=>{})).exe()},async setRoomList(){const{officeCode:e,roomType:t,holdingDate:i}=T.getSearchInput();y({request:E.Get.rentableList,param:{officeCode:e,roomType:t,rentYN:"Y"}}).success((async e=>{this.roomList=e,I.generate({openTime:OPEN_TIME,closeTime:CLOSE_TIME,interval:this.interval,holdingDate:this.holdingDate,roomList:this.roomList}),await this.search()})).exe()},async search(e={}){const{holdingDate:t=this.holdingDate}=T.getSearchInput();this.holdingDate=t;const i=moment(),o=i.format("YYYYMMDD")==moment(t).format("YYYYMMDD"),s=moment(OPEN_TIME,"HH:mm");moment(CLOSE_TIME,"HH:mm"),R.init({holdingDate:this.holdingDate});for(const e of this.roomList){const t=I.getTable(e.roomKey);t.init(),o&&R.disableInRange(t,s.clone(),i.clone()),H.assignListForDisplay({roomType:e.roomType,roomKey:e.roomKey,startDate:this.holdingDate,endDate:this.holdingDate}).then((i=>{if(!i||0==i.length)return;const o=moment(this.holdingDate),s=["REQUEST","APPROVED"];for(const n of i){if(n.room=e,!s.includes(n.appStatus))continue;const i=moment(n.beginDateTime),a=(i.hour(),i.minute(),moment(n.finishDateTime)),r=(a.hour(),a.minute(),i.clone().add(-this.interval,"m")),l=t.getAllRow(),c=new Set;for(const e of l){const t=e.time,i=moment(t,"HH:mm");if(o.set({hour:i.get("hour"),minute:i.get("minute")}),o.isSame(a))break;if(o.isAfter(a))break;o.isSameOrBefore(r)||c.add(e)}let d;c.forEach((e=>{1!=e.locked&&(d||(d=e),e.style.display="none",e.lockingTime(),e.onclick=()=>{x.showInfoModal(n)})}));try{const e=c.size;d.style.flexBasis=50*e-(e-1)+"px",d.style.display="",d.username.innerHTML=n.skdHost}catch(e){}}}))}}},x={init(){this.setCancelBtn(),this.setInitBtn(),this.setNextBtn()},showInfoModal(e){const t=u.get("infoReservedMeeting");t.querySelector("#titleInput").innerHTML=e.title,t.querySelector("#holdingDateInput").innerHTML=moment(e.holdingDate).format("YYYY.MM.DD.dd"),t.querySelector("#scheduleInput").innerHTML=moment(e.beginDateTime).format("HH:mm")+" ~ "+moment(e.finishDateTime).format("HH:mm"),t.querySelector("#roomNameInput").innerHTML=e.room?.roomName,t.querySelector("#hostInput").innerHTML=e.skdHost,t.querySelector("#writerNameplateInput").innerHTML=e.writer?.nameplate,t.querySelector("#writerTelInput").innerHTML=e.writer?.officeDeskPhone,t.show()},switchReserveBtn(e){const t=a("#initBtn");t&&(t.disabled=!e);const i=a("#nextBtn");i&&(i.disabled=!e)},setCancelBtn(){const e=a("#cancelBtn");e&&(e.onclick=()=>{location.href="/ewp/home"})},setInitBtn(){const e=a("#initBtn");e&&(e.disabled=!0,e.onclick=()=>{R.init(),I.getAllTable().forEach((e=>e.clear()))})},setNextBtn(){const e=a("#nextBtn");e&&(e.disabled=!0,e.onclick=()=>{const e=R.getReserveData();if(e){const{roomType:t,roomKey:i}=e.room,{holdingDate:o,startTime:s,endTime:n}=e;location.href=`/ewp/meeting/assign/post?roomType=${t}&roomKey=${i}&holdingDate=${o}&startTime=${s}&endTime=${n}`}})}},R={init(e={}){const{interval:t=this.interval,holdingDate:i=this.holdingDate}=e;this.interval=t,this.holdingDate=i,this.room=null,this.selected=[],this.initReserveDetail()},getReserveData(){return this.room?{room:this.room,holdingDate:this.holdingDate,startTime:this.startTime,endTime:this.endTime}:null},selectTime(e,t,i){const o=e.roomKey;if(1==t.locked||"SSO"!=loginType)return;this.room&&o!=this.room.roomKey&&(this.selected=[],I.getAllTable().filter((e=>e.roomKey!=o)).forEach((e=>e.clear())));const s=this.selected,n=s.length;let a=[];if(n>=1){let e=s[0],o=s[n-1];t==e?(s.splice(0,1),a=s):t==o?(s.splice(n-1,1),a=s):(t.timeNum<s[0].timeNum?(e=t,o=s[n-1],i.hasLock(e.timeNum,o.timeNum)&&(o=e)):t.timeNum>s[n-1].timeNum?(e=s[0],o=t,i.hasLock(e.timeNum,o.timeNum)&&(e=o)):(e=s[0],o=t),a=i.getRowInRange(e.timeNum,o.timeNum))}else a.push(t);a.length>1&&a.sort(((e,t)=>e.timeNum>t.timeNum?1:-1)),this.reserveSelectedTime(e,a)},reserveSelectedTime(e,t){this.selected=[],I.getTable(e.roomKey).clear();for(const e of t){if(1==e.locked)break;e.selectTime(),this.selected.push(e)}a("#reserveRoomNameInput"),a("#reserveScheduleInput"),0==this.selected.length?(this.room=null,this.initReserveDetail()):(this.room=e,this.startTime=this.selected[0].time,this.endTime=moment(this.selected[this.selected.length-1].time,"HH:mm").add(this.interval,"m").format("HH:mm"),this.setReserveDetail())},disableInRange(e,t,i){const o=100*t.hour()+t.minute();i.add(-this.interval,"m");const s=100*i.hour()+i.minute();e.getRowInRange(o,s).forEach((e=>e.disableTime()))},setReserveDetail(){const e=a("#reserveRoomNameInput");e&&(e.innerHTML=this.room.roomName);const t=a("#reserveScheduleInput");t&&(t.innerHTML=this.startTime+"~"+this.endTime),x.switchReserveBtn(!0)},initReserveDetail(){const e=a("#reserveRoomNameInput");e&&(e.innerHTML="");const t=a("#reserveScheduleInput");t&&(t.innerHTML=""),x.switchReserveBtn(!1)}},I={getAllTable(){return this.roomTableList},getTable(e){return this.roomTableList.find((t=>t.roomKey==e))},generate(e={}){const{holdingDate:t,openTime:i="08:00",closeTime:o="23:00",interval:s=10,roomList:n}=e;this.holdingDate=t,this.openTime=i,this.closeTime=o,this.interval=s;const r=[],l=a("#roomContainer");l.innerHTML="";for(const e of n){const t=this.createRoomTimeTableBox(),i=this.createRoomTitle(e);t.appendChild(i);const o=this.createRoomTable(e);t.appendChild(o),l.appendChild(t),o.roomKey=e.roomKey,r.push(o)}this.roomTableList=r},createRoomTimeTableBox:()=>o("div","p-1","flex-1","roomTitContainer","roomTimeContainer","flex-column","maxw-10","h-100"),createRoomTitle(e){const t=o("div","roomTit","w-100");t.style.height="60px";const i=o("span","roomName","my-1","d-flex","align-items-center");i.innerHTML=e.roomLabel,i.style.height="50px";const s=o("div","roomInfo","d-flex","flex-row","w-100","mb-1","align-items-center");s.style.height="10px";const n=o("div","text-detail","fs-7","w-50");e.roomFloor&&(n.innerHTML=e.roomFloor+"층");const a=o("div","text-detail","fs-7","ml-auto","text-right","w-50");return e.roomSize&&(a.innerHTML=e.roomSize+"인실"),s.appendChild(n),s.appendChild(a),t.appendChild(i),t.appendChild(s),t},createRoomTable(e){const t=e=>{const t=o("div","timeRow","selectable"),i=moment(e,"HH:mm");t.hour=i.hour(),t.minute=i.minute(),t.dataset.time=e,t.dataset.timeNum=100*t.hour+t.minute;const s=o("div","timeText");t.appendChild(s);const n=o("span","minute");n.innerHTML=e;const a=o("span","username");return s.appendChild(n),s.appendChild(a),t};if(!this.tableTemplate){const e=o("div","room"),i=moment(this.openTime,"HH:mm"),s=moment(this.closeTime,"HH:mm");for(;i.isBefore(s);){const o=t(i.format("HH:mm"));e.appendChild(o),i.add(this.interval,"m")}this.tableTemplate=e}const i=(()=>{const i=o("div","room"),s=moment(e.openTime,"HH:mm"),n=moment(e.closeTime,"HH:mm");for(;s.isBefore(n);){const e=t(s.format("HH:mm"));i.appendChild(e),s.add(this.interval,"m")}return i})();return((e,t)=>{const o=Array.from(i.children);o.forEach((i=>{i.locked=!1,i.selected=!1,i.disabled=!1,i.time=i.dataset.time,i.timeNum=1*i.dataset.timeNum;const o=i.querySelector(".timeText"),a=i.querySelector(".username");i.username=a,i.disableTime=()=>{n(i,"selectable"),n(o,"reserved"),i.disabled=!0,i.onclick=null},i.lockingTime=()=>{n(i,"selectable"),s(o,"reserved"),i.selected=!1,i.locked=!0,i.disabled=!1},i.selectTime=()=>{s(o,"selected"),i.selected=!0,i.locked=!1,i.disabled=!1},i.releaseTime=()=>{s(i,"selectable"),n(o,"reserved","selected"),i.selected=!1,i.locked=!1,i.disabled=!1,i.username.innerHTML="",i.onclick=()=>{R.selectTime(t,i,e)}}})),e.clear=()=>{o.forEach((e=>{0==e.locked&&0==e.disabled&&e.releaseTime()}))},e.init=()=>{o.forEach((e=>{e.releaseTime(),e.style.flexBasis="",e.style.display=""}))},e.getAllRow=()=>o,e.getRowInRange=(e,t)=>o.filter((i=>i.timeNum>=e&&i.timeNum<=t)),e.hasLock=(e,t)=>{for(const i of o)if(!(i.timeNum<e)){if(i.timeNum>t)break;if(1==i.locked)return!0}return!1}})(i,e),i}}})();