(()=>{"use strict";const t=document,e=document.querySelector("meta[name='_csrf']")?.content,n=document.querySelector("meta[name='_csrf_header']")?.content,a=t=>Object.fromEntries(Object.entries(t).filter((([t,e])=>null!=e))),r=(t,r="GET",o=null,s={},c={})=>{const d=((t,e)=>{const n=new URLSearchParams(a(e)).toString();return n?`${t}?${n}`:t})(t,s),i={"Content-Type":"application/json","X-Requested-With":"XMLHttpRequest"};e&&n&&!["GET","HEAD","OPTIONS","TRACE"].includes(r.toUpperCase())&&(i[n]=e);const p={method:r,headers:{...i,...c}};return o&&("multipart/form-data"===p.headers["Content-Type"]?(delete p.headers["Content-Type"],p.body=o instanceof FormData?o:new FormData):p.headers["Content-Type"].includes("application/x-www-form-urlencoded")?p.body=o instanceof FormData?o:new URLSearchParams(o).toString():p.body=o&&JSON.stringify(o)),console.log("Request","requestUrl:",d,"data:",o,"Options:",p),fetch(d,p).then((t=>{if(!t.ok)throw t;if("0"===t.headers.get("Content-Length"))return null;const e=t.headers.get("Content-Type");if(!e||!e.includes("application/json"))return t.text();try{return t.json()}catch(t){return null}})).catch((t=>{throw console.error("Fetch error:",t),t}))},o=(t,e={})=>r(t,"GET",null,a(e),{}),s={paperlessStatForCompany:(t={})=>{const{startDate:e,endDate:n}=t;return o("/api/ewp/meeting/stat/paperless/company",{startDate:e,endDate:n})},meetingCountAndAverageDurationStatForCompany:(t={})=>{const{startDate:e,endDate:n}=t;return o("/api/ewp/meeting/stat/count-and-avgduration/company",{startDate:e,endDate:n})},top5DepartmentWithMeetingForCompany:(t={})=>{const{startDate:e,endDate:n}=t;return o("/api/ewp/meeting/stat/top5-department/company",{startDate:e,endDate:n})},meetingMonthlyTrendForCompany:(t={})=>{const{startDate:e,endDate:n}=t;return console.log("getMeetingMonthlyTrendForCompany","startDate",e,"endDate",n),o("/api/ewp/meeting/stat/monthly-trend/company",{startDate:e,endDate:n})},paperlessStatForPersonal:(t={})=>{const{startDate:e,endDate:n}=t;return o("/api/ewp/meeting/stat/paperless/personal",{startDate:e,endDate:n})},meetingCountAndTotalDurationStatForHosting:(t={})=>{const{startDate:e,endDate:n}=t;return o("/api/ewp/meeting/stat/count-and-totalduration/hosting",{startDate:e,endDate:n})},meetingCountAndTotalDurationStatForAttendance:(t={})=>{const{startDate:e,endDate:n}=t;return o("/api/ewp/meeting/stat/count-and-totalduration/attendance",{startDate:e,endDate:n})},top5DepartmentWithMeetingForOffice:(t={})=>{const{startDate:e,endDate:n}=t;return o("/api/ewp/meeting/stat/top5-department/office",{startDate:e,endDate:n})},meetingMonthlyTrendForPersonal:(t={})=>{const{startDate:e,endDate:n}=t;return o("/api/ewp/meeting/stat/monthly-trend/personal",{startDate:e,endDate:n})}};document.addEventListener("DOMContentLoaded",(async()=>{c()}));const c=async()=>{const e=await s.paperlessStatForPersonal();var n;((n="[data-stat]")instanceof HTMLElement?n:t.querySelectorAll(n)).forEach((t=>{const n=((t,e)=>{switch(t){case"pageReduction":return e+"장";case"costReduction":return 20*e+"원";case"gasReduction":return(2.88*e).toFixed(2)+"g";case"waterReduction":return 10*e+"L";default:return""}})(t.getAttribute("data-stat"),e);t.textContent=n}))}})();