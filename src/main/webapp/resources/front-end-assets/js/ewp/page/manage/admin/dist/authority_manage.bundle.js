(()=>{"use strict";const e=e=>null==e||"object"==typeof e&&0===Object.keys(e).length||!(!Array.isArray(e)||0!==e.length)||"string"==typeof e&&0===e.trim().length,t=document,s=(e,...s)=>{const i=t.createElement(e);return n(i,...s),i},n=(t,...s)=>{const n=o(t);for(const t of s)e(t)||n?.classList.add(t);return n},i=(t,...s)=>{const n=o(t);for(const t of s)e(t)||n?.classList.remove(t);return n},o=e=>e instanceof HTMLElement?e:t.querySelector(e),a=e=>e instanceof HTMLElement?e:t.querySelectorAll(e),r={on(e,t){if(this._eventHandlers||(this._eventHandlers={}),"string"==typeof e)this._eventHandlers[e]||(this._eventHandlers[e]=[]),this._eventHandlers[e].push(t);else if("object"==typeof e)for(let t in e){let s=t,n=e[t];this._eventHandlers[s]||(this._eventHandlers[s]=[]),this._eventHandlers[s].push(n)}return this},off(e,t){if(!this._eventHandlers)return;if(!e&&!t)return void(this._eventHandlers={});if(e&&!t)return void delete this._eventHandlers[e];let s=this._eventHandlers?.[e];if(s){let e=s.indexOf(t);-1!==e&&s.splice(e,1)}return this},unbind(...e){this.off(...e)},trigger(e,...t){if(this._eventHandlers?.[e])if("string"==typeof e)this._eventHandlers[e].forEach((e=>e.apply(this,t)));else if("object"==typeof e)for(let t in e){let s=t,n=e[t];this._eventHandlers[s].forEach((e=>e.apply(this,n)))}}},d={collection:new Set,add(e){this.collection.add(e)},delete(e){this.collection.delete(e)},get(e){return Array.from(this.collection).find((t=>t.id==e))},getAll(){return this.collection},hasSingleton(){return!!Array.from(this.collection).find((e=>1==e.singleton))}},l=(e,t)=>{if("show"==t){if(d.hasSingleton()&&1!=e.singleton)return;1==e.singleton&&d.getAll().forEach((e=>{e.close()})),d.add(e)}else"hide"==t&&(d.delete(e),1!=e.persistent&&e.remove())},c={promisifyModal(e){const t="string"==typeof e?o(e):e;if(!t)return null;if(1==t.promisify)return t;const s=t.querySelectorAll("[data-modal-btn]");return s.forEach((e=>{e.addEventListener("click",(()=>{t.resolve?.(e.dataset.modalBtn),e.dataset.modalClose&&"manual"==e.dataset.modalClose||t.hide()})),e.setText=t=>(e.innerText=t,e),e.setColor=t=>{switch(t){case"blue":i("$btn","btn-red","btn-white"),n("$btn","btn-blue");break;case"red":i("$btn","btn-blue","btn-white"),n("$btn","btn-red");break;case"white":i("$btn","btn-red","btn-blue"),n("$btn","btn-white")}return e}})),t.getBtn=e=>{const t=Array.from(s).filter((t=>t.dataset.modalBtn==e));return t?.pop()},t.show=e=>(t.style.display="block",t.onshow?.(),t.onswitch?.(t,"show"),e?.(),new Promise(((e,s)=>{t.resolve=e,t.reject=s,t.close=()=>{e("CLOSE"),t.hide()}}))),t.hide=()=>{t.style.display="none",s.forEach((e=>{e.onclick=void 0})),t.onhide?.(),t.onswitch?.(t,"hide")},t.close=t.hide,t.enableBtn=e=>{const s=t.querySelector('[data-modal-btn="'+e+'"]');s&&(s.disabled=!1)},t.disableBtn=e=>{const s=t.querySelector('[data-modal-btn="'+e+'"]');s&&(s.disabled=!0)},t.on=(e,s)=>{switch(e){case"show":t.onshow=s;break;case"hide":t.onhide=s;break;case"switch":t.onswitch=s}return t},t.off=e=>{switch(e){case"show":t.onshow=null;break;case"hide":t.onhide=null;break;case"switch":t.onswitch=null}return t},t.promisify=!0,t},buildTemplate(e){const t=s("div","modal","top-front"),i=s("div","modalWrap");t.appendChild(i);const o=s("div","modal_content");i.appendChild(o);const a=s("div","modalTitle");a.style.display="none",o.appendChild(a),t.getTitleDiv=()=>a;const r=s("div","modalBody","flex-direction-column");r.style.display="none",o.appendChild(r),t.getMsgDiv=()=>r;const d=s("div","modalInfo");d.style.display="none",o.appendChild(d),t.getDetailDiv=()=>d;const l=s("div","modalBtnDiv");return"vertical"==e&&n(l,"flex-direction-column"),o.appendChild(l),t.addBtn=e=>{l.appendChild(e)},t.singleton=!1,t.addBlackout=()=>{t.style.background="rgba(0,0,0,0.5)"},t.removeBlackout=()=>{t.style.background=""},t},buildButton(e){switch(e){case"OK":const t=s("button","btn","btn-md","btn-blue");return t.innerHTML="확 인",t.dataset.modalBtn="OK",t;case"CLOSE":const i=s("button","btn","btn-md","btn-silver");return i.innerHTML="닫 기",i.dataset.modalBtn="CLOSE",i;case"CANCEL":const o=s("button","btn","btn-md","btn-silver");return o.innerHTML="취 소",o.dataset.modalBtn="CANCEL",o;case"DELETE":const a=s("button","btn","btn-md","btn-red");return a.innerHTML="삭 제",a.dataset.modalBtn="DELETE",a;default:const{isPrimary:r,key:d,text:l}=e,c=s("button","btn","btn-md");return n(c,1==r?"btn-blue":"btn-blue-border"),c.innerHTML=l,c.dataset.modalBtn=d,c}},buildModal(e={}){const{btnList:t,direction:n="horizontal",target:i=o("body")}=e,a=this.buildTemplate(n);return t.forEach((e=>a.addBtn(this.buildButton(e)))),a.setTitle=e=>{const t=a.getTitleDiv();t.style.display="",t.innerHTML=e},a.setMsg=e=>{const t=s("div","commonMent");t.innerHTML=e;const n=a.getMsgDiv();n.querySelector(".commonMent")?.remove(),n.style.display="",n.appendChild(t)},a.setDetail=(...e)=>{if(0==e.length)return;const t=s("ul","modalInfo");for(const n of e){const e=s("li");e.innerHTML=n.message?n.message:n,t.appendChild(e)}const n=a.getDetailDiv();n.style.display="",n.appendChild(t)},a.setErrorInfo=(...e)=>{const t=s("ul","modalInfo");for(const n of e){const e=s("li");e.innerHTML=n.message?n.message:n,t.appendChild(e)}a.setDetail(t)},a.persistent=!1,i.appendChild(a),this.promisifyModal(a).on(l)}},h={animations:{doubleBounce:()=>{const e=s("div","sk-double-bounce"),t=s("div","sk-child","sk-double-bounce1"),n=s("div","sk-child","sk-double-bounce2");return e.appendChild(t),e.appendChild(n),e},pulse:()=>s("div","sk-spinner","sk-spinner-pulse"),spinner:()=>{const e=s("div","sk-fading-circle");for(let t=0;t<12;t++){const n=s("div","sk-circle","sk-circle"+(t+1));e.appendChild(n)}return e},foldingCube:()=>{const e=s("div","sk-folding-cube"),t=s("div","sk-cube","sk-cube1"),n=s("div","sk-cube","sk-cube2"),i=s("div","sk-cube","sk-cube3"),o=s("div","sk-cube","sk-cube4");return e.appendChild(t),e.appendChild(n),e.appendChild(o),e.appendChild(i),e}},promisifyModal(e){const t="string"==typeof e?o(e):e;if(!t)return null;if(1==t.promisify)return t;const s=t.querySelectorAll("[data-modal-btn]");return t.show=()=>(n(t,"visible"),i(t,"hidden"),t.onshow?.(),t.onswitch?.(t,"show"),new Promise(((e,n)=>{s.forEach((s=>{s.onclick=()=>{e(s.dataset.modalBtn),t.hide()}})),t.close=()=>{e("CLOSE"),t.hide()}}))),t.hide=()=>{i(t,"visible"),n(t,"hidden"),s.forEach((e=>{e.onclick=void 0})),t.onhide?.(),t.onswitch?.(t,"hide")},t.close=t.hide,t.on=(e,s)=>{switch(e){case"show":t.onshow=s;break;case"hide":t.onhide=s;break;case"switch":t.onswitch=s}return t},t.off=e=>{switch(e){case"show":t.onshow=null;break;case"hide":t.onhide=null;break;case"hide":t.onswitch=null}return t},t.promisify=!0,t},buildTemplate(){const e=s("div","modal","loading-modal","visible"),t=s("div","loading-info-box","m-2");e.appendChild(t);const o=s("div","loading-animation");t.appendChild(o);const a=s("div","loading-text");return t.appendChild(a),e.setAnimation=t=>{const s=this.animations[t]?.();o.appendChild(s),e.animation=s},e.setPosition=t=>{e.style.position=t},e.setText=e=>{a.innerHTML=e},e.setTextColor=e=>{a.style.color=e},e.setAnimationColor=t=>{e.animation.setColor(t)},e.setBackgroundColor=t=>{e.style.backgroundColor=t},e.setBackgroundOpacity=t=>{e.style.opacity=t},e.persistent=!1,e.addBlackout=()=>{e.style.background="rgba(0,0,0,0.5)",i(o,"blackout")},e.removeBlackout=()=>{e.style.background="none",n(o,"blackout")},e},buildLoading(e={}){const{position:t="auto",text:s="",color:n="#fff",opacity:a="0.7",backgroundColor:r="rgb(0,0,0)",animation:c="doubleBounce",target:h=o("body"),blackout:p=!0,isModal:u=!0}=e,m=d.get("loadingModal")?d.get("loadingModal"):this.buildTemplate();return m.setPosition(t),m.setAnimation(c),m.setText(s),m.setTextColor(n),m.setBackgroundColor(r),m.setBackgroundOpacity(a),p?m.addBlackout():m.removeBlackout(),u||i(m,"modal"),h.appendChild(m),this.promisifyModal(m).on("switch",l)}},p={init(){this.target=o("body"),this.persistentMap=new Map,a(".modal").forEach(((e,t)=>{const s=c.promisifyModal(e)?.on("switch",l);if(s){s.persistent=!0;const e=s.id?s.id:"modal"+t;this.persistentMap.set(e,s),s.hide()}}))},persistentModal(e){return this.persistentMap.get(e)},info(e={}){const{id:t="infoModal",title:s="알 림",detail:n,msg:i,singleton:o=!1}=e;console.log("info modal","title",s,"msg",i,"detail",n);const a=c.buildModal({btnList:["OK"]});return a.id=t,a.singleton=o,s&&a.setTitle(s),i&&a.setMsg(i),n&&a.setDetail(...n),a},confirm(e={}){const{id:t="confirmModal",title:s,msg:n,detail:i,delMode:o=!1,singleton:a=!1}=e,r=1==o?["CANCEL","DELETE"]:["CANCEL","OK"],d=c.buildModal({btnList:r});return d.id=t,d.singleton=a,s&&d.setTitle(s),n&&d.setMsg(n),i&&d.setDetail(...i),d},select(e={}){const{id:t="selectModal",title:s,msg:n,select:i=[],singleton:o=!1,direction:a="vertical"}=e;i.push("CLOSE");const r=c.buildModal({btnList:i,direction:a});return r.id=t,r.singleton=o,s&&r.setTitle(s),n&&r.setMsg(n),r},error(e={}){const{id:t="errorModal",singleton:s=!1,response:{status:n,responseText:i,message:o,detail:a,error:r}}=e,d=c.buildModal({btnList:["OK"]});switch(d.id=t,d.singleton=s,n){case 0:d.setTitle("연결 실패"),d.setMsg("서버와의 연결이 끊어졌습니다. 인터넷 연결 상태를 확인하고 다시 시도해주세요."),d.onhide=e=>{location.href="/login"},d.singleton=!0;break;case 500:d.setTitle("500 Internal Server Error"),d.setMsg("서버에서 요청을 처리할 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.");break;case 400:d.setTitle("400 Bad Request"),d.setMsg("요청문에 오류가 있거나 서버가 요청을 이해하지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.");break;case 422:d.setTitle("422 Unprocessable Entity"),d.setMsg("요청에 대해 응답할 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.");break;case 401:d.setTitle("401 Unauthorized"),d.setMsg(i||"인증되지 않은 사용자의 요청입니다. 로그인 페이지로 이동합니다."),d.onhide=e=>{location.href="/login"},d.singleton=!0;break;case 403:d.setTitle("403 Forbidden"),d.setMsg("요청에 대한 권한이 없습니다.");break;case 404:d.setTitle("404 Not Found"),d.setMsg("요청한 페이지를 찾을 수 없습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.");break;case 409:d.setTitle("409 Conflict"),d.setMsg("요청한 서비스가 현재 사용 가능하지 않습니다. 다시 시도해 주세요.");break;default:d.setTitle("잠시 후 다시 시도해주세요"),d.setMsg("서버로부터 올바른 응답을 받지 못했습니다. 지속적으로 발생할 경우 관리자에게 문의해주세요.")}return o&&d.setTitle(o),a&&d.setMsg(a),r&&0!=r.length&&d.setErrorInfo(...r),d},loading(e={}){const{id:t="loadingModal",target:s=this.target}=e;s!==this.target&&(e.blackout=!1,e.isModal=!1);const n=h.buildLoading(e);return n.id=t,n}},u={__proto__:r,init(e={}){const{}=e},promisify:e=>c.promisifyModal(e),info:(e={})=>p.info(e).show(),confirm:(e={})=>p.confirm(e).show(),select:(e={})=>p.select(e).show(),error:(e={})=>p.error(e).show(),startLoading:(e={})=>p.loading(e).show(),endLoading(e={}){d.get("loadingModal")?.hide()},get:e=>p.persistentModal(e),getPopupList:()=>d.getAll(),show:e=>p.persistentModal(e).show(),close(e){const t=d.get(e);t?.close()},closeAll(){d.getAll().forEach((e=>{e.close()}))}};p.init();const m=["success-only","http-errors-only","no-exceptions"];class y{constructor(e={}){const{request:t,param:s,loading:n=!0,debug:i=!0,exception:o=m[1],exceptionModal:a=!0}=e;this.request=t,this.param=s,this.loading=n,this.debug=i,this.exception=o}success(e){return this.successExe=e,this}error(e){return this.errorExe=e,this}finally(e){return this.finallyExe=e,this}async exe(){try{this.loading&&u.startLoading();const e=await this.request(this.param);if(this.loading&&u.endLoading(),this.exception==m[0]&&200!=e.status)throw e;return this.successExe?.(e),e}catch(e){return this.loading&&u.endLoading(),this.debug&&console.log(e),this.exceptionModal&&u.error({response:e}),this.errorExe?.(e),null}finally{this.finallyExe?.()}}}const g=(e={})=>new y(e),b=document.querySelector("meta[name='_csrf']")?.content,f=document.querySelector("meta[name='_csrf_header']")?.content,v=e=>Object.fromEntries(Object.entries(e).filter((([e,t])=>null!=t))),C=(e,t="GET",s=null,n={},i={})=>{const o=((e,t)=>{const s=new URLSearchParams(v(t)).toString();return s?`${e}?${s}`:e})(e,n),a={"Content-Type":"application/json","X-Requested-With":"XMLHttpRequest"};b&&f&&!["GET","HEAD","OPTIONS","TRACE"].includes(t.toUpperCase())&&(a[f]=b);const r={method:t,headers:{...a,...i}};return s&&("multipart/form-data"===r.headers["Content-Type"]?(delete r.headers["Content-Type"],r.body=s instanceof FormData?s:new FormData):r.headers["Content-Type"].includes("application/x-www-form-urlencoded")?r.body=s instanceof FormData?s:new URLSearchParams(s).toString():r.body=s&&JSON.stringify(s)),console.log("Request","requestUrl:",o,"data:",s,"Options:",r),fetch(o,r).then((e=>{if(!e.ok)throw e;if("0"===e.headers.get("Content-Length"))return null;const t=e.headers.get("Content-Type");if(!t||!t.includes("application/json"))return e.text();try{return e.json()}catch(e){return null}})).catch((e=>{throw console.error("Fetch error:",e),e}))},L=(e,t={})=>C(e,"GET",null,v(t),{}),w=(e,{data:t=null,queryParams:s={},headers:n={}}={})=>C(e,"POST",t,v(s),n),M=(e,{data:t=null,queryParams:s={},headers:n={}}={})=>C(e,"PUT",t,v(s),n),T=(e,{data:t=null,queryParams:s={},headers:n={}}={})=>C(e,"DELETE",t,v(s),n),k=e=>L(`/api/ewp/dept/${e}/sub`),x={deptOne:e=>L(`/api/ewp/dept/${e}`),subDeptList:k,userList:(e={})=>{const{officeCode:t,deptId:s,deptName:n,userKey:i,userName:o}=e;return L("/api/ewp/user/list",{officeCode:t,deptId:s,deptName:n,userName:o})},subDeptList:k,userAuthorityForMeeting:e=>L(`/api/ewp/user/authority/meeting/${e}`),adminAuthorityForMeeting:e=>L(`/api/ewp/admin/authority/meeting/${e}`),approvalManagerPublicList:(e={})=>{const{officeCode:t,roomType:s}=e;return L("/api/ewp/public/roster/approval-manager/list",{officeCode:t,roomType:s})}},S="50001253",E=[{__proto__:r,name:"tree",title:"조직도",id:"user_tree",parentCode:S,nowLevel:-1,generateNav(){const e=s("li");return this.$nav=e,e.id=this.id+"-tab",e.innerHTML=this.title,e},generateTab(){const e=s("div","partSelectDiv","flex-column");this.$tab=e,e.id=this.id;const t=[{name:"사업부",text:"사업부선택"},{name:"부서",text:"부서선택"},{name:"팀",text:"팀선택"}],n=(e,t)=>{const n=s("option");return n.value=e,n.innerHTML=t,n},i=e=>{const s=t[e],i=s.node;i.innerHTML="",i.disabled=!0;const o=n(0,s.text);o.style.display="none",i.appendChild(o)},o=e=>{i(this.nowLevel);const s=t[this.nowLevel].node;0!=e.length&&(e.forEach((e=>{const{deptId:t,deptName:i}=e,o=n(t,i);s.appendChild(o)})),s.disabled=!1)},a=async()=>{const e=this.parentCode;this.nowLevel+=1;const t=await x.subDeptList(e);o(t)};return t.forEach(((t,n)=>{const o=s("div","d-flex","flex-row"),r=s("select");t.node=r,o.appendChild(r),e.appendChild(o),r.onchange=async()=>{const e=r.value;if(this.nowLevel>n)for(let e=n+1;e<=this.nowLevel;e++)i(e);this.nowLevel=n,this.parentCode=e,2!=this.nowLevel&&await a(),this.trigger("search",{deptId:e})},i(n)})),a(),this.clear=()=>{t.forEach(((e,t)=>{i(t)})),this.parentCode=S,this.nowLevel=-1,a()},this.setDeptList=e=>{o(e)},e},open(){n(this.$nav,"active"),n(this.$tab,"d-flex"),i(this.$tab,"d-none")},close(){i(this.$nav,"active"),i(this.$tab,"d-flex"),n(this.$tab,"d-none")}},{__proto__:r,name:"text",title:"직원검색",id:"user_search",generateNav(){const e=s("li");return this.$nav=e,e.id=this.id+"-tab",e.innerHTML=this.title,e},generateTab(){const e=s("div","singleSrchDiv","d-flex");this.$tab=e,e.id=this.id;const t=s("div","inputBox");e.appendChild(t);const n=s("input");t.appendChild(n),n.type="text",n.setAttribute("placeholder","이름을 입력해주세요"),n.maxLength=10,n.oninput=()=>{((e,t={})=>{e.value.length>=e.maxLength&&(t.returnValue=!1),e.value=e.value.replace(/[ \{\}\[\]\/?.,;:|\)*~`!^\-_+┼<>@\#$%&\'\"\\\(\=]/gi,"")})(n)},n.onkeyup=e=>{13==e.keyCode&&this.search()};const i=s("div","srchBtn");e.appendChild(i);const o=s("i","far","fa-search");return o.onclick=()=>{this.search()},i.appendChild(o),this.clear=()=>{n.value=""},this.search=()=>{const e=n.value;e&&""!=e&&this.trigger("search",{searchText:e,nameplate:!0})},e},open(){n(this.$nav,"active"),n(this.$tab,"d-flex"),i(this.$tab,"d-none")},close(){i(this.$nav,"active"),i(this.$tab,"d-flex"),n(this.$tab,"d-none")}}],D={getUserBook(){return this.userBook||(this.userBook={}),this.userBook},addUser(e){this.getUserBook()[e.userKey]=e},addUserList(e){const t=this.getUserBook();e.forEach((e=>{t[e.userKey]=e}))},deleteUserList(e){const t=this.getUserBook();e.forEach((e=>{delete t[e]}))},getUserByKey(e){return this.getUserBook()[e]}},q={init(e={}){const{officeCode:t,enableTab:s=["tree","text"],selectType:n="checkbox",checked:i=[],disabled:o=[]}=e;this.enableTab=s,this.officeCode=t,this.selectedList=[],this.checked=i,this.disabled=o,this.selectType=n,this.$modal?this.reset():this.generate()},reset(){this.clearTab(),this.clearUserSearchResultList(),this.clearAttendList()},generate(){this.$modal=s("div","modal"),this.$modal.id="addUserModal",this.$modal.style.display="none";const t=s("div","modalWrap"),i=s("div","modal_content"),a=s("div","modalBody","flex-direction-column");this.$modal.appendChild(t),t.appendChild(i),i.appendChild(a),(()=>{const e=s("ul","addUserTabDiv");a.appendChild(e);const t=s("div","px-3","pt-3","d-flex","flex-column");a.appendChild(t),this.selectTab=e=>{const t=E[e];E.forEach((e=>{e.isEnable&&e.close()})),this.clearTab(),this.clearUserSearchResultList(),t.open()},E.forEach(((s,n)=>{if(this.enableTab.includes(s.name)){s.isEnable=!0;const i=s.generateNav();e.appendChild(i),s.nav=i;const o=s.generateTab();t.appendChild(o),i.onclick=()=>{this.selectTab(n)}}else s.isEnable=!1})),this.clearTab=()=>{E.forEach(((e,t)=>{e.isEnable&&e.clear()}))}})(),(()=>{const t=s("div","px-3","pt-1","d-flex","flex-column");a.appendChild(t);const i=s("div","d-flex","flex-column","overflow-hidden","border","rounded","border-third","p-1");i.style.minHeight="10rem",i.style.maxHeight="20rem",t.appendChild(i);const o=s("div","d-flex","flex-row","border-bottom","pb-1"),r=s("div","pt-1","overflow-auto");(()=>{i.appendChild(o);const e=s("input");e.id="allChk",e.type="checkbox",e.style.visibility="hidden";const t=s("label","ml-2","my-auto");t.htmlFor="allChk",t.style.visibility="hidden",t.innerHTML="전체",o.appendChild(e),o.appendChild(t);const n=s("div","ml-auto","my-auto");o.appendChild(n);const a=s("span");n.appendChild(a);const r=s("span","mx-1");r.innerHTML="/",n.appendChild(r);const d=s("span");n.appendChild(d),e.onchange=()=>{const t=e.checked;this.getSearchResultList().forEach((e=>{const s=e.querySelector("input");if(0==s.disabled&&s.checked!=t){const e=new MouseEvent("click",{bubbles:!1,cancelable:!1});s.dispatchEvent(e)}}))},this.setAllUserCnt=()=>{const s=this.getSearchResultList().length;d.innerHTML=s,o.dataset.allCnt=s,0==s||"radio"==this.selectType?(e.style.visibility="hidden",t.style.visibility="hidden"):(e.style.visibility="visible",t.style.visibility="visible"),this.setSelectableUserCnt()},this.setSelectableUserCnt=()=>{const t=this.getSearchResultList().filter((e=>0==e.querySelector("input").disabled)).length;o.dataset.selectableCnt=t,o.dataset.disabledCnt=o.dataset.allCnt-t,0==t||"radio"==this.selectType?e.style.visibility="hidden":e.style.visibility="visible"},this.setSelectedUserCnt=()=>{const t=this.getSearchResultList().filter((e=>1==e.querySelector("input").checked)),s=t.length,n=t.filter((e=>0==e.querySelector("input").disabled)).length;a.innerHTML=s,o.dataset.selectedCnt=s,o.dataset.nowSelectedCnt=n,o.dataset.selectableCnt==n?(e.checked=!0,e.indeterminate=!1):0==n?(e.checked=!1,e.indeterminate=!1):(e.checked=!1,e.indeterminate=!0)}})(),(()=>{i.appendChild(r),this.getSearchResultList=()=>{const e=r.querySelectorAll("li");return Array.from(e)}})();const d=s("div","selectedUserDiv","border","rounded","border-third","overflow-auto","empty-hidden");d.style.maxHeight="10rem",t.appendChild(d);const l=e=>{const t=[];this.selectedList.forEach((s=>{if(e&&s.userKey!=e)t.push(s);else{const e=r.querySelector("#user"+s.userKey);e&&(e.checked=!1);const t=d.querySelector("#attend"+s.userKey);t&&t.remove()}})),this.selectedList=t,this.setSelectedUserCnt()};this.clearUserSearchResultList=()=>{r.innerHTML="",this.setAllUserCnt(),this.setSelectedUserCnt()},this.clearAttendList=()=>{this.selectedList=[],d.innerHTML=""};const c=(e,t)=>{const n=s("div","attend");n.id="attend"+e;const i=s("span");i.innerHTML=t,n.appendChild(i);const o=s("div","btn-del");o.setAttribute("title","삭제"),n.appendChild(o),o.onclick=()=>{l(e)},d.appendChild(n),this.setSelectedUserCnt()},h=(e,t,i)=>{const o=s("li","d-flex","my-2"),a=s("input");a.name="user",a.type=this.selectType,a.value=t,a.id="user"+t,o.appendChild(a);const d=s("label","ml-2","my-auto");d.innerHTML=i,d.htmlFor="user"+t,o.appendChild(d),r.appendChild(o),this.checked.includes(t)&&(a.checked=!0,"checkbox"==this.selectType&&(a.disabled=!0)),this.disabled.includes(t)&&(a.disabled=!0,n(o,"disabled")),this.selectedList.map((e=>e.userKey)).includes(t)&&(a.checked=!0),a.onchange=()=>{1!=a.disabled&&(1==a.checked?("radio"==this.selectType&&l(),this.selectedList.push({userKey:t,deptId:e,nameplate:i}),c(t,i)):l(t))}};E.forEach(((t,s)=>{t.on("search",(async t=>{const{deptId:s,searchText:n,nameplate:i=!1}=t,o=await x.userList({officeCode:this.officeCode,deptId:s,userName:n});this.clearUserSearchResultList(),e(o)||(D.addUserList(o),o.forEach((e=>{h(e.deptId,e.userKey,i?e.nameplate:e.userName)})),this.setAllUserCnt(),this.setSelectedUserCnt())}))}))})(),(()=>{const e=s("div","modalBtnDiv");i.appendChild(e);const t=s("div","btn","btn-md","btn-silver");t.innerHTML="취 소",this.getCancelBtn=()=>t,e.appendChild(t);const n=s("div","btn","btn-md","btn-blue");n.innerHTML="확 인",this.getConfirmBtn=()=>n,e.appendChild(n)})(),this.selectTab(0),o("body").appendChild(this.$modal)},destroy(){this.$modal.remove(),delete this.$modal,this.selectedList=[]},update(e={}){this.init(e)},show(){this.$modal||this.init(),this.$modal.style.display="block"},hide(){this.reset(),this.$modal.style.display="none"},getSelectedList(){return this.selectedList}},$={__proto__:r,init(e={}){const{onchange:t}=e;return q.init(e),q.getConfirmBtn().onclick=()=>{const e=q.getSelectedList();q.hide(),e.length>0&&(this.trigger("change",e),t?.(e))},q.getCancelBtn().onclick=()=>{q.hide()},this},update(e={}){return this.init(e)},show(){q.show()},getUser:e=>D.getUserByKey(e)},H={Get:{userOne:e=>L(`/api/ewp/admin/system/user/${e}`),officeApprovalPolicy:e=>L(`/api/ewp/manager/approval/office/${e}/policy`),masterDomainList:()=>L("/api/ewp/admin/system/roster/master-admin/list"),systemDomainList:()=>L("/api/ewp/admin/system/roster/system-admin/list"),roomManagerList:(e={})=>{const{officeCode:t,roomType:s}=e;return L("/api/ewp/admin/system/roster/room-manager/list",{officeCode:t,roomType:s})},requestManagerList:(e={})=>{const{officeCode:t,roomType:s}=e;return L("/api/ewp/admin/system/roster/request-manager/list",{officeCode:t})},assignOneForApproval:e=>L(`/api/ewp/manager/approval/meeting/assign/${e}`,{skdKey:e}),assignListForApproval:(e={})=>{const{officeCode:t,approvalStatus:s,roomType:n,roomKey:i,title:o,host:a,attendeeName:r,elecYN:d,secretYN:l,startDate:c,endDate:h,pageNo:p,pageCnt:u}=e;return L("/api/ewp/manager/approval/meeting/assign/list",{officeCode:t,approvalStatus:s,roomType:n,roomKey:i,title:o,host:a,attendeeName:r,elecYN:d,secretYN:l,startDate:c,endDate:h,pageNo:p,pageCnt:u})}},Post:{systemDomain:e=>w(`/api/ewp/admin/system/roster/system-admin/${e}`),requestManager:e=>w(`/api/ewp/admin/system/roster/request-manager/${e}`),roomManager:(e={})=>{const{officeCode:t,roomType:s,userId:n}=e;return w(`/api/ewp/admin/system/roster/room-manager/${n}`,{queryParams:{officeCode:t,roomType:s}})},assignApproval:(e={})=>{const{skdKey:t,status:s,comment:n}=e;return w(`/api/ewp/manager/approval/meeting/assign/${t}/approval/${s}`,{queryParams:{comment:n}})}},Put:{officeAutoApproval:(e={})=>{const{officeCode:t,autoYN:s}=e;return M(`/api/ewp/manager/approval/office/${t}/policy/${s}`)},meetingAssignTitleAndHost:(e={})=>{const{skdKey:t,title:s,skdHost:n}=e;return M(`/api/ewp/manager/approval/assign/${t}`,{queryParams:{title:s,skdHost:n}})}},Delete:{systemDomain:e=>T(`/api/ewp/admin/system/roster/system-admin/${e}`),requestManager:e=>T(`/api/ewp/admin/system/roster/request-manager/${e}`),roomManager:(e={})=>{const{officeCode:t,roomType:s,userId:n}=e;return T(`/api/ewp/admin/system/roster/room-manager/${n}`,{queryParams:{officeCode:t,roomType:s}})}}};window.onload=()=>{R.setAdminListBtn(),B.hideAllCardSection()};const B={async setSection(e){const t=this.showCardSection(e);switch(e){case"sys-master":this.setMasterDomainSection(t);break;case"sys-admin":this.setSystemDomainSection(t);break;case"mng-req":this.setRequestManagerSection(t);break;case"mng-item-mr":this.setRoomManagerSection(t,{roomType:"MEETING_ROOM"});break;case"mng-item-er":this.setRoomManagerSection(t,{roomType:"EDU_ROOM"});break;case"mng-item-hr":this.setRoomManagerSection(t,{roomType:"HALL"})}},showCardSection(e){const t=(e=>{switch(e){case"sys-master":case"sys-admin":case"mng-req":return o("section[data-section="+e+"]");case"mng-item-mr":case"mng-item-er":case"mng-item-hr":return o("section[data-section=mng-room]")}})(e);return t&&(t.style.display=""),t},hideAllCardSection(){a("section").forEach((e=>e.style.display="none"))},async setMasterDomainSection(e){const t=await _.getMasterDomainList();(await _.getSystemDomainList()).map((e=>e.userId)),t.map((e=>e.userId)),A({$section:e,userList:t})},async setSystemDomainSection(e){e.ondelete=e=>{g({request:H.Delete.systemDomain,param:e,exception:"success-only"}).success((async()=>{_.deleteSystemDomain(e)})).exe()},e.querySelector("[data-node=add]").onclick=async()=>{const t=await _.getMasterDomainList(),s=await _.getSystemDomainList(),n=t.map((e=>e.userId)),i=s.map((e=>e.userId));$.update({selectType:"checkbox",checked:i,disabled:n,onchange:async t=>{const s=t.pop().userKey;g({request:H.Post.systemDomain,param:s,exception:"success-only"}).success((async()=>{const t=await H.Get.userOne(s);t.domainRole="ROLE_SYSTEM_ADMIN",_.addSystemDomain(t),this.setSystemDomainSection(e)})).error((e=>{u.error({response:e})})).exe()}}).show()};const t=await _.getSystemDomainList();A({$section:e,userList:t,deletable:!0})},async setRequestManagerSection(e){e.ondelete=e=>{g({request:H.Delete.requestManager,param:e,exception:"success-only"}).success((async()=>{_.deleteRequestManager(e)})).error((e=>{u.error({response:e})})).exe()},e.querySelector("[data-node=add]").onclick=async()=>{const t=(await _.getRequestManagerList()).map((e=>e.userId));$.update({selectType:"checkbox",checked:t,disabled:t,onchange:async t=>{const s=t.pop().userKey;g({request:H.Post.requestManager,param:s,exception:"success-only"}).success((async()=>{const t=await H.Get.userOne(s);_.addRequestManager(t),this.setRequestManagerSection(e)})).exe()}}).show()};const t=await _.getRequestManagerList();A({$section:e,userList:t,deletable:!0})},async setRoomManagerSection(e,t={}){const s=e.querySelector("#officeSelect"),{officeCode:n=s.value,roomType:i="MEETING_ROOM"}=t,o=await _.getRoomManagerList(n,i);s.onchange=t=>{const s=t.target.value;this.setRoomManagerSection(e,{officeCode:s,roomType:i})},e.querySelector("#mng-title").innerText=(e=>{switch(e){case"MEETING_ROOM":return"회의실 담당자";case"EDU_ROOM":return"강의실 담당자";case"HALL":return"강당 담당자"}})(i),e.ondelete=e=>{const t=s.value;g({request:H.Delete.roomManager,param:{officeCode:t,roomType:i,userId:e},exception:"success-only"}).success((async()=>{})).error((e=>{u.error({response:e})})).exe()},e.querySelector("[data-node=add]").onclick=async()=>{const t=o.map((e=>e.userId));$.update({selectType:"checkbox",checked:t,disabled:t,onchange:async t=>{const n=t.pop().userKey,o=s.value;g({request:H.Post.roomManager,param:{officeCode:o,roomType:i,userId:n},exception:"success-only"}).success((async()=>{this.setRoomManagerSection(e,{officeCode:o,roomType:i})})).exe()}}).show()},A({$section:e,userList:o,deletable:!0})}},A=(e={})=>{const{$section:n,userList:i=[],deletable:o=!1}=e,a=(e={user:{}})=>{const{userId:i,userName:a="",officeName:r="",deptHierarchyName:d="",personalCellPhone:l="",officeDeskPhone:c="",email:h=""}=e.user,p=s("div","col"),m=s("div","card");return p.appendChild(m),(()=>{const e=s("div","card-body","d-flex","flex-row");m.appendChild(e),(()=>{const t=s("div","col-2","d-flex","justify-content-center"),n=s("img","card-img","card-img-top");n.src="/api/ewp/user/"+i+"/img",t.appendChild(n),e.appendChild(t)})(),(()=>{const t=s("div","col-8","p-3"),n=s("h3","card-title");n.innerText=a;const i=s("h4","card-text");i.innerText=r;const o=s("p","card-text");o.innerText=d,t.appendChild(n),t.appendChild(i),t.appendChild(o),e.appendChild(t)})(),o&&(()=>{const t=s("div","col-2","text-right"),o=s("button","btn-close");o.type="button",o.onclick=async()=>{"DELETE"==await u.confirm({msg:"해당 사용자를 명단에서 제거하시겠습니까?",delMode:!0})&&(n.ondelete?.(i),p.remove())},t.appendChild(o),e.appendChild(t)})()})(),(()=>{const e=s("div","card-footer");m.appendChild(e),(()=>{const n=s("div","list-unstyled","list-group","list-group-horizontal"),i=(e,n)=>{const i=s("li","mx-auto"),o=s("i","fas","mx-1","fa-"+e);i.appendChild(o);const a=(e=>t.createTextNode(e))(n);return i.appendChild(a),i},o=i("mobile-alt",l),a=i("phone-office",c),r=i("envelope",h);n.appendChild(o),n.appendChild(s("div","vr","mx-1")),n.appendChild(a),n.appendChild(s("div","vr","mx-1")),n.appendChild(r),e.appendChild(n)})()})(),p};n.style.display="",n.loaded=!0;const r=n.querySelector("article");r.innerHTML="";const d=s("div","bs-row","row-cols-1","row-cols-sm-1","row-cols-md-1","row-cols-lg-2","row-cols-xl-2","row-cols-xxl-3","g-4");for(const e of i){const t=a({user:e});d.appendChild(t)}r.appendChild(d)},_={async getMasterDomainList(){return this.masterDomainList||(this.masterDomainList=await H.Get.masterDomainList()),this.masterDomainList},async getSystemDomainList(){return this.systemDomainList||(this.systemDomainList=await H.Get.systemDomainList()),this.systemDomainList},addSystemDomain(e){this.systemDomainList.push(e)},deleteSystemDomain(e){this.systemDomainList=this.systemDomainList.filter((t=>t.userId!=e))},async getRequestManagerList(){if(!this.requsetManagerList){const e=await g({request:H.Get.requestManagerList}).exe();this.requsetManagerList=e}return this.requsetManagerList},addRequestManager(e){this.requsetManagerList.push(e)},deleteRequestManager(e){this.requsetManagerList=this.requsetManagerList.filter((t=>t.userId!=e))},getRoomManagerList:async(e,t)=>await g({request:H.Get.roomManagerList,param:{officeCode:e,roomType:t},loading:!1}).exe()},R={setAdminListBtn(){o("#sidebar");const t=a(".nav-link");t.forEach((s=>{const n=e=>{t.forEach((e=>e.classList.remove("active"))),e.classList.add("active"),B.hideAllCardSection();const s=e.dataset.role;B.setSection(s)};s.addEventListener("click",(()=>{n(s)})),((t,s)=>{const n=o(t);return!(!n||e(s))&&n.classList.contains(s)})(s,"active")&&n(s)}))}}})();