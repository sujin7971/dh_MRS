<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%-- 
* 회의 스케줄 페이지
- 회의관리프로그램 메인 페이지 역할
 --%>
<!DOCTYPE HTML>
<div class="noticeSection mt-0">
	<div class="titDiv">
	    <div class="pageTit"><span>공지사항 </span><span></span></div>
	</div>
	<div class="container" id="noticeContainer">
		<div id="fixedNoticeListBox"></div>
		<div id="nonfixedNoticeListBox"></div>
    </div>
</div>
<!-- 공지사항 보기 모달 -->
<div id="noticeViewModal" class="modal" style="display:none;">
    <div class="modalWrap">
        <div class="modal_content">
			
            <div class="modalTitle" id="office">공지사항</div>
            <div class="modalBody">
                <div class="modalFormDiv">
					<h2 class="mb-3" id="title"></h2>
					<div class="fs-6 fw-normal text-preline lh-base overflow-scroll border p-1" id="contents" style="min-height:20vh; max-height:40vh; min-width:20vw">
					</div>
					<div id="fileViewBox" class="flex-column my-2">
						<div class="bg-light border-bottom p-2">
							<i class="fas fa-paperclip"></i> 첨부파일 <span id="attachedCount" class="fw-bold"></span>
						</div>
						<ul class="list-group list-group-flush">
						</ul>
					</div>
					<div id="writerViewBox" class="fw-normal text-right mt-3 fs-6">
					</div>
                </div>
            </div>
            <div class="modalBtnDiv">
                <div class="btn btn-md btn-silver btn-cancle" data-modal-btn="CLOSE">닫 기</div>
            </div>
        </div>
    </div>
</div>
<script type="module" src="${urls.getForLookupPath('/resources/front-end-assets/js/ewp/partials/fragment/home/dist/home_notice.bundle.js')}"></script>
