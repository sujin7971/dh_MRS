import { Util, Dom, ModalService } from '/resources/core-assets/essential_index.js';
const MODAL_TEMPLATE = `
<div id="securityAgreementModal" class="modal" data-modal="securityAgreement">
	<div class="modalWrap">
		<div class="modal_content">
			<div class="modalBody position-relative p-0 m-3 h-100 overflow-auto">
				<div class="position-absolute end-0 border border-1 d-flex flex-row">
					<div class="dropdown">
						<button type="button" id="toggleBtn"
							class="btn btn-md btn-white border-0 dropdown-toggle">개인 서약서</button>
						<ul class="dropdown-menu" id="toggleTarget" style="display: none">
							<li><a data-action="single-layout" class="dropdown-item active" href="#">개인 서약서</a></li>
							<li><a data-action="all-layout" class="dropdown-item" href="#">전체 서약서</a></li>
						</ul>
					</div>
					<div class="vr"></div>
					<button type="button" data-modal-action="DOWNLOAD" data-modal-close="false" class="btn btn-md btn-white border-0" title="다운로드"><i class="fas fa-file-download"></i></button>
					<button type="button" data-modal-action="CLOSE" class="btn btn-md btn-white border-0" title="닫기"><i class="fas fa-times"></i></button>
				</div>
				<article id="agreement-layout" class="d-flex flex-column align-items-center">
					<article class="bg-white border border-1 d-flex flex-column justify-content-between">
						<header class="text-center py-5" data-fragment="header">
							<h1 class="fs-2">
								보 안<span class="mx-3"></span>서 약 서
							</h1>
						</header>
						<section id="oathSection" class="px-5" data-fragment="agreement-detail">
							<article id="docArticle" class="p-0">
								<p class="fs-5 py-2 lh-base h-10">
									<span id="phrase1">본인은 OOOO년 OO월 OO일 한국동서발전(주) OOO 회의를
										함에 있어</span> <span
										class="text-danger">보안준수사항을
										교육받았으며 다음 사항을 준수할 것을 엄숙히 서약합니다.</span>
								</p>
								<ol class="fs-5 pl-4 h-70">
									<li class="py-2 lh-base">본인은 해당 회의(평가) 내용이 국가안전보장 및
										한국동서발전(주)의 기밀자료임을 인정한다.</li>
									<li class="py-2 lh-base">본인은 본 회의(평가)간에 습득한 기밀사항을 누설하는 것이
										국가와 한국동서발전(주)의 이익에 위해를 끼칠 수 있음을 자각하고 보안 관련 제반 법령과 규정을 성실히
										이행한다.</li>
									<li class="py-2 lh-base">본인은 회의와 관련되어 지급받은 일체의 현황과 자료를
										반납하며, 지득한 제반사항을 私益을 위해 외부로 누설 및 유출하지 않는다.</li>
									<li class="py-2 lh-base">위 서약을 어길시에는 어떠한 경우라도 관련 법령에 따라
										엄중한 처벌을 받을 것을 서약한다.</li>
								</ol>
							</article>
						</section>
						<footer class="d-flex flex-column py-3">
							<p class="text-center fs-5 pb-5" id="phrase2"  data-fragment="agreement-date">OOOO년 OO월 OO일</p>
							<div id="single-layout" class="d-flex flex-row px-5" data-fragment="single-sign">
								<div class="col-3 fs-5">
									<span>서 약 자</span>
								</div>
								<div class="col-6 fs-5 d-flex flex-column" id="single-phrase">
								</div>
								<div class="col-3 fs-5 d-flex flex-row align-items-center justify-content-end">
									<div id="signLoc"></div>
								</div>
							</div>
							<div id="all-layout" class="d-none flex-row px-3">
								<table class="table table-sm table-bordered m-0 text-break" style="table-layout: fixed">
									<thead data-fragment="all-table-header">
										<tr>
											<th scope="col">소속</th>
											<th scope="col">이름</th>
											<th scope="col">서명</th>
											<th scope="col">소속</th>
											<th scope="col">이름</th>
											<th scope="col">서명</th>
										</tr>
									</thead>
									<tbody id="all-table-body" class="align-middle text-center" data-fragment="all-table-body">
									</tbody>
								</table>
							</div>
						</footer>
					</article>
				</article>
			</div>
		</div>
	</div>
</div>
`
const Modal = ModalService.getModalModel();
class SecurityAgreementModal extends Modal {
	constructor(options = {}) {
		super(options);
		this.layout = "single-layout";
		const $modalElement = this.modalElement;
		const $toggleBtn = $modalElement.querySelector("#toggleBtn");
		const $toggleTarget = $modalElement.querySelector("#toggleTarget");
		$toggleBtn.onclick = () => {
			Util.slideToggle($toggleTarget, { toggleElement: $toggleBtn });
		}
		const linkList = $toggleTarget.querySelectorAll("a");
		linkList.forEach($a => {
			const action = $a.dataset.action;
			$a.onclick = () => {
				this.selectLayout(action);
				$toggleBtn.innerHTML = $a.innerHTML;
				$toggleTarget.slideUp();
			}
		});
		this.on("action", (name, $button) => {
			if(name == "DOWNLOAD"){
				this.disableButton();
				const loadingModal = ModalService.loadingBuilder().setMessage("다운로드를 시작합니다...").build();
				loadingModal.on("show", () => {
					setTimeout(async () => {
						try {
							loadingModal.setMessage("파일을 생성중입니다. 잠시만 기다려주세요...");
							await this.download();
						} catch (err) {
							console.log(err);
							ModalService.infoBuilder().setMessage("PDF 변환 중에 문제가 발생하여 파일을 다운로드할 수 없습니다. 잠시 후 다시 시도해주세요.").build().show();
						} finally {
							this.enableButton();
							loadingModal.hide();
						}
					});
				}).show();
			}
		});
	}
	selectLayout(name){
		const $toggleTarget = this.modalElement.querySelector("#toggleTarget");
		const linkList = $toggleTarget.querySelectorAll("a");
		linkList.forEach($a => {
			const action = $a.dataset.action;
			if(action == name){
				Dom.addClass($a, "active");
			}else{
				Dom.removeClass($a, "active")
			}
		});
		const $singleLayout = Dom.getElement("#single-layout");
		const $allLayout = Dom.getElement("#all-layout");
		Dom.removeClass($singleLayout, "d-none", "d-flex");
		Dom.removeClass($allLayout, "d-none", "d-flex");
		if (name == "single-layout") {
			Dom.addClass($singleLayout, "d-flex");
			Dom.addClass($allLayout, "d-none");
			this.downloadFileName = `${this.title}_${this.myName}_서약서`;
		} else {
			Dom.addClass($singleLayout, "d-none");
			Dom.addClass($allLayout, "d-flex");
			this.downloadFileName = `${this.title}_서약서`;
		}
		this.layout = name;
	}
	show(params = {}) {
		const {
			title,
			holdingDate,
			myAgreements,
			allAgreements
		} = params;
		this.title = title;
		const holdingDateFormat = moment(holdingDate).format("YYYY년 MM월 DD일");
		const $pharse1 = Dom.getElement("#phrase1");
		$pharse1.innerText = `본인은 ${holdingDateFormat} 한국동서발전(주) ${title} 회의를 함에 있어`;
		const $pharse2 = Dom.getElement("#phrase2");
		$pharse2.innerText = `${holdingDateFormat}`;
		this.setSingleLayout(myAgreements);
		this.setAllLayout(allAgreements);
		this.selectLayout("single-layout");
		
		return super.show();
	}
	setSingleLayout(agreement = {}) {
		const {
			signSrc,
			deptName = "",
			positionName = "",
			userName = ""
		} = agreement;
		const $singlePhrase = Dom.getElement("#single-phrase");
		$singlePhrase.innerHTML = `
		<div><span>소 속: ${deptName}</span></div>
		<div><span>직 위: ${positionName}</span></div>
		<div><span>성 명: ${userName}</span></div>
		`
		const $signLoc = Dom.getElement("#signLoc");
		$signLoc.style.position = "absolute";
		$signLoc.style.pointerEvents = "none";
		$signLoc.innerHTML = "";
		const $signDiv = Util.createElement("div", "signImg");
		const $sign = Util.createElement("img");
		$sign.style.height = "60px";
		$sign.src = signSrc;
		$signDiv.appendChild($sign);
		$signLoc.appendChild($signDiv);
		
		this.myName = userName;
	}
	setAllLayout(agreementList) {
		const setList = agreementList.reduce((acc, cur, index) => {
			if (index % 2 === 0) {
				if (agreementList[index + 1] !== undefined) {
					acc.push([cur, agreementList[index + 1]]);
				} else {
					acc.push([cur]);
				}
			}
			return acc;
		}, []);
		const createTdSet = (agreement) => {
			const {
				signSrc,
				deptName = "",
				positionName = "",
				userName = ""
			} = agreement;
			const $td1 = Dom.createElement("td");
			$td1.innerHTML = deptName;
			const $td2 = Dom.createElement("td");
			$td2.innerHTML = userName;
			const $td3 = Dom.createElement("td");
			if (signSrc) {
				const $img = Dom.createElement("img");
				$img.style.height = "40px";
				$img.src = signSrc;
				$td3.appendChild($img);
			}
			return [$td1, $td2, $td3];
		}
		const $tableBody = Dom.getElement("#all-table-body");
		$tableBody.innerHTML = "";
		setList.forEach(agreementSet => {
			const first = agreementSet[0] || {};
			const second = agreementSet[1] || {};
			const $tr = Dom.createElement("tr");
			createTdSet(first).forEach($td => $tr.appendChild($td));
			createTdSet(second).forEach($td => $tr.appendChild($td));
			$tableBody.appendChild($tr);
		});
	}
	async download() {
		const imgList = Array.from(Dom.getElementAll("img")).filter($img => !this.modalElement.contains($img));
		imgList.forEach($img => {
			const src = $img.src;
			$img.setAttribute('src', '');
			$img.dataset.src = src;
		});
		const fragmentList = ((layout) => {
			const list = [
				Dom.getElement("[data-fragment=header]"),
				Dom.getElement("[data-fragment=agreement-detail]"),
				Dom.getElement("[data-fragment=agreement-date]")
			];
			if (layout == "single-layout") {
				list.push(Dom.getElement("[data-fragment=single-sign]"));
			} else {
				list.push(Dom.getElement("[data-fragment=all-table-header]"));
				const $tableBody = Dom.getElement("[data-fragment=all-table-body]");
				$tableBody?.querySelectorAll("tr")?.forEach($tr => list.push($tr));
			}
			return list;
		})(this.layout);
		try{
			const { promiseWithTimeout, saveAsPdf } = Util;
			const doc = await promiseWithTimeout(saveAsPdf(fragmentList, {nextPageElements: [Dom.getElement("[data-fragment=all-table-header]")]}), fragmentList.length * 2500);
			doc.save(`${this.downloadFileName}.pdf`); // pdf 저장
		}catch(err){
			Dom.getElementAll("iframe").forEach($frame => $frame.remove());
			throw err
		}finally{
			imgList.forEach($img => {
				const src = $img.dataset.src;
				$img.src = src;
				$img.setAttribute('data-src', '');
			});
		}
	}
}
export function setupSecurityAgreementModal() {
	if(setupSecurityAgreementModal.executed){
		return;
	}
	setupSecurityAgreementModal.executed = true;
	Dom.loadJavaScriptDynamically('/resources/library/jspdf/jspdf.umd.min.js');
	Dom.loadJavaScriptDynamically('/resources/library/html2canvas.js');
	const $modalElement = Dom.createFromString(MODAL_TEMPLATE);
	const modalInstance = new SecurityAgreementModal({
		modalElement: $modalElement,
		autoDestroy: false,
	});
	ModalService.add(modalInstance);
	return modalInstance;
}