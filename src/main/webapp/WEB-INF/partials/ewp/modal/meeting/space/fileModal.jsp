<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- modal 파일첨부 -->
<div id="fileModal" class="modal">
    <div class="modalWrap">
        <div class="modal_content">
            <div class="modalBody fileForm">

                <div class="row">
					<div class="item">
						<div class="d-flex flex-row mr-auto">
							<h4>첨부파일</h4>
							<div class="mx-1 fw-normal">
							<span id="fileSize">0</span><span> / 300MB</span>
							</div>
							<div class="mx-1 fw-normal">
							<span id="fileCount">0</span><span> / 10개</span>
							</div>
						</div>
						<button id="uploadBtn" class="btn btn-sm btn-blue-border">파일첨부</button>
					</div>
					<div class="answer">
						<div class="inputForm">
							<ul id="fileContainer">
							</ul>							
						</div>
					</div>
				</div>
            </div>
            <ul class="modalInfo">
                <li>첨부파일 지원 : 한글(HWP, HWPX), PDF, MS OFFICE 문서(PPT, PPTX, DOC, DOCX, XLS, XLSX), 이미지(JPG, JPEG, GIF, PNG, BMP) </li>
                <li>첨부파일은 최대 10개의 전체용량 최대 300Mb 까지 가능합니다.</li>
            </ul>
            <div class="modalBtnDiv">
                <button class="btn btn-md btn-silver" data-modal-btn="CLOSE">닫 기</button>
            </div>
        </div>
    </div>
</div>
